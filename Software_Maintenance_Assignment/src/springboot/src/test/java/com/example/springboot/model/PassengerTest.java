package com.example.springboot.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PassengerTest {

    private Passenger passenger;

    @BeforeEach
    void setUp() {
        passenger = new Passenger();
        passenger.setPassengerId("P001");
        passenger.setDocumentId("doc123");
        passenger.setName("John Doe");
        passenger.setFullName("John Doe");
        passenger.setPassportNo("A12345678");
        passenger.setEmail("john@example.com");
        passenger.setPhoneNumber("0123456789");
        passenger.setGender("Male");
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testGettersAndSetters() {
        assertEquals("P001", passenger.getPassengerId());
        assertEquals("doc123", passenger.getDocumentId());
        assertEquals("John Doe", passenger.getName());
        assertEquals("John Doe", passenger.getFullName());
        assertEquals("A12345678", passenger.getPassportNo());
        assertEquals("john@example.com", passenger.getEmail());
        assertEquals("0123456789", passenger.getPhoneNumber());
        assertEquals("Male", passenger.getGender());
    }

    @Test
    @DisplayName("Should create passenger with all args constructor")
    void testAllArgsConstructor() {
        Passenger newPassenger = new Passenger("P002", "doc456", "Jane", "Jane Doe", "B98765432", "jane@example.com",
                "0987654321", "Female");

        assertEquals("P002", newPassenger.getPassengerId());
        assertEquals("doc456", newPassenger.getDocumentId());
        assertEquals("Jane", newPassenger.getName());
        assertEquals("Jane Doe", newPassenger.getFullName());
        assertEquals("B98765432", newPassenger.getPassportNo());
        assertEquals("jane@example.com", newPassenger.getEmail());
        assertEquals("0987654321", newPassenger.getPhoneNumber());
        assertEquals("Female", newPassenger.getGender());
    }

    @Test
    @DisplayName("Should validate passport number correctly")
    void testValidPassportNo() {
        // Valid: [A-Z0-9]{6,9}
        assertTrue(passenger.ValidPassportNo("A12345")); // 6 chars
        assertEquals("A12345", passenger.getPassportNo());

        assertTrue(passenger.ValidPassportNo("A12345678")); // 9 chars
        assertEquals("A12345678", passenger.getPassportNo());

        // Invalid
        assertFalse(passenger.ValidPassportNo("A1234")); // Too short
        assertFalse(passenger.ValidPassportNo("A1234567890")); // Too long
        assertFalse(passenger.ValidPassportNo("abcde123")); // Lowercase not allowed in regex [A-Z0-9]
        assertFalse(passenger.ValidPassportNo(null));
    }

    @Test
    @DisplayName("Should validate name correctly")
    void testGetValidName() {
        // Valid: ^[a-zA-Z\\s]+$
        assertTrue(passenger.getValidName("John Doe"));
        assertEquals("John Doe", passenger.getName());
        assertEquals("John Doe", passenger.getFullName());

        assertTrue(passenger.getValidName("Alice"));
        assertEquals("Alice", passenger.getName());

        // Invalid
        assertFalse(passenger.getValidName("John123")); // Numbers
        assertFalse(passenger.getValidName("John_Doe")); // Special chars
        assertFalse(passenger.getValidName("")); // Empty (regex + means at least 1)
        assertFalse(passenger.getValidName(null));
    }

    @Test
    @DisplayName("Should validate email correctly")
    void testGetValidEmail() {
        // Valid: ^[A-Za-z0-9+_.-]+@(.+)+com$
        // Note: The regex in Passenger.java is a bit specific: @(.+)+com$ means it must
        // end with com

        assertTrue(passenger.getValidEmail("test@example.com"));
        assertEquals("test@example.com", passenger.getEmail());

        assertTrue(passenger.getValidEmail("user.name+tag@gmail.com"));

        // Invalid
        assertFalse(passenger.getValidEmail("invalid-email")); // No @
        assertFalse(passenger.getValidEmail("test@example.net")); // Doesn't end with com (based on regex)
        assertFalse(passenger.getValidEmail(null));
    }

    @Test
    @DisplayName("Should test equals, hashCode and toString")
    void testLombokMethods() {
        Passenger p1 = new Passenger("P1", "d1", "N", "FN", "P1", "e@c.com", "123", "M");
        Passenger p2 = new Passenger("P1", "d1", "N", "FN", "P1", "e@c.com", "123", "M");
        Passenger p3 = new Passenger("P2", "d2", "N2", "FN2", "P2", "e2@c.com", "456", "F");

        // Equals
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);

        // HashCode
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), p3.hashCode());

        // ToString
        assertNotNull(p1.toString());
        assertTrue(p1.toString().contains("P1"));
    }
}
