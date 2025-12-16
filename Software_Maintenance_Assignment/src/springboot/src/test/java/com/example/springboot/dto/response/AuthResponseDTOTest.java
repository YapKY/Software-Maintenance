package com.example.springboot.dto.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuthResponseDTO Tests")
class AuthResponseDTOTest {

    @Test
    @DisplayName("Should create AuthResponseDTO with tokens")
    void testWithTokens() {
        JWTResponseDTO tokens = JWTResponseDTO.builder()
                .accessToken("access")
                .refreshToken("refresh")
                .build();
        
        AuthResponseDTO dto = AuthResponseDTO.builder()
                .success(true)
                .message("Login successful")
                .tokens(tokens)
                .requiresMfa(false)
                .email("user@example.com")
                .build();
        
        assertTrue(dto.getSuccess());
        assertEquals("Login successful", dto.getMessage());
        assertNotNull(dto.getTokens());
        assertFalse(dto.getRequiresMfa());
        assertEquals("user@example.com", dto.getEmail());
    }

    @Test
    @DisplayName("Should create AuthResponseDTO requiring MFA")
    void testRequiringMfa() {
        AuthResponseDTO dto = AuthResponseDTO.builder()
                .success(true)
                .message("MFA required")
                .tokens(null)
                .requiresMfa(true)
                .mfaSessionToken("session-token")
                .email("user@example.com")
                .build();
        
        assertTrue(dto.getSuccess());
        assertTrue(dto.getRequiresMfa());
        assertEquals("session-token", dto.getMfaSessionToken());
        assertNull(dto.getTokens());
    }

    @Test
    @DisplayName("Should test all constructors")
    void testConstructors() {
        AuthResponseDTO dto1 = new AuthResponseDTO();
        dto1.setSuccess(true);
        dto1.setMessage("Test");
        dto1.setRequiresMfa(false);
        dto1.setEmail("test@example.com");
        
        AuthResponseDTO dto2 = new AuthResponseDTO(true, "Test", null, false, null, "test@example.com");
        
        assertEquals(dto1.getSuccess(), dto2.getSuccess());
        assertNotNull(dto1.toString());
    }
}