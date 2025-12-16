package com.example.springboot.dto.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MFAChallengeResponseDTO Tests")
class MFAChallengeResponseDTOTest {

    @Test
    @DisplayName("Should create MFAChallengeResponseDTO")
    void testValid() {
        MFAChallengeResponseDTO dto = MFAChallengeResponseDTO.builder()
                .mfaRequired(true)
                .sessionToken("session-token")
                .message("MFA required")
                .qrCodeUrl("https://example.com/qr")
                .build();
        
        assertTrue(dto.getMfaRequired());
        assertEquals("session-token", dto.getSessionToken());
        assertEquals("MFA required", dto.getMessage());
        assertEquals("https://example.com/qr", dto.getQrCodeUrl());
    }

    @Test
    @DisplayName("Should test all constructors")
    void testConstructors() {
        MFAChallengeResponseDTO dto1 = new MFAChallengeResponseDTO();
        dto1.setMfaRequired(true);
        dto1.setSessionToken("token");
        dto1.setMessage("Test");
        dto1.setQrCodeUrl("url");
        
        MFAChallengeResponseDTO dto2 = new MFAChallengeResponseDTO(true, "token", "Test", "url");
        
        assertEquals(dto1, dto2);
        assertNotNull(dto1.toString());
    }
}