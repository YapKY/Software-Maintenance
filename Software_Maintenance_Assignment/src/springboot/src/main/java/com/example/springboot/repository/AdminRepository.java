package com.example.springboot.repository;

import com.example.springboot.model.Admin;
import com.example.springboot.enums.Role;
import com.example.springboot.enums.Gender;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AdminRepository - Firestore implementation for "staff" collection
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class AdminRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "staff"; // Changed from "admins"

    public Admin save(Admin admin) {
        try {
            if (admin.getStaffId() == null) {
                DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
                admin.setStaffId(docRef.getId());
            }

            admin.setUpdatedAt(LocalDateTime.now());

            Map<String, Object> adminMap = convertToMap(admin);

            ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                    .document(admin.getStaffId())
                    .set(adminMap);

            result.get();

            log.info("Staff saved successfully: {}", admin.getStaffId());
            return admin;

        } catch (Exception e) {
            log.error("Failed to save staff: {}", e.getMessage());
            throw new RuntimeException("Failed to save staff", e);
        }
    }

    public Optional<Admin> findById(String staffId) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(staffId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return Optional.of(convertToAdmin(document));
            }
            return Optional.empty();

        } catch (Exception e) {
            log.error("Failed to find staff by ID: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Admin> findByEmail(String email) {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .limit(1)
                    .get();

            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            if (!documents.isEmpty()) {
                return Optional.of(convertToAdmin(documents.get(0)));
            }
            return Optional.empty();

        } catch (Exception e) {
            log.error("Failed to find staff by email: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public List<Admin> findByCreatedBy(String superadminId) {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("createdBy", superadminId)
                    .get();

            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            return documents.stream()
                    .map(this::convertToAdmin)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to find staff by creator: {}", e.getMessage());
            throw new RuntimeException("Failed to find staff", e);
        }
    }

    public int countAdminsCreatedBy(String superadminId) {
        return findByCreatedBy(superadminId).size();
    }

    private Map<String, Object> convertToMap(Admin admin) {
        Map<String, Object> map = new HashMap<>();
        map.put("staffId", admin.getStaffId()); // Renamed
        map.put("email", admin.getEmail());
        map.put("staffPass", admin.getStaffPass()); // Renamed
        map.put("name", admin.getName()); // Renamed
        map.put("phoneNumber", admin.getPhoneNumber());
        map.put("gender", admin.getGender().name()); // New
        map.put("position", admin.getPosition()); // New
        map.put("role", admin.getRole().name());
        map.put("mfaEnabled", admin.getMfaEnabled());
        map.put("accountLocked", admin.getAccountLocked());
        map.put("failedLoginAttempts", admin.getFailedLoginAttempts());
        map.put("createdBy", admin.getCreatedBy());
        map.put("lastLoginAt", admin.getLastLoginAt() != null ? admin.getLastLoginAt().toString() : null);
        map.put("createdAt", admin.getCreatedAt().toString());
        map.put("updatedAt", admin.getUpdatedAt().toString());
        return map;
    }

    private Admin convertToAdmin(DocumentSnapshot document) {
        Admin admin = new Admin();
        admin.setStaffId(document.getId());
        admin.setEmail(document.getString("email"));
        admin.setStaffPass(document.getString("staffPass")); // Renamed
        admin.setName(document.getString("name")); // Renamed
        admin.setPhoneNumber(document.getString("phoneNumber"));

        String genderStr = document.getString("gender");
        if (genderStr != null)
            admin.setGender(Gender.valueOf(genderStr));

        admin.setPosition(document.getString("position"));

        admin.setRole(Role.valueOf(document.getString("role")));
        admin.setMfaEnabled(document.getBoolean("mfaEnabled"));
        admin.setAccountLocked(document.getBoolean("accountLocked"));
        admin.setFailedLoginAttempts(document.getLong("failedLoginAttempts").intValue());
        admin.setCreatedBy(document.getString("createdBy"));

        // Parse lastLoginAt with timezone support
        String lastLoginStr = document.getString("lastLoginAt");
        if (lastLoginStr != null && !lastLoginStr.isEmpty()) {
            admin.setLastLoginAt(parseTimestamp(lastLoginStr));
        }

        // Parse createdAt and updatedAt with timezone support
        String createdAtStr = document.getString("createdAt");
        if (createdAtStr != null) {
            admin.setCreatedAt(parseTimestamp(createdAtStr));
        }

        String updatedAtStr = document.getString("updatedAt");
        if (updatedAtStr != null) {
            admin.setUpdatedAt(parseTimestamp(updatedAtStr));
        }

        return admin;
    }

    /**
     * Parse timestamp string that may contain 'Z' suffix or nanoseconds
     * Handles both LocalDateTime format and ISO-8601 with timezone
     */
    private LocalDateTime parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return LocalDateTime.now();
        }

        try {
            // If timestamp ends with 'Z', it's in UTC format - parse as Instant
            if (timestamp.endsWith("Z")) {
                return LocalDateTime.ofInstant(Instant.parse(timestamp), ZoneId.systemDefault());
            } else {
                // Otherwise parse as LocalDateTime directly
                return LocalDateTime.parse(timestamp);
            }
        } catch (Exception e) {
            log.warn("Failed to parse timestamp '{}': {}", timestamp, e.getMessage());
            return LocalDateTime.now();
        }
    }
}