package com.example.springboot.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    private String documentId;          // Firestore Document ID
    private String flightId;            // e.g., "F001"
    private String departureCountry;    // e.g., "Malaysia"
    private String arrivalCountry;      // e.g., "Japan"
    private String departureDate;       // e.g., "11/11/2023"
    private String arrivalDate;         // e.g., "12/11/2023"
    private int departureTime;          // e.g., 1300 (1:00 PM)
    private int arrivalTime;            // e.g., 2000 (8:00 PM)
    private int boardingTime;           // e.g., 1200 (12:00 PM)
    private double economyPrice;        // e.g., 200.00
    private double businessPrice;       // e.g., 400.00
    private String planeNo;             // e.g., "PL04"
    private int totalSeats;             // e.g., 32

    private String status = "ACTIVE";
    public Flight(String flightId, String departureCountry, String arrivalCountry,
                  String departureDate, String arrivalDate, int departureTime,
                  int arrivalTime, int boardingTime, double economyPrice,
                  double businessPrice, String planeNo, int totalSeats) {
        this.flightId = flightId;
        this.departureCountry = departureCountry;
        this.arrivalCountry = arrivalCountry;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.boardingTime = boardingTime;
        this.economyPrice = economyPrice;
        this.businessPrice = businessPrice;
        this.planeNo = planeNo;
        this.totalSeats = totalSeats;
        this.status = "ACTIVE";
    }
    

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(this.status);
    }
    
    public void deactivate() {
        this.status = "INACTIVE";
    }
    
    public void activate() {
        this.status = "ACTIVE";
    }
}