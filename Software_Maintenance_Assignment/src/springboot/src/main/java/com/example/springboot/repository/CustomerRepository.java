package com.example.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springboot.model.Customer;

import java.util.Optional;

/**
 * Customer Repository Interface
 * Provides CRUD operations and custom queries for Customer entities
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    // Find customer by IC number
    Optional<Customer> findByCustIcNo(String custIcNo);

    // Find customer by email
    Optional<Customer> findByEmail(String email);

    // Find customer by phone number
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    // Check if IC number exists (for registration validation)
    boolean existsByCustIcNo(String custIcNo);

    // Check if email exists (for registration validation)
    boolean existsByEmail(String email);

    // Check if phone number exists (for registration validation)
    boolean existsByPhoneNumber(String phoneNumber);

    // Check if password exists (legacy validation from original code)
    boolean existsByCustPassword(String custPassword);
}
