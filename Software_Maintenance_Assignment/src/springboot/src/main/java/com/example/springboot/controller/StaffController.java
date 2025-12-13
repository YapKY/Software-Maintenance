package com.example.springboot.controller;

import com.example.springboot.model.Staff;
import com.example.springboot.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Staff Controller - MVC Architecture
 * CONTROLLER LAYER: Handles HTTP requests and responses
 * Delegates business logic to Service layer
 */
@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
public class StaffController {

    @Autowired
    private StaffService staffService;

    /**
     * Staff login endpoint
     * Controller delegates authentication to Service layer
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> credentials) {
        String staffId = (String) credentials.get("staffId");
        String password = null;

        try {
            password = credentials.get("password").toString();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Password must be a 5-digit number"));
        }

        if (staffId == null || password == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("Staff ID and password are required"));
        }

        // Delegate to service layer
        Optional<Staff> staffOpt = staffService.authenticateStaff(staffId, password);

        if (staffOpt.isPresent()) {
            Staff staff = staffOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Staff Login Successful");
            response.put("staff", createStaffResponse(staff));
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid Staff ID or Password"));
        }
    }

    /**
     * Create new staff member
     */
    @PostMapping
    public ResponseEntity<?> createStaff(@RequestBody Staff staff) {
        try {
            // Delegate to service layer for validation and creation
            Staff savedStaff = staffService.createStaff(staff);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Staff created successfully");
            response.put("staff", createStaffResponse(savedStaff));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to create staff: " + e.getMessage()));
        }
    }

    /**
     * Get all staff members
     */
    @GetMapping
    public ResponseEntity<List<Staff>> getAllStaff() {
        List<Staff> staffList = staffService.getAllStaff();
        return ResponseEntity.ok(staffList);
    }

    /**
     * Get staff by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStaffById(@PathVariable String id) {
        Optional<Staff> staff = staffService.getStaffById(id);
        if (staff.isPresent()) {
            return ResponseEntity.ok(createStaffResponse(staff.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Staff not found"));
        }
    }

    /**
     * Get staff by staff ID
     */
    @GetMapping("/staffid/{staffId}")
    public ResponseEntity<?> getStaffByStaffId(@PathVariable String staffId) {
        Optional<Staff> staff = staffService.getStaffByStaffId(staffId);
        if (staff.isPresent()) {
            return ResponseEntity.ok(createStaffResponse(staff.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Staff not found"));
        }
    }

    /**
     * Update staff profile
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(@PathVariable String id, @RequestBody Staff updatedStaff) {
        try {
            Staff saved = staffService.updateStaff(id, updatedStaff);
            return ResponseEntity.ok(createStaffResponse(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete staff
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable String id) {
        try {
            staffService.deleteStaff(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Staff deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    // Helper methods for legacy-style responses
    private Map<String, Object> createStaffResponse(Staff staff) {
        Map<String, Object> response = new HashMap<>();
        response.put("staffId", staff.getStaffId());
        response.put("position", staff.getPosition());
        response.put("name", staff.getName());
        response.put("email", staff.getEmail());
        response.put("phoneNumber", staff.getPhoneNumber());
        response.put("gender", staff.getGender());
        return response;
    }

    /**
     * DEBUG ENDPOINT: Get all staff with full details
     * Use this to verify data exists and troubleshoot API issues
     */
    @GetMapping("/debug/all")
    public ResponseEntity<?> debugGetAllStaff() {
        List<Staff> staffList = staffService.getAllStaff();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", staffList.size());
        response.put("staff", staffList);
        return ResponseEntity.ok(response);
    }

    /**
     * HELPER ENDPOINT: Get staff by index (1-based)
     * Maps numeric index to actual database ID
     * Used for simple pagination/listing
     */
    @GetMapping("/index/{index}")
    public ResponseEntity<?> getStaffByIndex(@PathVariable int index) {
        List<Staff> staffList = staffService.getAllStaff();
        if (index > 0 && index <= staffList.size()) {
            Staff staff = staffList.get(index - 1);
            return ResponseEntity.ok(createStaffResponse(staff));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Staff index out of range. Available: 1-" + staffList.size()));
        }
    }

    /**
     * DEBUG ENDPOINT: Create test staff
     * Use this to manually add test data if seeding fails
     */
    @PostMapping("/debug/create-test")
    public ResponseEntity<?> debugCreateTestStaff() {
        try {
            Staff testStaff = new Staff(
                    "Test Manager",
                    "11111",
                    "Test Staff",
                    "012-1234567",
                    "Male",
                    "teststaff@example.com");
            testStaff.setStaffId(null); // Auto-generated
            Staff saved = staffService.createStaff(testStaff);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test staff created");
            response.put("staff", saved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error creating test staff: " + e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
