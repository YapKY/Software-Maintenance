package com.example.springboot.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Legacy-style Passenger entity
 * Minimal class representing the person traveling
 * 
 * @author ANG (Original Legacy Code)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "passengers")
public class Passenger extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "passenger_id_generator")
    @GenericGenerator(name = "passenger_id_generator", strategy = "com.example.springboot.generator.PassengerIdGenerator")
    private String id;

    @Column(unique = true, nullable = false, name = "passport_no")
    private String passportNo; // Format: [A-Z]\d{8}

    @Column(name = "profile_pic")
    private String profilePic; // URL or file path to profile picture

    public Passenger(String passportNo, String name, String email, String phoneNumber, String gender) {
        super(name, phoneNumber, gender, email);
        this.passportNo = passportNo;
    }

    // Legacy validation method - intentionally kept in entity (anti-pattern)
    public boolean ValidPassportNo(String passportNo) {
        // Basic validation: Check if the passport contains one letter and 8 digits
        if (passportNo.matches("[A-Z]\\d{8}")) {
            this.passportNo = passportNo;
            return true;
        } else {
            System.out.println("                Invalid Passport Number. Please enter a valid passport number.");
            System.out.printf("\n");
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Passenger ID: %s\n                Passport Number : %s\n%s",
                id, passportNo, super.toString());
    }
}
