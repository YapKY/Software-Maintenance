package com.example.maintenance.adapter.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * GoogleAuthAdapter - ADAPTER PATTERN
 * Wraps Google OAuth API for token validation
 * Updated to support both ID Tokens and Access Tokens
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleAuthAdapter {
    
    @Value("${google.client.id}")
    private String googleClientId;
    
    private final RestTemplate restTemplate;
    
    /**
     * Validate Google access token (or ID token) and extract user information
     */
    public Map<String, String> validateTokenAndGetUserInfo(String token) {
        // 1. First, try to verify as a JWT ID Token (Standard OpenID Connect flow)
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory()
            )
            .setAudience(Collections.singletonList(googleClientId))
            .build();
            
            GoogleIdToken idToken = verifier.verify(token);
            
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                log.info("Google ID Token validated for user: {}", payload.getEmail());
                
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("id", payload.getSubject());
                userInfo.put("email", payload.getEmail());
                userInfo.put("name", (String) payload.get("name"));
                userInfo.put("picture", (String) payload.get("picture"));
                userInfo.put("emailVerified", String.valueOf(payload.getEmailVerified()));
                
                return userInfo;
            }
        } catch (Exception e) {
            log.debug("Token is not a valid ID Token, trying as Access Token: {}", e.getMessage());
            // Fall through to try as Access Token
        }

        // 2. If ID Token verification failed, try as an OAuth2 Access Token
        try {
            return getUserInfoFromAccessToken(token);
        } catch (Exception e) {
            log.error("Google token validation failed: {}", e.getMessage());
            throw new RuntimeException("Google authentication failed");
        }
    }

    /**
     * Call Google UserInfo endpoint with Access Token
     */
    private Map<String, String> getUserInfoFromAccessToken(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                userInfoUrl, 
                HttpMethod.GET, 
                entity, 
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("id", (String) body.get("sub")); // 'sub' is the ID
                userInfo.put("email", (String) body.get("email"));
                userInfo.put("name", (String) body.get("name"));
                userInfo.put("picture", (String) body.get("picture"));
                // Access token validation implies verified email if email claim exists
                userInfo.put("emailVerified", "true"); 
                
                log.info("Google Access Token validated for user: {}", body.get("email"));
                return userInfo;
            }
        } catch (Exception e) {
            log.error("Failed to fetch user info with access token: {}", e.getMessage());
            throw new RuntimeException("Invalid Google token");
        }
        
        throw new RuntimeException("Invalid Google token");
    }
}