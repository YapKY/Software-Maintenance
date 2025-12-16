package com.example.springboot.dto.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MFAStatusDTOTest {

    @Test
    void testBuilderAndGetters() {
        String[] codes = {"123", "456"};
        MFAStatusDTO dto = MFAStatusDTO.builder()
                .mfaEnabled(true)
                .secret("secretKey")
                .qrCodeUrl("url")
                .backupCodes(codes)
                .build();

        assertTrue(dto.getMfaEnabled());
        assertEquals("secretKey", dto.getSecret());
        assertEquals("url", dto.getQrCodeUrl());
        assertArrayEquals(codes, dto.getBackupCodes());
    }

    @Test
    void testConstructorsAndSetters() {
        MFAStatusDTO dto = new MFAStatusDTO();
        dto.setMfaEnabled(false);
        assertFalse(dto.getMfaEnabled());
        
        MFAStatusDTO dtoAll = new MFAStatusDTO(true, "s", "u", null);
        assertTrue(dtoAll.getMfaEnabled());
    }

    @Test
    void testEqualsAndHashCode() {
        MFAStatusDTO dto1 = MFAStatusDTO.builder().secret("A").build();
        MFAStatusDTO dto2 = MFAStatusDTO.builder().secret("A").build();
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        MFAStatusDTO dto = MFAStatusDTO.builder().secret("Hidden").build();
        assertTrue(dto.toString().contains("Hidden"));
    }
}