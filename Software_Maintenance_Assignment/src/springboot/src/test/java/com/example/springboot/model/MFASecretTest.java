package com.example.springboot.model;

import com.example.springboot.enums.Role;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MFASecretTest {

    @Test
    void testBuilderDefaults() {
        MFASecret secret = MFASecret.builder().build();
        assertNotNull(secret.getCreatedAt());
        assertFalse(secret.getVerified());
    }

    @Test
    void testFullBuilder() {
        MFASecret secret = MFASecret.builder()
                .id("1")
                .userId("U123")
                .userRole(Role.ADMIN) // Fixed: Role.ADMIN
                .secret("XYZ")
                .backupCodes("1,2,3")
                .verified(true)
                .build();

        assertEquals("U123", secret.getUserId());
        assertTrue(secret.getVerified());
        assertEquals("XYZ", secret.getSecret());
        assertEquals(Role.ADMIN, secret.getUserRole()); // Fixed
    }

    @Test
    void testSetters() {
        MFASecret secret = new MFASecret();
        secret.setBackupCodes("999");
        assertEquals("999", secret.getBackupCodes());
    }

    @Test
    void testEqualsHashCodeToString() {
        // Fix: Use a fixed timestamp to ensure exact equality
        LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 1, 12, 0, 0);

        MFASecret s1 = MFASecret.builder()
                .id("A")
                .createdAt(fixedTime)
                .build();
        
        MFASecret s2 = MFASecret.builder()
                .id("A")
                .createdAt(fixedTime)
                .build();
        
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        assertTrue(s1.toString().contains("MFASecret"));
    }
}