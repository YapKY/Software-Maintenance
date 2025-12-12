package com.example.springboot.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Legacy-style Customer entity
 * Contains business logic mixed with entity - intentional anti-pattern
 * Stores password in plain text for legacy demonstration
 * 
 * @author KY YAP (Original Legacy Code)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "customer_id_generator")
    @GenericGenerator(name = "customer_id_generator", strategy = "com.example.springboot.generator.CustomerIdGenerator")
    private String id;

    @Column(unique = true, nullable = false, name = "ic_number")
    private String custIcNo; // Format: XXXXXX-XX-XXXX

    @Column(nullable = false, name = "password")
    private String custPassword; // Plain text password (legacy anti-pattern)

    @Column(name = "profile_pic")
    private String profilePic; // URL or file path to profile picture

    public Customer(String custIcNo, String custPassword, String name, String email, String phone, String gender) {
        super(name, phone, gender, email);
        this.custIcNo = custIcNo;
        this.custPassword = custPassword;
    }

    // Legacy validation methods - intentionally kept in entity (anti-pattern)

    public boolean getValidPassword(String password) {
        // Basic validation: Check if the password has a minimum length
        if (password.length() >= 8 && password.length() <= 8) {
            this.custPassword = password;
            return true;
        } else {
            System.out.println("         Password must be only 8 characters.");
            System.out.printf("\n");
        }
        return false;
    }

    public boolean getValidICNumber(String icNumber) {
        // Basic validation: Check if the I/C number has the expected format
        if (icNumber.matches("\\d{6}-\\d{2}-\\d{4}")) {
            this.custIcNo = icNumber;
            return true;
        } else {
            System.out.println("Invalid I/C number format. Please use the format XXXXXX-XX-XXXX.");
            System.out.printf("\n");
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Customer ID: %s\n                I/C Number : %s\n%s",
                id, custIcNo, super.toString());
    }
}
