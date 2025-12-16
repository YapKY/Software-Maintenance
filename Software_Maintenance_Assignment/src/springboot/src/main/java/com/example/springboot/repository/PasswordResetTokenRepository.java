package com.example.springboot.repository;

import com.example.springboot.model.PasswordResetToken;
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
 * PasswordResetTokenRepository - Firestore implementation
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PasswordResetTokenRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "password_reset_tokens";

    public PasswordResetToken save(PasswordResetToken token) {
        try {
            if (token.getId() == null) {
                DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
                token.setId(docRef.getId());
            }

            Map<String, Object> map = convertToMap(token);

            ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                    .document(token.getId())
                    .set(map);

            result.get();
            return token;

        } catch (Exception e) {
            log.error("Failed to save password reset token: {}", e.getMessage());
            throw new RuntimeException("Failed to save password reset token", e);
        }
    }

    public Optional<PasswordResetToken> findByToken(String token) {
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
            log.error("Failed to find password reset token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Map<String, Object> convertToMap(PasswordResetToken token) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", token.getId());
        map.put("token", token.getToken());
        map.put("userId", token.getUserId());
        map.put("userRole", token.getUserRole().name());
        map.put("email", token.getEmail());
        map.put("expiryDate", token.getExpiryDate().toString());
        map.put("used", token.getUsed());
        map.put("createdAt", token.getCreatedAt().toString());
        return map;
    }

    private PasswordResetToken convertToToken(DocumentSnapshot document) {
        PasswordResetToken token = new PasswordResetToken();
        token.setId(document.getId());
        token.setToken(document.getString("token"));
        token.setUserId(document.getString("userId"));

        // Parse role with fallback for legacy/invalid data
        String roleStr = document.getString("userRole");
        try {
            token.setUserRole(Role.valueOf(roleStr));
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Invalid role value '{}' in password reset token {}, defaulting to USER", roleStr,
                    document.getId());
            token.setUserRole(Role.USER);
        }

        token.setEmail(document.getString("email"));
        token.setExpiryDate(LocalDateTime.parse(document.getString("expiryDate")));
        token.setUsed(document.getBoolean("used"));
        token.setCreatedAt(LocalDateTime.parse(document.getString("createdAt")));
        return token;
    }
}