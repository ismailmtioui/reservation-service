package com.example.reservation_service.Service;

import com.example.reservation_service.Dto.FlightClassDTO;
import com.example.reservation_service.Dto.FlightDTO;
import com.example.reservation_service.Entity.Luggage;
import com.example.reservation_service.Entity.Passenger;
import com.example.reservation_service.Entity.Reservation;
import com.example.reservation_service.Repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestTemplate restTemplate; // Autowired RestTemplate

    private static final String FLIGHT_SERVICE_URL = "http://localhost:8080/api/flights/";

    public Reservation createReservation(Reservation reservation) {
        // Fetch flight information using the flight ID
        FlightDTO flight = restTemplate.getForObject(FLIGHT_SERVICE_URL + reservation.getFlightId(), FlightDTO.class);

        // Calculate the total price based on passenger type and luggage weight
        Double totalPrice = calculateTotalPrice(reservation, flight);

        reservation.setTotalPrice(totalPrice);
        return reservationRepository.save(reservation);
    }

    public Reservation getReservationById(String id) {
        return reservationRepository.findById(id).orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public void deleteReservation(String id) {
        reservationRepository.deleteById(id);
    }

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

    private Double applyLuggagePricing(Double price, Luggage luggage) {
        if (luggage.getWeight() >= 20 && luggage.getWeight() <= 30) {
            return price * 1.10;  // Add 10% for luggage between 20-30 kg
        } else if (luggage.getWeight() > 30) {
            return price * 1.15;  // Add 15% for luggage over 30 kg
        }
        return price;  // No extra charge for luggage <= 10 kg
    }
    public Reservation getPassengerByCin(String cin) {
        Optional<Reservation> reservation = reservationRepository.findByPassengersCin(cin);
        return reservation.orElse(null); // Returns null if not found
    }
}
