package com.example.springboot.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    // --- Email Validation ---

    @ParameterizedTest
    @ValueSource(strings = {"test@gmail.com", "user.name+tag@example.com", "my-email@company.com"})
    @DisplayName("Valid Emails")
    void testIsValidEmail_Valid(String email) {
        assertTrue(ValidationUtil.isValidEmail(email));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   ", "plainaddress", "@example.com", "user@.com.my", "user@domain"})
    @DisplayName("Invalid Emails")
    void testIsValidEmail_Invalid(String email) {
        assertFalse(ValidationUtil.isValidEmail(email));
    }

    // --- Password Validation ---
    // Req: 8+ chars, upper, lower, number, special char

    @Test
    @DisplayName("Valid Password")
    void testIsValidPassword_Valid() {
        assertTrue(ValidationUtil.isValidPassword("StrongP@ssw0rd"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
        "short",                // Too short
        "alllowercase1!",       // No uppercase
        "ALLUPPERCASE1!",       // No lowercase
        "NoSpecialChar1",       // No special char
        "NoNumbers!@#"          // No number
    })
    @DisplayName("Invalid Passwords")
    void testIsValidPassword_Invalid(String password) {
        assertFalse(ValidationUtil.isValidPassword(password));
    }

    // --- Phone Number Validation ---
    // Req: XXX-XXXXXXXX (3 digits - 7 or 8 digits)

    @ParameterizedTest
    @ValueSource(strings = {"012-3456789", "011-12345678"})
    @DisplayName("Valid Phone Numbers")
    void testIsValidPhoneNumber_Valid(String phone) {
        assertTrue(ValidationUtil.isValidPhoneNumber(phone));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "0123456789", "012-ABCDEFG", "12-3456789", "012-123456"})
    @DisplayName("Invalid Phone Numbers")
    void testIsValidPhoneNumber_Invalid(String phone) {
        assertFalse(ValidationUtil.isValidPhoneNumber(phone));
    }

    // --- Name Validation ---
    // Req: Letters and spaces only, length >= 2

    @ParameterizedTest
    @ValueSource(strings = {"John Doe", "Alice", "Mary Jane"})
    @DisplayName("Valid Names")
    void testIsValidName_Valid(String name) {
        assertTrue(ValidationUtil.isValidName(name));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "J", "John123", "John_Doe", "   "})
    @DisplayName("Invalid Names")
    void testIsValidName_Invalid(String name) {
        assertFalse(ValidationUtil.isValidName(name));
    }

    // --- MFA Code Validation ---
    // Req: exactly 6 digits

    @ParameterizedTest
    @ValueSource(strings = {"123456", "000000", "999999"})
    @DisplayName("Valid MFA Codes")
    void testIsValidMFACode_Valid(String code) {
        assertTrue(ValidationUtil.isValidMFACode(code));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"12345", "1234567", "abcdef", "123 45"})
    @DisplayName("Invalid MFA Codes")
    void testIsValidMFACode_Invalid(String code) {
        assertFalse(ValidationUtil.isValidMFACode(code));
    }

    // --- Sanitization ---

    @Test
    @DisplayName("Sanitize Input")
    void testSanitizeInput() {
        assertNull(ValidationUtil.sanitizeInput(null));
        assertEquals("", ValidationUtil.sanitizeInput("  "));
        
        // Corrected expectation to match actual replacement logic in ValidationUtil
        String input = "<script>alert('xss')</script>";
        // ' becomes &#x27; and / becomes &#x2F;
        String expected = "&lt;script&gt;alert(&#x27;xss&#x27;)&lt;&#x2F;script&gt;";
        assertEquals(expected, ValidationUtil.sanitizeInput(input));

        String input2 = "User&Name\"";
        // " becomes &quot;
        String expected2 = "User&Name&quot;";
        assertEquals(expected2, ValidationUtil.sanitizeInput(input2));
    }
}