package com.example.springboot.repository;

import com.example.springboot.model.RefreshToken;
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
 * RefreshTokenRepository - Firestore implementation
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "refresh_tokens";

    public RefreshToken save(RefreshToken token) {
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

            log.info("Refresh token saved successfully");
            return token;

        } catch (Exception e) {
            log.error("Failed to save refresh token: {}", e.getMessage());
            throw new RuntimeException("Failed to save refresh token", e);
        }
    }

    public Optional<RefreshToken> findByToken(String token) {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("token", token)
                    .limit(1)
                    .get();

            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            if (!documents.isEmpty()) {
                return Optional.of(convertToRefreshToken(documents.get(0)));
            }
            return Optional.empty();

        } catch (Exception e) {
            log.error("Failed to find refresh token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void revokeToken(String token) {
        try {
            Optional<RefreshToken> refreshToken = findByToken(token);
            if (refreshToken.isPresent()) {
                RefreshToken rt = refreshToken.get();
                rt.setRevoked(true);
                save(rt);
            }

        } catch (Exception e) {
            log.error("Failed to revoke token: {}", e.getMessage());
            throw new RuntimeException("Failed to revoke token", e);
        }
    }

    private Map<String, Object> convertToMap(RefreshToken token) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", token.getId());
        map.put("token", token.getToken());
        map.put("userId", token.getUserId());
        map.put("userRole", token.getUserRole().name());
        map.put("expiryDate", token.getExpiryDate().toString());
        map.put("revoked", token.getRevoked());
        map.put("createdAt", token.getCreatedAt().toString());
        return map;
    }

    private RefreshToken convertToRefreshToken(DocumentSnapshot document) {
        RefreshToken token = new RefreshToken();
        token.setId(document.getId());
        token.setToken(document.getString("token"));
        token.setUserId(document.getString("userId"));

        // Parse role with fallback for legacy/invalid data
        String roleStr = document.getString("userRole");
        try {
            token.setUserRole(Role.valueOf(roleStr));
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Invalid role value '{}' in refresh token {}, defaulting to USER", roleStr, document.getId());
            token.setUserRole(Role.USER);
        }

        token.setExpiryDate(LocalDateTime.parse(document.getString("expiryDate")));
        token.setRevoked(document.getBoolean("revoked"));
        token.setCreatedAt(LocalDateTime.parse(document.getString("createdAt")));
        return token;
    }
}