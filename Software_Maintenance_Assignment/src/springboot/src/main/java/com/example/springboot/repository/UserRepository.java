package com.example.springboot.repository;

import com.example.springboot.model.User;
import com.example.springboot.enums.Gender;
import com.example.springboot.enums.AuthProvider;
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
import java.util.stream.Collectors;

/**
 * UserRepository - Firestore implementation for User persistence
 * Replaces JPA repository
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "customers";

    /**
     * Save user to Firestore
     */
    public User save(User user) {
        try {
            if (user.getCustId() == null) {
                DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
                user.setCustId(docRef.getId());
            }

            user.setUpdatedAt(LocalDateTime.now());

            Map<String, Object> userMap = convertToMap(user);

            ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                    .document(user.getCustId())
                    .set(userMap);

            result.get();

            log.info("User saved successfully: {}", user.getCustId());
            return user;

        } catch (Exception e) {
            log.error("Failed to save user: {}", e.getMessage());
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        }
    }

    /**
     * Find user by ID
     */
    public Optional<User> findById(String userId) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(userId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return Optional.of(convertToUser(document));
            }
            return Optional.empty();

        } catch (Exception e) {
            log.error("Failed to find user by ID: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .limit(1)
                    .get();

            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            if (!documents.isEmpty()) {
                return Optional.of(convertToUser(documents.get(0)));
            }
            return Optional.empty();

        } catch (Exception e) {
            log.error("Failed to find user by email: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Find user by email and auth provider
     */
    public Optional<User> findByEmailAndAuthProvider(String email, AuthProvider provider) {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .whereEqualTo("authProvider", provider.name())
                    .limit(1)
                    .get();

            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            if (!documents.isEmpty()) {
                return Optional.of(convertToUser(documents.get(0)));
            }
            return Optional.empty();

        } catch (Exception e) {
            log.error("Failed to find user by email and provider: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Find user by provider ID and auth provider
     */
    public Optional<User> findByProviderIdAndAuthProvider(String providerId, AuthProvider provider) {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("providerId", providerId)
                    .whereEqualTo("authProvider", provider.name())
                    .limit(1)
                    .get();

            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            if (!documents.isEmpty()) {
                return Optional.of(convertToUser(documents.get(0)));
            }
            return Optional.empty();

        } catch (Exception e) {
            log.error("Failed to find user by provider ID: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    /**
     * Check if phone number exists
     */
    public boolean existsByPhoneNumber(String phoneNumber) {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("phoneNumber", phoneNumber)
                    .limit(1)
                    .get();

            return !query.get().getDocuments().isEmpty();

        } catch (Exception e) {
            log.error("Failed to check phone number existence: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Delete user by ID
     */
    public void deleteById(String userId) {
        try {
            ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                    .document(userId)
                    .delete();
            result.get();
            log.info("User deleted: {}", userId);

        } catch (Exception e) {
            log.error("Failed to delete user: {}", e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    /**
     * Find all users (for admin purposes)
     */
    public List<User> findAll() {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            return documents.stream()
                    .map(this::convertToUser)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to find all users: {}", e.getMessage());
            throw new RuntimeException("Failed to find all users", e);
        }
    }

    /**
     * Convert User entity to Firestore Map
     */
    private Map<String, Object> convertToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("custId", user.getCustId());
        map.put("email", user.getEmail());
        map.put("custPassword", user.getCustPassword());
        map.put("name", user.getName());
        map.put("custIcNo", user.getCustIcNo());

        // [FIX] Handle null Gender safely
        map.put("gender", user.getGender() != null ? user.getGender().name() : null);

        map.put("phoneNumber", user.getPhoneNumber());
        map.put("role", user.getRole().name());
        map.put("authProvider", user.getAuthProvider().name());
        map.put("providerId", user.getProviderId());
        map.put("mfaEnabled", user.getMfaEnabled());
        map.put("emailVerified", user.getEmailVerified());
        map.put("accountLocked", user.getAccountLocked());
        map.put("failedLoginAttempts", user.getFailedLoginAttempts());
        map.put("lastLoginAt", user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null);
        map.put("createdAt", user.getCreatedAt().toString());
        map.put("updatedAt", user.getUpdatedAt().toString());
        return map;
    }

    /**
     * Convert Firestore DocumentSnapshot to User entity
     */
    private User convertToUser(DocumentSnapshot document) {
        User user = new User();
        user.setCustId(document.getId());
        user.setEmail(document.getString("email"));
        user.setCustPassword(document.getString("custPassword"));
        user.setName(document.getString("name"));
        user.setCustIcNo(document.getString("custIcNo"));

        String genderStr = document.getString("gender");
        if (genderStr != null)
            user.setGender(Gender.valueOf(genderStr));

        user.setPhoneNumber(document.getString("phoneNumber"));

        // Parse role with fallback for legacy/invalid data
        String roleStr = document.getString("role");
        try {
            user.setRole(com.example.springboot.enums.Role.valueOf(roleStr));
        } catch (IllegalArgumentException | NullPointerException e) {
            // Handle legacy role values - default to USER
            log.warn("Invalid role value '{}' for user {}, defaulting to USER", roleStr, document.getId());
            user.setRole(com.example.springboot.enums.Role.USER);
        }

        user.setAuthProvider(AuthProvider.valueOf(document.getString("authProvider")));
        user.setProviderId(document.getString("providerId"));
        user.setMfaEnabled(document.getBoolean("mfaEnabled"));
        user.setEmailVerified(document.getBoolean("emailVerified"));
        user.setAccountLocked(document.getBoolean("accountLocked"));
        user.setFailedLoginAttempts(document.getLong("failedLoginAttempts").intValue());

        String lastLoginStr = document.getString("lastLoginAt");
        if (lastLoginStr != null)
            user.setLastLoginAt(LocalDateTime.parse(lastLoginStr));

        user.setCreatedAt(LocalDateTime.parse(document.getString("createdAt")));
        user.setUpdatedAt(LocalDateTime.parse(document.getString("updatedAt")));
        return user;
    }
}