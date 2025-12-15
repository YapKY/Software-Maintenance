package com.example.springboot.factory;

import com.example.springboot.enums.Role;
import com.example.springboot.exception.UnauthorizedException;
import com.example.springboot.strategy.registration.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * RegisterStrategyFactory - FACTORY PATTERN
 * Selects the appropriate registration strategy at runtime
 * based on the target role
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterStrategyFactory {
    
    private final EmailRegisterStrategy emailRegisterStrategy;
    private final AdminRegisterStrategy adminRegisterStrategy;
    
    /**
     * Factory method to get registration strategy based on role
     */
    public RegisterStrategy getRegisterStrategy(Role targetRole) {
        log.info("Selecting registration strategy for role: {}", targetRole);
        
        switch (targetRole) {
            case USER:
                return emailRegisterStrategy;
            case ADMIN:
                return adminRegisterStrategy;
            case SUPERADMIN:
                log.error("Superadmin registration not allowed");
                throw new UnauthorizedException("Superadmins cannot be created via registration");
            default:
                log.error("Unsupported registration role: {}", targetRole);
                throw new UnauthorizedException("Invalid registration role");
        }
    }
}
