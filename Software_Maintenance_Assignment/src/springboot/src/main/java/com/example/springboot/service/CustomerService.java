package com.example.springboot.service;

import com.example.springboot.model.Customer;
import com.example.springboot.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Customer Service Layer - Business Logic
 * Part of MVC Architecture: This is the Model layer business logic
 * Now using Firebase Firestore
 * 
 * NOTE: This is a legacy-style service with minimal separation
 * For modernization demo purposes
 */
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Authenticate customer with IC number and password
     * Legacy: Plain text password comparison
     */
    public Optional<Customer> authenticateCustomer(String icNumber, String password) {
        try {
            Optional<Customer> customer = customerRepository.findByCustIcNo(icNumber);

            if (customer.isPresent() && customer.get().getCustPassword().equals(password)) {
                return customer;
            }
            return Optional.empty();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error authenticating customer", e);
        }
    }

    /**
     * Register new customer with validation
     */
    public Customer registerCustomer(Customer customer) throws IllegalArgumentException {
        try {
            // Validate IC number
            if (!customer.getValidICNumber(customer.getCustIcNo())) {
                throw new IllegalArgumentException("Invalid I/C number format");
            }

            // Validate password
            if (!customer.getValidPassword(customer.getCustPassword())) {
                throw new IllegalArgumentException("Password must be more than 8 characters");
            }

            // Validate personal information
            if (!customer.getValidName(customer.getName())) {
                throw new IllegalArgumentException("Invalid name format");
            }

            if (!customer.getValidEmail(customer.getEmail())) {
                throw new IllegalArgumentException("Invalid email format");
            }

            if (!customer.getValidGender(customer.getGender())) {
                throw new IllegalArgumentException("Invalid gender");
            }

            if (!customer.getValidPhoneNumber(customer.getPhoneNumber())) {
                throw new IllegalArgumentException("Invalid phone number format");
            }

            // Check for duplicates
            if (customerRepository.existsByCustIcNo(customer.getCustIcNo())) {
                throw new IllegalArgumentException("I/C number already exists");
            }

            if (customerRepository.existsByEmail(customer.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }

            if (customerRepository.existsByPhoneNumber(customer.getPhoneNumber())) {
                throw new IllegalArgumentException("Phone number already exists");
            }

            if (customerRepository.existsByCustPassword(customer.getCustPassword())) {
                throw new IllegalArgumentException("Password already used by another customer");
            }

            return customerRepository.save(customer);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error registering customer", e);
        }
    }

    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() {
        try {
            return customerRepository.findAll();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error fetching customers", e);
        }
    }

    /**
     * Get customer by ID
     */
    public Optional<Customer> getCustomerById(String id) {
        try {
            return customerRepository.findById(id);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error fetching customer", e);
        }
    }

    /**
     * Get customer by IC number
     */
    public Optional<Customer> getCustomerByIcNumber(String icNumber) {
        try {
            return customerRepository.findByCustIcNo(icNumber);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error fetching customer by IC number", e);
        }
    }

    /**
     * Update customer profile
     */
    public Customer updateCustomer(String id, Customer updatedData) throws IllegalArgumentException {
        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

            // Check for email uniqueness if email is being changed
            if (updatedData.getEmail() != null && !updatedData.getEmail().equals(customer.getEmail())) {
                if (customerRepository.existsByEmail(updatedData.getEmail())) {
                    throw new IllegalArgumentException("Email already exists");
                }
            }

            // Check for phone number uniqueness if phone is being changed
            if (updatedData.getPhoneNumber() != null
                    && !updatedData.getPhoneNumber().equals(customer.getPhoneNumber())) {
                if (customerRepository.existsByPhoneNumber(updatedData.getPhoneNumber())) {
                    throw new IllegalArgumentException("Phone number already exists");
                }
            }

            if (updatedData.getName() != null) {
                customer.setName(updatedData.getName());
            }
            if (updatedData.getEmail() != null) {
                customer.setEmail(updatedData.getEmail());
            }
            if (updatedData.getPhoneNumber() != null) {
                customer.setPhoneNumber(updatedData.getPhoneNumber());
            }
            if (updatedData.getGender() != null) {
                customer.setGender(updatedData.getGender());
            }

            return customerRepository.save(customer);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error updating customer", e);
        }
    }

    /**
     * Delete customer
     */
    public void deleteCustomer(String id) throws IllegalArgumentException {
        try {
            Optional<Customer> customer = customerRepository.findById(id);
            if (!customer.isPresent()) {
                throw new IllegalArgumentException("Customer not found");
            }
            customerRepository.deleteById(id);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error deleting customer", e);
        }
    }
}
