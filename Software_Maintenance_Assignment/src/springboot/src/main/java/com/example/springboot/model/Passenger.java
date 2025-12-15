package com.example.springboot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Passenger {
    private String passengerId;
    private String documentId;
    private String name;
    private String fullName;
    private String passportNo;
    private String email;
    private String phoneNumber;
    private String gender;

    /**
     * Validator method for passport number
     */
    public boolean ValidPassportNo(String passportNo) {
        if (passportNo != null && passportNo.matches("[A-Z0-9]{6,9}")) {
            this.passportNo = passportNo;
            return true;
        }
        return false;
    }

    /**
     * Validator method for name
     */
    public boolean getValidName(String name) {
        if (name != null && name.matches("^[a-zA-Z\\s]+$")) {
            this.name = name;
            this.fullName = name;
            return true;
        }
        return false;
    }

    /**
     * Validator method for email
     */
    public boolean getValidEmail(String email) {
        if (email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)+com$")) {
            this.email = email;
            return true;
        }
        return false;
    }
}