package com.example.springboot.validation;

import com.example.springboot.model.Person;
import org.springframework.stereotype.Component;

/**
 * Strategy Pattern - Person Validation Strategy
 * Extracts validation logic from Person entity
 */
@Component
public class PersonValidator implements Validator<Person> {

    @Override
    public ValidationResult validate(Person person) {
        ValidationResult result = new ValidationResult();

        // Validate name
        if (person.getName() == null || !person.getName().matches("^[a-zA-Z\\s]+$")) {
            result.addError("Invalid name. Please use only letters and spaces.");
        }

        // Validate email
        if (person.getEmail() == null || !person.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)+com$")) {
            result.addError("Invalid email address. Please enter a valid email.");
        }

        // Validate gender
        if (person.getGender() == null ||
                (!person.getGender().equalsIgnoreCase("MALE") &&
                        !person.getGender().equalsIgnoreCase("FEMALE"))) {
            result.addError("Invalid gender. Please enter Male or Female.");
        }

        // Validate phone number
        if (person.getPhoneNumber() == null || !person.getPhoneNumber().matches("^\\d{3}-\\d{7,8}$")) {
            result.addError("Invalid phone number format. Expected format: XXX-XXXXXXX or XXX-XXXXXXXX");
        }

        return result;
    }

    public boolean isValidName(String name) {
        return name != null && name.matches("^[a-zA-Z\\s]+$");
    }

    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)+com$");
    }

    public boolean isValidGender(String gender) {
        return gender != null &&
                (gender.equalsIgnoreCase("MALE") || gender.equalsIgnoreCase("FEMALE"));
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^\\d{3}-\\d{7,8}$");
    }
}
