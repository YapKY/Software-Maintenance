package com.example.springboot.model;

import com.example.springboot.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SuperadminTest {

    @Test
    @DisplayName("Should create superadmin with builder")
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Superadmin superadmin = Superadmin.builder()
                .id("SA001")
                .email("super@example.com")
                .password("securePass")
                .fullName("Super Admin")
                .role(Role.SUPERADMIN)
                .mfaEnabled(true)
                .accountLocked(false)
                .lastLoginAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals("SA001", superadmin.getId());
        assertEquals("super@example.com", superadmin.getEmail());
        assertEquals("securePass", superadmin.getPassword());
        assertEquals("Super Admin", superadmin.getFullName());
        assertEquals(Role.SUPERADMIN, superadmin.getRole());
        assertTrue(superadmin.getMfaEnabled());
        assertFalse(superadmin.getAccountLocked());
        assertEquals(now, superadmin.getLastLoginAt());
        assertEquals(now, superadmin.getCreatedAt());
        assertEquals(now, superadmin.getUpdatedAt());
    }

    @Test
    @DisplayName("Should use default values in builder")
    void testBuilderDefaults() {
        Superadmin superadmin = Superadmin.builder().build();

        assertEquals(Role.SUPERADMIN, superadmin.getRole());
        assertTrue(superadmin.getMfaEnabled());
        assertFalse(superadmin.getAccountLocked());
        assertNotNull(superadmin.getCreatedAt());
        assertNotNull(superadmin.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testGettersAndSetters() {
        Superadmin superadmin = new Superadmin();
        LocalDateTime now = LocalDateTime.now();

        superadmin.setId("SA002");
        superadmin.setEmail("admin@example.com");
        superadmin.setPassword("pass123");
        superadmin.setFullName("Admin Two");
        superadmin.setRole(Role.ADMIN);
        superadmin.setMfaEnabled(false);
        superadmin.setAccountLocked(true);
        superadmin.setLastLoginAt(now);
        superadmin.setCreatedAt(now);
        superadmin.setUpdatedAt(now);

        assertEquals("SA002", superadmin.getId());
        assertEquals("admin@example.com", superadmin.getEmail());
        assertEquals("pass123", superadmin.getPassword());
        assertEquals("Admin Two", superadmin.getFullName());
        assertEquals(Role.ADMIN, superadmin.getRole());
        assertFalse(superadmin.getMfaEnabled());
        assertTrue(superadmin.getAccountLocked());
        assertEquals(now, superadmin.getLastLoginAt());
        assertEquals(now, superadmin.getCreatedAt());
        assertEquals(now, superadmin.getUpdatedAt());
    }

    @Test
    @DisplayName("Should test all args constructor")
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Superadmin superadmin = new Superadmin("SA003", "test@test.com", "pass", "Test Name", Role.SUPERADMIN, true,
                false, now, now, now);

        assertEquals("SA003", superadmin.getId());
        assertEquals("test@test.com", superadmin.getEmail());
    }

    @Test
    void testLombokMethods() {
        // Fix: Set fixed timestamps to ensure equals() and toString() match exactly
        LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
        
        Superadmin s1 = Superadmin.builder()
                .id("1")
                .email("a@b.com")
                .createdAt(fixedTime)
                .updatedAt(fixedTime)
                .build();
                
        Superadmin s2 = Superadmin.builder()
                .id("1")
                .email("a@b.com")
                .createdAt(fixedTime)
                .updatedAt(fixedTime)
                .build();

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        assertTrue(s1.toString().contains("a@b.com"));
    }
}
