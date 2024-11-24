package com.example.reservation_service.Web;


import com.example.reservation_service.Entity.Passenger;
import com.example.reservation_service.Entity.Reservation;
import com.example.reservation_service.Service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        return reservationService.createReservation(reservation);
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable String id) {
        return reservationService.getReservationById(id);
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable String id) {
        reservationService.deleteReservation(id);
    }
    @GetMapping("/passengers")
    public List<Passenger> getAllPassengers() {
        return reservationService.getAllReservations()
                .stream()
                .flatMap(reservation -> reservation.getPassengers().stream())
                .collect(Collectors.toList());
    }
    @GetMapping("/passenger/{cin}")
    public Reservation getPassengerByCin(@PathVariable String cin) {
        return reservationService.getPassengerByCin(cin);
    }
}
