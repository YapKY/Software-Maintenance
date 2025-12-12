package com.example.springboot.service;

import com.example.springboot.model.Staff;
import com.example.springboot.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Staff Service Layer - Business Logic
 * Part of MVC Architecture: This is the Model layer business logic
 */
@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    /**
     * Authenticate staff with ID and numeric password
     */
    public Optional<Staff> authenticateStaff(String staffId, String password) {
        return staffRepository.findByStaffIdAndStfPass(staffId, password);
    }

    /**
     * Create new staff member with validation
     */
    public Staff createStaff(Staff staff) throws IllegalArgumentException {
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
    }

    /**
     * Get all staff members
     */
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    /**
     * Get staff by ID
     */
    public Optional<Staff> getStaffById(String id) {
        return staffRepository.findById(id);
    }

    /**
     * Get staff by staff ID
     */
    public Optional<Staff> getStaffByStaffId(String staffId) {
        return staffRepository.findByStaffId(staffId);
    }

    /**
     * Update staff profile
     */
    public Staff updateStaff(String id, Staff updatedData) throws IllegalArgumentException {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

        if (updatedData.getName() != null) {
            staff.setName(updatedData.getName());
        }
        if (updatedData.getEmail() != null) {
            staff.setEmail(updatedData.getEmail());
        }
        if (updatedData.getPhoneNumber() != null) {
            staff.setPhoneNumber(updatedData.getPhoneNumber());
        }
        if (updatedData.getPosition() != null) {
            staff.setPosition(updatedData.getPosition());
        }

        return staffRepository.save(staff);
    }

    /**
     * Delete staff
     */
    public void deleteStaff(String id) throws IllegalArgumentException {
        if (!staffRepository.existsById(id)) {
            throw new IllegalArgumentException("Staff not found");
        }
        staffRepository.deleteById(id);
    }
}
