package com.example.springboot.validation;

import com.example.springboot.model.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonValidatorTest {

    private final PersonValidator personValidator = new PersonValidator();

    @Mock
    private Person person;

    @Test
    void testValidate_Success() {
        when(person.getName()).thenReturn("John Doe");
        when(person.getEmail()).thenReturn("john@example.com");
        when(person.getGender()).thenReturn("MALE");
        when(person.getPhoneNumber()).thenReturn("012-3456789");

        ValidationResult result = personValidator.validate(person);

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testValidate_InvalidName() {
        // Null
        when(person.getName()).thenReturn(null);
        assertFalse(personValidator.validate(person).isValid());

        // Numbers in name
        when(person.getName()).thenReturn("John123");
        ValidationResult result = personValidator.validate(person);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Invalid name"));
    }

    @Test
    void testValidate_InvalidEmail() {
        // Null
        when(person.getName()).thenReturn("John");
        when(person.getEmail()).thenReturn(null);
        assertFalse(personValidator.validate(person).isValid());

        // Invalid format (no .com) based on regex provided
        when(person.getEmail()).thenReturn("test@test.net");
        ValidationResult result = personValidator.validate(person);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Invalid email"));
    }

    @Test
    void testValidate_InvalidGender() {
        when(person.getName()).thenReturn("John");
        when(person.getEmail()).thenReturn("john@test.com");
        
        // Null
        when(person.getGender()).thenReturn(null);
        assertFalse(personValidator.validate(person).isValid());

        // Invalid string
        when(person.getGender()).thenReturn("Unknown");
        ValidationResult result = personValidator.validate(person);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Invalid gender"));
    }

    @Test
    void testValidate_InvalidPhoneNumber() {
        when(person.getName()).thenReturn("John");
        when(person.getEmail()).thenReturn("john@test.com");
        when(person.getGender()).thenReturn("MALE");

        // Null
        when(person.getPhoneNumber()).thenReturn(null);
        assertFalse(personValidator.validate(person).isValid());

        // Wrong format
        when(person.getPhoneNumber()).thenReturn("0123456789"); // Missing dash
        ValidationResult result = personValidator.validate(person);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Invalid phone number"));
    }

    @Test
    void testHelperMethods() {
        // Name
        assertTrue(personValidator.isValidName("John Doe"));
        assertFalse(personValidator.isValidName("John123"));
        assertFalse(personValidator.isValidName(null));

        // Email
        assertTrue(personValidator.isValidEmail("test@domain.com"));
        assertFalse(personValidator.isValidEmail("test@domain.org")); // Regex specific
        assertFalse(personValidator.isValidEmail(null));

        // Gender
        assertTrue(personValidator.isValidGender("MALE"));
        assertTrue(personValidator.isValidGender("female"));
        assertFalse(personValidator.isValidGender("other"));
        assertFalse(personValidator.isValidGender(null));

        // Phone
        assertTrue(personValidator.isValidPhoneNumber("012-3456789"));
        assertTrue(personValidator.isValidPhoneNumber("011-12345678"));
        assertFalse(personValidator.isValidPhoneNumber("012345678"));
        assertFalse(personValidator.isValidPhoneNumber(null));
    }
}