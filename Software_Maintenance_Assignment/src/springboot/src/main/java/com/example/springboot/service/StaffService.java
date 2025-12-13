package com.example.springboot.service;

import com.example.springboot.model.Staff;
import com.example.springboot.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Staff Service Layer - Business Logic
 * Part of MVC Architecture: This is the Model layer business logic
 * Now using Firebase Firestore
 */
@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    /**
     * Authenticate staff with ID and numeric password
     */
    public Optional<Staff> authenticateStaff(String staffId, String password) {
        try {
            return staffRepository.findByStaffIdAndStfPass(staffId, password);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error authenticating staff", e);
        }
    }

    /**
     * Create new staff member with validation
     */
    public Staff createStaff(Staff staff) throws IllegalArgumentException {
        try {
            if (staff.getStaffId() == null || staff.getStaffId().isEmpty()) {
                throw new IllegalArgumentException("Staff ID is required");
            }

            if (staff.getStfPass() == null || !staff.getStfPass().matches("\\d{5}")) {
                throw new IllegalArgumentException("Password must be a 5-digit number string");
            }

            if (!staff.getValidName(staff.getName())) {
                throw new IllegalArgumentException("Invalid name format");
            }

            if (!staff.getValidEmail(staff.getEmail())) {
                throw new IllegalArgumentException("Invalid email format");
            }

            if (staffRepository.existsByStaffId(staff.getStaffId())) {
                throw new IllegalArgumentException("Staff ID already exists");
            }

            return staffRepository.save(staff);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error creating staff", e);
        }
    }

    /**
     * Get all staff members
     */
    public List<Staff> getAllStaff() {
        try {
            return staffRepository.findAll();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error fetching staff", e);
        }
    }

    /**
     * Get staff by ID
     */
    public Optional<Staff> getStaffById(String id) {
        try {
            return staffRepository.findById(id);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error fetching staff", e);
        }
    }

    /**
     * Get staff by staff ID
     */
    public Optional<Staff> getStaffByStaffId(String staffId) {
        try {
            return staffRepository.findByStaffId(staffId);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error fetching staff by staff ID", e);
        }
    }

    /**
     * Update staff profile
     */
    public Staff updateStaff(String id, Staff updatedData) throws IllegalArgumentException {
        try {
            Staff staff = staffRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

            // Check for email uniqueness if email is being changed
            if (updatedData.getEmail() != null && !updatedData.getEmail().equals(staff.getEmail())) {
                if (staffRepository.existsByEmail(updatedData.getEmail())) {
                    throw new IllegalArgumentException("Email already exists");
                }
            }

            // Check for phone number uniqueness if phone is being changed
            if (updatedData.getPhoneNumber() != null && !updatedData.getPhoneNumber().equals(staff.getPhoneNumber())) {
                if (staffRepository.existsByPhoneNumber(updatedData.getPhoneNumber())) {
                    throw new IllegalArgumentException("Phone number already exists");
                }
            }

            if (updatedData.getName() != null) {
                staff.setName(updatedData.getName());
            }
            if (updatedData.getEmail() != null) {
                staff.setEmail(updatedData.getEmail());
            }
            if (updatedData.getPhoneNumber() != null) {
                staff.setPhoneNumber(updatedData.getPhoneNumber());
            }
            if (updatedData.getGender() != null) {
                staff.setGender(updatedData.getGender());
            }
            if (updatedData.getPosition() != null) {
                staff.setPosition(updatedData.getPosition());
            }

            return staffRepository.save(staff);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error updating staff", e);
        }
    }

    /**
     * Delete staff
     */
    public void deleteStaff(String id) throws IllegalArgumentException {
        try {
            Optional<Staff> staff = staffRepository.findById(id);
            if (!staff.isPresent()) {
                throw new IllegalArgumentException("Staff not found");
            }
            staffRepository.deleteById(id);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error deleting staff", e);
        }
    }
}
