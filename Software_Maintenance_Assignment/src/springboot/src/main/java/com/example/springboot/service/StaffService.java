package com.example.springboot.service;

import com.example.springboot.model.Staff;
import com.example.springboot.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Staff Service
 * Handles business logic for staff management
 * Provides authentication and CRUD operations
 */
@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    /**
     * Authenticate staff login
     * Validates staffId and password combination
     * Matches legacy Staff.login() method
     * 
     * @param staffId  The staff ID (e.g., "S001")
     * @param password The password as integer (e.g., 11111)
     * @return Staff object if authentication successful, null otherwise
     */
    public Staff authenticate(String staffId, int password) {
        // Find staff by ID
        Staff staff = staffRepository.findByStaffId(staffId);

        if (staff == null) {
            return null; // Staff ID not found
        }

        // Validate password using Staff model's method
        if (staff.login(staffId, password)) {
            return staff; // Authentication successful
        }

        return null; // Invalid password
    }

    /**
     * Get staff by Staff ID
     * 
     * @param staffId The staff ID to find
     * @return Staff object if found, null otherwise
     */
    public Staff getStaffByStaffId(String staffId) {
        return staffRepository.findByStaffId(staffId);
    }

    /**
     * Get all staff members
     * 
     * @return List of all staff
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<Staff> getAllStaff() throws ExecutionException, InterruptedException {
        return staffRepository.findAll();
    }

    /**
     * Get all managers
     * 
     * @return List of staff with Manager position
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<Staff> getAllManagers() throws ExecutionException, InterruptedException {
        return staffRepository.findByPosition("Manager");
    }

    /**
     * Get all controllers
     * 
     * @return List of staff with Airline Controller position
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<Staff> getAllControllers() throws ExecutionException, InterruptedException {
        return staffRepository.findByPosition("Airline Controller");
    }

    /**
     * Add a new staff member
     * Validates required fields and checks for duplicate staffId
     * 
     * @param staff The staff object to add
     * @return The saved staff with documentId
     * @throws IllegalArgumentException if validation fails
     */
    public Staff addStaff(Staff staff) {
        // Validation 1: Staff ID is required
        if (staff.getStaffId() == null || staff.getStaffId().trim().isEmpty()) {
            throw new IllegalArgumentException("Staff ID is required");
        }

        // Validation 2: Check if staff ID already exists
        if (staffRepository.existsByStaffId(staff.getStaffId())) {
            throw new IllegalArgumentException("Staff ID already exists: " + staff.getStaffId());
        }

        // Validation 3: Name is required
        if (staff.getName() == null || staff.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Staff name is required");
        }

        // Validation 4: Position is required
        if (staff.getPosition() == null || staff.getPosition().trim().isEmpty()) {
            throw new IllegalArgumentException("Staff position is required");
        }

        // Validation 5: Password is required
        if (staff.getStfPass() == null || staff.getStfPass().trim().isEmpty()) {
            throw new IllegalArgumentException("Staff password is required");
        }

        // Validation 6: Password must be 5 digits
        if (!staff.getStfPass().matches("\\d{5}")) {
            throw new IllegalArgumentException("Password must be exactly 5 digits");
        }

        // Validation 7: Email format (basic validation)
        if (staff.getEmail() != null && !staff.getEmail().isEmpty()) {
            if (!staff.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Invalid email format");
            }
        }

        // Validation 8: Position must be valid
        if (!staff.getPosition().equals("Manager") &&
                !staff.getPosition().equals("Airline Controller")) {
            throw new IllegalArgumentException("Position must be either 'Manager' or 'Airline Controller'");
        }

        // Save to repository
        return staffRepository.save(staff);
    }

    /**
     * Update staff information
     * 
     * @param staffId      The staff ID to update
     * @param updatedStaff The updated staff object
     * @return The updated staff
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IllegalArgumentException if staff not found
     */
    public Staff updateStaff(String staffId, Staff updatedStaff)
            throws ExecutionException, InterruptedException {

        // Check if staff exists
        Staff existingStaff = staffRepository.findByStaffId(staffId);
        if (existingStaff == null) {
            throw new IllegalArgumentException("Staff not found: " + staffId);
        }

        // Validate password if being updated
        if (updatedStaff.getStfPass() != null && !updatedStaff.getStfPass().isEmpty()) {
            if (!updatedStaff.getStfPass().matches("\\d{5}")) {
                throw new IllegalArgumentException("Password must be exactly 5 digits");
            }
        }

        // Update fields
        updatedStaff.setStaffId(staffId); // Ensure staffId doesn't change
        updatedStaff.setDocumentId(staffId); // Firestore document ID

        staffRepository.update(staffId, updatedStaff);

        return updatedStaff;
    }

    /**
     * Delete a staff member
     * 
     * @param staffId The staff ID to delete
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IllegalArgumentException if staff not found
     */
    public void deleteStaff(String staffId) throws ExecutionException, InterruptedException {
        // Check if staff exists
        Staff staff = staffRepository.findByStaffId(staffId);
        if (staff == null) {
            throw new IllegalArgumentException("Staff not found: " + staffId);
        }

        staffRepository.delete(staffId);
    }

    /**
     * Check if a staff member exists
     * 
     * @param staffId The staff ID to check
     * @return true if staff exists, false otherwise
     */
    public boolean staffExists(String staffId) {
        return staffRepository.existsByStaffId(staffId);
    }

    /**
     * Change staff password
     * 
     * @param staffId     The staff ID
     * @param oldPassword The current password (for verification)
     * @param newPassword The new password
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IllegalArgumentException if validation fails
     */
    public void changePassword(String staffId, int oldPassword, String newPassword)
            throws ExecutionException, InterruptedException {

        // Authenticate with old password first
        Staff staff = authenticate(staffId, oldPassword);
        if (staff == null) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new password
        if (!newPassword.matches("\\d{5}")) {
            throw new IllegalArgumentException("New password must be exactly 5 digits");
        }

        // Update password
        staff.setStfPass(newPassword);
        staffRepository.update(staffId, staff);
    }

    /**
     * Get staff count by position
     * 
     * @param position The position to count
     * @return Number of staff with the given position
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public int getStaffCountByPosition(String position) throws ExecutionException, InterruptedException {
        return staffRepository.findByPosition(position).size();
    }

    /**
     * Initialize default staff members (for first-time setup)
     * Matches the legacy system's default staff array from main.java
     * Only adds if staff collection is empty
     * 
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void initializeDefaultStaff() throws ExecutionException, InterruptedException {
        // Check if staff already exist
        if (!getAllStaff().isEmpty()) {
            System.out.println("ℹ️  Staff already initialized. Skipping default staff creation.");
            return;
        }

        System.out.println("📝 Initializing default staff members from legacy system...");

        // Create default staff from legacy system (main.java lines 97-106)
        // Original: staffArray[0] = new Staff("S001", "Manager", 11111, "Apple Doe",
        // "015-5555555", "MALE", "john@example.com");
        Staff[] defaultStaff = {
                new Staff("S001", "Manager", "11111", "Apple Doe", "015-5555555", "MALE", "john@example.com"),
                new Staff("S002", "Airline Controller", "22222", "Bun Smith", "012-345678", "FEMALE",
                        "jane@example.com"),
                new Staff("S003", "Airline Controller", "33333", "Cookies Johnson", "014-567890", "MALE",
                        "bob@example.com"),
                new Staff("S004", "Airline Controller", "44444", "Duck Brown", "019-7899908", "FEMALE",
                        "alice@example.com"),
                new Staff("S005", "Airline Controller", "55555", "Egg Lee", "013-2727589", "MALE", "eva@example.com"),
                new Staff("S006", "Airline Controller", "66666", "Fruit Chan", "019-9999999", "FEMALE",
                        "owom@example.com"),
                new Staff("S007", "Airline Controller", "77777", "Grass wong", "018-7976902", "MALE",
                        "pema@example.com"),
                new Staff("S008", "Airline Controller", "88888", "Ham chan", "017-7787960", "FEMALE",
                        "sosy@example.com"),
                new Staff("S009", "Airline Controller", "99999", "Ice loo", "0197891111", "MALE",
                        "kokonut@example.com"),
                new Staff("S000", "Airline Controller", "00000", "Juice hee", "014-4444444", "FEMALE",
                        "polipo@example.com")
        };

        for (Staff staff : defaultStaff) {
            try {
                staffRepository.save(staff);
                System.out.println("   ✓ Created staff: " + staff.getStaffId() + " - " + staff.getName() + " ("
                        + staff.getPosition() + ")");
            } catch (Exception e) {
                System.err.println("   ✗ Failed to create staff " + staff.getStaffId() + ": " + e.getMessage());
            }
        }

        System.out.println("✅ Default staff initialization complete! (10 staff members)");
    }
}