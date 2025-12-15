package com.example.springboot.adapter.recaptcha;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * RecaptchaAdapter - ADAPTER PATTERN
 * Wraps Google reCAPTCHA API for validation
 */
@Slf4j
@Component
public class RecaptchaAdapter {
    
    @Value("${recaptcha.secret.key}")
    private String recaptchaSecretKey;
    
    private final RestTemplate restTemplate;
    
    // Test keys that should always pass
    private static final String TEST_SITE_KEY = "6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI";
    private static final String TEST_SECRET_KEY = "6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe";
    
    public RecaptchaAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Validate reCAPTCHA token
     */
    public boolean validateRecaptcha(String recaptchaToken) {
        // Allow bypass for test environment or test keys
        if (recaptchaToken == null || recaptchaToken.isEmpty()) {
            log.warn("reCAPTCHA token is empty");
            return false;
        }
        
        // If using test secret key, always return true in development
        if (TEST_SECRET_KEY.equals(recaptchaSecretKey)) {
            log.info("Using test reCAPTCHA key - validation bypassed");
            return true;
        }
        
        try {
            String url = "https://www.google.com/recaptcha/api/siteverify";
            
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", recaptchaSecretKey);
            params.add("response", recaptchaToken);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Boolean success = (Boolean) body.get("success");
                
                if (Boolean.TRUE.equals(success)) {
                    log.info("reCAPTCHA validation successful");
                    return true;
                } else {
                    log.warn("reCAPTCHA validation failed: {}", body.get("error-codes"));
                    return false;
                }
            }
            
            log.error("reCAPTCHA validation failed: Invalid response");
            return false;
            
        } catch (Exception e) {
            log.error("reCAPTCHA validation exception: {}", e.getMessage());
            // In development, you might want to return true here to avoid blocking
            // In production, return false
            return false;
        }
    }
}