package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmailVerificationTokenTest {

    @Test
    void testBuilderDefaults() {
        // Test that @Builder.Default works (used=false, createdAt=now)
        EmailVerificationToken token = EmailVerificationToken.builder().build();
        
        assertNotNull(token.getCreatedAt());
        assertFalse(token.getUsed());
        assertNull(token.getToken());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        LocalDateTime expiry = LocalDateTime.now().plusDays(1);
        LocalDateTime created = LocalDateTime.now();
        
        EmailVerificationToken token = new EmailVerificationToken(
            "id1", "token123", "user1", "test@test.com", expiry, true, created
        );

        assertEquals("id1", token.getId());
        assertEquals("token123", token.getToken());
        assertEquals("user1", token.getUserId());
        assertEquals("test@test.com", token.getEmail());
        assertEquals(expiry, token.getExpiryDate());
        assertTrue(token.getUsed());
        assertEquals(created, token.getCreatedAt());
    }

    @Test
    void testSetters() {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken("newToken");
        assertEquals("newToken", token.getToken());
    }

    @Test
    void testIsExpired_NotExpired() {
        EmailVerificationToken token = EmailVerificationToken.builder()
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        assertFalse(token.isExpired());
    }

    @Test
    void testIsExpired_Expired() {
        EmailVerificationToken token = EmailVerificationToken.builder()
                .expiryDate(LocalDateTime.now().minusHours(1))
                .build();
        assertTrue(token.isExpired());
    }

    @Test
    void testEqualsAndHashCode() {
        EmailVerificationToken t1 = EmailVerificationToken.builder().id("1").build();
        EmailVerificationToken t2 = EmailVerificationToken.builder().id("1").build();
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    void testToString() {
        EmailVerificationToken token = EmailVerificationToken.builder().token("ABC").build();
        assertTrue(token.toString().contains("ABC"));
    }
}