package com.example.reservation_service.Service;

import com.example.reservation_service.Dto.FlightClassDTO;
import com.example.reservation_service.Dto.FlightDTO;
import com.example.reservation_service.Dto.PassengerCredentialsDTO;
import com.example.reservation_service.Entity.Luggage;
import com.example.reservation_service.Entity.Passenger;
import com.example.reservation_service.Entity.Reservation;
import com.example.reservation_service.Repository.ReservationRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private AmqpTemplate amqpTemplate;  // RabbitMQ template to send messages

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestTemplate restTemplate;  // Autowired RestTemplate for making HTTP requests

    // Flight service URL
    private static final String FLIGHT_SERVICE_URL = "http://localhost:8080/api/flights/";

    // RabbitMQ queue name
    private static final String QUEUE_NAME = "passengerQueue";

    // Create a reservation and send passenger credentials to RabbitMQ
    public Reservation createReservation(Reservation reservation) {
        // Fetch flight information using the flight ID
        FlightDTO flight = restTemplate.getForObject(FLIGHT_SERVICE_URL + reservation.getFlightId(), FlightDTO.class);

        // Calculate the total price based on passenger type and luggage weight
        Double totalPrice = calculateTotalPrice(reservation, flight);

        // Set the total price for the reservation
        reservation.setTotalPrice(totalPrice);

        // Save the reservation in the database
        Reservation savedReservation = reservationRepository.save(reservation);

        // Send passenger credentials to RabbitMQ
        sendPassengerCredentialsToRabbitMQ(reservation);


        return savedReservation;
    }



    // Retrieve a reservation by its ID
    public Reservation getReservationById(String id) {
        return reservationRepository.findById(id).orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    // Retrieve all reservations
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // Delete a reservation by ID
    public void deleteReservation(String id) {
        reservationRepository.deleteById(id);
    }

    // Calculate the total price for the reservation
    private Double calculateTotalPrice(Reservation reservation, FlightDTO flight) {
        Double price = flight.getBasePrice();

        // Adjust price based on selected flight class
        for (FlightClassDTO flightClass : flight.getFlightClasses()) {
            if (flightClass.getFlightClassType() == reservation.getFlightClassType()) {
                price = flightClass.getPrice();
                break;
            }
        }

        // Apply passenger pricing logic
        for (Passenger passenger : reservation.getPassengers()) {
            price = applyPassengerPricing(price, passenger);
        }

        // Apply luggage pricing logic
        for (Luggage luggage : reservation.getLuggage()) {
            price = applyLuggagePricing(price, luggage);
        }

        return price;
    }

    // Apply pricing logic based on passenger type
    private Double applyPassengerPricing(Double price, Passenger passenger) {
        switch (passenger.getPassengerType()) {
            case INFANT:
                return price / 2;  // Infant pays half price
            case STUDENT:
                return price * 0.75;  // Student gets a 25% discount
            case ADULT:
            default:
                return price;  // Adult pays full price
        }
    }

    // Apply pricing logic based on luggage weight
    private Double applyLuggagePricing(Double price, Luggage luggage) {
        if (luggage.getWeight() >= 20 && luggage.getWeight() <= 30) {
            return price * 1.10;  // Add 10% for luggage between 20-30 kg
        } else if (luggage.getWeight() > 30) {
            return price * 1.15;  // Add 15% for luggage over 30 kg
        }
        return price;  // No extra charge for luggage <= 10 kg
    }

    // Retrieve a reservation by passenger's CIN
    public Reservation getPassengerByCin(String cin) {
        Optional<Reservation> reservation = reservationRepository.findByPassengersCin(cin);
        return reservation.orElse(null); // Returns null if not found
    }

    // Retrieve all passenger credentials (email, password, cin)
    public List<PassengerCredentialsDTO> getAllPassengerCredentials() {
        List<Reservation> reservations = reservationRepository.findAll();
        List<PassengerCredentialsDTO> credentials = new ArrayList<>();

        for (Reservation reservation : reservations) {
            credentials.addAll(reservation.getPassengers().stream()
                    .map(passenger -> new PassengerCredentialsDTO(passenger.getEmail(), passenger.getPassword(), passenger.getCin()))
                    .collect(Collectors.toList()));
        }
        return credentials;
    }

    // Send passenger credentials (email, password, CIN) to RabbitMQ
    private void sendPassengerCredentialsToRabbitMQ(Reservation reservation) {
        for (Passenger passenger : reservation.getPassengers()) {
            // Validate the CIN field before sending
            if (passenger.getCin() == null || passenger.getCin().isEmpty()) {
                throw new IllegalArgumentException("CIN is required for passenger: " + passenger.getEmail());
            }

            // Create the DTO to send to the user microservice, including CIN
            PassengerCredentialsDTO credentialsDTO = new PassengerCredentialsDTO(
                    passenger.getEmail(), passenger.getPassword(), passenger.getCin()
            );

            // Log the message before sending it
            System.out.println("Sending passenger credentials to RabbitMQ: " + credentialsDTO);

            // Send the passenger credentials to RabbitMQ
            amqpTemplate.convertAndSend(QUEUE_NAME, credentialsDTO);
        }
    }
    public PassengerCredentialsDTO getPassengerCredentialsByCin(String cin) {
        // Find a reservation by passenger's CIN
        Optional<Reservation> reservation = reservationRepository.findByPassengersCin(cin);
        if (reservation.isPresent()) {
            return reservation.get().getPassengers().stream()
                    .filter(passenger -> passenger.getCin().equals(cin))
                    .map(passenger -> new PassengerCredentialsDTO(passenger.getEmail(), passenger.getPassword(), passenger.getCin()))
                    .findFirst()
                    .orElse(null);  // Return null if no matching passenger found
        }
        return null;  // Return null if no reservation with that CIN is found
    }
}
