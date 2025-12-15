package com.example.springboot.validation;

import com.example.springboot.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Strategy Pattern - Customer Validation Strategy
 * Extracts validation logic from Customer entity
 */
@Component
public class CustomerValidator implements Validator<Customer> {

    @Autowired
    private PersonValidator personValidator;

    @Override
    public ValidationResult validate(Customer customer) {
        ValidationResult result = new ValidationResult();

        // Validate Person fields first
        ValidationResult personResult = personValidator.validate(customer);
        if (!personResult.isValid()) {
            personResult.getErrors().forEach(result::addError);
        }

        // Validate IC number
        if (customer.getCustIcNo() == null || !customer.getCustIcNo().matches("\\d{6}-\\d{2}-\\d{4}")) {
            result.addError("Invalid I/C number format. Please use the format XXXXXX-XX-XXXX.");
        }

        // Validate password
        if (customer.getCustPassword() == null || customer.getCustPassword().length() <= 8) {
            result.addError("Password must be more than 8 characters.");
        }

        return result;
    }

    public boolean isValidICNumber(String icNumber) {
        return icNumber != null && icNumber.matches("\\d{6}-\\d{2}-\\d{4}");
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() > 8;
    }
}
