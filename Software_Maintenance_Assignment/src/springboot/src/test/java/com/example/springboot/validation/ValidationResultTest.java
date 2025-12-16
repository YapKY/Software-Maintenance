package com.example.springboot.validation;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ValidationResultTest {

    @Test
    void testDefaultConstructor() {
        ValidationResult result = new ValidationResult();
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testAddError() {
        ValidationResult result = new ValidationResult();
        result.addError("Error 1");
        
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("Error 1", result.getErrors().get(0));
        
        result.addError("Error 2");
        assertEquals(2, result.getErrors().size());
    }

    @Test
    void testGetErrorMessage() {
        ValidationResult result = new ValidationResult();
        result.addError("Error 1");
        result.addError("Error 2");

        String msg = result.getErrorMessage();
        assertTrue(msg.contains("Error 1"));
        assertTrue(msg.contains("Error 2"));
        assertTrue(msg.contains("; ")); // Verify delimiter
    }

    @Test
    void testStaticSuccess() {
        ValidationResult result = ValidationResult.success();
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testStaticFailure() {
        ValidationResult result = ValidationResult.failure("Critical Fail");
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("Critical Fail", result.getErrors().get(0));
    }
}