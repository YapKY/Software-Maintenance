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

        // Validate Person fields first
        ValidationResult personResult = personValidator.validate(staff);
        if (!personResult.isValid()) {
            personResult.getErrors().forEach(result::addError);
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
