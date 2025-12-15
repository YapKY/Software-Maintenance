package com.example.springboot.model;

import lombok.Data;

@Data
public class Ticket {
    private String documentId;
    private String bookingReference; // Unique UUID for QR Code
    
    // References
    private String customerId;
    private String passengerId;
    private String seatId;
    private String flightId;  // NEW
    
    // Display fields
    private String seatNumberDisplay;
    private String seatClassDisplay;
    
    // Full objects for display (not stored in DB)
    private Flight flightDetails;      // NEW
    private Passenger passengerDetails; // NEW
}