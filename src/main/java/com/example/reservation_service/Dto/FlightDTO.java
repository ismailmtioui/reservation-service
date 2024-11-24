package com.example.reservation_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightDTO {

    private Long idFlight;
    private String departure;
    private String destination;
    private String schedule; // Can be replaced with LocalDateTime for better handling
    private Integer flightCapacity;
    private Double duration; // In hours
    private String company;
    private Integer availableSeats;
    private Double basePrice;
    private List<FlightClassDTO> flightClasses;

}
