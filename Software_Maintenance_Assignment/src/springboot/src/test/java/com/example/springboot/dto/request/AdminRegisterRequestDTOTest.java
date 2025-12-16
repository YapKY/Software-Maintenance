package com.example.springboot.dto.request;

import com.example.springboot.enums.Gender;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AdminRegisterRequestDTO Tests")
class AdminRegisterRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid AdminRegisterRequestDTO")
    void testValidAdminRegisterRequestDTO() {
        AdminRegisterRequestDTO dto = AdminRegisterRequestDTO.builder()
                .email("admin@example.com")
                .staffPass("Password123!")
                .name("John Doe")
                .phoneNumber("012-3456789")
                .gender(Gender.MALE)
                .position("Manager")
                .mfaEnabled(true)
                .build();

        Set<ConstraintViolation<AdminRegisterRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation on invalid inputs")
    void testInvalidInputs() {
        AdminRegisterRequestDTO dto = AdminRegisterRequestDTO.builder()
                .email("bad-email")          // Invalid email
                .staffPass("short")          // Password too short
                .name("")                    // Empty name
                .position("Manager123")      // Position with digits (pattern violation)
                .gender(null)                // Null gender
                .build();

        Set<ConstraintViolation<AdminRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        
        // Verify specific constraints
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("staffPass")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("position")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("gender")));
    }

    @Test
    @DisplayName("Should test Builder and Boilerplate")
    void testBoilerplate() {
        AdminRegisterRequestDTO dto = new AdminRegisterRequestDTO(
            "email@test.com", "Pass123!", "Name", "0123", Gender.FEMALE, "Admin", false
        );
        
        assertEquals("email@test.com", dto.getEmail());
        assertEquals("Admin", dto.getPosition());
        assertEquals(Gender.FEMALE, dto.getGender());
        assertFalse(dto.getMfaEnabled());
        
        assertNotNull(dto.toString());
    }
}