package com.example.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springboot.model.Passenger;

import java.util.Optional;

/**
 * Passenger Repository Interface
 * Provides CRUD operations and custom queries for Passenger entities
 */
@Repository
public interface PassengerRepository extends JpaRepository<Passenger, String> {

    // Find passenger by passport number
    Optional<Passenger> findByPassportNo(String passportNo);

    // Find passenger by email
    Optional<Passenger> findByEmail(String email);

    // Check if passport number exists
    boolean existsByPassportNo(String passportNo);
}
