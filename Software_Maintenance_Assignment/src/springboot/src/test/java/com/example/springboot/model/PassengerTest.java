package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PassengerTest {

    @Test
    void testPassengerGettersAndSetters() {
        // Arrange & Act
        Passenger passenger = new Passenger();
        passenger.setDocumentId("pass123");
        passenger.setFullName("John Doe");
        passenger.setPassportNo("A12345678");
        passenger.setEmail("john@example.com");
        passenger.setPhoneNumber("012-3456789");

        // Assert
        assertEquals("pass123", passenger.getDocumentId());
        assertEquals("John Doe", passenger.getFullName());
        assertEquals("A12345678", passenger.getPassportNo());
        assertEquals("john@example.com", passenger.getEmail());
        assertEquals("012-3456789", passenger.getPhoneNumber());
    }

    @Test
    void testPassengerDefaultConstructor() {
        // Act
        Passenger passenger = new Passenger();

        // Assert
        assertNull(passenger.getDocumentId());
        assertNull(passenger.getFullName());
        assertNull(passenger.getPassportNo());
    }

    @Test
    void testPassengerPassportFormat() {
        // Arrange & Act
        Passenger passenger = new Passenger();
        passenger.setPassportNo("A12345678");

        // Assert
        assertTrue(passenger.getPassportNo().matches("^[A-Z]\\d{8}$"));
    }

    @Test
    void testPassengerPhoneFormat() {
        // Arrange & Act
        Passenger passenger = new Passenger();
        passenger.setPhoneNumber("012-3456789");

        // Assert
        assertTrue(passenger.getPhoneNumber().matches("^\\d{3}-\\d{7,8}$"));
    }

    @Test
    void testPassengerEmailFormat() {
        // Arrange & Act
        Passenger passenger = new Passenger();
        passenger.setEmail("test@example.com");

        // Assert
        assertTrue(passenger.getEmail().contains("@"));
        assertTrue(passenger.getEmail().contains("."));
    }
}
