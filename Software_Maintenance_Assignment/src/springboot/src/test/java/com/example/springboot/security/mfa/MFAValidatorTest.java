package com.example.springboot.security.mfa;

import com.example.springboot.dto.response.MFAStatusDTO;
import com.example.springboot.enums.Role;
import com.example.springboot.service.MFAService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MFAValidatorTest {

    @Mock
    private MFAService mfaService;

    @InjectMocks
    private MFAValidator mfaValidator;

    // --- validateMFACode Tests ---

    @Test
    @DisplayName("validateMFACode - Success")
    void testValidateMFACode_Success() {
        when(mfaService.validateMFACode("user1", Role.USER, "123456")).thenReturn(true);
        boolean result = mfaValidator.validateMFACode("user1", Role.USER, "123456");
        assertTrue(result);
    }

    @Test
    @DisplayName("validateMFACode - Failure")
    void testValidateMFACode_Failure() {
        when(mfaService.validateMFACode("user1", Role.USER, "wrong")).thenReturn(false);
        boolean result = mfaValidator.validateMFACode("user1", Role.USER, "wrong");
        assertFalse(result);
    }

    @Test
    @DisplayName("validateMFACode - Exception handled gracefully")
    void testValidateMFACode_Exception() {
        when(mfaService.validateMFACode(anyString(), any(Role.class), anyString()))
                .thenThrow(new RuntimeException("Service down"));
        
        boolean result = mfaValidator.validateMFACode("user1", Role.USER, "123456");
        assertFalse(result, "Should return false on exception");
    }

    // --- isMFAEnabled Tests ---

    @Test
    @DisplayName("isMFAEnabled - True")
    void testIsMFAEnabled_True() {
        MFAStatusDTO status = MFAStatusDTO.builder().mfaEnabled(true).build();
        when(mfaService.getMFAStatus("user1", Role.USER)).thenReturn(status);

        boolean result = mfaValidator.isMFAEnabled("user1", Role.USER);
        assertTrue(result);
    }

    @Test
    @DisplayName("isMFAEnabled - False")
    void testIsMFAEnabled_False() {
        MFAStatusDTO status = MFAStatusDTO.builder().mfaEnabled(false).build();
        when(mfaService.getMFAStatus("user1", Role.USER)).thenReturn(status);

        boolean result = mfaValidator.isMFAEnabled("user1", Role.USER);
        assertFalse(result);
    }

    @Test
    @DisplayName("isMFAEnabled - Exception handled gracefully")
    void testIsMFAEnabled_Exception() {
        when(mfaService.getMFAStatus(anyString(), any(Role.class)))
                .thenThrow(new RuntimeException("DB Error"));

        boolean result = mfaValidator.isMFAEnabled("user1", Role.USER);
        assertFalse(result, "Should return false on exception");
    }
}