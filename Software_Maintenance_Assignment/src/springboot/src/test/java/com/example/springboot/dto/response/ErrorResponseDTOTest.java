package com.example.springboot.dto.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ErrorResponseDTO Tests")
class ErrorResponseDTOTest {

    @Test
    @DisplayName("Should create valid ErrorResponseDTO")
    void testValid() {
        ErrorResponseDTO dto = ErrorResponseDTO.builder()
                .status(400)
                .error("Bad Request")
                .message("Invalid input")
                .path("/api/test")
                .build();
        
        assertEquals(400, dto.getStatus());
        assertEquals("Bad Request", dto.getError());
        assertEquals("Invalid input", dto.getMessage());
        assertEquals("/api/test", dto.getPath());
        assertNotNull(dto.getTimestamp());
    }

    @Test
    @DisplayName("Should test timestamp default")
    void testTimestampDefault() {
        ErrorResponseDTO dto = ErrorResponseDTO.builder()
                .status(500)
                .error("Internal Error")
                .message("Something went wrong")
                .build();
        
        assertNotNull(dto.getTimestamp());
        assertTrue(dto.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Should test all constructors")
    void testConstructors() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponseDTO dto1 = new ErrorResponseDTO();
        dto1.setStatus(404);
        dto1.setError("Not Found");
        dto1.setMessage("Resource not found");
        dto1.setTimestamp(now);
        dto1.setPath("/api/resource");
        
        ErrorResponseDTO dto2 = new ErrorResponseDTO(404, "Not Found", "Resource not found", now, "/api/resource");
        
        assertEquals(dto1, dto2);
        assertNotNull(dto1.toString());
    }
}