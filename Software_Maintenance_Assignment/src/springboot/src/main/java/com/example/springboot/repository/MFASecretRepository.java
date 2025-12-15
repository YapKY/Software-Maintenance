package com.example.springboot.repository;

import com.example.springboot.model.MFASecret;
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
 * MFASecretRepository - Firestore implementation
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MFASecretRepository {
    
    private final Firestore firestore;
    private static final String COLLECTION_NAME = "mfa_secrets";
    
    public MFASecret save(MFASecret mfaSecret) {
        try {
            if (mfaSecret.getId() == null) {
                DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
                mfaSecret.setId(docRef.getId());
            }
            
            Map<String, Object> map = convertToMap(mfaSecret);
            
            ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(mfaSecret.getId())
                .set(map);
            
            result.get();
            
            log.info("MFA secret saved successfully");
            return mfaSecret;
            
        } catch (Exception e) {
            log.error("Failed to save MFA secret: {}", e.getMessage());
            throw new RuntimeException("Failed to save MFA secret", e);
        }
    }
    
    public Optional<MFASecret> findByUserIdAndUserRole(String userId, Role userRole) {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereEqualTo("userRole", userRole.name())
                .limit(1)
                .get();
            
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            
            if (!documents.isEmpty()) {
                return Optional.of(convertToMFASecret(documents.get(0)));
            }
            return Optional.empty();
            
        } catch (Exception e) {
            log.error("Failed to find MFA secret: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    public boolean existsByUserIdAndUserRole(String userId, Role userRole) {
        return findByUserIdAndUserRole(userId, userRole).isPresent();
    }
    
    public void deleteByUserIdAndUserRole(String userId, Role userRole) {
        try {
            Optional<MFASecret> mfaSecret = findByUserIdAndUserRole(userId, userRole);
            if (mfaSecret.isPresent()) {
                ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                    .document(mfaSecret.get().getId())
                    .delete();
                result.get();
                log.info("MFA secret deleted");
            }
            
        } catch (Exception e) {
            log.error("Failed to delete MFA secret: {}", e.getMessage());
            throw new RuntimeException("Failed to delete MFA secret", e);
        }
    }
    
    private Map<String, Object> convertToMap(MFASecret mfaSecret) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", mfaSecret.getId());
        map.put("userId", mfaSecret.getUserId());
        map.put("userRole", mfaSecret.getUserRole().name());
        map.put("secret", mfaSecret.getSecret());
        map.put("backupCodes", mfaSecret.getBackupCodes());
        map.put("verified", mfaSecret.getVerified()); // [FIX] Save verified status
        map.put("createdAt", mfaSecret.getCreatedAt().toString());
        return map;
    }
    
    private MFASecret convertToMFASecret(DocumentSnapshot document) {
        MFASecret mfaSecret = new MFASecret();
        mfaSecret.setId(document.getId());
        mfaSecret.setUserId(document.getString("userId"));
        mfaSecret.setUserRole(Role.valueOf(document.getString("userRole")));
        mfaSecret.setSecret(document.getString("secret"));
        mfaSecret.setBackupCodes(document.getString("backupCodes"));
        
        // [FIX] Load verified status safely
        Boolean verified = document.getBoolean("verified");
        mfaSecret.setVerified(verified != null ? verified : false);
        
        mfaSecret.setCreatedAt(LocalDateTime.parse(document.getString("createdAt")));
        return mfaSecret;
    }
}