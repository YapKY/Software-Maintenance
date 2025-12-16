package com.example.springboot.dto.response;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdminListResponseDTOTest {

    @Test
    void testBuilderAndGetters() {
        List<AdminProfileDTO> adminList = Collections.singletonList(new AdminProfileDTO());
        
        AdminListResponseDTO dto = AdminListResponseDTO.builder()
                .success(true)
                .message("Success")
                .totalAdmins(5)
                .admins(adminList)
                .build();

        assertTrue(dto.getSuccess());
        assertEquals("Success", dto.getMessage());
        assertEquals(5, dto.getTotalAdmins());
        assertEquals(adminList, dto.getAdmins());
    }

    @Test
    void testAllArgsConstructor() {
        List<AdminProfileDTO> adminList = Collections.emptyList();
        AdminListResponseDTO dto = new AdminListResponseDTO(true, "Msg", 1, adminList);

        assertTrue(dto.getSuccess());
        assertEquals("Msg", dto.getMessage());
        assertEquals(1, dto.getTotalAdmins());
        assertEquals(adminList, dto.getAdmins());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        AdminListResponseDTO dto = new AdminListResponseDTO();
        dto.setSuccess(false);
        dto.setMessage("Error");
        dto.setTotalAdmins(0);
        dto.setAdmins(null);

        assertFalse(dto.getSuccess());
        assertEquals("Error", dto.getMessage());
        assertEquals(0, dto.getTotalAdmins());
        assertNull(dto.getAdmins());
    }

    @Test
    void testEqualsAndHashCode() {
        AdminListResponseDTO dto1 = AdminListResponseDTO.builder().success(true).message("Test").build();
        AdminListResponseDTO dto2 = AdminListResponseDTO.builder().success(true).message("Test").build();
        AdminListResponseDTO dto3 = AdminListResponseDTO.builder().success(false).message("Diff").build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testToString() {
        AdminListResponseDTO dto = AdminListResponseDTO.builder().message("StringTest").build();
        String toString = dto.toString();
        assertTrue(toString.contains("StringTest"));
        assertTrue(toString.contains("AdminListResponseDTO"));
    }
}