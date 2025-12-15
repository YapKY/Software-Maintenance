package com.example.springboot.validation;

import com.example.springboot.model.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Strategy Pattern - Staff Validation Strategy
 * Extracts validation logic from Staff entity
 */
@Component
public class StaffValidator implements Validator<Staff> {

    @Autowired
    private PersonValidator personValidator;

    @Override
    public ValidationResult validate(Staff staff) {
        ValidationResult result = new ValidationResult();

        // Validate name
        if (staff.getName() == null || staff.getName().trim().isEmpty()) {
            result.addError("Name is required.");
        } else if (!staff.getName().matches("^[a-zA-Z\\s]+$")) {
            result.addError("Name must contain only letters and spaces.");
        }

        // Validate email
        if (staff.getEmail() != null && !staff.getEmail().isEmpty()) {
            if (!staff.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                result.addError("Invalid email format.");
            }
        }

        // Validate phone number
        if (staff.getPhoneNumber() != null && !staff.getPhoneNumber().isEmpty()) {
            if (!staff.getPhoneNumber().matches("^\\d{3}-\\d{7,8}$")) {
                result.addError("Phone number must be in format XXX-XXXXXXXX.");
            }
        }

        // Validate gender
        if (staff.getGender() != null && !staff.getGender().isEmpty()) {
            String gender = staff.getGender().toUpperCase();
            if (!gender.equals("MALE") && !gender.equals("FEMALE")) {
                result.addError("Gender must be MALE or FEMALE.");
            }
        }

        // Validate staff password (5-digit number)
        if (staff.getStfPass() == null || !staff.getStfPass().matches("\\d{5}")) {
            result.addError("Password must be a 5-digit number.");
        }

        // Validate position
        if (staff.getPosition() == null || staff.getPosition().trim().isEmpty()) {
            result.addError("Position is required.");
        }

        return result;
    }

    public boolean isValidPassword(String password) {
        return password != null && password.matches("\\d{5}");
    }

    public boolean isValidPosition(String position) {
        return position != null && !position.trim().isEmpty();
    }
}
