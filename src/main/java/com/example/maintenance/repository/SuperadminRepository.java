package com.example.maintenance.repository;

import com.example.maintenance.domain.model.Superadmin;
import com.example.maintenance.domain.enums.Role;
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
        superadmin.setRole(Role.valueOf(document.getString("role")));
        superadmin.setMfaEnabled(document.getBoolean("mfaEnabled"));
        superadmin.setAccountLocked(document.getBoolean("accountLocked"));
        
        String lastLoginStr = document.getString("lastLoginAt");
        if (lastLoginStr != null) {
            superadmin.setLastLoginAt(LocalDateTime.parse(lastLoginStr));
        }
        
        superadmin.setCreatedAt(LocalDateTime.parse(document.getString("createdAt")));
        superadmin.setUpdatedAt(LocalDateTime.parse(document.getString("updatedAt")));
        
        return superadmin;
    }
}

