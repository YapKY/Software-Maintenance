package com.example.springboot.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Person.java (Abstract Class).
 * We use a concrete anonymous subclass to test the logic inherited by Customer/Staff.
 */
class PersonTest {

    private Person person;

    // Concrete implementation of abstract class Person for testing purposes
    private static class TestPerson extends Person {
        public TestPerson() {
            super();
        }
    }

    @BeforeEach
    void setUp() {
        person = new TestPerson();
    }

    // ==========================================
    // Name Validation Tests
    // ==========================================

    @Test
    @DisplayName("Validate Name - Positive: Alphabets and spaces")
    void testGetValidName_Success() {
        boolean result = person.getValidName("John Doe");
        assertTrue(result, "Should return true for valid name");
        assertEquals("John Doe", person.getName(), "Name should be set in the object");
    }

    @Test
    @DisplayName("Validate Name - Negative: Contains numbers")
    void testGetValidName_Failure_Numbers() {
        boolean result = person.getValidName("John Doe 123");
        assertFalse(result, "Should return false for name with numbers");
        assertNull(person.getName(), "Name should not be set");
    }

    @Test
    @DisplayName("Validate Name - Negative: Special characters")
    void testGetValidName_Failure_SpecialChars() {
        boolean result = person.getValidName("John@Doe");
        assertFalse(result, "Should return false for name with special characters");
    }

    // ==========================================
    // Email Validation Tests
    //Regex used in Person.java: "^[A-Za-z0-9+_.-]+@(.+)+com$"
    // ==========================================

    @Test
    @DisplayName("Validate Email - Positive: Standard .com email")
    void testGetValidEmail_Success() {
        boolean result = person.getValidEmail("test.user+1@gmail.com");
        assertTrue(result, "Should return true for valid .com email");
        assertEquals("test.user+1@gmail.com", person.getEmail());
    }

    @Test
    @DisplayName("Validate Email - Negative: Non .com email (Constraint check)")
    void testGetValidEmail_Failure_NonCom() {
        // The regex in Person.java enforces ending with 'com'
        boolean result = person.getValidEmail("test@university.edu");
        assertFalse(result, "Should return false for non-.com domains based on current regex");
    }

    @Test
    @DisplayName("Validate Email - Negative: Invalid format")
    void testGetValidEmail_Failure_InvalidFormat() {
        boolean result = person.getValidEmail("plainaddress");
        assertFalse(result);
    }

    // ==========================================
    // Gender Validation Tests
    // ==========================================

    @ParameterizedTest
    @ValueSource(strings = {"MALE", "male", "MaLe", "FEMALE", "female", "FeMaLe"})
    @DisplayName("Validate Gender - Positive: Case Insensitive Check")
    void testGetValidGender_Success(String genderInput) {
        boolean result = person.getValidGender(genderInput);
        assertTrue(result);
        // The method stores the gender exactly as passed in the legacy logic
        assertEquals(genderInput, person.getGender()); 
    }

    @Test
    @DisplayName("Validate Gender - Negative: Invalid Option")
    void testGetValidGender_Failure() {
        boolean result = person.getValidGender("Robot");
        assertFalse(result);
        assertNull(person.getGender());
    }

    // ==========================================
    // Phone Number Validation Tests
    // Regex: "^\\d{3}-\\d{7,8}$"
    // ==========================================

    @Test
    @DisplayName("Validate Phone - Positive: Valid format XXX-XXXXXXX")
    void testGetValidPhoneNumber_Success() {
        boolean result = person.getValidPhoneNumber("012-3456789");
        assertTrue(result);
        assertEquals("012-3456789", person.getPhoneNumber());
    }

    @Test
    @DisplayName("Validate Phone - Positive: Valid format XXX-XXXXXXXX")
    void testGetValidPhoneNumber_Success_Longer() {
        boolean result = person.getValidPhoneNumber("011-12345678");
        assertTrue(result);
        assertEquals("011-12345678", person.getPhoneNumber());
    }

    @Test
    @DisplayName("Validate Phone - Negative: Missing Dash")
    void testGetValidPhoneNumber_Failure_NoDash() {
        boolean result = person.getValidPhoneNumber("0123456789");
        assertFalse(result, "Should fail if dash is missing");
    }

    @Test
    @DisplayName("Validate Phone - Negative: Contains Letters")
    void testGetValidPhoneNumber_Failure_Letters() {
        boolean result = person.getValidPhoneNumber("012-ABCDEFG");
        assertFalse(result);
    }

    // ==========================================
    // Equality and ToString Tests
    // ==========================================

    @Test
    @DisplayName("Test Equals and HashCode")
    void testEquals() {
        Person p1 = new TestPerson();
        p1.setName("Alice");
        p1.setEmail("alice@test.com");
        p1.setPhoneNumber("012-1111111");
        p1.setGender("FEMALE");

        Person p2 = new TestPerson();
        p2.setName("Alice");
        p2.setEmail("alice@test.com");
        p2.setPhoneNumber("012-1111111");
        p2.setGender("FEMALE");

        Person p3 = new TestPerson();
        p3.setName("Bob");

        assertEquals(p1, p2, "Objects with same data should be equal");
        assertNotEquals(p1, p3, "Objects with different data should not be equal");
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    @DisplayName("Test ToString Format")
    void testToString() {
        person.setName("John");
        person.setGender("MALE");
        person.setEmail("john@test.com");
        person.setPhoneNumber("012-9999999");

        String output = person.toString();
        
        assertTrue(output.contains("Name : John"));
        assertTrue(output.contains("Gender : MALE"));
        assertTrue(output.contains("Email Address : john@test.com"));
        assertTrue(output.contains("Phone Number : 012-9999999"));
    }
}