package com.example.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springboot.model.Staff;

import java.util.Optional;

/**
 * Staff Repository Interface
 * Provides CRUD operations and custom queries for Staff entities
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {

    // Find staff by staff ID
    Optional<Staff> findByStaffId(String staffId);

    // Find staff by email
    Optional<Staff> findByEmail(String email);

    // Check if staff ID exists
    boolean existsByStaffId(String staffId);

    // Find staff by staff ID and password (for login)
    Optional<Staff> findByStaffIdAndStfPass(String staffId, String stfPass);
}
