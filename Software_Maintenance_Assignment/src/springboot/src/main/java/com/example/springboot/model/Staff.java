package com.example.springboot.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Legacy-style Staff entity
 * Contains business logic mixed with entity - intentional anti-pattern
 * Uses numeric password for legacy demonstration
 * 
 * @author nicho (Original Legacy Code)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "staff")
public class Staff extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "staff_id_generator")
    @GenericGenerator(name = "staff_id_generator", strategy = "com.example.springboot.generator.StaffIdGenerator")
    private String id;

    @Column(unique = true, nullable = false, name = "staff_id")
    private String staffId; // e.g., "S001"

    @Column(nullable = false)
    private String position; // e.g., "Manager", "Airline Controller"

    @Column(nullable = false, name = "password")
    private String stfPass; // Password stored as VARCHAR (legacy anti-pattern)

    @Column(name = "profile_pic")
    private String profilePic; // URL or file path to profile picture

    public Staff(String staffId, String position, String stfPass, String name, String phoneNo, String gender,
            String email) {
        super(name, phoneNo, gender, email);
        this.staffId = staffId;
        this.position = position;
        this.stfPass = stfPass;
    }

    // Legacy login method - intentionally kept in entity (anti-pattern)
    public boolean login(String staffID, String staffPassword) {
        if (staffID.equals(this.staffId) && staffPassword.equals(this.stfPass)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "               Staff ID: " + staffId
                + "\n               Staff Position: " + position
                + "\n               Staff name: " + super.getName()
                + "\n               Phone No: " + super.getPhoneNumber()
                + "\n               Gender: " + super.getGender()
                + "\n               Email: " + super.getEmail();
    }
}
