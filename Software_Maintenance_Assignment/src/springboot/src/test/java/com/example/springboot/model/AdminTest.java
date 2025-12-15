package com.example.springboot.model;

import com.example.springboot.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    @Test
    void testAdminBuilderAndDefaults() {
        Admin admin = Admin.builder()
                .email("test@admin.com")
                .build();

        // Check assigned values
        assertEquals("test@admin.com", admin.getEmail());

        // Check @Builder.Default values
        assertEquals(Role.ADMIN, admin.getRole());
        assertFalse(admin.getMfaEnabled());
        assertFalse(admin.getAccountLocked());
        assertEquals(0, admin.getFailedLoginAttempts());
        assertNotNull(admin.getCreatedAt());
        assertNotNull(admin.getUpdatedAt());
    }

    @Test
    void testAdminAllArgs() {
        Admin admin = Admin.builder()
                .staffId("S001")
                .role(Role.SUPERADMIN) // Overriding default
                .build();

        assertEquals("S001", admin.getStaffId());
        assertEquals(Role.SUPERADMIN, admin.getRole());
    }
}