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

class UserRegisterRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private UserRegisterRequestDTO.UserRegisterRequestDTOBuilder getValidBuilder() {
        return UserRegisterRequestDTO.builder()
                .email("test@test.com")
                .password("Password@123")
                .name("Test User")
                .custIcNo("900101-14-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-1234567")
                .recaptchaToken("token");
    }

    @Test
    void testValidRequest() {
        UserRegisterRequestDTO dto = getValidBuilder().build();
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidPasswordComplexity() {
        // Missing special char and uppercase
        UserRegisterRequestDTO dto = getValidBuilder()
                .password("password123")
                .build();

        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must contain")));
    }

    @Test
    void testInvalidIcFormat() {
        // Wrong format
        UserRegisterRequestDTO dto = getValidBuilder()
                .custIcNo("900101141234") // Missing dashes
                .build();

        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("IC Format")));
    }

    @Test
    void testInvalidPhoneFormat() {
        // Wrong format
        UserRegisterRequestDTO dto = getValidBuilder()
                .phoneNumber("0123456789") // Missing dash
                .build();

        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Phone format")));
    }
}