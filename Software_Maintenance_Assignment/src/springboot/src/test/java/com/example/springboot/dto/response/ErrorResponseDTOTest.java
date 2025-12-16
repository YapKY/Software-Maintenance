package com.example.springboot.dto.response;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseDTOTest {

    @Test
    void testBuilderDefaultTimestamp() {
        // Test that @Builder.Default works
        ErrorResponseDTO dto = ErrorResponseDTO.builder()
                .status(404)
                .error("Not Found")
                .build();
        
        assertNotNull(dto.getTimestamp());
        assertEquals(404, dto.getStatus());
    }

    @Test
    void testBuilderExplicitTimestamp() {
        LocalDateTime specificTime = LocalDateTime.of(2023, 1, 1, 12, 0);
        ErrorResponseDTO dto = ErrorResponseDTO.builder()
                .timestamp(specificTime)
                .build();
        
        assertEquals(specificTime, dto.getTimestamp());
    }

    @Test
    void testAllFields() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponseDTO dto = new ErrorResponseDTO(500, "Error", "Message", now, "/api/test");

        assertEquals(500, dto.getStatus());
        assertEquals("Error", dto.getError());
        assertEquals("Message", dto.getMessage());
        assertEquals(now, dto.getTimestamp());
        assertEquals("/api/test", dto.getPath());
    }

    @Test
    void testNoArgsConstructor() {
        ErrorResponseDTO dto = new ErrorResponseDTO();
        dto.setStatus(200);
        assertEquals(200, dto.getStatus());
        // Default builder value is not applied in NoArgs constructor, ensuring null unless set
        // But if instantiated via builder it is set. 
        // Here we just test setter works.
    }

    @Test
    void testEqualsAndHashCode() {
        ErrorResponseDTO dto1 = ErrorResponseDTO.builder().status(400).build();
        ErrorResponseDTO dto2 = ErrorResponseDTO.builder().status(400).build();
        // Timestamps might differ slightly if not mocked, but with Builder they are generated at call time.
        // To be safe in equality check, we set timestamp manually
        LocalDateTime t = LocalDateTime.now();
        dto1.setTimestamp(t);
        dto2.setTimestamp(t);
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
    
    @Test
    void testToString() {
        ErrorResponseDTO dto = ErrorResponseDTO.builder().error("Critical").build();
        assertTrue(dto.toString().contains("Critical"));
    }
}