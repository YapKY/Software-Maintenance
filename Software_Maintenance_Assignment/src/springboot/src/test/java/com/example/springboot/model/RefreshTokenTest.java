package com.example.springboot.model;

import com.example.springboot.enums.Role;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    @Test
    void testBuilderDefaults() {
        RefreshToken token = RefreshToken.builder().build();
        assertNotNull(token.getCreatedAt());
        assertFalse(token.getRevoked()); // Default is false
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusDays(7);
        
        RefreshToken token = new RefreshToken(
            "rt-123", "token_string", "user-1", Role.USER, expiry, true, now
        );

        assertEquals("rt-123", token.getId());
        assertEquals("token_string", token.getToken());
        assertEquals("user-1", token.getUserId());
        assertEquals(Role.USER, token.getUserRole());
        assertEquals(expiry, token.getExpiryDate());
        assertTrue(token.getRevoked());
        assertEquals(now, token.getCreatedAt());
    }

    @Test
    void testSetters() {
        RefreshToken token = new RefreshToken();
        token.setToken("newToken");
        token.setRevoked(true);
        
        assertEquals("newToken", token.getToken());
        assertTrue(token.getRevoked());
    }

    @Test
    void testIsExpired_NotExpired() {
        RefreshToken token = RefreshToken.builder()
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        assertFalse(token.isExpired());
    }

    @Test
    void testIsExpired_Expired() {
        RefreshToken token = RefreshToken.builder()
                .expiryDate(LocalDateTime.now().minusHours(1))
                .build();
        assertTrue(token.isExpired());
    }

    @Test
    void testEqualsHashCodeToString() {
        // Fix: Use a fixed timestamp to ensure exact equality checks
        LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 1, 12, 0, 0);

        RefreshToken t1 = RefreshToken.builder()
                .token("ABC")
                .createdAt(fixedTime)
                .build();
        
        RefreshToken t2 = RefreshToken.builder()
                .token("ABC")
                .createdAt(fixedTime)
                .build();
        
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
        assertTrue(t1.toString().contains("RefreshToken"));
        assertTrue(t1.toString().contains("ABC"));
    }
}