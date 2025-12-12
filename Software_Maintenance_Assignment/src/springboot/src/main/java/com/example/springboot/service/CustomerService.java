package com.example.springboot.service;

import com.example.springboot.model.Customer;
import com.example.springboot.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Customer Service Layer - Business Logic
 * Part of MVC Architecture: This is the Model layer business logic
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
        Optional<Customer> customer = customerRepository.findByCustIcNo(icNumber);

        if (customer.isPresent() && customer.get().getCustPassword().equals(password)) {
            return customer;
        }
        return Optional.empty();
    }

    /**
     * Register new customer with validation
     */
    public Customer registerCustomer(Customer customer) throws IllegalArgumentException {
        // Validate IC number
        if (!customer.getValidICNumber(customer.getCustIcNo())) {
            throw new IllegalArgumentException("Invalid I/C number format");
        }

        // Validate password
        if (!customer.getValidPassword(customer.getCustPassword())) {
            throw new IllegalArgumentException("Password must be exactly 8 characters");
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
    }

    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Get customer by ID
     */
    public Optional<Customer> getCustomerById(String id) {
        return customerRepository.findById(id);
    }

    /**
     * Get customer by IC number
     */
    public Optional<Customer> getCustomerByIcNumber(String icNumber) {
        return customerRepository.findByCustIcNo(icNumber);
    }

    /**
     * Update customer profile
     */
    public Customer updateCustomer(String id, Customer updatedData) throws IllegalArgumentException {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

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
    }

    /**
     * Delete customer
     */
    public void deleteCustomer(String id) throws IllegalArgumentException {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found");
        }
        customerRepository.deleteById(id);
    }
}
