package com.example.springboot.dto.response;

import com.example.springboot.enums.Role;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SuperadminProfileDTOTest {

    @Test
    void testBuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        SuperadminProfileDTO dto = SuperadminProfileDTO.builder()
                .id("SA1")
                .email("sa@test.com")
                .fullName("Super Admin")
                .role(Role.SUPERADMIN) // Fixed: Role.SUPERADMIN
                .mfaEnabled(true)
                .lastLoginAt(now)
                .createdAt(now)
                .totalAdminsCreated(10)
                .build();

        assertEquals("SA1", dto.getId());
        assertEquals("sa@test.com", dto.getEmail());
        assertEquals("Super Admin", dto.getFullName());
        assertEquals(Role.SUPERADMIN, dto.getRole()); // Fixed
        assertTrue(dto.getMfaEnabled());
        assertEquals(now, dto.getLastLoginAt());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(10, dto.getTotalAdminsCreated());
    }

    @Test
    void testConstructorsAndSetters() {
        SuperadminProfileDTO dto = new SuperadminProfileDTO();
        dto.setId("SA2");
        assertEquals("SA2", dto.getId());
        
        SuperadminProfileDTO dtoAll = new SuperadminProfileDTO("1", "e", "n", Role.SUPERADMIN, false, null, null, 0); // Fixed
        assertEquals("e", dtoAll.getEmail());
    }

    @Test
    void testEqualsAndHashCode() {
        SuperadminProfileDTO dto1 = SuperadminProfileDTO.builder().id("1").build();
        SuperadminProfileDTO dto2 = SuperadminProfileDTO.builder().id("1").build();
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        SuperadminProfileDTO dto = SuperadminProfileDTO.builder().email("test").build();
        assertTrue(dto.toString().contains("test"));
    }
}