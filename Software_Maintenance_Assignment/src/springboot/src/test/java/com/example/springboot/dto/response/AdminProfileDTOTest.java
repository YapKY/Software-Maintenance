package com.example.springboot.dto.response;

import com.example.springboot.enums.Role;
import com.example.springboot.enums.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AdminProfileDTO Tests - 100% Coverage")
class AdminProfileDTOTest {

    @Test
    @DisplayName("Should create valid AdminProfileDTO with all fields")
    void testValidWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("staff123")
                .email("admin@example.com")
                .name("John Doe")
                .phoneNumber("012-3456789")
                .gender(Gender.MALE)
                .position("Manager")
                .role(Role.ADMIN)
                .mfaEnabled(true)
                .createdBy("superadmin")
                .lastLoginAt(now)
                .createdAt(now)
                .build();
        
        assertEquals("staff123", dto.getStaffId());
        assertEquals("admin@example.com", dto.getEmail());
        assertEquals("John Doe", dto.getName());
        assertEquals("012-3456789", dto.getPhoneNumber());
        assertEquals(Gender.MALE, dto.getGender());
        assertEquals("Manager", dto.getPosition());
        assertEquals(Role.ADMIN, dto.getRole());
        assertTrue(dto.getMfaEnabled());
        assertEquals("superadmin", dto.getCreatedBy());
        assertNotNull(dto.getLastLoginAt());
        assertNotNull(dto.getCreatedAt());
    }

    @Test
    @DisplayName("Should test with female gender")
    void testWithFemaleGender() {
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("staff456")
                .email("jane@example.com")
                .name("Jane Doe")
                .gender(Gender.FEMALE)
                .position("Developer")
                .role(Role.ADMIN)
                .build();
        
        assertEquals(Gender.FEMALE, dto.getGender());
    }

    @Test
    @DisplayName("Should test with SUPERADMIN role")
    void testWithSuperadminRole() {
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("super1")
                .email("super@example.com")
                .name("Super User")
                .role(Role.SUPERADMIN)
                .build();
        
        assertEquals(Role.SUPERADMIN, dto.getRole());
    }

    @Test
    @DisplayName("Should test with mfaEnabled false")
    void testWithMfaDisabled() {
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("staff789")
                .email("test@example.com")
                .name("Test User")
                .mfaEnabled(false)
                .build();
        
        assertFalse(dto.getMfaEnabled());
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void testNullOptionalFields() {
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("123")
                .email("test@example.com")
                .name("Test")
                .phoneNumber(null)
                .gender(null)
                .position(null)
                .role(null)
                .mfaEnabled(null)
                .createdBy(null)
                .lastLoginAt(null)
                .createdAt(null)
                .build();
        
        assertNull(dto.getPhoneNumber());
        assertNull(dto.getGender());
        assertNull(dto.getPosition());
        assertNull(dto.getRole());
        assertNull(dto.getMfaEnabled());
        assertNull(dto.getCreatedBy());
        assertNull(dto.getLastLoginAt());
        assertNull(dto.getCreatedAt());
    }

    @Test
    @DisplayName("Should test no-args constructor")
    void testNoArgsConstructor() {
        AdminProfileDTO dto = new AdminProfileDTO();
        assertNotNull(dto);
        assertNull(dto.getStaffId());
        assertNull(dto.getEmail());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        AdminProfileDTO dto = new AdminProfileDTO(
            "staff123", "admin@example.com", "John Doe",
            "012-3456789", Gender.MALE, "Manager", Role.ADMIN,
            true, "superadmin", now, now
        );
        
        assertEquals("staff123", dto.getStaffId());
        assertEquals("admin@example.com", dto.getEmail());
        assertEquals("John Doe", dto.getName());
    }

    @Test
    @DisplayName("Should test setters")
    void testSetters() {
        LocalDateTime now = LocalDateTime.now();
        AdminProfileDTO dto = new AdminProfileDTO();
        
        dto.setStaffId("123");
        dto.setEmail("test@example.com");
        dto.setName("Test User");
        dto.setPhoneNumber("012-3456789");
        dto.setGender(Gender.FEMALE);
        dto.setPosition("Developer");
        dto.setRole(Role.ADMIN);
        dto.setMfaEnabled(true);
        dto.setCreatedBy("admin");
        dto.setLastLoginAt(now);
        dto.setCreatedAt(now);
        
        assertEquals("123", dto.getStaffId());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("Test User", dto.getName());
        assertEquals("012-3456789", dto.getPhoneNumber());
        assertEquals(Gender.FEMALE, dto.getGender());
        assertEquals("Developer", dto.getPosition());
        assertEquals(Role.ADMIN, dto.getRole());
        assertTrue(dto.getMfaEnabled());
        assertEquals("admin", dto.getCreatedBy());
        assertEquals(now, dto.getLastLoginAt());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    @DisplayName("Should test equals - same object")
    void testEqualsSameObject() {
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("123")
                .email("test@example.com")
                .name("Test")
                .build();
        
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("Should test equals - equal objects")
    void testEqualsEqualObjects() {
        LocalDateTime now = LocalDateTime.now();
        AdminProfileDTO dto1 = AdminProfileDTO.builder()
                .staffId("123")
                .email("test@example.com")
                .name("Test")
                .phoneNumber("012-3456789")
                .gender(Gender.MALE)
                .position("Manager")
                .role(Role.ADMIN)
                .mfaEnabled(true)
                .createdBy("admin")
                .lastLoginAt(now)
                .createdAt(now)
                .build();
        
        AdminProfileDTO dto2 = AdminProfileDTO.builder()
                .staffId("123")
                .email("test@example.com")
                .name("Test")
                .phoneNumber("012-3456789")
                .gender(Gender.MALE)
                .position("Manager")
                .role(Role.ADMIN)
                .mfaEnabled(true)
                .createdBy("admin")
                .lastLoginAt(now)
                .createdAt(now)
                .build();
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Should test equals - different objects")
    void testEqualsDifferentObjects() {
        AdminProfileDTO dto1 = AdminProfileDTO.builder()
                .staffId("123")
                .email("test1@example.com")
                .build();
        
        AdminProfileDTO dto2 = AdminProfileDTO.builder()
                .staffId("456")
                .email("test2@example.com")
                .build();
        
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Should test equals - null")
    void testEqualsNull() {
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("123")
                .build();
        
        assertNotEquals(null, dto);
    }

    @Test
    @DisplayName("Should test equals - different class")
    void testEqualsDifferentClass() {
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("123")
                .build();
        
        assertNotEquals(dto, new Object());
    }

    @Test
    @DisplayName("Should test hashCode consistency")
    void testHashCodeConsistency() {
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("123")
                .email("test@example.com")
                .name("Test")
                .build();
        
        int hashCode1 = dto.hashCode();
        int hashCode2 = dto.hashCode();
        
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should test toString")
    void testToString() {
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("staff123")
                .email("admin@example.com")
                .name("John Doe")
                .phoneNumber("012-3456789")
                .gender(Gender.MALE)
                .position("Manager")
                .role(Role.ADMIN)
                .mfaEnabled(true)
                .createdBy("superadmin")
                .build();
        
        String toString = dto.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("staff123"));
        assertTrue(toString.contains("admin@example.com"));
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("Manager"));
    }

    @Test
    @DisplayName("Should test toString with null values")
    void testToStringWithNulls() {
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("123")
                .build();
        
        String toString = dto.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("123"));
    }

    @Test
    @DisplayName("Should test builder pattern comprehensively")
    void testBuilderPattern() {
        LocalDateTime now = LocalDateTime.now();
        
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("builder-test")
                .email("builder@example.com")
                .name("Builder Test")
                .phoneNumber("012-9999999")
                .gender(Gender.FEMALE)
                .position("Tester")
                .role(Role.ADMIN)
                .mfaEnabled(false)
                .createdBy("system")
                .lastLoginAt(now)
                .createdAt(now)
                .build();
        
        assertEquals("builder-test", dto.getStaffId());
        assertEquals("builder@example.com", dto.getEmail());
        assertEquals("Builder Test", dto.getName());
        assertEquals("012-9999999", dto.getPhoneNumber());
        assertEquals(Gender.FEMALE, dto.getGender());
        assertEquals("Tester", dto.getPosition());
        assertEquals(Role.ADMIN, dto.getRole());
        assertFalse(dto.getMfaEnabled());
        assertEquals("system", dto.getCreatedBy());
        assertEquals(now, dto.getLastLoginAt());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    @DisplayName("Should test with past dates")
    void testWithPastDates() {
        LocalDateTime pastDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("123")
                .email("test@example.com")
                .name("Test")
                .lastLoginAt(pastDate)
                .createdAt(pastDate)
                .build();
        
        assertEquals(pastDate, dto.getLastLoginAt());
        assertEquals(pastDate, dto.getCreatedAt());
    }

    @Test
    @DisplayName("Should test with future dates")
    void testWithFutureDates() {
        LocalDateTime futureDate = LocalDateTime.of(2030, 12, 31, 23, 59);
        
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("123")
                .email("test@example.com")
                .name("Test")
                .lastLoginAt(futureDate)
                .createdAt(futureDate)
                .build();
        
        assertEquals(futureDate, dto.getLastLoginAt());
        assertEquals(futureDate, dto.getCreatedAt());
    }

    @Test
    @DisplayName("Should test empty string values")
    void testEmptyStringValues() {
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("")
                .email("")
                .name("")
                .phoneNumber("")
                .position("")
                .createdBy("")
                .build();
        
        assertEquals("", dto.getStaffId());
        assertEquals("", dto.getEmail());
        assertEquals("", dto.getName());
        assertEquals("", dto.getPhoneNumber());
        assertEquals("", dto.getPosition());
        assertEquals("", dto.getCreatedBy());
    }

    @Test
    @DisplayName("Should test special characters in strings")
    void testSpecialCharacters() {
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId("staff-123_test")
                .email("test+admin@example.com")
                .name("O'Brien-Smith")
                .phoneNumber("012-3456789")
                .position("Senior Developer & Manager")
                .createdBy("super@admin")
                .build();
        
        assertEquals("staff-123_test", dto.getStaffId());
        assertEquals("test+admin@example.com", dto.getEmail());
        assertEquals("O'Brien-Smith", dto.getName());
        assertEquals("Senior Developer & Manager", dto.getPosition());
    }

    @Test
    @DisplayName("Should test long string values")
    void testLongStringValues() {
        String longString = "a".repeat(200);
        
        AdminProfileDTO dto = AdminProfileDTO.builder()
                .staffId(longString)
                .email(longString + "@example.com")
                .name(longString)
                .phoneNumber("012-3456789")
                .position(longString)
                .createdBy(longString)
                .build();
        
        assertEquals(longString, dto.getStaffId());
        assertTrue(dto.getEmail().contains(longString));
        assertEquals(longString, dto.getName());
        assertEquals(longString, dto.getPosition());
        assertEquals(longString, dto.getCreatedBy());
    }

    @Test
    @DisplayName("Should test canEqual method if exists")
    void testCanEqual() {
        AdminProfileDTO dto1 = AdminProfileDTO.builder()
                .staffId("123")
                .build();
        
        AdminProfileDTO dto2 = AdminProfileDTO.builder()
                .staffId("123")
                .build();
        
        assertEquals(dto1, dto2);
        
        // Test with different types
        Object obj = new Object();
        assertNotEquals(dto1, obj);
    }
}