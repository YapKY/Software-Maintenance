package com.example.springboot.service;

import com.example.springboot.model.Customer;
import com.example.springboot.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Optional<Customer> authenticateCustomer(String icNumber, String password) {
        try {
            Optional<Customer> customerOpt = customerRepository.findByCustIcNo(icNumber);
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                if (customer.getCustPassword().equals(password)) {
                    return Optional.of(customer);
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error authenticating customer: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Customer registerCustomer(Customer customer) {
        try {
            return customerRepository.save(customer);
        } catch (Exception e) {
            log.error("Error registering customer: {}", e.getMessage());
            throw new RuntimeException("Failed to register customer", e);
        }
    }

    public List<Customer> getAllCustomers() {
        try {
            return customerRepository.findAll();
        } catch (Exception e) {
            log.error("Error getting all customers: {}", e.getMessage());
            throw new RuntimeException("Failed to get customers", e);
        }
    }

    public Optional<Customer> getCustomerById(String id) {
        try {
            return customerRepository.findById(id);
        } catch (Exception e) {
            log.error("Error getting customer by id: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Customer> getCustomerByIcNumber(String icNumber) {
        try {
            return customerRepository.findByCustIcNo(icNumber);
        } catch (Exception e) {
            log.error("Error getting customer by IC: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Customer updateCustomer(String id, Customer updatedCustomer) {
        try {
            Optional<Customer> existingOpt = customerRepository.findById(id);
            if (!existingOpt.isPresent()) {
                throw new IllegalArgumentException("Customer not found: " + id);
            }

            Customer existing = existingOpt.get();

            // Merge only non-null fields from updatedCustomer into existing
            if (updatedCustomer.getName() != null && !updatedCustomer.getName().isEmpty()) {
                existing.setName(updatedCustomer.getName());
            }
            if (updatedCustomer.getPhoneNumber() != null && !updatedCustomer.getPhoneNumber().isEmpty()) {
                existing.setPhoneNumber(updatedCustomer.getPhoneNumber());
            }
            if (updatedCustomer.getGender() != null && !updatedCustomer.getGender().isEmpty()) {
                existing.setGender(updatedCustomer.getGender());
            }

            return customerRepository.save(existing);
        } catch (Exception e) {
            log.error("Error updating customer: {}", e.getMessage());
            throw new RuntimeException("Failed to update customer", e);
        }
    }

    public void deleteCustomer(String id) {
        try {
            customerRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting customer: {}", e.getMessage());
            throw new RuntimeException("Failed to delete customer", e);
        }
    }
}
