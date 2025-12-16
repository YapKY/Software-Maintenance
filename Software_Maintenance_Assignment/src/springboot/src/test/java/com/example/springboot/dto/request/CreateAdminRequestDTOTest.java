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

@DisplayName("CreateAdminRequestDTO Tests")
class CreateAdminRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid CreateAdminRequestDTO")
    void testValidCreateAdminRequestDTO() {
        CreateAdminRequestDTO dto = CreateAdminRequestDTO.builder()
                .email("admin@example.com")
                .password("Password123!")
                .fullName("John Doe")
                .phoneNumber("012-3456789")
                .mfaEnabled(true)
                .build();

        Set<ConstraintViolation<CreateAdminRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid DTO should have no violations");
    }

    @Test
    @DisplayName("Should fail when email is blank")
    void testBlankEmail() {
        CreateAdminRequestDTO dto = CreateAdminRequestDTO.builder()
                .email("")
                .password("Password123!")
                .fullName("John Doe")
                .mfaEnabled(true)
                .build();

        Set<ConstraintViolation<CreateAdminRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email is required")));
    }

    @Test
    @DisplayName("Should fail when email format is invalid")
    void testInvalidEmailFormat() {
        CreateAdminRequestDTO dto = CreateAdminRequestDTO.builder()
                .email("invalid-email")
                .password("Password123!")
                .fullName("John Doe")
                .mfaEnabled(true)
                .build();

        Set<ConstraintViolation<CreateAdminRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid email format")));
    }

    @Test
    @DisplayName("Should fail when password is invalid (too short/long)")
    void testInvalidPassword() {
        // Too short
        CreateAdminRequestDTO dtoShort = CreateAdminRequestDTO.builder()
                .email("admin@example.com")
                .password("Short1!")
                .fullName("John Doe")
                .build();
        
        Set<ConstraintViolation<CreateAdminRequestDTO>> violationsShort = validator.validate(dtoShort);
        assertFalse(violationsShort.isEmpty());

        // Too long (129 chars)
        String longPassword = "a".repeat(129);
        CreateAdminRequestDTO dtoLong = CreateAdminRequestDTO.builder()
                .email("admin@example.com")
                .password(longPassword)
                .fullName("John Doe")
                .build();
        
        Set<ConstraintViolation<CreateAdminRequestDTO>> violationsLong = validator.validate(dtoLong);
        assertFalse(violationsLong.isEmpty());
    }

    @Test
    @DisplayName("Should test all constructors, getters, setters, and toString")
    void testBoilerplateCode() {
        // No Args Constructor & Setters
        CreateAdminRequestDTO dto = new CreateAdminRequestDTO();
        dto.setEmail("admin@example.com");
        dto.setPassword("Password123!");
        dto.setFullName("John Doe");
        dto.setPhoneNumber("012-3456789");
        dto.setMfaEnabled(false);

        assertEquals("admin@example.com", dto.getEmail());
        assertEquals("Password123!", dto.getPassword());
        assertEquals("John Doe", dto.getFullName());
        assertFalse(dto.getMfaEnabled());

        // All Args Constructor
        CreateAdminRequestDTO dto2 = new CreateAdminRequestDTO(
            "admin@example.com", "Password123!", "John Doe", "012-3456789", true
        );
        assertEquals(dto2.getEmail(), dto.getEmail());

        // Equals & HashCode
        CreateAdminRequestDTO dto3 = CreateAdminRequestDTO.builder()
                .email("admin@example.com")
                .password("Password123!")
                .fullName("John Doe")
                .phoneNumber("012-3456789")
                .mfaEnabled(false)
                .build();
        
        assertEquals(dto, dto3);
        assertEquals(dto.hashCode(), dto3.hashCode());

        // ToString
        assertNotNull(dto.toString());
    }
}