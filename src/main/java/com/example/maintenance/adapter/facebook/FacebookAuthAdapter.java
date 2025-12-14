package com.example.maintenance.adapter.facebook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * FacebookAuthAdapter - ADAPTER PATTERN
 * Wraps Facebook Graph API for token validation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FacebookAuthAdapter {
    
    @Value("${facebook.app.id}")
    private String facebookAppId;
    
    @Value("${facebook.app.secret}")
    private String facebookAppSecret;
    
    private final RestTemplate restTemplate;
    
    /**
     * Validate Facebook access token and extract user information
     */
    public Map<String, String> validateTokenAndGetUserInfo(String accessToken) {
        try {
            // Verify token with Facebook
            String verifyUrl = String.format(
                "https://graph.facebook.com/debug_token?input_token=%s&access_token=%s|%s",
                accessToken, facebookAppId, facebookAppSecret
            );
            
            ResponseEntity<Map> verifyResponse = restTemplate.getForEntity(verifyUrl, Map.class);
            
            if (verifyResponse.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Facebook token validation failed");
            }
            
            // Get user info
            String userInfoUrl = String.format(
                "https://graph.facebook.com/me?fields=id,name,email&access_token=%s",
                accessToken
            );
            
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
            
            if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = userInfoResponse.getBody();
                
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("id", (String) body.get("id"));
                userInfo.put("email", (String) body.get("email"));
                userInfo.put("name", (String) body.get("name"));
                
                log.info("Facebook token validated for user: {}", body.get("email"));
                return userInfo;
            } else {
                throw new RuntimeException("Failed to get Facebook user info");
            }
            
        } catch (Exception e) {
            log.error("Facebook token validation failed: {}", e.getMessage());
            throw new RuntimeException("Facebook authentication failed");
        }
    }
}
