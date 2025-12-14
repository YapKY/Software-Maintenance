package com.example.springboot.factory;

import com.example.springboot.model.Passenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PassengerFactory
 * 
 * Tests: Passenger creation with validation
 * Coverage: Passport format, email format, phone format validation
 * Target: 90%+ coverage
 */
class PassengerFactoryTest {

    private PassengerFactory factory;

    @BeforeEach
    void setUp() {
        factory = new PassengerFactory();
    }

    // ========== Valid Passenger Creation Tests ==========

    @Test
    void testCreatePassenger_ValidData_Success() {
        // Act
        Passenger passenger = factory.createPassenger(
                "John Doe",
                "A12345678",
                "john@example.com",
                "012-3456789"
        );

        // Assert
        assertNotNull(passenger);
        assertEquals("John Doe", passenger.getFullName());
        assertEquals("A12345678", passenger.getPassportNo());
        assertEquals("john@example.com", passenger.getEmail());
        assertEquals("012-3456789", passenger.getPhoneNumber());
    }

    @Test
    void testCreatePassenger_DifferentValidPassport() {
        // Act
        Passenger passenger = factory.createPassenger(
                "Jane Smith",
                "B98765432",
                "jane@example.com",
                "011-9876543"
        );

        // Assert
        assertEquals("B98765432", passenger.getPassportNo());
    }

    @Test
    void testCreatePassenger_AllUppercaseLetters() {
        // Test with Z as the letter
        Passenger passenger = factory.createPassenger(
                "Test User",
                "Z11111111",
                "test@example.com",
                "012-1111111"
        );

        assertEquals("Z11111111", passenger.getPassportNo());
    }

    // ========== Invalid Name Tests ==========

    @Test
    void testCreatePassenger_NullName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger(null, "A12345678", "email@test.com", "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_EmptyName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("", "A12345678", "email@test.com", "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_BlankName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("   ", "A12345678", "email@test.com", "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_TooShortName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("A", "A12345678", "email@test.com", "012-3456789");
        });
    }

    // ========== Invalid Passport Format Tests ==========

    @Test
    void testCreatePassenger_NullPassport_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", null, "email@test.com", "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_EmptyPassport_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "", "email@test.com", "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_InvalidPassportFormat_TooShort() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "A1234567", "email@test.com", "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_InvalidPassportFormat_TooLong() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "A123456789", "email@test.com", "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_InvalidPassportFormat_NoLetter() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "123456789", "email@test.com", "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_InvalidPassportFormat_TwoLetters() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "AB12345678", "email@test.com", "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_InvalidPassportFormat_LetterNotAtStart() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "1A2345678", "email@test.com", "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_InvalidPassportFormat_ContainsNonDigits() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "A1234567X", "email@test.com", "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_InvalidPassportFormat_LowercaseLetter() {
        // Lowercase letters are not automatically converted - they should be rejected
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "a12345678", "email@test.com", "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_InvalidPassportFormat_WithWhitespace() {
        // Whitespace is not automatically trimmed - should be rejected
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", " A12345678 ", "email@test.com", "012-3456789");
        });
    }

    // ========== Invalid Email Tests ==========

    @Test
    void testCreatePassenger_NullEmail_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "A12345678", null, "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_EmptyEmail_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "A12345678", "", "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_InvalidEmail_NoAtSymbol() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "A12345678", "invalidemail.com", "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_InvalidEmail_NoDomain() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "A12345678", "invalid@", "012-3456789");
        });
    }

    @Test
    void testCreatePassenger_InvalidEmail_NoLocalPart() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "A12345678", "@example.com", "012-3456789");
        });
    }

    // ========== Invalid Phone Number Tests ==========

    @Test
    void testCreatePassenger_NullPhone_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "A12345678", "email@test.com", null);
        });
    }

    @Test
    void testCreatePassenger_EmptyPhone_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "A12345678", "email@test.com", "");
        });
    }

    @Test
    void testCreatePassenger_InvalidPhone_WrongFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "A12345678", "email@test.com", "0123456789");
        });
    }

    @Test
    void testCreatePassenger_InvalidPhone_MissingDash() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "A12345678", "email@test.com", "01234567890");
        });
    }

    @Test
    void testCreatePassenger_InvalidPhone_TooShort() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createPassenger("John Doe", "A12345678", "email@test.com", "012-345678");
        });
    }
}