package com.example.springboot.dto.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageResponseDTOTest {

    @Test
    void testBuilderAndGetters() {
        Object dataObj = new Object();
        MessageResponseDTO dto = MessageResponseDTO.builder()
                .success(true)
                .message("Operation done")
                .data(dataObj)
                .build();

        assertTrue(dto.getSuccess());
        assertEquals("Operation done", dto.getMessage());
        assertSame(dataObj, dto.getData());
    }

    @Test
    void testConstructorsAndSetters() {
        MessageResponseDTO dto = new MessageResponseDTO(false, "Fail", null);
        assertFalse(dto.getSuccess());
        
        dto.setMessage("New Message");
        assertEquals("New Message", dto.getMessage());
    }

    @Test
    void testEqualsAndHashCode() {
        MessageResponseDTO dto1 = MessageResponseDTO.builder().message("A").build();
        MessageResponseDTO dto2 = MessageResponseDTO.builder().message("A").build();
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        MessageResponseDTO dto = MessageResponseDTO.builder().message("Test").build();
        assertTrue(dto.toString().contains("Test"));
    }
}