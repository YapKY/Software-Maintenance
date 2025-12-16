package com.example.springboot.model;

import com.example.springboot.enums.Role;
import com.example.springboot.enums.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Admin Model Tests")
class AdminTest {

    @Test
    @DisplayName("Should create Admin with builder")
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Admin admin = Admin.builder()
                .staffId("staff123")
                .email("admin@example.com")
                .staffPass("hashedPassword")
                .name("John Doe")
                .phoneNumber("012-3456789")
                .gender(Gender.MALE)
                .position("Manager")
                .role(Role.ADMIN)
                .mfaEnabled(true)
                .accountLocked(false)
                .failedLoginAttempts(0)
                .createdBy("superadmin")
                .lastLoginAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        assertEquals("staff123", admin.getStaffId());
        assertEquals("admin@example.com", admin.getEmail());
        assertEquals("hashedPassword", admin.getStaffPass());
        assertEquals("John Doe", admin.getName());
        assertEquals("012-3456789", admin.getPhoneNumber());
        assertEquals(Gender.MALE, admin.getGender());
        assertEquals("Manager", admin.getPosition());
        assertEquals(Role.ADMIN, admin.getRole());
        assertTrue(admin.getMfaEnabled());
        assertFalse(admin.getAccountLocked());
        assertEquals(0, admin.getFailedLoginAttempts());
    }

    @Test
    @DisplayName("Should test default values")
    void testDefaults() {
        Admin admin = Admin.builder()
                .staffId("123")
                .email("test@example.com")
                .staffPass("pass")
                .name("Test")
                .build();
        
        assertEquals(Role.ADMIN, admin.getRole());
        assertFalse(admin.getMfaEnabled());
        assertFalse(admin.getAccountLocked());
        assertEquals(0, admin.getFailedLoginAttempts());
        assertNotNull(admin.getCreatedAt());
        assertNotNull(admin.getUpdatedAt());
    }

    @Test
    @DisplayName("Should test all constructors")
    void testConstructors() {
        Admin admin1 = new Admin();
        admin1.setStaffId("123");
        admin1.setEmail("test@example.com");
        admin1.setStaffPass("pass");
        admin1.setName("Test");
        admin1.setPhoneNumber("012-3456789");
        admin1.setGender(Gender.FEMALE);
        admin1.setPosition("Developer");
        admin1.setRole(Role.ADMIN);
        admin1.setMfaEnabled(true);
        admin1.setAccountLocked(false);
        admin1.setFailedLoginAttempts(0);
        
        LocalDateTime now = LocalDateTime.now();
        Admin admin2 = new Admin("123", "test@example.com", "pass", "Test",
                "012-3456789", Gender.FEMALE, "Developer", Role.ADMIN, true, false, 0,
                "creator", null, now, now);
        
        assertEquals(admin1.getStaffId(), admin2.getStaffId());
        assertEquals(admin1.getEmail(), admin2.getEmail());
        assertNotNull(admin1.toString());
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void testEqualsAndHashCode() {
        Admin admin1 = Admin.builder()
                .staffId("123")
                .email("test@example.com")
                .staffPass("pass")
                .name("Test")
                .build();
        
        Admin admin2 = Admin.builder()
                .staffId("123")
                .email("test@example.com")
                .staffPass("pass")
                .name("Test")
                .build();
        
        assertEquals(admin1, admin2);
        assertEquals(admin1.hashCode(), admin2.hashCode());
    }

    @Test
    @DisplayName("Should handle failed login attempts")
    void testFailedLoginAttempts() {
        Admin admin = Admin.builder()
                .staffId("123")
                .email("test@example.com")
                .staffPass("pass")
                .name("Test")
                .failedLoginAttempts(3)
                .build();
        
        assertEquals(3, admin.getFailedLoginAttempts());
        
        admin.setFailedLoginAttempts(admin.getFailedLoginAttempts() + 1);
        assertEquals(4, admin.getFailedLoginAttempts());
    }

    @Test
    @DisplayName("Should handle account locking")
    void testAccountLocking() {
        Admin admin = Admin.builder()
                .staffId("123")
                .email("test@example.com")
                .staffPass("pass")
                .name("Test")
                .accountLocked(false)
                .build();
        
        assertFalse(admin.getAccountLocked());
        
        admin.setAccountLocked(true);
        assertTrue(admin.getAccountLocked());
    }
}