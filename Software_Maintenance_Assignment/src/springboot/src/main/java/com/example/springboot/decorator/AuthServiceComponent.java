package com.example.springboot.decorator;

import com.example.springboot.dto.request.LoginRequestDTO;
import com.example.springboot.dto.response.AuthResponseDTO;

/**
 * AuthServiceComponent - DECORATOR PATTERN base interface
 * Defines authentication service operations
 */
public interface AuthServiceComponent {
    
    AuthResponseDTO performAuthentication(LoginRequestDTO loginRequest);
}