package com.example.springboot.dto.request;

import com.example.springboot.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin (Staff) Registration Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminRegisterRequestDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String staffPass; // Renamed from password
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name; // Renamed from fullName
    
    private String phoneNumber;
    
    @NotNull(message = "Gender is required")
    private Gender gender;
    
    @NotBlank(message = "Position is required")
    @Pattern(regexp = "^[^0-9]+$", message = "Position must not contain digits")
    private String position;
    
    @NotNull(message = "MFA enabled flag is required")
    @Builder.Default
    private Boolean mfaEnabled = true;
}