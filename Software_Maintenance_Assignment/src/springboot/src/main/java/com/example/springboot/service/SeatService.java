package com.example.springboot.service;

import com.example.springboot.model.Seat;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Seat Service
 * Handles seat creation and management in Firestore
 */
@Service
public class SeatService {

    private static final String COLLECTION_NAME = "seats";
    private static final int BUSINESS_CLASS_COUNT = 4; // First 4 seats are Business
    private static final int SEAT_NUMBER_START = 100;  // Seats start from 100

    @Autowired
    private Firestore firestore;

    /**
     * Create seats for a new flight
     * 
     * @param flightId The flight ID (e.g., "F002")
     * @param totalSeats Total number of seats to create (e.g., 32)
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void createSeatsForFlight(String flightId, int totalSeats) 
            throws ExecutionException, InterruptedException {
        
        System.out.println("Creating " + totalSeats + " seats for flight " + flightId);
        
        List<Map<String, Object>> seatBatch = new ArrayList<>();
        
        for (int i = 0; i < totalSeats; i++) {
            int seatNumber = SEAT_NUMBER_START + i;
            
            // First 4 seats (100-103) are Business, rest are Economy
            String typeOfSeat = (i < BUSINESS_CLASS_COUNT) ? "Business" : "Economy";
            
            Map<String, Object> seatData = new HashMap<>();
            seatData.put("flightId", flightId);
            seatData.put("seatNumber", seatNumber);
            seatData.put("statusSeat", "Available");  // Default status
            seatData.put("typeOfSeat", typeOfSeat);
            
            seatBatch.add(seatData);
        }
        
        // Save all seats to Firestore
        for (Map<String, Object> seatData : seatBatch) {
            firestore.collection(COLLECTION_NAME)
                .add(seatData)
                .get();
        }
        
        System.out.println("✓ Successfully created " + totalSeats + " seats for flight " + flightId);
        System.out.println("  - Business Class: Seats " + SEAT_NUMBER_START + "-" + (SEAT_NUMBER_START + BUSINESS_CLASS_COUNT - 1));
        System.out.println("  - Economy Class: Seats " + (SEAT_NUMBER_START + BUSINESS_CLASS_COUNT) + "-" + (SEAT_NUMBER_START + totalSeats - 1));
    }

    /**
     * Delete all seats for a flight
     * Called when a flight is deleted
     * 
     * @param flightId The flight ID
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void deleteSeatsForFlight(String flightId) 
            throws ExecutionException, InterruptedException {
        
        System.out.println("Deleting seats for flight " + flightId);
        
        var querySnapshot = firestore.collection(COLLECTION_NAME)
            .whereEqualTo("flightId", flightId)
            .get()
            .get();
        
        int deletedCount = 0;
        for (var document : querySnapshot.getDocuments()) {
            document.getReference().delete().get();
            deletedCount++;
        }
        
        System.out.println("✓ Deleted " + deletedCount + " seats for flight " + flightId);
    }

    /**
     * Get all seats for a flight
     * 
     * @param flightId The flight ID
     * @return List of seats
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<Seat> getSeatsForFlight(String flightId) 
            throws ExecutionException, InterruptedException {
        
        var querySnapshot = firestore.collection(COLLECTION_NAME)
            .whereEqualTo("flightId", flightId)
            .orderBy("seatNumber")
            .get()
            .get();
        
        List<Seat> seats = new ArrayList<>();
        for (var document : querySnapshot.getDocuments()) {
            Seat seat = new Seat();
            seat.setDocumentId(document.getId());
            seat.setFlightId(document.getString("flightId"));
            seat.setSeatNumber(document.getLong("seatNumber").intValue());
            seat.setStatusSeat(document.getString("statusSeat"));
            seat.setTypeOfSeat(document.getString("typeOfSeat"));
            seats.add(seat);
        }
        
        return seats;
    }

    /**
     * Get seat availability count for a flight
     * 
     * @param flightId The flight ID
     * @return Map with available/booked counts
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public Map<String, Integer> getSeatAvailability(String flightId) 
            throws ExecutionException, InterruptedException {
        
        List<Seat> seats = getSeatsForFlight(flightId);
        
        int availableCount = 0;
        int bookedCount = 0;
        
        for (Seat seat : seats) {
            if ("Available".equalsIgnoreCase(seat.getStatusSeat())) {
                availableCount++;
            } else if ("Booked".equalsIgnoreCase(seat.getStatusSeat())) {
                bookedCount++;
            }
        }
        
        Map<String, Integer> availability = new HashMap<>();
        availability.put("total", seats.size());
        availability.put("available", availableCount);
        availability.put("booked", bookedCount);
        
        return availability;
    }
}