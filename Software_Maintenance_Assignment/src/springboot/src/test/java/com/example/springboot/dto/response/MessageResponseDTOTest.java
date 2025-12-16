package com.example.springboot.dto.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MessageResponseDTO Tests")
class MessageResponseDTOTest {

    @Test
    @DisplayName("Should create MessageResponseDTO")
    void testValid() {
        Object data = "test data";
        MessageResponseDTO dto = MessageResponseDTO.builder()
                .success(true)
                .message("Operation successful")
                .data(data)
                .build();
        
        assertTrue(dto.getSuccess());
        assertEquals("Operation successful", dto.getMessage());
        assertEquals("test data", dto.getData());
    }

    @Test
    @DisplayName("Should handle null data")
    void testNullData() {
        MessageResponseDTO dto = MessageResponseDTO.builder()
                .success(false)
                .message("Error occurred")
                .data(null)
                .build();
        
        assertFalse(dto.getSuccess());
        assertNull(dto.getData());
    }

    @Test
    @DisplayName("Should test all constructors")
    void testConstructors() {
        MessageResponseDTO dto1 = new MessageResponseDTO();
        dto1.setSuccess(true);
        dto1.setMessage("Test");
        dto1.setData("data");
        
        MessageResponseDTO dto2 = new MessageResponseDTO(true, "Test", "data");
        
        assertEquals(dto1, dto2);
        assertNotNull(dto1.toString());
    }
}