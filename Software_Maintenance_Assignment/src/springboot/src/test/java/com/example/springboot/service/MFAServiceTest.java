package com.example.springboot.service;

import com.example.springboot.dto.response.MFAStatusDTO;
import com.example.springboot.enums.Role;
import com.example.springboot.exception.MFAValidationException;
import com.example.springboot.exception.UserNotFoundException;
import com.example.springboot.model.MFASecret;
import com.example.springboot.repository.MFASecretRepository;
import com.example.springboot.security.mfa.TOTPGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MFAServiceTest {

    @Mock
    private MFASecretRepository mfaSecretRepository;

    @Mock
    private TOTPGenerator totpGenerator;

    @InjectMocks
    private MFAService mfaService;

    private final String userId = "user123";
    private final Role role = Role.USER;

    @Test
    void testSetupMFA_NewSetup_Success() {
        when(mfaSecretRepository.findByUserIdAndUserRole(userId, role)).thenReturn(Optional.empty());
        when(totpGenerator.generateSecret()).thenReturn("SECRET123");
        when(totpGenerator.generateQRCodeUrl(eq("SECRET123"), anyString())).thenReturn("http://qrcode.url");

        MFAStatusDTO result = mfaService.setupMFA(userId, role);

        assertNotNull(result);
        assertEquals("SECRET123", result.getSecret());
        assertEquals("http://qrcode.url", result.getQrCodeUrl());
        assertFalse(result.getMfaEnabled());
        assertNotNull(result.getBackupCodes());
        assertEquals(10, result.getBackupCodes().length); // 10 backup codes generated

        verify(mfaSecretRepository).save(any(MFASecret.class));
    }

    @Test
    void testSetupMFA_OverwriteUnverified_Success() {
        MFASecret existing = MFASecret.builder().verified(false).build();
        when(mfaSecretRepository.findByUserIdAndUserRole(userId, role)).thenReturn(Optional.of(existing));
        when(totpGenerator.generateSecret()).thenReturn("SECRET123");

        MFAStatusDTO result = mfaService.setupMFA(userId, role);

        verify(mfaSecretRepository).deleteByUserIdAndUserRole(userId, role);
        verify(mfaSecretRepository).save(any(MFASecret.class));
        assertNotNull(result);
    }

    @Test
    void testSetupMFA_AlreadyVerified_ThrowsException() {
        MFASecret existing = MFASecret.builder().verified(true).build();
        when(mfaSecretRepository.findByUserIdAndUserRole(userId, role)).thenReturn(Optional.of(existing));

        assertThrows(MFAValidationException.class, () -> mfaService.setupMFA(userId, role));
        verify(mfaSecretRepository, never()).save(any());
    }

    @Test
    void testValidateMFACode_Success_TOTP() {
        MFASecret secret = MFASecret.builder().secret("SECRET123").build();
        when(mfaSecretRepository.findByUserIdAndUserRole(userId, role)).thenReturn(Optional.of(secret));
        when(totpGenerator.validateCode("SECRET123", "123456")).thenReturn(true);

        boolean isValid = mfaService.validateMFACode(userId, role, "123456");

        assertTrue(isValid);
    }

    @Test
    void testValidateMFACode_Success_BackupCode() {
        // Need to run encryption logic once to get a valid encrypted string for mocking?
        // Or assume the service uses a deterministic flow or we mock encryption?
        // Actually, MFAService generates keys internally for encryption, so it's hard to mock encrypted string exactly matching the service logic without using reflection or actually letting the service encrypt it first.
        // A better approach for Unit Test here: We can rely on the fact that we setup MFA first to get valid backup codes in the repo mock.
        
        // 1. Run setup to generate encrypted codes inside the service logic context (but mocking repo saves)
        when(mfaSecretRepository.findByUserIdAndUserRole(userId, role)).thenReturn(Optional.empty());
        when(totpGenerator.generateSecret()).thenReturn("SECRET");
        MFAStatusDTO setup = mfaService.setupMFA(userId, role);
        String validBackupCode = setup.getBackupCodes()[0];

        // Capture the saved secret
        // Since we can't easily capture the argument passed to save in the setup call and reuse it in the same test method cleanly for findBy...
        // Let's assume the service works and we simply use ArgumentCaptor.
        
        // Re-mock for validation phase
        org.mockito.ArgumentCaptor<MFASecret> captor = org.mockito.ArgumentCaptor.forClass(MFASecret.class);
        verify(mfaSecretRepository).save(captor.capture());
        MFASecret savedSecret = captor.getValue();
        
        when(mfaSecretRepository.findByUserIdAndUserRole(userId, role)).thenReturn(Optional.of(savedSecret));
        
        boolean isValid = mfaService.validateMFACode(userId, role, validBackupCode);
        assertTrue(isValid);
    }

    @Test
    void testValidateMFACode_Failure_InvalidCode() {
        MFASecret secret = MFASecret.builder().secret("SECRET123").backupCodes("key:encrypted").build();
        when(mfaSecretRepository.findByUserIdAndUserRole(userId, role)).thenReturn(Optional.of(secret));
        
        // TOTP fail
        when(totpGenerator.validateCode(any(), eq("000000"))).thenReturn(false);
        // Backup fail (decryption will likely fail or produce garbage, assuming "key:encrypted" is dummy)
        
        // We expect it to try decrypt, fail or not match
        // Note: decryptBackupCodes throws exception on invalid Base64, caught by catch block
        
        boolean isValid = mfaService.validateMFACode(userId, role, "000000");
        assertFalse(isValid);
    }

    @Test
    void testValidateMFACode_UserNotFound() {
        when(mfaSecretRepository.findByUserIdAndUserRole(userId, role)).thenReturn(Optional.empty());

        assertThrows(MFAValidationException.class, () -> mfaService.validateMFACode(userId, role, "123456"));
    }

    @Test
    void testVerifyAndEnableMFA_Success() {
        MFASecret secret = MFASecret.builder().secret("SECRET123").verified(false).build();
        when(mfaSecretRepository.findByUserIdAndUserRole(userId, role)).thenReturn(Optional.of(secret));
        when(totpGenerator.validateCode("SECRET123", "123456")).thenReturn(true);

        boolean result = mfaService.verifyAndEnableMFA(userId, role, "123456");

        assertTrue(result);
        assertTrue(secret.getVerified());
        verify(mfaSecretRepository).save(secret);
    }

    @Test
    void testVerifyAndEnableMFA_Failure() {
        MFASecret secret = MFASecret.builder().secret("SECRET123").verified(false).build();
        when(mfaSecretRepository.findByUserIdAndUserRole(userId, role)).thenReturn(Optional.of(secret));
        when(totpGenerator.validateCode("SECRET123", "000000")).thenReturn(false);

        boolean result = mfaService.verifyAndEnableMFA(userId, role, "000000");

        assertFalse(result);
        assertFalse(secret.getVerified());
        verify(mfaSecretRepository, never()).save(secret);
    }

    @Test
    void testDisableMFA_Success() {
        when(mfaSecretRepository.existsByUserIdAndUserRole(userId, role)).thenReturn(true);

        mfaService.disableMFA(userId, role);

        verify(mfaSecretRepository).deleteByUserIdAndUserRole(userId, role);
    }

    @Test
    void testDisableMFA_NotFound() {
        when(mfaSecretRepository.existsByUserIdAndUserRole(userId, role)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> mfaService.disableMFA(userId, role));
    }

    @Test
    void testGetMFAStatus_Enabled() {
        MFASecret secret = MFASecret.builder().verified(true).build();
        when(mfaSecretRepository.findByUserIdAndUserRole(userId, role)).thenReturn(Optional.of(secret));

        MFAStatusDTO status = mfaService.getMFAStatus(userId, role);

        assertTrue(status.getMfaEnabled());
    }

    @Test
    void testGetMFAStatus_Disabled_NotVerified() {
        MFASecret secret = MFASecret.builder().verified(false).build();
        when(mfaSecretRepository.findByUserIdAndUserRole(userId, role)).thenReturn(Optional.of(secret));

        MFAStatusDTO status = mfaService.getMFAStatus(userId, role);

        assertFalse(status.getMfaEnabled());
    }

    @Test
    void testRegenerateBackupCodes_Success() {
        MFASecret secret = MFASecret.builder().secret("SECRET").build();
        when(mfaSecretRepository.findByUserIdAndUserRole(userId, role)).thenReturn(Optional.of(secret));

        String[] newCodes = mfaService.regenerateBackupCodes(userId, role);

        assertNotNull(newCodes);
        assertEquals(10, newCodes.length);
        verify(mfaSecretRepository).save(secret); // Should preserve secret but update backup codes
        assertNotNull(secret.getBackupCodes());
    }
}