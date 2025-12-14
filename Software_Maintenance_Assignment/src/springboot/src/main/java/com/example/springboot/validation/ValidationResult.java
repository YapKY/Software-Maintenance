package com.example.springboot.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates validation results
 * Part of Strategy Pattern implementation
 */
public class ValidationResult {
    private boolean valid;
    private List<String> errors;

    public ValidationResult() {
        this.valid = true;
        this.errors = new ArrayList<>();
    }

    public void addError(String error) {
        this.valid = false;
        this.errors.add(error);
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getErrorMessage() {
        return String.join("; ", errors);
    }

    public static ValidationResult success() {
        return new ValidationResult();
    }

    public static ValidationResult failure(String error) {
        ValidationResult result = new ValidationResult();
        result.addError(error);
        return result;
    }
}
