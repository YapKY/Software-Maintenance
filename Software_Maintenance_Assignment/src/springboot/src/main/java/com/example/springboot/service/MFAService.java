package com.example.springboot.service;

import com.example.springboot.model.MFASecret;
import com.example.springboot.enums.Role;
import com.example.springboot.dto.response.MFAStatusDTO;
import com.example.springboot.exception.MFAValidationException;
import com.example.springboot.exception.UserNotFoundException;
import com.example.springboot.repository.MFASecretRepository;
import com.example.springboot.security.mfa.TOTPGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MFAService {
    
    private final MFASecretRepository mfaSecretRepository;
    private final TOTPGenerator totpGenerator;
    
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int BACKUP_CODES_COUNT = 10;
    private static final int BACKUP_CODE_LENGTH = 8;
    
    public MFAStatusDTO setupMFA(String userId, Role userRole) {
        try {
            // [FIX] Check if existing MFA is actually verified
            Optional<MFASecret> existing = mfaSecretRepository.findByUserIdAndUserRole(userId, userRole);
            if (existing.isPresent()) {
                if (Boolean.TRUE.equals(existing.get().getVerified())) {
                    throw new MFAValidationException("MFA is already setup and verified for this user");
                }
                // If exists but not verified, we overwrite it (re-setup)
                mfaSecretRepository.deleteByUserIdAndUserRole(userId, userRole);
            }
            
            String secret = totpGenerator.generateSecret();
            String[] backupCodes = generateBackupCodes();
            String encryptedBackupCodes = encryptBackupCodes(backupCodes);
            
            MFASecret mfaSecret = MFASecret.builder()
                .userId(userId)
                .userRole(userRole)
                .secret(secret)
                .backupCodes(encryptedBackupCodes)
                .verified(false) // [FIX] Initially false
                .build();
                
            mfaSecretRepository.save(mfaSecret);
            
            String qrCodeUrl = totpGenerator.generateQRCodeUrl(secret, "Airline-" + userId);
            
            return MFAStatusDTO.builder()
                .mfaEnabled(false) // Setup started but not enabled yet
                .secret(secret)
                .qrCodeUrl(qrCodeUrl)
                .backupCodes(backupCodes)
                .build();
                
        } catch (Exception e) {
            throw new MFAValidationException("Failed to setup MFA: " + e.getMessage());
        }
    }
    
    public boolean validateMFACode(String userId, Role userRole, String code) {
        try {
            MFASecret mfaSecret = mfaSecretRepository.findByUserIdAndUserRole(userId, userRole)
                .orElseThrow(() -> new UserNotFoundException("MFA not setup for this user"));
            
            // Validate code logic...
            if (code.matches("\\d{6}")) {
                if (totpGenerator.validateCode(mfaSecret.getSecret(), code)) {
                    return true;
                }
            }
            
            if (code.length() == BACKUP_CODE_LENGTH) {
                String[] backupCodes = decryptBackupCodes(mfaSecret.getBackupCodes());
                for (String backupCode : backupCodes) {
                    if (backupCode.equals(code)) {
                        return true;
                    }
                }
            }
            return false;
            
        } catch (Exception e) {
            throw new MFAValidationException("Failed to validate MFA code");
        }
    }
    
    /**
     * [FIX] New method to verify and enable MFA
     */
    public boolean verifyAndEnableMFA(String userId, Role userRole, String code) {
        // First validate code using existing logic
        if (validateMFACode(userId, userRole, code)) {
            // If valid, mark secret as verified
            MFASecret mfaSecret = mfaSecretRepository.findByUserIdAndUserRole(userId, userRole)
                .orElseThrow(() -> new UserNotFoundException("MFA setup not found"));
            
            mfaSecret.setVerified(true);
            mfaSecretRepository.save(mfaSecret);
            return true;
        }
        return false;
    }
    
    public void disableMFA(String userId, Role userRole) {
        if (!mfaSecretRepository.existsByUserIdAndUserRole(userId, userRole)) {
            throw new UserNotFoundException("MFA not setup for this user");
        }
        mfaSecretRepository.deleteByUserIdAndUserRole(userId, userRole);
    }
    
    public MFAStatusDTO getMFAStatus(String userId, Role userRole) {
        // [FIX] Only return true if secret exists AND is verified
        Optional<MFASecret> secretOpt = mfaSecretRepository.findByUserIdAndUserRole(userId, userRole);
        boolean mfaEnabled = secretOpt.isPresent() && Boolean.TRUE.equals(secretOpt.get().getVerified());
        
        return MFAStatusDTO.builder().mfaEnabled(mfaEnabled).build();
    }
    
    public String[] regenerateBackupCodes(String userId, Role userRole) {
        try {
            MFASecret mfaSecret = mfaSecretRepository.findByUserIdAndUserRole(userId, userRole)
                .orElseThrow(() -> new UserNotFoundException("MFA not setup for this user"));
            
            String[] backupCodes = generateBackupCodes();
            String encryptedBackupCodes = encryptBackupCodes(backupCodes);
            
            mfaSecret.setBackupCodes(encryptedBackupCodes);
            mfaSecretRepository.save(mfaSecret);
            
            return backupCodes;
            
        } catch (Exception e) {
            throw new MFAValidationException("Failed to regenerate backup codes");
        }
    }
    
    private String[] generateBackupCodes() {
        String[] codes = new String[BACKUP_CODES_COUNT];
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < BACKUP_CODES_COUNT; i++) {
            codes[i] = generateRandomCode(BACKUP_CODE_LENGTH, random);
        }
        return codes;
    }
    
    private String generateRandomCode(int length, SecureRandom random) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
    
    private String encryptBackupCodes(String[] codes) throws Exception {
        String codesString = String.join(",", codes);
        KeyGenerator keyGen = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();
        
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        byte[] encrypted = cipher.doFinal(codesString.getBytes());
        String keyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        String encryptedString = Base64.getEncoder().encodeToString(encrypted);
        
        return keyString + ":" + encryptedString;
    }
    
    private String[] decryptBackupCodes(String encryptedData) throws Exception {
        String[] parts = encryptedData.split(":");
        byte[] keyBytes = Base64.getDecoder().decode(parts[0]);
        byte[] encryptedBytes = Base64.getDecoder().decode(parts[1]);
        
        SecretKey secretKey = new SecretKeySpec(keyBytes, ENCRYPTION_ALGORITHM);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        byte[] decrypted = cipher.doFinal(encryptedBytes);
        return new String(decrypted).split(",");
    }
}