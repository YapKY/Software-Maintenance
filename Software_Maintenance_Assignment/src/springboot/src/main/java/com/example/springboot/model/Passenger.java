package com.example.springboot.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data <<<<<<<HEAD @EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor

public class Passenger {
    private String documentId;
    private String fullName;
    private String passportNo;
    private String email;
    private String phoneNumber;
}