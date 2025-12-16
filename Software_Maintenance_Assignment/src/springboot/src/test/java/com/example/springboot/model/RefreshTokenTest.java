package com.example.springboot.model;

import com.example.springboot.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RefreshToken Model Tests")
class RefreshTokenTest {

    @Test
    @DisplayName("Should create RefreshToken with builder")
    void testBuilder() {
        LocalDateTime expiry = LocalDateTime.now().plusDays(7);
        RefreshToken token = RefreshToken.builder()
                .id("token123")
                .token("refresh-token")
                .userId("user123")
                .userRole(Role.USER)
                .expiryDate(expiry)
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        assertEquals("token123", token.getId());
        assertEquals("refresh-token", token.getToken());
        assertEquals("user123", token.getUserId());
        assertEquals(Role.USER, token.getUserRole());
        assertFalse(token.getRevoked());
    }

    @Test
    @DisplayName("Should test isExpired method")
    void testIsExpired() {
        RefreshToken expiredToken = RefreshToken.builder()
                .expiryDate(LocalDateTime.now().minusDays(1))
                .build();
        
        assertTrue(expiredToken.isExpired());
        
        RefreshToken validToken = RefreshToken.builder()
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
        
        assertFalse(validToken.isExpired());
    }

    @Test
    @DisplayName("Should test default values")
    void testDefaults() {
        RefreshToken token = RefreshToken.builder()
                .id("123")
                .token("token")
                .userId("user123")
                .userRole(Role.USER)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
        
        assertFalse(token.getRevoked());
        assertNotNull(token.getCreatedAt());
    }

    @Test
    @DisplayName("Should test all constructors")
    void testConstructors() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusDays(7);
        
        RefreshToken token1 = new RefreshToken();
        token1.setId("123");
        token1.setToken("token");
        token1.setUserId("user123");
        token1.setUserRole(Role.USER);
        token1.setExpiryDate(expiry);
        token1.setRevoked(false);
        token1.setCreatedAt(now);
        
        RefreshToken token2 = new RefreshToken("123", "token",
                "user123", Role.USER, expiry, false, now);
        
        assertEquals(token1, token2);
        assertNotNull(token1.toString());
    }

    @Test
    @DisplayName("Should handle token revocation")
    void testRevocation() {
        RefreshToken token = RefreshToken.builder()
                .id("123")
                .token("token")
                .userId("user123")
                .userRole(Role.USER)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
        
        assertFalse(token.getRevoked());
        
        token.setRevoked(true);
        assertTrue(token.getRevoked());
    }

    @Test
    @DisplayName("Should handle different user roles")
    void testDifferentRoles() {
        RefreshToken userToken = RefreshToken.builder()
                .userId("user1")
                .userRole(Role.USER)
                .token("token1")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
        
        RefreshToken adminToken = RefreshToken.builder()
                .userId("admin1")
                .userRole(Role.SUPERADMIN)
                .token("token2")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
        
        assertEquals(Role.USER, userToken.getUserRole());
        assertEquals(Role.SUPERADMIN, adminToken.getUserRole());
    }
}