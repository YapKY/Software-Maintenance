package com.example.springboot.decorator;

import com.example.springboot.model.*;
import com.example.springboot.enums.Role;
import com.example.springboot.dto.request.LoginRequestDTO;
import com.example.springboot.dto.response.AuthResponseDTO;
import com.example.springboot.dto.response.JWTResponseDTO;
import com.example.springboot.exception.InvalidCredentialsException;
import com.example.springboot.exception.RateLimitExceededException;
import com.example.springboot.repository.*;
import com.example.springboot.security.jwt.JwtTokenProvider;
import com.example.springboot.security.ratelimit.RateLimiter;
import com.example.springboot.service.MFAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * AuthServiceImpl - Core authentication with rate limiting integration
 */
@Slf4j
@Service("baseAuthService")
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthServiceComponent {
    
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final SuperadminRepository superadminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MFAService mfaService;
    private final RateLimiter rateLimiter;
    
    @Override
    public AuthResponseDTO performAuthentication(LoginRequestDTO loginRequest) {
        String email = loginRequest.getEmail();
        log.info("Performing authentication for: {}", email);
        
        // Check rate limit BEFORE authentication
        if (rateLimiter.isBlocked(email)) {
            long remainingTime = rateLimiter.getBlockTimeRemaining(email);
            throw new RateLimitExceededException(
                String.format("Too many failed attempts. Please try again in %d seconds.", remainingTime)
            );
        }
        
        // Try to find user in all three tables
        Object userEntity = findUserByEmail(email);
        
        if (userEntity == null) {
            rateLimiter.recordFailedAttempt(email);
            log.warn("User not found: {}", email);
            throw new InvalidCredentialsException("Invalid email or password");
        }
        
        // Validate password and generate response based on user type
        try {
            AuthResponseDTO response;
            
            if (userEntity instanceof User) {
                response = authenticateUser((User) userEntity, loginRequest);
            } else if (userEntity instanceof Admin) {
                response = authenticateAdmin((Admin) userEntity, loginRequest);
            } else if (userEntity instanceof Superadmin) {
                response = authenticateSuperadmin((Superadmin) userEntity, loginRequest);
            } else {
                rateLimiter.recordFailedAttempt(email);
                throw new InvalidCredentialsException("Invalid email or password");
            }
            
            // Clear rate limit on successful authentication
            if (response.getSuccess() && response.getTokens() != null) {
                rateLimiter.clearAttempts(email);
            }
            
            return response;
            
        } catch (InvalidCredentialsException e) {
            rateLimiter.recordFailedAttempt(email);
            throw e;
        }
    }
    
    private Object findUserByEmail(String email) {
        // Check User table
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) return user;
        
        // Check Admin table
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) return admin;
        
        // Check Superadmin table
        return superadminRepository.findByEmail(email).orElse(null);
    }
    
    private AuthResponseDTO authenticateUser(User user, LoginRequestDTO request) {
        // Check account status
        if (user.getAccountLocked()) {
            throw new InvalidCredentialsException("Account is locked");
        }
        
        if (!user.getEmailVerified()) {
            throw new InvalidCredentialsException("Email not verified");
        }
        
        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getCustPassword())) {
            handleFailedLogin(user);
            throw new InvalidCredentialsException("Invalid email or password");
        }
        
        // Reset failed attempts on successful password validation
        user.setFailedLoginAttempts(0);
        
        // Check MFA
        if (user.getMfaEnabled()) {
            if (request.getMfaCode() == null) {
                // MFA required but not provided
                String mfaSessionToken = jwtTokenProvider.generateMFASessionToken(
                    user.getCustId(), user.getEmail(), Role.USER
                );
                return AuthResponseDTO.builder()
                    .success(false)
                    .message("MFA code required")
                    .requiresMfa(true)
                    .mfaSessionToken(mfaSessionToken)
                    .email(user.getEmail()) // [FIX] Added email for consistency
                    .build();
            } else {
                // Validate MFA code
                if (!mfaService.validateMFACode(user.getCustId(), Role.USER, request.getMfaCode())) {
                    throw new InvalidCredentialsException("Invalid MFA code");
                }
            }
        }
        
        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Generate tokens
        JWTResponseDTO tokens = jwtTokenProvider.generateTokens(
            user.getCustId(), user.getEmail(), Role.USER
        );
        
        return AuthResponseDTO.builder()
            .success(true)
            .message("Login successful")
            .tokens(tokens)
            .requiresMfa(false)
            .build();
    }
    
    private AuthResponseDTO authenticateAdmin(Admin admin, LoginRequestDTO request) {
        if (admin.getAccountLocked()) {
            throw new InvalidCredentialsException("Account is locked");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), admin.getStaffPass())) {
            handleFailedLoginAdmin(admin);
            throw new InvalidCredentialsException("Invalid email or password");
        }
        
        admin.setFailedLoginAttempts(0);
        
        // MFA handling for Admin
        if (admin.getMfaEnabled() && request.getMfaCode() == null) {
            String mfaSessionToken = jwtTokenProvider.generateMFASessionToken(
                admin.getStaffId(), admin.getEmail(), Role.ADMIN
            );
            return AuthResponseDTO.builder()
                .success(false)
                .message("MFA code required")
                .requiresMfa(true)
                .mfaSessionToken(mfaSessionToken)
                .email(admin.getEmail()) // [FIX] Added email for consistency
                .build();
        } else if (admin.getMfaEnabled()) {
            if (!mfaService.validateMFACode(admin.getStaffId(), Role.ADMIN, request.getMfaCode())) {
                throw new InvalidCredentialsException("Invalid MFA code");
            }
        }
        
        admin.setLastLoginAt(LocalDateTime.now());
        adminRepository.save(admin);
        
        JWTResponseDTO tokens = jwtTokenProvider.generateTokens(
            admin.getStaffId(), admin.getEmail(), Role.ADMIN
        );
        
        return AuthResponseDTO.builder()
            .success(true)
            .message("Login successful")
            .tokens(tokens)
            .requiresMfa(false)
            .build();
    }
    
    private AuthResponseDTO authenticateSuperadmin(Superadmin superadmin, LoginRequestDTO request) {
        if (superadmin.getAccountLocked()) {
            throw new InvalidCredentialsException("Account is locked");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), superadmin.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        
        // Superadmin ALWAYS requires MFA
        if (request.getMfaCode() == null) {
            String mfaSessionToken = jwtTokenProvider.generateMFASessionToken(
                superadmin.getId(), superadmin.getEmail(), Role.SUPERADMIN
            );
            return AuthResponseDTO.builder()
                .success(false)
                .message("MFA code required")
                .requiresMfa(true)
                .mfaSessionToken(mfaSessionToken)
                .email(superadmin.getEmail()) // [FIX] Added email for consistency
                .build();
        }
        
        if (!mfaService.validateMFACode(superadmin.getId(), Role.SUPERADMIN, request.getMfaCode())) {
            throw new InvalidCredentialsException("Invalid MFA code");
        }
        
        superadmin.setLastLoginAt(LocalDateTime.now());
        superadminRepository.save(superadmin);
        
        JWTResponseDTO tokens = jwtTokenProvider.generateTokens(
            superadmin.getId(), superadmin.getEmail(), Role.SUPERADMIN
        );
        
        return AuthResponseDTO.builder()
            .success(true)
            .message("Login successful")
            .tokens(tokens)
            .requiresMfa(false)
            .build();
    }
    
    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        
        if (attempts >= 5) {
            user.setAccountLocked(true);
            log.warn("User account locked due to failed attempts: {}", user.getEmail());
        }
        
        userRepository.save(user);
    }
    
    private void handleFailedLoginAdmin(Admin admin) {
        int attempts = admin.getFailedLoginAttempts() + 1;
        admin.setFailedLoginAttempts(attempts);
        
        if (attempts >= 5) {
            admin.setAccountLocked(true);
            log.warn("Admin account locked due to failed attempts: {}", admin.getEmail());
        }
        
        adminRepository.save(admin);
    }
}