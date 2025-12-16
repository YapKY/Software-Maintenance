package com.example.springboot.dto.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthResponseDTOTest {

    @Test
    void testBuilderAndGetters() {
        JWTResponseDTO jwt = new JWTResponseDTO();
        AuthResponseDTO dto = AuthResponseDTO.builder()
                .success(true)
                .message("Login successful")
                .tokens(jwt)
                .requiresMfa(false)
                .mfaSessionToken("session-123")
                .email("user@test.com")
                .build();

        assertTrue(dto.getSuccess());
        assertEquals("Login successful", dto.getMessage());
        assertEquals(jwt, dto.getTokens());
        assertFalse(dto.getRequiresMfa());
        assertEquals("session-123", dto.getMfaSessionToken());
        assertEquals("user@test.com", dto.getEmail());
    }

    @Test
    void testAllArgsConstructor() {
        AuthResponseDTO dto = new AuthResponseDTO(true, "Msg", null, true, "token", "email@test.com");
        assertTrue(dto.getRequiresMfa());
        assertEquals("email@test.com", dto.getEmail());
    }

    @Test
    void testSetters() {
        AuthResponseDTO dto = new AuthResponseDTO();
        dto.setEmail("new@test.com");
        assertEquals("new@test.com", dto.getEmail());
    }

    @Test
    void testEqualsAndHashCode() {
        AuthResponseDTO dto1 = AuthResponseDTO.builder().email("e1").build();
        AuthResponseDTO dto2 = AuthResponseDTO.builder().email("e1").build();
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
    
    @Test
    void testToString() {
        AuthResponseDTO dto = AuthResponseDTO.builder().message("Auth").build();
        assertTrue(dto.toString().contains("Auth"));
    }
}