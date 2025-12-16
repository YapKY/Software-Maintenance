package com.example.springboot.dto.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MFAStatusDTO Tests")
class MFAStatusDTOTest {

    @Test
    @DisplayName("Should create MFAStatusDTO with setup info")
    void testWithSetupInfo() {
        String[] backupCodes = {"code1", "code2", "code3"};
        MFAStatusDTO dto = MFAStatusDTO.builder()
                .mfaEnabled(true)
                .secret("secret-key")
                .qrCodeUrl("https://example.com/qr")
                .backupCodes(backupCodes)
                .build();
        
        assertTrue(dto.getMfaEnabled());
        assertEquals("secret-key", dto.getSecret());
        assertEquals("https://example.com/qr", dto.getQrCodeUrl());
        assertArrayEquals(backupCodes, dto.getBackupCodes());
    }

    @Test
    @DisplayName("Should test all constructors")
    void testConstructors() {
        String[] codes = {"c1", "c2"};
        MFAStatusDTO dto1 = new MFAStatusDTO();
        dto1.setMfaEnabled(false);
        dto1.setSecret("secret");
        dto1.setQrCodeUrl("url");
        dto1.setBackupCodes(codes);
        
        MFAStatusDTO dto2 = new MFAStatusDTO(false, "secret", "url", codes);
        
        assertEquals(dto1, dto2);
        assertNotNull(dto1.toString());
    }
}