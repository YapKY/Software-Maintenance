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

@DisplayName("UserRegisterRequestDTO Tests - 100% Coverage")
class UserRegisterRequestDTOTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid UserRegisterRequestDTO with MALE gender")
    void testValidWithMaleGender() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        assertEquals(Gender.MALE, dto.getGender());
    }

    @Test
    @DisplayName("Should create valid UserRegisterRequestDTO with FEMALE gender")
    void testValidWithFemaleGender() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("jane@example.com")
                .password("Password123!")
                .name("Jane Doe")
                .custIcNo("654321-11-4321")
                .gender(Gender.FEMALE)
                .phoneNumber("013-9876543")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        assertEquals(Gender.FEMALE, dto.getGender());
    }

    @Test
    @DisplayName("Should fail when email is blank")
    void testBlankEmail() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("")
                .password("Password123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email is required")));
    }

    @Test
    @DisplayName("Should fail when email is null")
    void testNullEmail() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email(null)
                .password("Password123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when email format is invalid")
    void testInvalidEmailFormat() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("invalid-email")
                .password("Password123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid email format")));
    }

    @Test
    @DisplayName("Should fail when password doesn't have uppercase")
    void testPasswordNoUppercase() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("password123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getMessage().contains("uppercase") || 
            v.getMessage().contains("Password must contain")));
    }

    @Test
    @DisplayName("Should fail when password doesn't have lowercase")
    void testPasswordNoLowercase() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("PASSWORD123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when password doesn't have number")
    void testPasswordNoNumber() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when password doesn't have special character")
    void testPasswordNoSpecialChar() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when password is too short")
    void testPasswordTooShort() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Pass1!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when password is too long")
    void testPasswordTooLong() {
        String longPassword = "A1!" + "a".repeat(126);
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password(longPassword)
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept valid password with all special characters")
    void testValidPasswordWithVariousSpecialChars() {
        String[] specialChars = {"@", "$", "!", "%", "*", "?", "&"};
        
        for (String specialChar : specialChars) {
            UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                    .email("user@example.com")
                    .password("Password123" + specialChar)
                    .name("John Doe")
                    .custIcNo("123456-12-1234")
                    .gender(Gender.MALE)
                    .phoneNumber("012-3456789")
                    .recaptchaToken("recaptcha-token")
                    .build();
            
            Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "Should accept password with " + specialChar);
        }
    }

    @Test
    @DisplayName("Should fail when name is blank")
    void testBlankName() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when name is too short")
    void testNameTooShort() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("A")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when name is too long")
    void testNameTooLong() {
        String longName = "a".repeat(101);
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name(longName)
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept name with exactly 2 characters")
    void testNameMinLength() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("Ab")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept name with exactly 100 characters")
    void testNameMaxLength() {
        String name = "a".repeat(100);
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name(name)
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when custIcNo is blank")
    void testBlankCustIcNo() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("John Doe")
                .custIcNo("")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when custIcNo format is wrong - no hyphens")
    void testInvalidIcNoNoHyphens() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("John Doe")
                .custIcNo("123456121234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("IC Format")));
    }

    @Test
    @DisplayName("Should fail when custIcNo has wrong pattern")
    void testInvalidIcNoWrongPattern() {
        String[] invalidIcNos = {
            "12345-12-1234",   // Too few digits in first part
            "1234567-12-1234", // Too many digits in first part
            "123456-1-1234",   // Too few digits in second part
            "123456-123-1234", // Too many digits in second part
            "123456-12-123",   // Too few digits in third part
            "123456-12-12345", // Too many digits in third part
            "abcdef-12-1234",  // Letters instead of numbers
            "123456-ab-1234",  // Letters in second part
            "123456-12-abcd"   // Letters in third part
        };
        
        for (String icNo : invalidIcNos) {
            UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                    .email("user@example.com")
                    .password("Password123!")
                    .name("John Doe")
                    .custIcNo(icNo)
                    .gender(Gender.MALE)
                    .phoneNumber("012-3456789")
                    .recaptchaToken("recaptcha-token")
                    .build();
            
            Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty(), "Should fail for IC: " + icNo);
        }
    }

    @Test
    @DisplayName("Should accept valid custIcNo variations")
    void testValidIcNoVariations() {
        String[] validIcNos = {
            "123456-12-1234",
            "000000-00-0000",
            "999999-99-9999",
            "111111-11-1111"
        };
        
        for (String icNo : validIcNos) {
            UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                    .email("user@example.com")
                    .password("Password123!")
                    .name("John Doe")
                    .custIcNo(icNo)
                    .gender(Gender.MALE)
                    .phoneNumber("012-3456789")
                    .recaptchaToken("recaptcha-token")
                    .build();
            
            Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "Should accept IC: " + icNo);
        }
    }

    @Test
    @DisplayName("Should fail when gender is null")
    void testNullGender() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(null)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Gender is required")));
    }

    @Test
    @DisplayName("Should fail when phoneNumber is blank")
    void testBlankPhoneNumber() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when phoneNumber has wrong format - no hyphen")
    void testInvalidPhoneNoHyphen() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("0123456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Phone format")));
    }

    @Test
    @DisplayName("Should fail when phoneNumber has wrong pattern")
    void testInvalidPhonePattern() {
        String[] invalidPhones = {
            "12-3456789",    // Too few digits before hyphen
            "0123-3456789",  // Too many digits before hyphen
            "012-345678",    // Too few digits after hyphen (6 digits)
            "012-345678901", // Too many digits after hyphen (9 digits)
            "abc-3456789",   // Letters before hyphen
            "012-abcdefg"    // Letters after hyphen
        };
        
        for (String phone : invalidPhones) {
            UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                    .email("user@example.com")
                    .password("Password123!")
                    .name("John Doe")
                    .custIcNo("123456-12-1234")
                    .gender(Gender.MALE)
                    .phoneNumber(phone)
                    .recaptchaToken("recaptcha-token")
                    .build();
            
            Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty(), "Should fail for phone: " + phone);
        }
    }

    @Test
    @DisplayName("Should accept valid phoneNumber with 7 digits")
    void testValidPhoneWith7Digits() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept valid phoneNumber with 8 digits")
    void testValidPhoneWith8Digits() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.FEMALE)
                .phoneNumber("012-34567890")
                .recaptchaToken("recaptcha-token")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when recaptchaToken is blank")
    void testBlankRecaptchaToken() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("")
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("reCAPTCHA token is required")));
    }

    @Test
    @DisplayName("Should fail when recaptchaToken is null")
    void testNullRecaptchaToken() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken(null)
                .build();
        
        Set<ConstraintViolation<UserRegisterRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should test no-args constructor")
    void testNoArgsConstructor() {
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO();
        assertNotNull(dto);
        assertNull(dto.getEmail());
        assertNull(dto.getPassword());
        assertNull(dto.getName());
    }

    @Test
    @DisplayName("Should test all-args constructor")
    void testAllArgsConstructor() {
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO(
            "user@example.com", "Password123!", "John Doe",
            "123456-12-1234", Gender.MALE, "012-3456789", "recaptcha-token"
        );
        
        assertEquals("user@example.com", dto.getEmail());
        assertEquals("Password123!", dto.getPassword());
        assertEquals("John Doe", dto.getName());
        assertEquals("123456-12-1234", dto.getCustIcNo());
        assertEquals(Gender.MALE, dto.getGender());
        assertEquals("012-3456789", dto.getPhoneNumber());
        assertEquals("recaptcha-token", dto.getRecaptchaToken());
    }

    @Test
    @DisplayName("Should test all setters")
    void testAllSetters() {
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("TestPass123!");
        dto.setName("Test User");
        dto.setCustIcNo("111111-11-1111");
        dto.setGender(Gender.FEMALE);
        dto.setPhoneNumber("013-9999999");
        dto.setRecaptchaToken("test-token");
        
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("TestPass123!", dto.getPassword());
        assertEquals("Test User", dto.getName());
        assertEquals("111111-11-1111", dto.getCustIcNo());
        assertEquals(Gender.FEMALE, dto.getGender());
        assertEquals("013-9999999", dto.getPhoneNumber());
        assertEquals("test-token", dto.getRecaptchaToken());
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void testEqualsAndHashCode() {
        UserRegisterRequestDTO dto1 = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("token")
                .build();
        
        UserRegisterRequestDTO dto2 = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("token")
                .build();
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Should test toString")
    void testToString() {
        UserRegisterRequestDTO dto = UserRegisterRequestDTO.builder()
                .email("user@example.com")
                .password("Password123!")
                .name("John Doe")
                .custIcNo("123456-12-1234")
                .gender(Gender.MALE)
                .phoneNumber("012-3456789")
                .recaptchaToken("token")
                .build();
        
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("user@example.com"));
        assertTrue(toString.contains("John Doe"));
    }
}