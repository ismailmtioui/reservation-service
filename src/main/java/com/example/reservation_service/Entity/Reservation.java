package com.example.reservation_service.Entity;

import com.example.reservation_service.Dto.FlightClassType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reservations")
public class Reservation {

    @Id
    private String id;
    private Long flightId;
    private List<Passenger> passengers;
    private List<Luggage> luggage;
    private FlightClassType flightClassType;
    private Double totalPrice;

}
