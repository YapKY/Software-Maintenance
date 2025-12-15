package com.example.springboot.dto.request;

import com.example.springboot.enums.Gender;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminRegisterRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRequest() {
        AdminRegisterRequestDTO dto = AdminRegisterRequestDTO.builder()
                .email("admin@test.com")
                .staffPass("password123")
                .name("Admin Name")
                .position("Manager")
                .gender(Gender.MALE)
                .mfaEnabled(true)
                .build();

        Set<ConstraintViolation<AdminRegisterRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid DTO should have no violations");
    }

    @Test
    void testInvalidEmail() {
        AdminRegisterRequestDTO dto = AdminRegisterRequestDTO.builder()
                .email("invalid-email") // Invalid format
                .staffPass("password123")
                .name("Name")
                .position("Manager")
                .gender(Gender.FEMALE)
                .build();

        Set<ConstraintViolation<AdminRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid email format")));
    }

    @Test
    void testInvalidPasswordShort() {
        AdminRegisterRequestDTO dto = AdminRegisterRequestDTO.builder()
                .email("test@test.com")
                .staffPass("short") // Too short
                .name("Name")
                .position("Manager")
                .gender(Gender.FEMALE)
                .build();

        Set<ConstraintViolation<AdminRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must be")));
    }

    @Test
    void testInvalidPositionWithDigits() {
        AdminRegisterRequestDTO dto = AdminRegisterRequestDTO.builder()
                .email("test@test.com")
                .staffPass("password123")
                .name("Name")
                .position("Manager123") // Contains digits
                .gender(Gender.FEMALE)
                .build();

        Set<ConstraintViolation<AdminRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Position must not contain digits")));
    }
}