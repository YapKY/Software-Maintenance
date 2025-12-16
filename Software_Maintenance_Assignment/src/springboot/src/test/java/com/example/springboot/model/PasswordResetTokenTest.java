package com.example.springboot.model;

import com.example.springboot.enums.Role;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetTokenTest {

    @Test
    void testBuilderDefaults() {
        PasswordResetToken token = PasswordResetToken.builder().build();
        assertNotNull(token.getCreatedAt());
        assertFalse(token.getUsed());
    }

    @Test
    void testIsExpired() {
        PasswordResetToken validToken = PasswordResetToken.builder()
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();
        assertFalse(validToken.isExpired());

        PasswordResetToken expiredToken = PasswordResetToken.builder()
                .expiryDate(LocalDateTime.now().minusMinutes(10))
                .build();
        assertTrue(expiredToken.isExpired());
    }

    @Test
    void testAllFields() {
        LocalDateTime now = LocalDateTime.now();
        PasswordResetToken token = new PasswordResetToken(
                "id", "tok", "uid", Role.USER, "e@e.com", now, true, now
        );
        assertEquals("e@e.com", token.getEmail());
        assertEquals(Role.USER, token.getUserRole());
    }

    @Test
    void testEqualsHashCodeToString() {
        // Fix: Use a fixed timestamp for both objects to ensure exact equality
        LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 1, 12, 0, 0);
        
        PasswordResetToken t1 = PasswordResetToken.builder()
                .token("A")
                .createdAt(fixedTime)
                .build();
                
        PasswordResetToken t2 = PasswordResetToken.builder()
                .token("A")
                .createdAt(fixedTime)
                .build();
        
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
        assertTrue(t1.toString().contains("A"));
    }
}