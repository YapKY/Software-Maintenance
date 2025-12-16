package com.example.springboot.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustId("C001");
        customer.setCustIcNo("123456-12-1234");
        customer.setCustPassword("password123");
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPhoneNumber("0123456789");
        customer.setGender("Male");
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testGettersAndSetters() {
        assertEquals("C001", customer.getCustId());
        assertEquals("123456-12-1234", customer.getCustIcNo());
        assertEquals("password123", customer.getCustPassword());
        assertEquals("John Doe", customer.getName());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals("0123456789", customer.getPhoneNumber());
        assertEquals("Male", customer.getGender());
    }

    @Test
    @DisplayName("Should create customer with custom constructor")
    void testCustomConstructor() {
        Customer newCustomer = new Customer("987654-32-1098", "pass123", "Jane Doe", "jane@example.com", "0198765432",
                "Female");

        assertEquals("987654-32-1098", newCustomer.getCustIcNo());
        assertEquals("pass123", newCustomer.getCustPassword());
        assertEquals("Jane Doe", newCustomer.getName());
        assertEquals("jane@example.com", newCustomer.getEmail());
        assertEquals("0198765432", newCustomer.getPhoneNumber());
        assertEquals("Female", newCustomer.getGender());
    }

    @Test
    @DisplayName("Should validate password correctly")
    void testGetValidPassword() {
        // Valid (> 8 chars)
        assertTrue(customer.getValidPassword("securePassword"));
        assertEquals("securePassword", customer.getCustPassword());

        // Invalid (<= 8 chars)
        assertFalse(customer.getValidPassword("short"));
        assertNotEquals("short", customer.getCustPassword()); // Should not update

        assertFalse(customer.getValidPassword("12345678"));
    }

    @Test
    @DisplayName("Should validate IC number correctly")
    void testGetValidICNumber() {
        // Valid format
        assertTrue(customer.getValidICNumber("111111-11-1111"));
        assertEquals("111111-11-1111", customer.getCustIcNo());

        // Invalid format
        assertFalse(customer.getValidICNumber("123456789012"));
        assertNotEquals("123456789012", customer.getCustIcNo());

        assertFalse(customer.getValidICNumber("123456-12-123")); // Too short
        assertFalse(customer.getValidICNumber("123456-12-12345")); // Too long
        assertFalse(customer.getValidICNumber("abcdef-gh-ijkl")); // Non-digits
    }

    @Test
    @DisplayName("Should test equals, hashCode and toString")
    void testLombokMethods() {
        Customer c1 = new Customer("123456-12-1234", "pass", "John", "john@example.com", "012", "Male");
        c1.setCustId("1");
        Customer c2 = new Customer("123456-12-1234", "pass", "John", "john@example.com", "012", "Male");
        c2.setCustId("1");
        Customer c3 = new Customer("987654-32-1098", "pass", "Jane", "jane@example.com", "019", "Female");
        c3.setCustId("2");

        // Equals
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);

        // HashCode
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1.hashCode(), c3.hashCode());

        // ToString
        assertNotNull(c1.toString());
        assertTrue(c1.toString().contains("Customer ID"));
        assertTrue(c1.toString().contains("I/C Number"));
    }
}
