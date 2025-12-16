package com.example.springboot.dto.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MFAChallengeResponseDTOTest {

    @Test
    void testBuilderAndGetters() {
        MFAChallengeResponseDTO dto = MFAChallengeResponseDTO.builder()
                .mfaRequired(true)
                .sessionToken("sess-token")
                .message("Scan QR")
                .qrCodeUrl("http://qr")
                .build();

        assertTrue(dto.getMfaRequired());
        assertEquals("sess-token", dto.getSessionToken());
        assertEquals("Scan QR", dto.getMessage());
        assertEquals("http://qr", dto.getQrCodeUrl());
    }

    @Test
    void testConstructorsAndSetters() {
        MFAChallengeResponseDTO dto = new MFAChallengeResponseDTO();
        dto.setMfaRequired(false);
        assertFalse(dto.getMfaRequired());

        MFAChallengeResponseDTO dtoAll = new MFAChallengeResponseDTO(true, "s", "m", "u");
        assertEquals("s", dtoAll.getSessionToken());
    }

    @Test
    void testEqualsAndHashCode() {
        MFAChallengeResponseDTO dto1 = MFAChallengeResponseDTO.builder().sessionToken("S1").build();
        MFAChallengeResponseDTO dto2 = MFAChallengeResponseDTO.builder().sessionToken("S1").build();
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        MFAChallengeResponseDTO dto = MFAChallengeResponseDTO.builder().message("MFA").build();
        assertTrue(dto.toString().contains("MFA"));
    }
}