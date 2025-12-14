package com.example.maintenance.strategy.registration;

import com.example.maintenance.adapter.firebase.FirebaseAdapter;
import com.example.maintenance.adapter.recaptcha.RecaptchaAdapter;
import com.example.maintenance.domain.model.User;
import com.example.maintenance.domain.enums.AuthProvider;
import com.example.maintenance.domain.enums.Role;
import com.example.maintenance.dto.request.UserRegisterRequestDTO;
import com.example.maintenance.dto.response.AuthResponseDTO;
import com.example.maintenance.exception.InvalidCredentialsException;
import com.example.maintenance.repository.UserRepository;
import com.example.maintenance.service.EmailService;
import com.example.maintenance.service.EmailVerificationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * EmailRegisterStrategy - UPDATED with real email verification
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailRegisterStrategy implements RegisterStrategy {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecaptchaAdapter recaptchaAdapter;
    private final EmailService emailService;  // NEW
    private final EmailVerificationTokenService tokenService;  // NEW
    private final FirebaseAdapter firebaseAdapter;
    
    @Override
    public AuthResponseDTO register(Object registrationData, String recaptchaToken) {
        try {
            // Validate reCAPTCHA
            if (!recaptchaAdapter.validateRecaptcha(recaptchaToken)) {
                throw new InvalidCredentialsException("reCAPTCHA validation failed");
            }
            
            UserRegisterRequestDTO request = (UserRegisterRequestDTO) registrationData;
            log.info("User registration attempt for: {}", request.getEmail());
            
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new InvalidCredentialsException("Email already registered");
            }
            
            // Check if phone number already exists
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new InvalidCredentialsException("Phone number already registered");
            }

            String firebaseUid;

            try {
                firebaseUid = firebaseAdapter.createUser(
                    request.getEmail(),
                    request.getPassword(),
                    request.getName()
                );
            } catch (Exception e) {
                log.error("Failed to create user in Firebase: {}", e.getMessage());
                // Handle case where user might exist in Firebase but not in DB (inconsistent state)
                // or other Firebase errors (weak password, invalid email)
                throw new InvalidCredentialsException("Registration failed: " + e.getMessage());
            }

            // Create new user (emailVerified = false initially)
            User newUser = User.builder()
                .custId(firebaseUid)              // Changed
                .email(request.getEmail())
                .custPassword(passwordEncoder.encode(request.getPassword())) // Changed
                .name(request.getName())          // Changed
                .custIcNo(request.getCustIcNo())  // New
                .gender(request.getGender())      // New
                .phoneNumber(request.getPhoneNumber())
                .authProvider(AuthProvider.EMAIL)
                .role(Role.USER)
                .emailVerified(false)
                .mfaEnabled(false)
                .accountLocked(false)
                .failedLoginAttempts(0)
                .build();
                
            User savedUser = userRepository.save(newUser);
            
            // Generate verification token
            String verificationToken = tokenService.createVerificationToken(
                savedUser.getCustId(), 
                savedUser.getEmail()
            );
            
            // Send verification email (async)
            emailService.sendVerificationEmail(savedUser, verificationToken);
            
            log.info("User registered successfully: {}", savedUser.getEmail());
            
            return AuthResponseDTO.builder()
                .success(true)
                .message("Registration successful! Please check your email to verify your account.")
                .requiresMfa(false)
                .build();
                
        } catch (ClassCastException e) {
            log.error("Invalid registration data type");
            throw new InvalidCredentialsException("Invalid registration format");
        } catch (InvalidCredentialsException e) {
            log.error("Registration validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("User registration failed: {}", e.getMessage());
            throw new InvalidCredentialsException("Registration failed: " + e.getMessage());
        }
    }
    
    @Override
    public String getStrategyName() {
        return "EMAIL_USER_REGISTRATION";
    }
}