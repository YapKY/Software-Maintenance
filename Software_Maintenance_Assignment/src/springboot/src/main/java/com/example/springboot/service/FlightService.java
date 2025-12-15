package com.example.springboot.service;

import com.example.springboot.model.Flight;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Flight Service with Seat Creation Integration
 * Handles flight operations and automatically creates/manages seats
 */
@Service
public class FlightService {

    private static final String COLLECTION_NAME = "flights";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");

    @Autowired
    private Firestore firestore;

    @Autowired
    private SeatService seatService;  // Inject SeatService

    // ==================== ADD FLIGHT (WITH SEAT CREATION) ====================
    /**
     * Add a new flight and create seats automatically
     */
    public Flight addFlight(Flight flight) throws ExecutionException, InterruptedException {
        // Validate flight
        validateFlight(flight, false);

        // Set status to ACTIVE
        flight.setStatus("ACTIVE");

        // Prepare flight data
        Map<String, Object> flightData = new HashMap<>();
        flightData.put("flightId", flight.getFlightId());
        flightData.put("departureCountry", flight.getDepartureCountry());
        flightData.put("arrivalCountry", flight.getArrivalCountry());
        flightData.put("departureDate", flight.getDepartureDate());
        flightData.put("arrivalDate", flight.getArrivalDate());
        flightData.put("departureTime", flight.getDepartureTime());
        flightData.put("arrivalTime", flight.getArrivalTime());
        flightData.put("boardingTime", flight.getBoardingTime());
        flightData.put("economyPrice", flight.getEconomyPrice());
        flightData.put("businessPrice", flight.getBusinessPrice());
        flightData.put("planeNo", flight.getPlaneNo());
        flightData.put("totalSeats", flight.getTotalSeats());
        flightData.put("status", flight.getStatus());

        // Save flight to Firestore
        DocumentReference docRef = firestore.collection(COLLECTION_NAME)
            .add(flightData)
            .get();

        flight.setDocumentId(docRef.getId());

        // ✅ AUTOMATICALLY CREATE SEATS FOR THIS FLIGHT
        System.out.println("Flight " + flight.getFlightId() + " added successfully. Creating seats...");
        seatService.createSeatsForFlight(flight.getFlightId(), flight.getTotalSeats());

        return flight;
    }

    // ==================== UPDATE FLIGHT ====================
    /**
     * Update an existing flight
     */
    public Flight updateFlight(String documentId, Flight flight) 
            throws ExecutionException, InterruptedException {
        
        // Validate flight
        validateFlight(flight, true);

        // Get existing flight
        DocumentSnapshot document = firestore.collection(COLLECTION_NAME)
            .document(documentId)
            .get()
            .get();

        if (!document.exists()) {
            throw new IllegalArgumentException("Flight not found with ID: " + documentId);
        }

        // Prepare update data
        Map<String, Object> updates = new HashMap<>();
        updates.put("flightId", flight.getFlightId());
        updates.put("departureCountry", flight.getDepartureCountry());
        updates.put("arrivalCountry", flight.getArrivalCountry());
        updates.put("departureDate", flight.getDepartureDate());
        updates.put("arrivalDate", flight.getArrivalDate());
        updates.put("departureTime", flight.getDepartureTime());
        updates.put("arrivalTime", flight.getArrivalTime());
        updates.put("boardingTime", flight.getBoardingTime());
        updates.put("economyPrice", flight.getEconomyPrice());
        updates.put("businessPrice", flight.getBusinessPrice());
        updates.put("planeNo", flight.getPlaneNo());
        updates.put("totalSeats", flight.getTotalSeats());

        // Update in Firestore
        firestore.collection(COLLECTION_NAME)
            .document(documentId)
            .update(updates)
            .get();

        flight.setDocumentId(documentId);
        
        // Note: If totalSeats changed, you might want to update seats
        // For now, we'll keep existing seats unchanged
        
        return flight;
    }

    // ==================== DELETE FLIGHT (SOFT DELETE + DELETE SEATS) ====================
    /**
     * Soft delete a flight by setting status to INACTIVE
     * Also deletes all associated seats
     */
    public void deleteFlight(String documentId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = firestore.collection(COLLECTION_NAME)
            .document(documentId)
            .get()
            .get();

        if (!document.exists()) {
            throw new IllegalArgumentException("Flight not found");
        }

        String flightId = document.getString("flightId");

        // Set status to INACTIVE (soft delete)
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "INACTIVE");

        firestore.collection(COLLECTION_NAME)
            .document(documentId)
            .update(updates)
            .get();

