package com.example.springboot.model;

import lombok.Data;

@Data
public class Payment {
    private String documentId;
    private Double amount;
    private String paymentDate; // ISO 8601 String
    private boolean paymentStatus;
    private String stripePaymentIntentId; // Proof of payment
    private String ticketId; // Link to ticket
}