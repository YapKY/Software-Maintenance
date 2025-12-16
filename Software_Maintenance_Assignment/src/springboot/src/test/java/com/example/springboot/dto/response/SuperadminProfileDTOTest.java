package com.example.springboot.dto.response;

import com.example.springboot.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SuperadminProfileDTO Tests")
class SuperadminProfileDTOTest {

    @Test
    @DisplayName("Should create valid SuperadminProfileDTO")
    void testValid() {
        LocalDateTime now = LocalDateTime.now();
        SuperadminProfileDTO dto = SuperadminProfileDTO.builder()
                .id("super123")
                .email("super@example.com")
                .fullName("Super Admin")
                .role(Role.SUPERADMIN)
                .mfaEnabled(true)
                .lastLoginAt(now)
                .createdAt(now)
                .totalAdminsCreated(5)
                .build();
        
        assertEquals("super123", dto.getId());
        assertEquals("super@example.com", dto.getEmail());
        assertEquals("Super Admin", dto.getFullName());
        assertEquals(Role.SUPERADMIN, dto.getRole());
        assertTrue(dto.getMfaEnabled());
        assertNotNull(dto.getLastLoginAt());
        assertNotNull(dto.getCreatedAt());
        assertEquals(5, dto.getTotalAdminsCreated());
    }

    @Test
    @DisplayName("Should test all constructors")
    void testConstructors() {
        LocalDateTime now = LocalDateTime.now();
        SuperadminProfileDTO dto1 = new SuperadminProfileDTO();
        dto1.setId("123");
        dto1.setEmail("test@example.com");
        dto1.setFullName("Test");
        dto1.setRole(Role.SUPERADMIN);
        dto1.setMfaEnabled(false);
        dto1.setLastLoginAt(now);
        dto1.setCreatedAt(now);
        dto1.setTotalAdminsCreated(10);
        
        SuperadminProfileDTO dto2 = new SuperadminProfileDTO("123", "test@example.com", 
                "Test", Role.SUPERADMIN, false, now, now, 10);
        
        assertEquals(dto1, dto2);
        assertNotNull(dto1.toString());
    }

    @Test
    @DisplayName("Should handle null totalAdminsCreated")
    void testNullTotalAdmins() {
        SuperadminProfileDTO dto = SuperadminProfileDTO.builder()
                .id("123")
                .email("test@example.com")
                .fullName("Test")
                .role(Role.SUPERADMIN)
                .build();
        
        assertNull(dto.getTotalAdminsCreated());
    }
}