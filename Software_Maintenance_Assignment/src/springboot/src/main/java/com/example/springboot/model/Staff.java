package com.example.springboot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Staff Model
 * Represents airline staff members (Manager and Airline Controller)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    
    // Firestore document ID (same as staffId for easy lookup)
    private String documentId;
    
    // Staff identification
    private String staffId;          // e.g., "S001", "S002"
    private String stfPass;          // e.g., "11111" (5 digits)
    private String position;         // "Manager" or "Airline Controller"
    private String name;             // e.g., "Apple Doe"
    private String phoneNumber;      // e.g., "015-5555555"
    private String gender;           // "MALE" or "FEMALE"
    private String email;            // e.g., "john@example.com"
    
    /**
     * Convenience constructor without documentId
     */
    public Staff(String staffId, String position, String stfPass, 
                 String name, String phoneNumber, String gender, String email) {
        this.staffId = staffId;
        this.position = position;
        this.stfPass = stfPass;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.email = email;
    }
    
    /**
     * Validate login credentials
     */
    public boolean login(String inputStaffId, int inputPassword) {
        if (!this.staffId.equals(inputStaffId)) {
            return false;
        }
        
        try {
            long storedPassword = Long.parseLong(this.stfPass);
            return storedPassword == inputPassword;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Check if staff is a Manager
     */
    public boolean isManager() {
        return "Manager".equalsIgnoreCase(this.position);
    }
    
    /**
     * Check if staff is an Airline Controller
     */
    public boolean isController() {
        return "Airline Controller".equalsIgnoreCase(this.position);
    }
}