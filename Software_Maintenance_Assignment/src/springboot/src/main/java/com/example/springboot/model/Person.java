package com.example.springboot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Person {

    private String name;
    private String phoneNumber;
    private String gender;
    private String email;

    // Legacy validation methods - intentionally kept in entity class (anti-pattern)

    public boolean getValidName(String name) {
        // Basic validation: Check if the name contains only letters and spaces
        if (name.matches("^[a-zA-Z\\s]+$")) {
            this.name = name;
            return true;
        } else {
            System.out.println("       Invalid name. Please use only letters and spaces.");
            System.out.printf("\n");
        }
        return false;
    }

    public boolean getValidEmail(String email) {
        // Basic email validation using a regular expression
        if (email.matches("^[A-Za-z0-9+_.-]+@(.+)+com$")) {
            this.email = email;
            return true;
        } else {
            System.out.println("       Invalid email address. Please enter a valid email.");
            System.out.printf("\n");
        }
        return false;
    }

    public boolean getValidGender(String gender) {
        switch (gender.toUpperCase()) {
            case "MALE":
            case "FEMALE":
                this.gender = gender;
                return true;
            default:
                System.out.println("      Invalid gender. Please enter Male or Female.");
                System.out.printf("\n");
        }
        return false;
    }

    public boolean getValidPhoneNumber(String phoneNumber) {
        // Basic phone number validation using a regular expression
        if (phoneNumber.matches("^\\d{3}-\\d{7,8}$")) {
            this.phoneNumber = phoneNumber;
            return true;
        } else {
            System.out.println("   Invalid phone number format. Please use the format XXX-XXXXXXXX.");
            System.out.printf("\n");
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Person other = (Person) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.phoneNumber, other.phoneNumber)) {
            return false;
        }
        return Objects.equals(this.gender, other.gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phoneNumber, gender, email);
    }
    
    @Override
    public String toString() {
        return String.format(
                "                Name : %s\n                Gender : %s\n                Email Address : %s\n                Phone Number : %s",
                name, gender, email, phoneNumber);
    }
}
