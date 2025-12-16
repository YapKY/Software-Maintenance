package com.example.springboot.validation;

import com.example.springboot.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerValidatorTest {

    @Mock
    private PersonValidator personValidator;

    @Mock
    private Customer customer;

    @InjectMocks
    private CustomerValidator customerValidator;

    @Test
    void testValidate_Success() {
        // Arrange
        when(personValidator.validate(customer)).thenReturn(new ValidationResult()); // Valid person
        when(customer.getCustIcNo()).thenReturn("900101-14-1234");
        when(customer.getCustPassword()).thenReturn("password123");

        // Act
        ValidationResult result = customerValidator.validate(customer);

        // Assert
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testValidate_FailsOnPersonValidation() {
        // Arrange
        ValidationResult personErrors = new ValidationResult();
        personErrors.addError("Invalid Name");
        
        when(personValidator.validate(customer)).thenReturn(personErrors);
        when(customer.getCustIcNo()).thenReturn("900101-14-1234");
        when(customer.getCustPassword()).thenReturn("password123");

        // Act
        ValidationResult result = customerValidator.validate(customer);

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Invalid Name"));
    }

    @Test
    void testValidate_InvalidIC() {
        // Arrange
        when(personValidator.validate(customer)).thenReturn(new ValidationResult());
        // Test null
        when(customer.getCustIcNo()).thenReturn(null);
        when(customer.getCustPassword()).thenReturn("password123");

        ValidationResult resultNull = customerValidator.validate(customer);
        assertFalse(resultNull.isValid());
        assertTrue(resultNull.getErrorMessage().contains("Invalid I/C number"));

        // Test wrong format
        when(customer.getCustIcNo()).thenReturn("1234567890");
        ValidationResult resultFormat = customerValidator.validate(customer);
        assertFalse(resultFormat.isValid());
    }

    @Test
    void testValidate_InvalidPassword() {
        // Arrange
        when(personValidator.validate(customer)).thenReturn(new ValidationResult());
        when(customer.getCustIcNo()).thenReturn("900101-14-1234");
        
        // Test null
        when(customer.getCustPassword()).thenReturn(null);
        ValidationResult resultNull = customerValidator.validate(customer);
        assertFalse(resultNull.isValid());
        assertTrue(resultNull.getErrorMessage().contains("Password must be more than 8 characters"));

        // Test short
        when(customer.getCustPassword()).thenReturn("12345678"); // exactly 8
        ValidationResult resultShort = customerValidator.validate(customer);
        assertFalse(resultShort.isValid());
    }

    @Test
    void testIsValidICNumber() {
        assertTrue(customerValidator.isValidICNumber("900101-14-1234"));
        assertFalse(customerValidator.isValidICNumber("900101141234"));
        assertFalse(customerValidator.isValidICNumber(null));
    }

    @Test
    void testIsValidPassword() {
        assertTrue(customerValidator.isValidPassword("123456789"));
        assertFalse(customerValidator.isValidPassword("12345678"));
        assertFalse(customerValidator.isValidPassword(null));
    }
}