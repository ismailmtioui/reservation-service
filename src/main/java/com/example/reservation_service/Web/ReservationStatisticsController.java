package com.example.reservation_service.Web;

import com.example.reservation_service.Entity.Reservation;
import com.example.reservation_service.Service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.reservation_service.Entity.Luggage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations/statistics")
public class ReservationStatisticsController {

    @Autowired
    private ReservationService reservationService;

    // Endpoint to get the statistics for the reservations
    @GetMapping("/passenger-type-count")
    public Map<String, Long> getPassengerTypeCount() {
        List<Reservation> reservations = reservationService.getAllReservations();

        // Aggregate passenger types
        return reservations.stream()
                .flatMap(reservation -> reservation.getPassengers().stream())
                .collect(Collectors.groupingBy(passenger -> passenger.getPassengerType().name(), Collectors.counting()));
    }

    // Endpoint to get the total price of all reservations
    @GetMapping("/total-price")
    public Double getTotalPrice() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return reservations.stream()
                .mapToDouble(Reservation::getTotalPrice)
                .sum();
    }

    // Endpoint to get average luggage weight for all reservations
    @GetMapping("/average-luggage-weight")
    public Double getAverageLuggageWeight() {
        List<Reservation> reservations = reservationService.getAllReservations();

        // Calculate the average weight of luggage
        return reservations.stream()
                .flatMap(reservation -> reservation.getLuggage().stream())
                .mapToDouble(Luggage::getWeight)
                .average()
                .orElse(0.0);
    }
}
