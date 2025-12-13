package com.example.springboot.controller;

import com.example.springboot.model.Flight;
import com.example.springboot.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightController {

    @Autowired
    private FlightService flightService;

    /**
     * Search flights by criteria
     * POST /api/flights/search
     * Body: { "departureCountry": "Malaysia", "arrivalCountry": "Japan", "departureDate": "11/11/2023" }
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
     * Get all flights
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
     * GET /api/flights/{id}
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
     * Get flight by flightId field (e.g., "F001")
     * GET /api/flights/by-flight-id/{flightId}
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
}