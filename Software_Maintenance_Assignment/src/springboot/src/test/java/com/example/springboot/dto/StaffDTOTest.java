package com.example.springboot.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StaffDTOTest {

    @Test
    void testBuilderAndGetters() {
        StaffDTO dto = StaffDTO.builder()
                .staffId("S500")
                .position("Manager")
                .name("Bob Smith")
                .email("bob@company.com")
                .phoneNumber("019-8765432")
                .gender("MALE")
                .build();

        assertEquals("S500", dto.getStaffId());
        assertEquals("Manager", dto.getPosition());
        assertEquals("Bob Smith", dto.getName());
        assertEquals("bob@company.com", dto.getEmail());
        assertEquals("019-8765432", dto.getPhoneNumber());
        assertEquals("MALE", dto.getGender());
    }

    @Test
    void testPartialBuild() {
        // Test building with only some fields set
        StaffDTO dto = StaffDTO.builder()
                .staffId("S501")
                .build();

        assertEquals("S501", dto.getStaffId());
        assertNull(dto.getName());
        assertNull(dto.getPosition());
    }
}