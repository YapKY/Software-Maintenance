package com.example.springboot.model;

import com.example.springboot.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MFASecret Model Tests")
class MFASecretTest {

    @Test
    @DisplayName("Should create MFASecret with builder")
    void testBuilder() {
        MFASecret secret = MFASecret.builder()
                .id("secret123")
                .userId("user123")
                .userRole(Role.USER)
                .secret("JBSWY3DPEHPK3PXP")
                .backupCodes("code1,code2,code3")
                .verified(true)
                .createdAt(LocalDateTime.now())
                .build();
        
        assertEquals("secret123", secret.getId());
        assertEquals("user123", secret.getUserId());
        assertEquals(Role.USER, secret.getUserRole());
        assertEquals("JBSWY3DPEHPK3PXP", secret.getSecret());
        assertEquals("code1,code2,code3", secret.getBackupCodes());
        assertTrue(secret.getVerified());
    }

    @Test
    @DisplayName("Should test default values")
    void testDefaults() {
        MFASecret secret = MFASecret.builder()
                .id("123")
                .userId("user123")
                .userRole(Role.USER)
                .secret("secret")
                .build();
        
        assertFalse(secret.getVerified());
        assertNotNull(secret.getCreatedAt());
    }

    @Test
    @DisplayName("Should test all constructors")
    void testConstructors() {
        LocalDateTime now = LocalDateTime.now();
        
        MFASecret secret1 = new MFASecret();
        secret1.setId("123");
        secret1.setUserId("user123");
        secret1.setUserRole(Role.ADMIN);
        secret1.setSecret("secret");
        secret1.setBackupCodes("codes");
        secret1.setVerified(true);
        secret1.setCreatedAt(now);
        
        MFASecret secret2 = new MFASecret("123", "user123", Role.ADMIN,
                "secret", "codes", true, now);
        
        assertEquals(secret1, secret2);
        assertNotNull(secret1.toString());
    }

    @Test
    @DisplayName("Should handle different roles")
    void testDifferentRoles() {
        MFASecret userSecret = MFASecret.builder()
                .userId("user1")
                .userRole(Role.USER)
                .secret("secret1")
                .build();
        
        MFASecret adminSecret = MFASecret.builder()
                .userId("admin1")
                .userRole(Role.ADMIN)
                .secret("secret2")
                .build();
        
        assertEquals(Role.USER, userSecret.getUserRole());
        assertEquals(Role.ADMIN, adminSecret.getUserRole());
    }
}