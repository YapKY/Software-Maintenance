package com.example.springboot.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginRequestDTO Tests")
class LoginRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid LoginRequestDTO")
    void testValidLoginRequestDTO() {
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .recaptchaToken("valid-token")
                .mfaCode("123456") // Optional but valid
                .build();

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when mandatory fields are missing")
    void testMissingFields() {
        LoginRequestDTO dto = LoginRequestDTO.builder().build(); // Empty

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("reCAPTCHA token is required")));
    }

    @Test
    @DisplayName("Should validate Email Format")
    void testInvalidEmail() {
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("not-an-email")
                .password("Pass123!")
                .recaptchaToken("token")
                .build();
        
        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid email format")));
    }
}