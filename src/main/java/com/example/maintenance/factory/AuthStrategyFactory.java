package com.example.maintenance.factory;

import com.example.maintenance.domain.enums.AuthProvider;
import com.example.maintenance.exception.InvalidCredentialsException;
import com.example.maintenance.strategy.authentication.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * AuthStrategyFactory - FACTORY PATTERN
 * Selects the appropriate authentication strategy at runtime
 * based on the auth provider
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthStrategyFactory {
    
    private final EmailAuthStrategy emailAuthStrategy;
    private final GoogleAuthStrategy googleAuthStrategy;
    private final FacebookAuthStrategy facebookAuthStrategy;
    
    /**
     * Factory method to get authentication strategy based on provider
     */
    public AuthStrategy getAuthStrategy(AuthProvider provider) {
        log.info("Selecting auth strategy for provider: {}", provider);
        
        switch (provider) {
            case EMAIL:
                return emailAuthStrategy;
            case GOOGLE:
                return googleAuthStrategy;
            case FACEBOOK:
                return facebookAuthStrategy;
            default:
                log.error("Unsupported auth provider: {}", provider);
                throw new InvalidCredentialsException("Unsupported authentication provider");
        }
    }
}

