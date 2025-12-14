package com.example.springboot.validation;

/**
 * Strategy Pattern - Validator Interface
 * Defines the contract for validation strategies
 */
public interface Validator<T> {
    ValidationResult validate(T object);
}
