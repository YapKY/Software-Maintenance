package com.example.springboot.dto;

import com.example.springboot.model.Passenger;

import lombok.Data;

@Data
public class BookingRequestDTO {
    private String customerId;
    private String seatId;
    private Double amount;
    private String stripePaymentIntentId;
    private Passenger passenger; // Nested object to create new passenger
}