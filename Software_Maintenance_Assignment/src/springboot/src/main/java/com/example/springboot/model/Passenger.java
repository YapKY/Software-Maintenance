package com.example.springboot.model;

import lombok.Data;

@Data
public class Passenger {
    private String documentId;
    private String fullName;
    private String passportNo;
    private String email;
    private String phoneNumber;
}