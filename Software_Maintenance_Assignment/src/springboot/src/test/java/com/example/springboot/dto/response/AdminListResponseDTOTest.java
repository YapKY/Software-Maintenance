package com.example.springboot.dto.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AdminListResponseDTO Tests")
class AdminListResponseDTOTest {

    @Test
    @DisplayName("Should create valid AdminListResponseDTO")
    void testValid() {
        List<AdminProfileDTO> admins = Arrays.asList(
            AdminProfileDTO.builder().staffId("1").build(),
            AdminProfileDTO.builder().staffId("2").build()
        );
        
        AdminListResponseDTO dto = AdminListResponseDTO.builder()
                .success(true)
                .message("Success")
                .totalAdmins(2)
                .admins(admins)
                .build();
        
        assertTrue(dto.getSuccess());
        assertEquals("Success", dto.getMessage());
        assertEquals(2, dto.getTotalAdmins());
        assertEquals(2, dto.getAdmins().size());
    }

    @Test
    @DisplayName("Should test all constructors")
    void testConstructors() {
        AdminListResponseDTO dto1 = new AdminListResponseDTO();
        dto1.setSuccess(true);
        dto1.setMessage("Test");
        dto1.setTotalAdmins(1);
        dto1.setAdmins(Arrays.asList());
        
        AdminListResponseDTO dto2 = new AdminListResponseDTO(true, "Test", 1, Arrays.asList());
        
        assertEquals(dto1, dto2);
        assertNotNull(dto1.toString());
    }
}