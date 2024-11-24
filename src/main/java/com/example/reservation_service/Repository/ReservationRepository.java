package com.example.reservation_service.Repository;


import com.example.reservation_service.Entity.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, String> {
    Optional<Reservation> findByPassengersCin(String cin);
}
