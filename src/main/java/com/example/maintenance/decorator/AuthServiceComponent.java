package com.example.maintenance.decorator;

import com.example.maintenance.dto.request.LoginRequestDTO;
import com.example.maintenance.dto.response.AuthResponseDTO;

/**
 * AuthServiceComponent - DECORATOR PATTERN base interface
 * Defines authentication service operations
 */
public interface AuthServiceComponent {
    
    AuthResponseDTO performAuthentication(LoginRequestDTO loginRequest);
}