package com.example.springboot.controller;

import com.example.springboot.model.Passenger;
import com.example.springboot.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Passenger Controller - Legacy Style
 * Minimal implementation for passenger management
 * Intentional anti-pattern for modernization demonstration
 */
@RestController
@RequestMapping("/api/passengers")
@CrossOrigin(origins = "*")
public class PassengerController {

    @Autowired
    private PassengerRepository passengerRepository;

    /**
     * Create new passenger
     */
    @PostMapping
    public ResponseEntity<?> createPassenger(@RequestBody Passenger passenger) {
        try {
            // Legacy validation
            if (!passenger.ValidPassportNo(passenger.getPassportNo())) {
                return ResponseEntity.badRequest().body(createErrorResponse("Invalid passport number format"));
            }

            if (!passenger.getValidName(passenger.getName())) {
                return ResponseEntity.badRequest().body(createErrorResponse("Invalid name format"));
            }

            if (!passenger.getValidEmail(passenger.getEmail())) {
                return ResponseEntity.badRequest().body(createErrorResponse("Invalid email format"));
            }

            // Check if passport already exists
            if (passengerRepository.existsByPassportNo(passenger.getPassportNo())) {
                return ResponseEntity.badRequest().body(createErrorResponse("Passport number already exists"));
            }

            // Save passenger
            Passenger savedPassenger = passengerRepository.save(passenger);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Passenger created successfully");
            response.put("passenger", createPassengerResponse(savedPassenger));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to create passenger: " + e.getMessage()));
        }
    }

    /**
     * Get all passengers
     */
    @GetMapping
    public ResponseEntity<List<Passenger>> getAllPassengers() {
        List<Passenger> passengers = passengerRepository.findAll();
        return ResponseEntity.ok(passengers);
    }

    /**
     * Get passenger by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPassengerById(@PathVariable String id) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        if (passenger.isPresent()) {
            return ResponseEntity.ok(createPassengerResponse(passenger.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Passenger not found"));
        }
    }

    /**
     * Get passenger by passport number
     */
    @GetMapping("/passport/{passportNo}")
    public ResponseEntity<?> getPassengerByPassportNo(@PathVariable String passportNo) {
        Optional<Passenger> passenger = passengerRepository.findByPassportNo(passportNo);
        if (passenger.isPresent()) {
            return ResponseEntity.ok(createPassengerResponse(passenger.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Passenger not found"));
        }
    }

    /**
     * Update passenger
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePassenger(@PathVariable String id, @RequestBody Passenger updatedPassenger) {
        Optional<Passenger> existingPassenger = passengerRepository.findById(id);

        if (existingPassenger.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Passenger not found"));
        }

        Passenger passenger = existingPassenger.get();

        // Update fields
        if (updatedPassenger.getName() != null) {
            passenger.setName(updatedPassenger.getName());
        }
        if (updatedPassenger.getEmail() != null) {
            passenger.setEmail(updatedPassenger.getEmail());
        }
        if (updatedPassenger.getPhoneNumber() != null) {
            passenger.setPhoneNumber(updatedPassenger.getPhoneNumber());
        }

        Passenger saved = passengerRepository.save(passenger);
        return ResponseEntity.ok(createPassengerResponse(saved));
    }

    /**
     * Delete passenger
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePassenger(@PathVariable String id) {
        if (passengerRepository.existsById(id)) {
            passengerRepository.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Passenger deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Passenger not found"));
        }
    }

    // Helper methods
    private Map<String, Object> createPassengerResponse(Passenger passenger) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", passenger.getId());
        response.put("passportNo", passenger.getPassportNo());
        response.put("name", passenger.getName());
        response.put("email", passenger.getEmail());
        response.put("phoneNumber", passenger.getPhoneNumber());
        response.put("gender", passenger.getGender());
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
