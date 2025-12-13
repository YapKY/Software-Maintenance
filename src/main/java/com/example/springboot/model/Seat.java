package com.example.springboot.model;

import lombok.Data;

@Data
public class Seat {
    private String documentId;  // Firestore Document ID
    private int seatNumber;     // e.g., 101
    private String typeOfSeat;  // "Economy", "Business"
    private String statusSeat;  // "Empty", "Booked"
    private String flightId;    // Reference to Flight
}