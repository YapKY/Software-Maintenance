package com.example.springboot.repository;

import com.example.springboot.model.Superadmin;
import com.example.springboot.enums.Role;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SuperadminRepository - Firestore implementation
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SuperadminRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "superadmins";

    public Superadmin save(Superadmin superadmin) {
        try {
            if (superadmin.getId() == null) {
                DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
                superadmin.setId(docRef.getId());
            }

            superadmin.setUpdatedAt(LocalDateTime.now());

            Map<String, Object> map = convertToMap(superadmin);

            ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                    .document(superadmin.getId())
                    .set(map);

            result.get();

            log.info("Superadmin saved successfully: {}", superadmin.getId());
            return superadmin;

        } catch (Exception e) {
            log.error("Failed to save superadmin: {}", e.getMessage());
            throw new RuntimeException("Failed to save superadmin", e);
        }
    }

    public Optional<Superadmin> findById(String superadminId) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(superadminId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return Optional.of(convertToSuperadmin(document));
            }
            return Optional.empty();

        } catch (Exception e) {
            log.error("Failed to find superadmin by ID: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Superadmin> findByEmail(String email) {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .limit(1)
                    .get();

            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            if (!documents.isEmpty()) {
                return Optional.of(convertToSuperadmin(documents.get(0)));
            }
            return Optional.empty();

        } catch (Exception e) {
            log.error("Failed to find superadmin by email: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    private Map<String, Object> convertToMap(Superadmin superadmin) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", superadmin.getId());
        map.put("email", superadmin.getEmail());
        map.put("password", superadmin.getPassword());
        map.put("fullName", superadmin.getFullName());
        map.put("role", superadmin.getRole().name());
        map.put("mfaEnabled", superadmin.getMfaEnabled());
        map.put("accountLocked", superadmin.getAccountLocked());
        map.put("lastLoginAt", superadmin.getLastLoginAt() != null ? superadmin.getLastLoginAt().toString() : null);
        map.put("createdAt", superadmin.getCreatedAt().toString());
        map.put("updatedAt", superadmin.getUpdatedAt().toString());
        return map;
    }

    private Superadmin convertToSuperadmin(DocumentSnapshot document) {
        Superadmin superadmin = new Superadmin();
        superadmin.setId(document.getId());
        superadmin.setEmail(document.getString("email"));
        superadmin.setPassword(document.getString("password"));
        superadmin.setFullName(document.getString("fullName"));

        // Handle role with null safety and fallback for legacy/invalid data
        String roleStr = document.getString("role");
        if (roleStr != null) {
            try {
                superadmin.setRole(Role.valueOf(roleStr));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role value '{}' for superadmin {}, defaulting to SUPERADMIN", roleStr,
                        document.getId());
                superadmin.setRole(Role.SUPERADMIN);
            }
        } else {
            superadmin.setRole(Role.SUPERADMIN); // Default
        }

        // Handle boolean fields with null safety
        Boolean mfaEnabled = document.getBoolean("mfaEnabled");
        superadmin.setMfaEnabled(mfaEnabled != null ? mfaEnabled : true);

        Boolean accountLocked = document.getBoolean("accountLocked");
        superadmin.setAccountLocked(accountLocked != null ? accountLocked : false);

        // Handle lastLoginAt with null safety
        String lastLoginStr = document.getString("lastLoginAt");
        if (lastLoginStr != null && !lastLoginStr.isEmpty()) {
            try {
                superadmin.setLastLoginAt(LocalDateTime.parse(lastLoginStr));
            } catch (Exception e) {
                log.warn("Failed to parse lastLoginAt: {}", lastLoginStr);
            }
        }

        // Handle createdAt and updatedAt with null safety
        String createdAtStr = document.getString("createdAt");
        if (createdAtStr != null && !createdAtStr.isEmpty()) {
            try {
                superadmin.setCreatedAt(LocalDateTime.parse(createdAtStr));
            } catch (Exception e) {
                log.warn("Failed to parse createdAt: {}", createdAtStr);
                superadmin.setCreatedAt(LocalDateTime.now());
            }
        } else {
            superadmin.setCreatedAt(LocalDateTime.now());
        }

        String updatedAtStr = document.getString("updatedAt");
        if (updatedAtStr != null && !updatedAtStr.isEmpty()) {
            try {
                superadmin.setUpdatedAt(LocalDateTime.parse(updatedAtStr));
            } catch (Exception e) {
                log.warn("Failed to parse updatedAt: {}", updatedAtStr);
                superadmin.setUpdatedAt(LocalDateTime.now());
            }
        } else {
            superadmin.setUpdatedAt(LocalDateTime.now());
        }

        return superadmin;
    }
}
