package com.example.springboot.controller;

import com.example.springboot.model.Flight;
import com.example.springboot.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.Firestore;

/**
 * Flight REST API Controller
 * Handles CRUD operations for flights
 * All validation is done in FlightService layer
 */
@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightRestController {

    @Autowired
    private FlightService flightService;

    @Autowired
    private Firestore firestore;

    /**
     * Get all active flights
     * GET /api/flights
     */
    @GetMapping
    public ResponseEntity<?> getAllFlights() {
        try {
            List<Flight> flights = flightService.getAllFlights();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "flights", flights
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Failed to fetch flights: " + e.getMessage()
            ));
        }
    }

    /**
     * Get flight by document ID
     * GET /api/flights/{documentId}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFlightById(@PathVariable String id) {
        try {
            Flight flight = flightService.getFlightById(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "flight", flight
            ));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Search flights by criteria
     * POST /api/flights/search
     */
    @PostMapping("/search")
    public ResponseEntity<?> searchFlights(@RequestBody Map<String, String> searchParams) {
        try {
            String departureCountry = searchParams.get("departureCountry");
            String arrivalCountry = searchParams.get("arrivalCountry");
            String departureDate = searchParams.get("departureDate");

            // Validate inputs
            if (departureCountry == null || arrivalCountry == null || departureDate == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Missing required fields: departureCountry, arrivalCountry, departureDate"
                ));
            }

            List<Flight> flights = flightService.searchFlights(departureCountry, arrivalCountry, departureDate);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "count", flights.size(),
                "flights", flights
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Search failed: " + e.getMessage()
            ));
        }
    }

    /**
     * Add new flight
     * POST /api/flights
     * * Validation is done in FlightService layer
     */
    @PostMapping
    public ResponseEntity<?> addFlight(@RequestBody Flight flight) {
        // Manual authentication check removed
        try {
            Flight createdFlight = flightService.addFlight(flight);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Flight " + flight.getFlightId() + " added successfully!",
                "flight", createdFlight
            ));
        } catch (IllegalArgumentException e) {
            // Validation errors from service layer
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to add flight: " + e.getMessage()
            ));
        }
    }

    /**
     * Update flight
     * PUT /api/flights/{documentId}
     * * Validation is done in FlightService layer
     */
    @PutMapping("/{documentId}")
    public ResponseEntity<?> updateFlight(
            @PathVariable String documentId,
            @RequestBody Flight flight) {
        
        // Manual authentication check removed
        try {
            Flight updatedFlight = flightService.updateFlight(documentId, flight);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Flight " + updatedFlight.getFlightId() + " updated successfully!",
                "flight", updatedFlight
            ));
        } catch (IllegalArgumentException e) {
            // Validation errors from service layer
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to update flight: " + e.getMessage()
            ));
        }
    }

    /**
     * Soft delete flight - Set status to INACTIVE
     * DELETE /api/flights/{documentId}
     * * Note: This is a SOFT DELETE - flight data is preserved
     */
    @DeleteMapping("/{documentId}")
    public ResponseEntity<?> deleteFlight(@PathVariable String documentId) {
        
        // Manual authentication and RBAC checks removed.
        // Ensure you use @PreAuthorize("hasRole('ADMIN')") or similar configuration in SecurityConfig.

        try {
            // Get flight info before deactivating
            Flight flight = flightService.getFlightById(documentId);
            String flightId = flight.getFlightId();
            
            // Soft delete (set status to INACTIVE)
            flightService.deleteFlight(documentId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Flight " + flightId + " deactivated successfully (data preserved)",
                "deactivatedFlightId", flightId
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to deactivate flight: " + e.getMessage()
            ));
        }
    }

    /**
     * Get flight by Flight ID (e.g., "F001")
     * GET /api/flights/by-id/{flightId}
     */
    @GetMapping("/by-flight-id/{flightId}")
    public ResponseEntity<?> getFlightByFlightId(@PathVariable String flightId) {
        try {
            Flight flight = flightService.getFlightByFlightId(flightId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "flight", flight
            ));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get flights statistics
     * GET /api/flights/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getFlightStats() {
        try {
            List<Flight> flights = flightService.getAllFlights();
            
            int totalFlights = flights.size();
            int totalSeats = flights.stream()
                .mapToInt(Flight::getTotalSeats)
                .sum();
            
            double avgEconomyPrice = flights.stream()
                .mapToDouble(Flight::getEconomyPrice)
                .average()
                .orElse(0.0);
            
            double avgBusinessPrice = flights.stream()
                .mapToDouble(Flight::getBusinessPrice)
                .average()
                .orElse(0.0);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalFlights", totalFlights);
            stats.put("totalSeats", totalSeats);
            stats.put("averageEconomyPrice", Math.round(avgEconomyPrice * 100.0) / 100.0);
            stats.put("averageBusinessPrice", Math.round(avgBusinessPrice * 100.0) / 100.0);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "stats", stats
            ));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to retrieve statistics: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{flightId}/seats/stats")
    public ResponseEntity<?> getSeatStats(@PathVariable String flightId) {
        try {
            // Query seats collection for this flight
            List<QueryDocumentSnapshot> allSeats = firestore.collection("seats")
                .whereEqualTo("flightId", flightId)
                .get()
                .get()
                .getDocuments();

            int totalSeats = allSeats.size();
            int bookedSeats = 0;
            int availableSeats = 0;

            for (QueryDocumentSnapshot seat : allSeats) {
                String status = seat.getString("statusSeat");
                if ("Booked".equalsIgnoreCase(status)) {
                    bookedSeats++;
                } else if ("Available".equalsIgnoreCase(status)) {
                    availableSeats++;
                }
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "flightId", flightId,
                "totalSeats", totalSeats,
                "ticketsSold", bookedSeats,
                "availableSeats", availableSeats
            ));

        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to retrieve seat statistics: " + e.getMessage()
            ));
        }
    }
}