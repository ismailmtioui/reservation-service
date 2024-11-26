package com.example.reservation_service.Dto;

public class PassengerCredentialsDTO {

    private String email;
    private String password;
    private String cin;

    // Constructor
    public PassengerCredentialsDTO(String email, String password, String cin) {
        this.email = email;
        this.password = password;
        this.cin = cin;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCin() {
        return cin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }
}
