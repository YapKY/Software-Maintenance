package com.example.springboot.controller;

import com.example.springboot.model.Customer;
import com.example.springboot.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Customer Controller - MVC Architecture
 * CONTROLLER LAYER: Handles HTTP requests and responses
 * Delegates business logic to Service layer
 */
@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * Customer login endpoint
     * Controller delegates authentication to Service layer
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String icNumber = credentials.get("icNumber");
        String password = credentials.get("password");

        if (icNumber == null || password == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("I/C number and password are required"));
        }

        // Delegate to service layer
        Optional<Customer> customerOpt = customerService.authenticateCustomer(icNumber, password);

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Log In Successful");
            response.put("customer", createCustomerResponse(customer));
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Log In Unsuccessful...Please input valid I/C number and Password"));
        }
    }

    /**
     * Customer registration endpoint
     * Controller delegates validation and registration to Service layer
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Customer customer) {
        try {
            // Delegate to service layer for validation and registration
            Customer savedCustomer = customerService.registerCustomer(customer);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration Successful");
            response.put("customer", createCustomerResponse(savedCustomer));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Registration failed: " + e.getMessage()));
        }
    }

    /**
     * Get all customers
     */
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable String id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        if (customer.isPresent()) {
            return ResponseEntity.ok(createCustomerResponse(customer.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Customer not found"));
        }
    }

    /**
     * Get customer by IC number
     */
    @GetMapping("/ic/{icNumber}")
    public ResponseEntity<?> getCustomerByIcNumber(@PathVariable String icNumber) {
        Optional<Customer> customer = customerService.getCustomerByIcNumber(icNumber);
        if (customer.isPresent()) {
            return ResponseEntity.ok(createCustomerResponse(customer.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Customer not found"));
        }
    }

    /**
     * Update customer profile
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable String id, @RequestBody Customer updatedCustomer) {
        try {
            Customer saved = customerService.updateCustomer(id, updatedCustomer);
            return ResponseEntity.ok(createCustomerResponse(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete customer
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id) {
        try {
            customerService.deleteCustomer(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Customer deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    // Helper methods for legacy-style responses
    private Map<String, Object> createCustomerResponse(Customer customer) {
        Map<String, Object> response = new HashMap<>();
        response.put("custId", customer.getCustId());
        response.put("custIcNo", customer.getCustIcNo()); // Fixed: matches frontend expectation
        response.put("name", customer.getName());
        response.put("email", customer.getEmail());
        response.put("phoneNumber", customer.getPhoneNumber());
        response.put("gender", customer.getGender());
        return response;
    }

    /**
     * DEBUG ENDPOINT: Get all customers with full details
     * Use this to verify data exists and troubleshoot API issues
     */
    @GetMapping("/debug/all")
    public ResponseEntity<?> debugGetAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", customers.size());
        response.put("customers", customers);
        return ResponseEntity.ok(response);
    }

    /**
     * HELPER ENDPOINT: Get customer by index (1-based)
     * Maps numeric index to actual database ID
     * Used for simple pagination/listing
     */
    @GetMapping("/index/{index}")
    public ResponseEntity<?> getCustomerByIndex(@PathVariable int index) {
        List<Customer> customers = customerService.getAllCustomers();
        if (index > 0 && index <= customers.size()) {
            Customer customer = customers.get(index - 1);
            return ResponseEntity.ok(createCustomerResponse(customer));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Customer index out of range. Available: 1-" + customers.size()));
        }
    }

    /**
     * DEBUG ENDPOINT: Create test customer
     * Use this to manually add test data if seeding fails
     */
    @PostMapping("/debug/create-test")
    public ResponseEntity<?> debugCreateTestCustomer() {
        try {
            Customer testCustomer = new Customer(
                    "040225-14-1143",
                    "12345678",
                    "Test Customer",
                    "test@example.com",
                    "011-1234567",
                    "Male");
            Customer saved = customerService.registerCustomer(testCustomer);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test customer created");
            response.put("customer", saved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error creating test customer: " + e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
