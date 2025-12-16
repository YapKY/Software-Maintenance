package com.example.springboot.validation;

import com.example.springboot.model.Staff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffValidatorTest {

    @Mock
    private PersonValidator personValidator; // Injected but unused in validate(), mocked to satisfy dependency

    @Mock
    private Staff staff;

    @InjectMocks
    private StaffValidator staffValidator;

    @Test
    void testValidate_Success() {
        when(staff.getName()).thenReturn("Alice Smith");
        when(staff.getEmail()).thenReturn("alice@test.com");
        when(staff.getPhoneNumber()).thenReturn("012-3456789");
        when(staff.getGender()).thenReturn("FEMALE");
        when(staff.getStfPass()).thenReturn("12345");
        when(staff.getPosition()).thenReturn("Manager");

        ValidationResult result = staffValidator.validate(staff);
        assertTrue(result.isValid());
    }

    @Test
    void testValidate_Name() {
        // Null
        when(staff.getName()).thenReturn(null);
        ValidationResult r1 = staffValidator.validate(staff);
        assertFalse(r1.isValid());
        assertTrue(r1.getErrorMessage().contains("Name is required"));

        // Empty
        when(staff.getName()).thenReturn(" ");
        ValidationResult r2 = staffValidator.validate(staff);
        assertFalse(r2.isValid());
        assertTrue(r2.getErrorMessage().contains("Name is required"));

        // Invalid Chars
        when(staff.getName()).thenReturn("Alice123");
        ValidationResult r3 = staffValidator.validate(staff);
        assertFalse(r3.isValid());
        assertTrue(r3.getErrorMessage().contains("only letters and spaces"));
    }

    @Test
    void testValidate_Email() {
        when(staff.getName()).thenReturn("Alice");
        
        // Valid (Optional check logic: if not null/empty -> validate)
        when(staff.getEmail()).thenReturn(null); // Should pass if null is handled as "not checked" or logic prevents crash. 
        // Code: "if (staff.getEmail() != null && !staff.getEmail().isEmpty())"
        
        // Test invalid format
        when(staff.getEmail()).thenReturn("invalid-email");
        ValidationResult result = staffValidator.validate(staff);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Invalid email format"));
    }

    @Test
    void testValidate_PhoneNumber() {
        when(staff.getName()).thenReturn("Alice");
        
        // Invalid format
        when(staff.getPhoneNumber()).thenReturn("12345");
        ValidationResult result = staffValidator.validate(staff);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Phone number must be in format"));
    }

    @Test
    void testValidate_Gender() {
        when(staff.getName()).thenReturn("Alice");

        // Invalid gender
        when(staff.getGender()).thenReturn("Robot");
        ValidationResult result = staffValidator.validate(staff);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Gender must be MALE or FEMALE"));
    }

    @Test
    void testValidate_Password() {
        when(staff.getName()).thenReturn("Alice");

        // Null
        when(staff.getStfPass()).thenReturn(null);
        assertFalse(staffValidator.validate(staff).isValid());

        // Wrong format (not 5 digits)
        when(staff.getStfPass()).thenReturn("1234");
        ValidationResult result = staffValidator.validate(staff);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Password must be a 5-digit number"));
    }

    @Test
    void testValidate_Position() {
        when(staff.getName()).thenReturn("Alice");
        when(staff.getStfPass()).thenReturn("12345");

        // Null
        when(staff.getPosition()).thenReturn(null);
        ValidationResult r1 = staffValidator.validate(staff);
        assertFalse(r1.isValid());
        assertTrue(r1.getErrorMessage().contains("Position is required"));

        // Empty
        when(staff.getPosition()).thenReturn("");
        ValidationResult r2 = staffValidator.validate(staff);
        assertFalse(r2.isValid());
    }

    @Test
    void testHelperMethods() {
        assertTrue(staffValidator.isValidPassword("12345"));
        assertFalse(staffValidator.isValidPassword("1234"));
        assertFalse(staffValidator.isValidPassword(null));

        assertTrue(staffValidator.isValidPosition("Manager"));
        assertFalse(staffValidator.isValidPosition(""));
        assertFalse(staffValidator.isValidPosition(null));
    }
}