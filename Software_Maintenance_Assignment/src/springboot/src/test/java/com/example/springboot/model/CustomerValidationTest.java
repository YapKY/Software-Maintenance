package com.example.springboot.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for Customer Model Validation
 * Tests validation methods for Customer entities
 */
@DisplayName("Customer Model Validation Tests")
class CustomerValidationTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
    }

    // ==================== PASSWORD VALIDATION TESTS ====================

    @Test
    @DisplayName("Should validate password with more than 8 characters")
    void testValidPasswordSuccess() {
        // Act
        boolean result = customer.getValidPassword("SecurePass123");

        // Assert
        assertTrue(result);
        assertEquals("SecurePass123", customer.getCustPassword());
    }

    @Test
    @DisplayName("Should validate password with exactly 9 characters")
    void testValidPasswordMinimumLength() {
        // Act
        boolean result = customer.getValidPassword("12345678P");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should reject password with exactly 8 characters")
    void testInvalidPasswordExactlyEight() {
        // Act
        boolean result = customer.getValidPassword("12345678");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject password with less than 8 characters")
    void testInvalidPasswordTooShort() {
        // Act
        boolean result = customer.getValidPassword("Short");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject empty password")
    void testInvalidPasswordEmpty() {
        // Act
        boolean result = customer.getValidPassword("");

        // Assert
        assertFalse(result);
    }

    // ==================== IC NUMBER VALIDATION TESTS ====================

    @Test
    @DisplayName("Should validate IC number with correct format")
    void testValidIcNumberSuccess() {
        // Act
        boolean result = customer.getValidICNumber("123456-12-1234");

        // Assert
        assertTrue(result);
        assertEquals("123456-12-1234", customer.getCustIcNo());
    }

    @Test
    @DisplayName("Should validate IC number with different digits")
    void testValidIcNumberDifferentDigits() {
        // Act
        boolean result = customer.getValidICNumber("654321-99-9999");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should reject IC number without dashes")
    void testInvalidIcNumberNoDashes() {
        // Act
        boolean result = customer.getValidICNumber("123456121234");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject IC number with wrong format")
    void testInvalidIcNumberWrongFormat() {
        // Act
        boolean result = customer.getValidICNumber("XXXXXX-XX-XXXX");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject IC number with missing digits")
    void testInvalidIcNumberMissingDigits() {
        // Act
        boolean result = customer.getValidICNumber("12345-1-123");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject empty IC number")
    void testInvalidIcNumberEmpty() {
        // Act
        boolean result = customer.getValidICNumber("");

        // Assert
        assertFalse(result);
    }

    // ==================== INHERITED PERSON VALIDATION TESTS ====================

    @Test
    @DisplayName("Should validate name with only letters and spaces")
    void testValidNameSuccess() {
        // Act
        boolean result = customer.getValidName("John Doe");

        // Assert
        assertTrue(result);
        assertEquals("John Doe", customer.getName());
    }

    @Test
    @DisplayName("Should validate single word name")
    void testValidNameSingleWord() {
        // Act
        boolean result = customer.getValidName("Alice");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should reject name with numbers")
    void testInvalidNameWithNumbers() {
        // Act
        boolean result = customer.getValidName("John123");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject name with special characters")
    void testInvalidNameWithSpecialChars() {
        // Act
        boolean result = customer.getValidName("John@Doe");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject empty name")
    void testInvalidNameEmpty() {
        // Act
        boolean result = customer.getValidName("");

        // Assert
        assertFalse(result);
    }

    // ==================== EMAIL VALIDATION TESTS ====================

    @Test
    @DisplayName("Should validate email with correct format")
    void testValidEmailSuccess() {
        // Act
        boolean result = customer.getValidEmail("john@example.com");

        // Assert
        assertTrue(result);
        assertEquals("john@example.com", customer.getEmail());
    }

    @Test
    @DisplayName("Should validate email with numbers and special characters")
    void testValidEmailWithNumbers() {
        // Act
        boolean result = customer.getValidEmail("john.doe+test@domain.com");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should reject email without @ symbol")
    void testInvalidEmailNoAtSymbol() {
        // Act
        boolean result = customer.getValidEmail("johnexample.com");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject email without domain")
    void testInvalidEmailNoDomain() {
        // Act
        boolean result = customer.getValidEmail("john@");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject email with spaces")
    void testInvalidEmailWithSpaces() {
        // Act
        boolean result = customer.getValidEmail("john @example.com");

        // Assert
        assertFalse(result);
    }

    // ==================== GENDER VALIDATION TESTS ====================

    @Test
    @DisplayName("Should validate gender as MALE")
    void testValidGenderMale() {
        // Act
        boolean result = customer.getValidGender("MALE");

        // Assert
        assertTrue(result);
        assertEquals("MALE", customer.getGender());
    }

    @Test
    @DisplayName("Should validate gender as FEMALE")
    void testValidGenderFemale() {
        // Act
        boolean result = customer.getValidGender("FEMALE");

        // Assert
        assertTrue(result);
        assertEquals("FEMALE", customer.getGender());
    }

    @Test
    @DisplayName("Should validate lowercase gender male")
    void testValidGenderLowercaseMale() {
        // Act
        boolean result = customer.getValidGender("male");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should validate mixed case gender female")
    void testValidGenderMixedCaseFemale() {
        // Act
        boolean result = customer.getValidGender("Female");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should reject invalid gender")
    void testInvalidGender() {
        // Act
        boolean result = customer.getValidGender("Other");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject empty gender")
    void testInvalidGenderEmpty() {
        // Act
        boolean result = customer.getValidGender("");

        // Assert
        assertFalse(result);
    }

    // ==================== PHONE NUMBER VALIDATION TESTS ====================

    @Test
    @DisplayName("Should validate phone number with correct format")
    void testValidPhoneNumberSuccess() {
        // Act
        boolean result = customer.getValidPhoneNumber("012-34567890");

        // Assert
        assertTrue(result);
        assertEquals("012-34567890", customer.getPhoneNumber());
    }

    @Test
    @DisplayName("Should validate phone number with 7 digits after dash")
    void testValidPhoneNumber7Digits() {
        // Act
        boolean result = customer.getValidPhoneNumber("012-1234567");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should reject phone number without dash")
    void testInvalidPhoneNumberNoDash() {
        // Act
        boolean result = customer.getValidPhoneNumber("01234567890");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject phone number with wrong prefix format")
    void testInvalidPhoneNumberWrongPrefix() {
        // Act
        boolean result = customer.getValidPhoneNumber("01-34567890");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject phone number with too few digits")
    void testInvalidPhoneNumberTooFewDigits() {
        // Act
        boolean result = customer.getValidPhoneNumber("012-123456");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject empty phone number")
    void testInvalidPhoneNumberEmpty() {
        // Act
        boolean result = customer.getValidPhoneNumber("");

        // Assert
        assertFalse(result);
    }

    // ==================== OBJECT EQUALITY TESTS ====================

    @Test
    @DisplayName("Should determine two customers with same data are equal")
    void testCustomerEqualitySuccess() {
        // Arrange
        Customer customer1 = new Customer();
        customer1.setName("John Doe");
        customer1.setEmail("john@example.com");
        customer1.setPhoneNumber("012-12345678");
        customer1.setGender("Male");

        Customer customer2 = new Customer();
        customer2.setName("John Doe");
        customer2.setEmail("john@example.com");
        customer2.setPhoneNumber("012-12345678");
        customer2.setGender("Male");

        // Act & Assert
        assertEquals(customer1, customer2);
    }

    @Test
    @DisplayName("Should determine customers with different emails are not equal")
    void testCustomerInequalityDifferentEmail() {
        // Arrange
        Customer customer1 = new Customer();
        customer1.setName("John Doe");
        customer1.setEmail("john@example.com");
        customer1.setPhoneNumber("012-12345678");
        customer1.setGender("Male");

        Customer customer2 = new Customer();
        customer2.setName("John Doe");
        customer2.setEmail("jane@example.com");
        customer2.setPhoneNumber("012-12345678");
        customer2.setGender("Male");

        // Act & Assert
        assertNotEquals(customer1, customer2);
    }
}
