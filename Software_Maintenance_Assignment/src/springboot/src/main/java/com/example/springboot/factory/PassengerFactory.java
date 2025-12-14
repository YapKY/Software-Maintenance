package com.example.springboot.factory;

import com.example.springboot.model.Passenger;
import org.springframework.stereotype.Component;

/**
 * Factory Pattern Implementation for Passenger Creation
 * 
 * Centralizes passenger object creation and validation logic.
 * Ensures consistent passenger data structure across the application.
 */
@Component
public class PassengerFactory {
    
    /**
     * Creates a new passenger with validated data
     * 
     * @param fullName Passenger's full name
     * @param passportNo Passport number (must match pattern [A-Z]\d{8})
     * @param email Email address
     * @param phoneNumber Phone number
     * @return A validated Passenger object
     */
    public Passenger createPassenger(String fullName, String passportNo, String email, String phoneNumber) {
        validatePassengerData(fullName, passportNo, email, phoneNumber);
        
        Passenger passenger = new Passenger();
        passenger.setFullName(fullName.trim());
        passenger.setPassportNo(passportNo.trim().toUpperCase());
        passenger.setEmail(email.trim().toLowerCase());
        passenger.setPhoneNumber(phoneNumber.trim());
        
        return passenger;
    }
    
    /**
     * Creates a passenger from DTO/Request object
     * Useful for REST API integrations
     */
    public Passenger createFromRequest(PassengerRequest request) {
        return createPassenger(
            request.getFullName(),
            request.getPassportNo(),
            request.getEmail(),
            request.getPhoneNumber()
        );
    }
    
    /**
     * Validates passenger data according to business rules
     */
    private void validatePassengerData(String fullName, String passportNo, String email, String phoneNumber) {
        // Full name validation
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger name cannot be empty");
        }
        if (fullName.trim().length() < 2) {
            throw new IllegalArgumentException("Passenger name must be at least 2 characters");
        }
        
        // Passport validation: [A-Z]\d{8} format
        if (passportNo == null || !passportNo.matches("^[A-Z]\\d{8}$")) {
            throw new IllegalArgumentException(
                "Invalid passport format. Expected: One uppercase letter followed by 8 digits (e.g., A12345678)"
            );
        }
        
        // Email validation
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // Phone validation: XXX-XXXXXXX format
        if (phoneNumber == null || !phoneNumber.matches("^\\d{3}-\\d{7,8}$")) {
            throw new IllegalArgumentException(
                "Invalid phone format. Expected: XXX-XXXXXXX (e.g., 012-3456789)"
            );
        }
    }
    
    /**
     * Inner class for Passenger creation requests
     * Can be moved to DTO package if needed
     */
    public static class PassengerRequest {
        private String fullName;
        private String passportNo;
        private String email;
        private String phoneNumber;
        
        // Getters and setters
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getPassportNo() { return passportNo; }
        public void setPassportNo(String passportNo) { this.passportNo = passportNo; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }
}