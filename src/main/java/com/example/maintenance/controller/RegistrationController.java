package com.example.maintenance.controller;

import com.example.maintenance.domain.enums.Role;
import com.example.maintenance.dto.request.AdminRegisterRequestDTO;
import com.example.maintenance.dto.request.UserRegisterRequestDTO;
import com.example.maintenance.dto.response.AuthResponseDTO;
import com.example.maintenance.service.RegistrationExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * RegistrationController - Handles registration requests
 * Uses Strategy + Factory patterns via RegistrationExecutionService
 */
@Slf4j
@RestController
@RequestMapping("/api/register")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@Validated
public class RegistrationController {
    
    private final RegistrationExecutionService registrationExecutionService;
    
    /**
     * User Self-Registration - Uses EmailRegisterStrategy
     * PUBLIC endpoint - anyone can register as User
     */
    @PostMapping("/user")
    public ResponseEntity<AuthResponseDTO> registerUser(
        @Valid @RequestBody UserRegisterRequestDTO request
    ) {
        try {
            log.info("User registration request for: {}", request.getEmail());
            AuthResponseDTO response = registrationExecutionService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("User registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AuthResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
    
    /**
     * Admin Registration - Uses AdminRegisterStrategy
     * PROTECTED endpoint - only Superadmins can create Admins
     */
    @PostMapping("/admin")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<AuthResponseDTO> registerAdmin(
        @Valid @RequestBody AdminRegisterRequestDTO request
    ) {
        try {
            log.info("Admin registration request for: {}", request.getEmail());
            AuthResponseDTO response = registrationExecutionService.registerAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Admin registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AuthResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
}
