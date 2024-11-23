package com.example.reservation_service.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {

    private String email;
    private String cin;
    private String password;
    private Integer age;
    private PassengerType passengerType;

}
