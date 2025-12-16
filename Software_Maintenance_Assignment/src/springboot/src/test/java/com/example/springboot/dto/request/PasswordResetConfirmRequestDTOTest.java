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

@DisplayName("PasswordResetConfirmRequestDTO Tests")
class PasswordResetConfirmRequestDTOTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid PasswordResetConfirmRequestDTO")
    void testValid() {
        PasswordResetConfirmRequestDTO dto = PasswordResetConfirmRequestDTO.builder()
                .token("valid-token-123")
                .newPassword("NewPass123!")
                .confirmPassword("NewPass123!")
                .build();
        Set<ConstraintViolation<PasswordResetConfirmRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when token is blank")
    void testBlankToken() {
        PasswordResetConfirmRequestDTO dto = PasswordResetConfirmRequestDTO.builder()
                .token("")
                .newPassword("NewPass123!")
                .confirmPassword("NewPass123!")
                .build();
        Set<ConstraintViolation<PasswordResetConfirmRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Token is required")));
    }

    @Test
    @DisplayName("Should fail when newPassword is blank")
    void testBlankNewPassword() {
        PasswordResetConfirmRequestDTO dto = PasswordResetConfirmRequestDTO.builder()
                .token("valid-token")
                .newPassword("")
                .confirmPassword("NewPass123!")
                .build();
        Set<ConstraintViolation<PasswordResetConfirmRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("New password is required")));
    }

    @Test
    @DisplayName("Should fail when confirmPassword is blank")
    void testBlankConfirmPassword() {
        PasswordResetConfirmRequestDTO dto = PasswordResetConfirmRequestDTO.builder()
                .token("valid-token")
                .newPassword("NewPass123!")
                .confirmPassword("")
                .build();
        Set<ConstraintViolation<PasswordResetConfirmRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Confirm password is required")));
    }

    @Test
    @DisplayName("Should test all constructors")
    void testConstructors() {
        PasswordResetConfirmRequestDTO dto1 = new PasswordResetConfirmRequestDTO();
        dto1.setToken("token");
        dto1.setNewPassword("Pass123!");
        dto1.setConfirmPassword("Pass123!");

        PasswordResetConfirmRequestDTO dto2 = new PasswordResetConfirmRequestDTO("token", "Pass123!", "Pass123!");
        
        assertEquals(dto1, dto2);
        assertNotNull(dto1.toString());
        assertEquals(dto1.getToken(), dto2.getToken());
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}