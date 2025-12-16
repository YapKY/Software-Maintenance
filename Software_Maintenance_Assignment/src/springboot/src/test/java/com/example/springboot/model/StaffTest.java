package com.example.springboot.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StaffTest {

    private Staff staff;

    @BeforeEach
    void setUp() {
        staff = new Staff();
        staff.setStaffId("S001");
        staff.setStfPass("12345");
        staff.setName("Alice Johnson");
        staff.setEmail("alice@example.com");
        staff.setPhoneNumber("0123456789");
        staff.setGender("Female");
        staff.setPosition("Manager");
        staff.setDocumentId("doc123");
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testGettersAndSetters() {
        assertEquals("S001", staff.getStaffId());
        assertEquals("12345", staff.getStfPass());
        assertEquals("Alice Johnson", staff.getName());
        assertEquals("alice@example.com", staff.getEmail());
        assertEquals("0123456789", staff.getPhoneNumber());
        assertEquals("Female", staff.getGender());
        assertEquals("Manager", staff.getPosition());
        assertEquals("doc123", staff.getDocumentId());
    }

    @Test
    @DisplayName("Should use alias methods for name correctly")
    void testNameAliases() {
        assertEquals("Alice Johnson", staff.getFullName());

        staff.setFullName("Bob Smith");
        assertEquals("Bob Smith", staff.getName());
        assertEquals("Bob Smith", staff.getFullName());
    }

    @Test
    @DisplayName("Should create staff with all args constructor")
    void testAllArgsConstructor() {
        Staff newStaff = new Staff("doc1", "S002", "54321", "Airline Controller", "Bob", "0111111111", "Male",
                "bob@example.com");

        assertEquals("doc1", newStaff.getDocumentId());
        assertEquals("S002", newStaff.getStaffId());
        assertEquals("54321", newStaff.getStfPass());
        assertEquals("Airline Controller", newStaff.getPosition());
        assertEquals("Bob", newStaff.getName());
        assertEquals("0111111111", newStaff.getPhoneNumber());
        assertEquals("Male", newStaff.getGender());
        assertEquals("bob@example.com", newStaff.getEmail());
    }

    @Test
    @DisplayName("Should create staff with custom constructor")
    void testCustomConstructor() {
        Staff newStaff = new Staff("S003", "Manager", "99999", "Charlie", "0122222222", "Male", "charlie@example.com");

        assertNull(newStaff.getDocumentId());
        assertEquals("S003", newStaff.getStaffId());
        assertEquals("Manager", newStaff.getPosition());
        assertEquals("99999", newStaff.getStfPass());
        assertEquals("Charlie", newStaff.getName());
        assertEquals("0122222222", newStaff.getPhoneNumber());
        assertEquals("Male", newStaff.getGender());
        assertEquals("charlie@example.com", newStaff.getEmail());
    }

    @Test
    @DisplayName("Should validate login correctly")
    void testLogin() {
        // Success
        assertTrue(staff.login("S001", 12345));

        // Fail - Wrong ID
        assertFalse(staff.login("S999", 12345));

        // Fail - Wrong Password
        assertFalse(staff.login("S001", 54321));

        // Fail - Invalid Password Format in stored password
        staff.setStfPass("NotANumber");
        assertFalse(staff.login("S001", 12345));
    }

    @Test
    @DisplayName("Should check position correctly")
    void testPositionChecks() {
        // Manager
        staff.setPosition("Manager");
        assertTrue(staff.isManager());
        assertFalse(staff.isController());

        // Case insensitive
        staff.setPosition("manager");
        assertTrue(staff.isManager());

        // Airline Controller
        staff.setPosition("Airline Controller");
        assertFalse(staff.isManager());
        assertTrue(staff.isController());

        // Case insensitive
        staff.setPosition("airline controller");
        assertTrue(staff.isController());

        // Other
        staff.setPosition("Pilot");
        assertFalse(staff.isManager());
        assertFalse(staff.isController());
    }

    @Test
    @DisplayName("Should test equals, hashCode and toString")
    void testLombokMethods() {
        Staff staff1 = new Staff("doc1", "S001", "12345", "Manager", "Alice", "0123456789", "Female",
                "alice@example.com");
        Staff staff2 = new Staff("doc1", "S001", "12345", "Manager", "Alice", "0123456789", "Female",
                "alice@example.com");
        Staff staff3 = new Staff("doc2", "S002", "54321", "Controller", "Bob", "0987654321", "Male", "bob@example.com");

        // Equals
        assertEquals(staff1, staff2);
        assertNotEquals(staff1, staff3);

        // HashCode
        assertEquals(staff1.hashCode(), staff2.hashCode());
        assertNotEquals(staff1.hashCode(), staff3.hashCode());

        // ToString
        assertNotNull(staff1.toString());
        assertTrue(staff1.toString().contains("S001"));
        assertTrue(staff1.toString().contains("Alice"));
    }
}
