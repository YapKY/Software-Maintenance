package com.example.springboot.model;

import lombok.Data;

@Data
public class Ticket {
    private String documentId;
    private String bookingReference; // Unique UUID for QR Code
    
    // References (Foreign Keys in NoSQL)
    private String customerId;
    private String passengerId;
    private String seatId;
    
    // Display Helpers (Not stored in DB, populated by Service)
    private String seatNumberDisplay;
    private String seatClassDisplay;
}