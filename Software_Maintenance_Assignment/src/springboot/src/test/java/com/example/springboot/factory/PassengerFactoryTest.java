package com.example.springboot.factory;

import com.example.springboot.model.Passenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PassengerFactoryTest {

    private PassengerFactory factory;

    @BeforeEach
    void setUp() {
        factory = new PassengerFactory();
    }

    // ========== Valid Passenger Creation Tests ==========

    @Test
    void testCreatePassenger_Success() {
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
    void testCreatePassenger_TrimsWhitespace() {
        // Act
        Passenger passenger = factory.createPassenger(
            "  John Doe  ",
            "  A12345678  ",
            "  john@example.com  ",
            "  012-3456789  "
        );

        // Assert
        assertEquals("John Doe", passenger.getFullName());
        assertEquals("A12345678", passenger.getPassportNo());
        assertEquals("john@example.com", passenger.getEmail());
        assertEquals("012-3456789", passenger.getPhoneNumber());
    }

    @Test
    void testCreatePassenger_ConvertsPassportToUppercase() {
        // Act
        Passenger passenger = factory.createPassenger(
            "John Doe",
            "a12345678",  // lowercase
            "john@example.com",
            "012-3456789"
        );

        // Assert
        assertEquals("A12345678", passenger.getPassportNo());
    }

    @Test
    void testCreatePassenger_ConvertsEmailToLowercase() {
        // Act
        Passenger passenger = factory.createPassenger(
            "John Doe",
            "A12345678",
            "John@EXAMPLE.COM",  // uppercase
            "012-3456789"
        );

        // Assert
        assertEquals("john@example.com", passenger.getEmail());
    }

    // ========== Validation Tests - Full Name ==========

    @Test
    void testCreatePassenger_NullName_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.createPassenger(null, "A12345678", "john@example.com", "012-3456789")
        );
        assertEquals("Passenger name cannot be empty", exception.getMessage());
    }

    @Test
    void testCreatePassenger_EmptyName_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.createPassenger("", "A12345678", "john@example.com", "012-3456789")
        );
        assertEquals("Passenger name cannot be empty", exception.getMessage());
    }

    @Test
    void testCreatePassenger_ShortName_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.createPassenger("J", "A12345678", "john@example.com", "012-3456789")
        );
        assertEquals("Passenger name must be at least 2 characters", exception.getMessage());
    }

    @Test
    void testCreatePassenger_MinimumValidName() {
        // Act
        Passenger passenger = factory.createPassenger(
            "Jo",  // Exactly 2 characters
            "A12345678",
            "john@example.com",
            "012-3456789"
        );

        // Assert
        assertEquals("Jo", passenger.getFullName());
    }

    // ========== Validation Tests - Passport ==========

    @Test
    void testCreatePassenger_NullPassport_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.createPassenger("John Doe", null, "john@example.com", "012-3456789")
        );
        assertTrue(exception.getMessage().contains("Invalid passport format"));
    }

    @Test
    void testCreatePassenger_InvalidPassportFormat_NoLetter_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.createPassenger("John Doe", "12345678", "john@example.com", "012-3456789")
        );
        assertTrue(exception.getMessage().contains("Invalid passport format"));
    }

    @Test
    void testCreatePassenger_InvalidPassportFormat_TooManyDigits_ThrowsException() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> factory.createPassenger("John Doe", "A123456789", "john@example.com", "012-3456789")
        );
    }

    @Test
    void testCreatePassenger_InvalidPassportFormat_TooFewDigits_ThrowsException() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> factory.createPassenger("John Doe", "A1234567", "john@example.com", "012-3456789")
        );
    }

    @Test
    void testCreatePassenger_InvalidPassportFormat_LowercaseLetter_Success() {
        // Lowercase should be converted to uppercase
        Passenger passenger = factory.createPassenger(
            "John Doe",
            "b98765432",
            "john@example.com",
            "012-3456789"
        );

        assertEquals("B98765432", passenger.getPassportNo());
    }

    // ========== Validation Tests - Email ==========

    @Test
    void testCreatePassenger_NullEmail_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.createPassenger("John Doe", "A12345678", null, "012-3456789")
        );
        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    void testCreatePassenger_InvalidEmail_NoAtSign_ThrowsException() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> factory.createPassenger("John Doe", "A12345678", "johnexample.com", "012-3456789")
        );
    }

    @Test
    void testCreatePassenger_InvalidEmail_NoDomain_ThrowsException() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> factory.createPassenger("John Doe", "A12345678", "john@", "012-3456789")
        );
    }

    // ========== Validation Tests - Phone Number ==========

    @Test
    void testCreatePassenger_NullPhone_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.createPassenger("John Doe", "A12345678", "john@example.com", null)
        );
        assertTrue(exception.getMessage().contains("Invalid phone format"));
    }

    @Test
    void testCreatePassenger_InvalidPhone_NoDash_ThrowsException() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> factory.createPassenger("John Doe", "A12345678", "john@example.com", "0123456789")
        );
    }

    @Test
    void testCreatePassenger_InvalidPhone_WrongFormat_ThrowsException() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> factory.createPassenger("John Doe", "A12345678", "john@example.com", "12-3456789")
        );
    }

    @Test
    void testCreatePassenger_ValidPhone_SevenDigits() {
        // Act
        Passenger passenger = factory.createPassenger(
            "John Doe",
            "A12345678",
            "john@example.com",
            "012-3456789"
        );

        // Assert
        assertEquals("012-3456789", passenger.getPhoneNumber());
    }

    @Test
    void testCreatePassenger_ValidPhone_EightDigits() {
        // Act
        Passenger passenger = factory.createPassenger(
            "John Doe",
            "A12345678",
            "john@example.com",
            "012-34567890"
        );

        // Assert
        assertEquals("012-34567890", passenger.getPhoneNumber());
    }

    // ========== Create from Request Tests ==========

    @Test
    void testCreateFromRequest_Success() {
        // Arrange
        PassengerFactory.PassengerRequest request = new PassengerFactory.PassengerRequest();
        request.setFullName("Jane Smith");
        request.setPassportNo("B98765432");
        request.setEmail("jane@example.com");
        request.setPhoneNumber("013-9876543");

        // Act
        Passenger passenger = factory.createFromRequest(request);

        // Assert
        assertEquals("Jane Smith", passenger.getFullName());
        assertEquals("B98765432", passenger.getPassportNo());
        assertEquals("jane@example.com", passenger.getEmail());
        assertEquals("013-9876543", passenger.getPhoneNumber());
    }
}
