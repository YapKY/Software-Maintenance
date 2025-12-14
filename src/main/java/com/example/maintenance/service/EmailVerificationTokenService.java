package com.example.maintenance.service;

import com.example.maintenance.domain.model.EmailVerificationToken;
import com.example.maintenance.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * EmailVerificationTokenService - Manages email verification tokens
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationTokenService {
    
    private final EmailVerificationTokenRepository tokenRepository;
    private static final int EXPIRATION_HOURS = 24;
    
    /**
     * Create verification token for user
     */
    public String createVerificationToken(String userId, String email) {
        String token = UUID.randomUUID().toString();
        
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
            .token(token)
            .userId(userId)
            .email(email)
            .expiryDate(LocalDateTime.now().plusHours(EXPIRATION_HOURS))
            .used(false)
            .build();
        
        tokenRepository.save(verificationToken);
        
        log.info("Verification token created for user: {}", userId);
        return token;
    }
    
    /**
     * Validate verification token
     */
    public boolean validateToken(String token) {
        Optional<EmailVerificationToken> verificationToken = tokenRepository.findByToken(token);
        
        if (verificationToken.isEmpty()) {
            log.warn("Verification token not found: {}", token);
            return false;
        }
        
        EmailVerificationToken vToken = verificationToken.get();
        
        if (vToken.getUsed()) {
            log.warn("Verification token already used: {}", token);
            return false;
        }
        
        if (vToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Verification token expired: {}", token);
            return false;
        }
        
        return true;
    }
    
    /**
     * Mark token as used
     */
    public void markTokenAsUsed(String token) {
        Optional<EmailVerificationToken> verificationToken = tokenRepository.findByToken(token);
        
        if (verificationToken.isPresent()) {
            EmailVerificationToken vToken = verificationToken.get();
            vToken.setUsed(true);
            tokenRepository.save(vToken);
            log.info("Verification token marked as used");
        }
    }
    
    /**
     * Get user ID from token
     */
    public Optional<String> getUserIdFromToken(String token) {
        return tokenRepository.findByToken(token)
            .map(EmailVerificationToken::getUserId);
    }
}