        // ✅ DELETE ALL SEATS FOR THIS FLIGHT
        System.out.println("Flight " + flightId + " deactivated. Deleting associated seats...");
        seatService.deleteSeatsForFlight(flightId);
    }

    // ==================== GET ALL ACTIVE FLIGHTS ====================
    public List<Flight> getAllFlights() throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> documents = firestore.collection(COLLECTION_NAME)
            .whereEqualTo("status", "ACTIVE")
            .get()
            .get()
            .getDocuments();

        return documents.stream()
            .map(this::documentToFlight)
            .collect(Collectors.toList());
    }

    // ==================== GET FLIGHT BY DOCUMENT ID ====================
    public Flight getFlightById(String documentId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = firestore.collection(COLLECTION_NAME)
            .document(documentId)
            .get()
            .get();

        if (!document.exists()) {
            throw new IllegalArgumentException("Flight not found");
        }

        return documentToFlight(document);
    }

    // ==================== GET FLIGHT BY FLIGHT ID ====================
    public Flight getFlightByFlightId(String flightId) throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> documents = firestore.collection(COLLECTION_NAME)
            .whereEqualTo("flightId", flightId)
            .whereEqualTo("status", "ACTIVE")
            .get()
            .get()
            .getDocuments();

        if (documents.isEmpty()) {
            throw new IllegalArgumentException("Flight not found: " + flightId);
        }

        return documentToFlight(documents.get(0));
    }

    // ==================== SEARCH FLIGHTS ====================
    public List<Flight> searchFlights(String departureCountry, String arrivalCountry, String departureDate) 
            throws ExecutionException, InterruptedException {
        
        Query query = firestore.collection(COLLECTION_NAME)
            .whereEqualTo("status", "ACTIVE");

        if (departureCountry != null && !departureCountry.isEmpty()) {
            query = query.whereEqualTo("departureCountry", departureCountry);
        }
        if (arrivalCountry != null && !arrivalCountry.isEmpty()) {
            query = query.whereEqualTo("arrivalCountry", arrivalCountry);
        }
        if (departureDate != null && !departureDate.isEmpty()) {
            query = query.whereEqualTo("departureDate", departureDate);
        }

        List<QueryDocumentSnapshot> documents = query.get().get().getDocuments();

        return documents.stream()
            .map(this::documentToFlight)
            .collect(Collectors.toList());
    }

    // ==================== VALIDATION ====================
    public void validateFlight(Flight flight, boolean isUpdate) throws ExecutionException, InterruptedException {
        List<String> errors = new ArrayList<>();

        // Flight ID validation
        if (flight.getFlightId() == null || !flight.getFlightId().matches("^F\\d{3,4}$")) {
            errors.add("Flight ID must be in format F001, F002, etc.");
        }

        // Check duplicate Flight ID (only for new flights)
        if (!isUpdate && flightIdExists(flight.getFlightId())) {
            errors.add("Flight ID " + flight.getFlightId() + " already exists");
        }

        // Countries validation
        if (flight.getDepartureCountry() == null || flight.getDepartureCountry().isEmpty()) {
            errors.add("Departure country is required");
        }
        if (flight.getArrivalCountry() == null || flight.getArrivalCountry().isEmpty()) {
            errors.add("Arrival country is required");
        }
        if (flight.getDepartureCountry() != null && flight.getArrivalCountry() != null 
            && flight.getDepartureCountry().equals(flight.getArrivalCountry())) {
            errors.add("Departure and arrival countries must be different");
        }

        // Dates validation
        if (flight.getDepartureDate() == null || !isValidDate(flight.getDepartureDate())) {
            errors.add("Departure date is required and must be in DD/MM/YYYY format");
        }
        if (flight.getArrivalDate() == null || !isValidDate(flight.getArrivalDate())) {
            errors.add("Arrival date is required and must be in DD/MM/YYYY format");
        }

        // Times validation
        if (flight.getBoardingTime() < 0 || flight.getBoardingTime() > 2359 || !isValidTime(flight.getBoardingTime())) {
            errors.add("Boarding time must be between 0000 and 2359 in 24-hour format");
        }
        if (flight.getDepartureTime() < 0 || flight.getDepartureTime() > 2359 || !isValidTime(flight.getDepartureTime())) {
            errors.add("Departure time must be between 0000 and 2359 in 24-hour format");
        }
        if (flight.getArrivalTime() < 0 || flight.getArrivalTime() > 2359 || !isValidTime(flight.getArrivalTime())) {
            errors.add("Arrival time must be between 0000 and 2359 in 24-hour format");
        }
        if (flight.getBoardingTime() >= flight.getDepartureTime()) {
            errors.add("Boarding time must be before departure time");
        }

        // Prices validation
        if (flight.getEconomyPrice() <= 0) {
            errors.add("Economy price must be greater than 0");
        }
        if (flight.getBusinessPrice() <= 0) {
            errors.add("Business price must be greater than 0");
        }
        if (flight.getBusinessPrice() <= flight.getEconomyPrice()) {
            errors.add("Business price must be higher than economy price");
        }

        // Plane number validation
        if (flight.getPlaneNo() == null || !flight.getPlaneNo().matches("^PL\\d{2,3}$")) {
            errors.add("Plane number must be in format PL01, PL02, etc.");
        }

        // Seats validation
        if (flight.getTotalSeats() <= 0) {
            errors.add("Total seats must be greater than 0");
        }
        if (flight.getTotalSeats() % 4 != 0) {
            errors.add("Total seats must be a multiple of 4");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }
    }

    private boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean isValidTime(int time) {
        int hours = time / 100;
        int minutes = time % 100;
        return hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59;
    }

    private boolean flightIdExists(String flightId) throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> documents = firestore.collection(COLLECTION_NAME)
            .whereEqualTo("flightId", flightId)
            .whereEqualTo("status", "ACTIVE")
            .get()
            .get()
            .getDocuments();

        return !documents.isEmpty();
    }

    // ==================== HELPER METHODS ====================
    private Flight documentToFlight(DocumentSnapshot document) {
        Flight flight = new Flight();
        flight.setDocumentId(document.getId());
        flight.setFlightId(document.getString("flightId"));
        flight.setDepartureCountry(document.getString("departureCountry"));
        flight.setArrivalCountry(document.getString("arrivalCountry"));
        flight.setDepartureDate(document.getString("departureDate"));
        flight.setArrivalDate(document.getString("arrivalDate"));
        flight.setDepartureTime(document.getLong("departureTime").intValue());
        flight.setArrivalTime(document.getLong("arrivalTime").intValue());
        flight.setBoardingTime(document.getLong("boardingTime").intValue());
        flight.setEconomyPrice(document.getDouble("economyPrice"));
        flight.setBusinessPrice(document.getDouble("businessPrice"));
        flight.setPlaneNo(document.getString("planeNo"));
        flight.setTotalSeats(document.getLong("totalSeats").intValue());
        flight.setStatus(document.getString("status"));
        return flight;
    }
}