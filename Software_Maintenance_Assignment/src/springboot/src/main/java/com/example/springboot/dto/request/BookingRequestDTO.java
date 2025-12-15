package com.example.springboot.dto.request;

import com.example.springboot.model.Passenger;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingRequestDTO {
    private String customerId;
    private String seatId;
    private Double amount;
    private String stripePaymentIntentId;
    private Passenger passenger; // Nested object to create new passenger
}