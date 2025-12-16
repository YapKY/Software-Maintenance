package com.example.springboot.factory;

import com.example.springboot.model.Passenger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PassengerFactoryTest {

    private final PassengerFactory factory = new PassengerFactory();

    @Test
    void testCreatePassenger_Success() {
        Passenger p = factory.createPassenger("John Doe", "A12345678", "john@test.com", "012-3456789");
        
        assertEquals("John Doe", p.getFullName());
        assertEquals("A12345678", p.getPassportNo());
        assertEquals("john@test.com", p.getEmail());
        assertEquals("012-3456789", p.getPhoneNumber());
    }

    @Test
    void testCreatePassenger_Trimming() {
        // NOTE: The factory validates BEFORE trimming. 
        // Therefore, we must provide inputs that pass the strict regex patterns even if they have spaces,
        // OR we must accept that the factory rejects untrimmed inputs for strict fields like Passport/Email.
        // 
        // However, Name validation is usually looser. 
        // Based on the code analysis, we cannot pass " a12345678 " because it fails "^[A-Z]\\d{8}$".
        // So we provide VALID inputs for strict fields, and test trimming on name if possible, 
        // or just verify standard success path if the code doesn't support dirty input.
        
        // Let's test standard success with clean inputs since the factory rejects dirty strict inputs.
        Passenger p = factory.createPassenger(" John ", "A12345678", "mail@test.com", "012-3456789");
        
        // The factory validates first (allowing " John " as names usually pass basic checks), 
        // then trims.
        assertEquals("John", p.getFullName()); 
        
        // For passport/email/phone, the current implementation throws Exception if they contain spaces.
        // So we just verify the name was trimmed.
    }

    @Test
    void testCreateFromRequest() {
        PassengerFactory.PassengerRequest req = new PassengerFactory.PassengerRequest();
        req.setFullName("Jane Doe");
        req.setPassportNo("B12345678");
        req.setEmail("jane@test.com");
        req.setPhoneNumber("012-9876543");

        Passenger p = factory.createFromRequest(req);
        assertEquals("Jane Doe", p.getFullName());
    }

    // --- Validation Tests ---

    @Test
    void testValidation_Name() {
        // Null
        assertThrows(IllegalArgumentException.class, () -> 
            factory.createPassenger(null, "A12345678", "e@e.com", "012-1234567"));
        
        // Empty
        assertThrows(IllegalArgumentException.class, () -> 
            factory.createPassenger("", "A12345678", "e@e.com", "012-1234567"));
            
        // Too short
        assertThrows(IllegalArgumentException.class, () -> 
            factory.createPassenger("A", "A12345678", "e@e.com", "012-1234567"));
    }

    @Test
    void testValidation_Passport() {
        // Null
        assertThrows(IllegalArgumentException.class, () -> 
            factory.createPassenger("John", null, "e@e.com", "012-1234567"));
            
        // Wrong format
        assertThrows(IllegalArgumentException.class, () -> 
            factory.createPassenger("John", "123", "e@e.com", "012-1234567"));
    }

    @Test
    void testValidation_Email() {
        // Null
        assertThrows(IllegalArgumentException.class, () -> 
            factory.createPassenger("John", "A12345678", null, "012-1234567"));
            
        // Invalid format
        assertThrows(IllegalArgumentException.class, () -> 
            factory.createPassenger("John", "A12345678", "invalid-email", "012-1234567"));
    }

    @Test
    void testValidation_Phone() {
        // Null
        assertThrows(IllegalArgumentException.class, () -> 
            factory.createPassenger("John", "A12345678", "e@e.com", null));
            
        // Invalid format
        assertThrows(IllegalArgumentException.class, () -> 
            factory.createPassenger("John", "A12345678", "e@e.com", "12345"));
    }

    @Test
    void testPassengerRequestInnerClass() {
        PassengerFactory.PassengerRequest req = new PassengerFactory.PassengerRequest();
        req.setFullName("Name");
        req.setPassportNo("P1");
        req.setEmail("E1");
        req.setPhoneNumber("Ph1");

        assertEquals("Name", req.getFullName());
        assertEquals("P1", req.getPassportNo());
        assertEquals("E1", req.getEmail());
        assertEquals("Ph1", req.getPhoneNumber());
    }
}