package com.example.springboot.repository;

import com.example.springboot.model.EmailVerificationToken;
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
 * EmailVerificationTokenRepository - Firestore repository for verification tokens
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class EmailVerificationTokenRepository {
    
    private final Firestore firestore;
    private static final String COLLECTION_NAME = "email_verification_tokens";
    
    public EmailVerificationToken save(EmailVerificationToken token) {
        try {
            if (token.getId() == null) {
                DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
                token.setId(docRef.getId());
            }
            
            Map<String, Object> tokenMap = convertToMap(token);
            
            ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(token.getId())
                .set(tokenMap);
            
            result.get();
            
            log.info("Email verification token saved");
            return token;
            
        } catch (Exception e) {
            log.error("Failed to save verification token: {}", e.getMessage());
            throw new RuntimeException("Failed to save verification token", e);
        }
    }
    
    public Optional<EmailVerificationToken> findByToken(String token) {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("token", token)
                .limit(1)
                .get();
            
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            
            if (!documents.isEmpty()) {
                return Optional.of(convertToToken(documents.get(0)));
            }
            return Optional.empty();
            
        } catch (Exception e) {
            log.error("Failed to find verification token: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    private Map<String, Object> convertToMap(EmailVerificationToken token) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", token.getId());
        map.put("token", token.getToken());
        map.put("userId", token.getUserId());
        map.put("email", token.getEmail());
        map.put("expiryDate", token.getExpiryDate().toString());
        map.put("used", token.getUsed());
        map.put("createdAt", token.getCreatedAt().toString());
        return map;
    }
    
    private EmailVerificationToken convertToToken(DocumentSnapshot document) {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setId(document.getId());
        token.setToken(document.getString("token"));
        token.setUserId(document.getString("userId"));
        token.setEmail(document.getString("email"));
        token.setExpiryDate(LocalDateTime.parse(document.getString("expiryDate")));
        token.setUsed(document.getBoolean("used"));
        token.setCreatedAt(LocalDateTime.parse(document.getString("createdAt")));
        return token;
    }
}