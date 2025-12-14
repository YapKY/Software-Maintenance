package com.example.maintenance.service;

import com.example.maintenance.domain.enums.Role;
import com.example.maintenance.dto.request.AdminRegisterRequestDTO;
import com.example.maintenance.dto.request.UserRegisterRequestDTO;
import com.example.maintenance.dto.response.AuthResponseDTO;
import com.example.maintenance.factory.RegisterStrategyFactory;
import com.example.maintenance.strategy.registration.RegisterStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * RegistrationExecutionService - Orchestrates registration using Strategy + Factory patterns
 * Controller → Service → Factory → Strategy → Adapter → Repository
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationExecutionService {
    
    private final RegisterStrategyFactory registerStrategyFactory; // Factory Pattern
    
    /**
     * Register user using Strategy Pattern
     */
    public AuthResponseDTO registerUser(UserRegisterRequestDTO request) {
        try {
            log.info("Executing user registration");
            
            // Get strategy from factory
            RegisterStrategy strategy = registerStrategyFactory.getRegisterStrategy(Role.USER);
            
            // Execute strategy
            return strategy.register(request, request.getRecaptchaToken());
            
        } catch (Exception e) {
            log.error("User registration execution failed: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Register admin using Strategy Pattern (Superadmin only)
     */
    public AuthResponseDTO registerAdmin(AdminRegisterRequestDTO request) {
        try {
            log.info("Executing admin registration");
            
            // Get strategy from factory
            RegisterStrategy strategy = registerStrategyFactory.getRegisterStrategy(Role.ADMIN);
            
            // Execute strategy (will verify caller is Superadmin)
            return strategy.register(request, null);
            
        } catch (Exception e) {
            log.error("Admin registration execution failed: {}", e.getMessage());
            throw e;
        }
    }
}
