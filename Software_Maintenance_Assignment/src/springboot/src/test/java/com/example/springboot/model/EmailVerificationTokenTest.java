package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmailVerificationToken Model Tests")
class EmailVerificationTokenTest {

    @Test
    @DisplayName("Should create EmailVerificationToken with builder")
    void testBuilder() {
        LocalDateTime expiry = LocalDateTime.now().plusHours(24);
        EmailVerificationToken token = EmailVerificationToken.builder()
                .id("token123")
                .token("verification-token")
                .userId("user123")
                .email("user@example.com")
                .expiryDate(expiry)
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        assertEquals("token123", token.getId());
        assertEquals("verification-token", token.getToken());
        assertEquals("user123", token.getUserId());
        assertEquals("user@example.com", token.getEmail());
        assertFalse(token.getUsed());
        assertNotNull(token.getExpiryDate());
    }

    @Test
    @DisplayName("Should test isExpired method")
    void testIsExpired() {
        EmailVerificationToken expiredToken = EmailVerificationToken.builder()
                .expiryDate(LocalDateTime.now().minusHours(1))
                .build();
        
        assertTrue(expiredToken.isExpired());
        
        EmailVerificationToken validToken = EmailVerificationToken.builder()
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        
        assertFalse(validToken.isExpired());
    }

    @Test
    @DisplayName("Should test default values")
    void testDefaults() {
        EmailVerificationToken token = EmailVerificationToken.builder()
                .id("123")
                .token("token")
                .userId("user123")
                .email("user@example.com")
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        
        assertFalse(token.getUsed());
        assertNotNull(token.getCreatedAt());
    }

    @Test
    @DisplayName("Should test all constructors")
    void testConstructors() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusHours(24);
        
        EmailVerificationToken token1 = new EmailVerificationToken();
        token1.setId("123");
        token1.setToken("token");
        token1.setUserId("user123");
        token1.setEmail("user@example.com");
        token1.setExpiryDate(expiry);
        token1.setUsed(false);
        token1.setCreatedAt(now);
        
        EmailVerificationToken token2 = new EmailVerificationToken("123", "token",
                "user123", "user@example.com", expiry, false, now);
        
        assertEquals(token1, token2);
        assertNotNull(token1.toString());
    }

    @Test
    @DisplayName("Should test token usage")
    void testTokenUsage() {
        EmailVerificationToken token = EmailVerificationToken.builder()
                .id("123")
                .token("token")
                .userId("user123")
                .email("user@example.com")
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();
        
        assertFalse(token.getUsed());
        
        token.setUsed(true);
        assertTrue(token.getUsed());
    }
}