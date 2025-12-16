package com.example.springboot.model;

import com.example.springboot.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PasswordResetToken Model Tests")
class PasswordResetTokenTest {

    @Test
    @DisplayName("Should create PasswordResetToken with builder")
    void testBuilder() {
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);
        PasswordResetToken token = PasswordResetToken.builder()
                .id("token123")
                .token("reset-token")
                .userId("user123")
                .userRole(Role.USER)
                .email("user@example.com")
                .expiryDate(expiry)
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        assertEquals("token123", token.getId());
        assertEquals("reset-token", token.getToken());
        assertEquals("user123", token.getUserId());
        assertEquals(Role.USER, token.getUserRole());
        assertEquals("user@example.com", token.getEmail());
        assertFalse(token.getUsed());
    }

    @Test
    @DisplayName("Should test isExpired method")
    void testIsExpired() {
        PasswordResetToken expiredToken = PasswordResetToken.builder()
                .expiryDate(LocalDateTime.now().minusHours(1))
                .build();
        
        assertTrue(expiredToken.isExpired());
        
        PasswordResetToken validToken = PasswordResetToken.builder()
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        
        assertFalse(validToken.isExpired());
    }

    @Test
    @DisplayName("Should test default values")
    void testDefaults() {
        PasswordResetToken token = PasswordResetToken.builder()
                .id("123")
                .token("token")
                .userId("user123")
                .userRole(Role.USER)
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
        LocalDateTime expiry = now.plusHours(1);
        
        PasswordResetToken token1 = new PasswordResetToken();
        token1.setId("123");
        token1.setToken("token");
        token1.setUserId("user123");
        token1.setUserRole(Role.ADMIN);
        token1.setEmail("admin@example.com");
        token1.setExpiryDate(expiry);
        token1.setUsed(false);
        token1.setCreatedAt(now);
        
        PasswordResetToken token2 = new PasswordResetToken("123", "token",
                "user123", Role.ADMIN, "admin@example.com", expiry, false, now);
        
        assertEquals(token1, token2);
        assertNotNull(token1.toString());
    }

    @Test
    @DisplayName("Should handle different user roles")
    void testDifferentRoles() {
        PasswordResetToken userToken = PasswordResetToken.builder()
                .userId("user1")
                .userRole(Role.USER)
                .email("user@example.com")
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        
        PasswordResetToken adminToken = PasswordResetToken.builder()
                .userId("admin1")
                .userRole(Role.ADMIN)
                .email("admin@example.com")
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        
        assertEquals(Role.USER, userToken.getUserRole());
        assertEquals(Role.ADMIN, adminToken.getUserRole());
    }
}