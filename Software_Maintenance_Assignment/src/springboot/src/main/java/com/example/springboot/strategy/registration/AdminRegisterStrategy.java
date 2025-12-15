package com.example.springboot.strategy.registration;

import com.example.springboot.adapter.recaptcha.RecaptchaAdapter;
import com.example.springboot.model.Admin;
import com.example.springboot.enums.Role;
import com.example.springboot.dto.request.AdminRegisterRequestDTO;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.exception.InvalidCredentialsException;
import com.example.springboot.exception.UnauthorizedException;
import com.example.springboot.repository.AdminRepository;
import com.example.springboot.service.MFAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * AdminRegisterStrategy - Handles Staff creation by Superadmin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminRegisterStrategy implements RegisterStrategy {
    
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final MFAService mfaService;
    private final RecaptchaAdapter recaptchaAdapter;
    
    @Override
    public AuthResponseDTO register(Object registrationData, String recaptchaToken) {
        try {
            // Verify caller is Superadmin
            String currentUserRole = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .iterator()
                .next()
                .getAuthority();
                
            if (!"ROLE_SUPERADMIN".equals(currentUserRole)) {
                log.warn("Unauthorized staff creation attempt by: {}", currentUserRole);
                throw new UnauthorizedException("Only Superadmins can create Staff accounts");
            }
            
            // Get Superadmin ID from authentication
            String superadminId = SecurityContextHolder.getContext().getAuthentication().getName();
            
            AdminRegisterRequestDTO request = (AdminRegisterRequestDTO) registrationData;
            log.info("Staff creation attempt by Superadmin {}: {}", superadminId, request.getEmail());
            
            // Check if email already exists
            if (adminRepository.existsByEmail(request.getEmail())) {
                throw new InvalidCredentialsException("Email already registered");
            }
            
            // Create new staff
            Admin newAdmin = Admin.builder()
                .email(request.getEmail())
                .staffPass(passwordEncoder.encode(request.getStaffPass())) // Changed to staffPass
                .name(request.getName())           // Changed to name
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())       // New
                .position(request.getPosition())   // New
                .role(Role.ADMIN)
                .mfaEnabled(false) 
                .accountLocked(false)
                .failedLoginAttempts(0)
                .createdBy(superadminId)
                .build();
                
            Admin savedAdmin = adminRepository.save(newAdmin);
            
            log.info("Staff created successfully by Superadmin {}: {}", 
                superadminId, savedAdmin.getEmail());
            
            return AuthResponseDTO.builder()
                .success(true)
                .message("Staff account created successfully.")
                .requiresMfa(false)
                .build();
                
        } catch (ClassCastException e) {
            log.error("Invalid registration data type");
            throw new InvalidCredentialsException("Invalid registration format");
        } catch (Exception e) {
            log.error("Staff creation failed: {}", e.getMessage());
            throw new InvalidCredentialsException("Staff creation failed: " + e.getMessage());
        }
    }
    
    @Override
    public String getStrategyName() {
        return "SUPERADMIN_ADMIN_CREATION";
    }
}