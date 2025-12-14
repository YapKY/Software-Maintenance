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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequestDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must contain uppercase, lowercase, number, and special character"
    )
    private String password; // Kept as 'password' in JSON request for frontend compatibility, maps to custPassword
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name; // Renamed from fullName
    
    @NotBlank(message = "IC Number is required")
    @Pattern(regexp = "^\\d{6}-\\d{2}-\\d{4}$", message = "IC Format: XXXXXX-XX-XXXX")
    private String custIcNo; // New Field

    @NotNull(message = "Gender is required")
    private Gender gender; // New Field
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{3}-\\d{7,8}$", message = "Phone format: XXX-XXXXXXXX")
    private String phoneNumber;
    
    @NotBlank(message = "reCAPTCHA token is required")
    private String recaptchaToken;
}