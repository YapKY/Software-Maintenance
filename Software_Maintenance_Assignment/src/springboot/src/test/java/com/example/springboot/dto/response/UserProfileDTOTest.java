package com.example.springboot.dto.response;

import com.example.springboot.enums.Role;
import com.example.springboot.enums.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserProfileDTO Tests - 100% Coverage")
class UserProfileDTOTest {

    @Test
    @DisplayName("Should create valid UserProfileDTO with all fields")
    void testValidWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        UserProfileDTO dto = UserProfileDTO.builder()
                .id("user123")
                .email("user@example.com")
                .fullName("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .role(Role.USER)
                .mfaEnabled(false)
                .emailVerified(true)
                .lastLoginAt(now)
                .createdAt(now)
                .build();
        
        assertEquals("user123", dto.getId());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals("John Doe", dto.getFullName());
        assertEquals("123456-12-1234", dto.getCustIcNo());
        assertEquals(Gender.MALE, dto.getGender());
        assertEquals("012-3456789", dto.getPhoneNumber());
        assertEquals(Role.USER, dto.getRole());
        assertFalse(dto.getMfaEnabled());
        assertTrue(dto.getEmailVerified());
        assertEquals(now, dto.getLastLoginAt());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    @DisplayName("Should test with female gender")
    void testWithFemaleGender() {
        UserProfileDTO dto = UserProfileDTO.builder()
                .id("user456")
                .email("jane@example.com")
                .fullName("Jane Doe")
                .custIcNo("654321-11-4321")
                .gender(Gender.FEMALE)
                .phoneNumber("013-9876543")
                .build();
        
        assertEquals(Gender.FEMALE, dto.getGender());
    }

    @Test
    @DisplayName("Should test with mfaEnabled true")
    void testWithMfaEnabled() {
        UserProfileDTO dto = UserProfileDTO.builder()
                .id("user789")
                .email("secure@example.com")
                .fullName("Secure User")
                .mfaEnabled(true)
                .build();
        
        assertTrue(dto.getMfaEnabled());
    }

    @Test
    @DisplayName("Should test with emailVerified false")
    void testWithEmailNotVerified() {
        UserProfileDTO dto = UserProfileDTO.builder()
                .id("user999")
                .email("unverified@example.com")
                .fullName("Unverified User")
                .emailVerified(false)
                .build();
        
        assertFalse(dto.getEmailVerified());
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void testNullOptionalFields() {
        UserProfileDTO dto = UserProfileDTO.builder()
                .id("123")
                .email("test@example.com")
                .fullName(null)
                .custIcNo(null)
                .gender(null)
                .phoneNumber(null)
                .role(null)
                .mfaEnabled(null)
                .emailVerified(null)
                .lastLoginAt(null)
                .createdAt(null)
                .build();
        
        assertNull(dto.getFullName());
        assertNull(dto.getCustIcNo());
        assertNull(dto.getGender());
        assertNull(dto.getPhoneNumber());
        assertNull(dto.getRole());
        assertNull(dto.getMfaEnabled());
        assertNull(dto.getEmailVerified());
        assertNull(dto.getLastLoginAt());
        assertNull(dto.getCreatedAt());
    }

    @Test
    @DisplayName("Should test no-args constructor")
    void testNoArgsConstructor() {
        UserProfileDTO dto = new UserProfileDTO();
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getEmail());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        UserProfileDTO dto = new UserProfileDTO(
            "user123", "user@example.com", "John Doe",
            "123456-12-1234", Gender.MALE, "012-3456789",
            Role.USER, true, false, now, now
        );
        
        assertEquals("user123", dto.getId());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals("John Doe", dto.getFullName());
        assertEquals("123456-12-1234", dto.getCustIcNo());
        assertEquals(Gender.MALE, dto.getGender());
        assertEquals("012-3456789", dto.getPhoneNumber());
        assertEquals(Role.USER, dto.getRole());
        assertTrue(dto.getMfaEnabled());
        assertFalse(dto.getEmailVerified());
    }

    @Test
    @DisplayName("Should test setters")
    void testSetters() {
        LocalDateTime now = LocalDateTime.now();
        UserProfileDTO dto = new UserProfileDTO();
        
        dto.setId("123");
        dto.setEmail("test@example.com");
        dto.setFullName("Test User");
        dto.setCustIcNo("123456-12-1234");
        dto.setGender(Gender.FEMALE);
        dto.setPhoneNumber("012-3456789");
        dto.setRole(Role.USER);
        dto.setMfaEnabled(true);
        dto.setEmailVerified(false);
        dto.setLastLoginAt(now);
        dto.setCreatedAt(now);
        
        assertEquals("123", dto.getId());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("Test User", dto.getFullName());
        assertEquals("123456-12-1234", dto.getCustIcNo());
        assertEquals(Gender.FEMALE, dto.getGender());
        assertEquals("012-3456789", dto.getPhoneNumber());
        assertEquals(Role.USER, dto.getRole());
        assertTrue(dto.getMfaEnabled());
        assertFalse(dto.getEmailVerified());
        assertEquals(now, dto.getLastLoginAt());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        UserProfileDTO dto1 = UserProfileDTO.builder()
                .id("123")
                .email("test@example.com")
                .fullName("Test")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .role(Role.USER)
                .mfaEnabled(true)
                .emailVerified(false)
                .lastLoginAt(now)
                .createdAt(now)
                .build();
        
        UserProfileDTO dto2 = UserProfileDTO.builder()
                .id("123")
                .email("test@example.com")
                .fullName("Test")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .role(Role.USER)
                .mfaEnabled(true)
                .emailVerified(false)
                .lastLoginAt(now)
                .createdAt(now)
                .build();
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Should test toString")
    void testToString() {
        UserProfileDTO dto = UserProfileDTO.builder()
                .id("user123")
                .email("user@example.com")
                .fullName("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .role(Role.USER)
                .build();
        
        String toString = dto.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("user123"));
        assertTrue(toString.contains("user@example.com"));
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("123456-12-1234"));
    }

    @Test
    @DisplayName("Should test with ADMIN role")
    void testWithAdminRole() {
        UserProfileDTO dto = UserProfileDTO.builder()
                .id("admin1")
                .email("admin@example.com")
                .fullName("Admin User")
                .role(Role.ADMIN)
                .build();
        
        assertEquals(Role.ADMIN, dto.getRole());
    }

    @Test
    @DisplayName("Should test IC number variations")
    void testIcNumberVariations() {
        String[] icNumbers = {
            "123456-12-1234",
            "000000-00-0000",
            "999999-99-9999",
            "111111-11-1111"
        };
        
        for (String icNo : icNumbers) {
            UserProfileDTO dto = UserProfileDTO.builder()
                    .id("user" + icNo)
                    .email("user@example.com")
                    .fullName("Test User")
                    .custIcNo(icNo)
                    .build();
            
            assertEquals(icNo, dto.getCustIcNo());
        }
    }

    @Test
    @DisplayName("Should test phone number variations")
    void testPhoneNumberVariations() {
        String[] phoneNumbers = {
            "012-3456789",
            "013-12345678",
            "011-1234567",
            "019-99999999"
        };
        
        for (String phone : phoneNumbers) {
            UserProfileDTO dto = UserProfileDTO.builder()
                    .id("user")
                    .email("user@example.com")
                    .fullName("Test User")
                    .phoneNumber(phone)
                    .build();
            
            assertEquals(phone, dto.getPhoneNumber());
        }
    }
}