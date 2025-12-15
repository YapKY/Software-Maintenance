package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void testPaymentGettersAndSetters() {
        // Arrange & Act
        Payment payment = new Payment();
        payment.setDocumentId("payment123");
        payment.setAmount(200.50);
        payment.setPaymentDate("2023-11-11T13:00:00");
        payment.setPaymentStatus(true);
        payment.setStripePaymentIntentId("pi_123456");
        payment.setTicketId("ticket123");

        // Assert
        assertEquals("payment123", payment.getDocumentId());
        assertEquals(200.50, payment.getAmount());
        assertEquals("2023-11-11T13:00:00", payment.getPaymentDate());
        assertTrue(payment.isPaymentStatus());
        assertEquals("pi_123456", payment.getStripePaymentIntentId());
        assertEquals("ticket123", payment.getTicketId());
    }

    @Test
    void testPaymentDefaultConstructor() {
        // Act
        Payment payment = new Payment();

        // Assert
        assertNull(payment.getDocumentId());
        assertNull(payment.getAmount());
        assertFalse(payment.isPaymentStatus());
    }

    @Test
    void testPaymentSuccessStatus() {
        // Arrange & Act
        Payment payment = new Payment();
        payment.setPaymentStatus(true);
        payment.setStripePaymentIntentId("pi_success");

        // Assert
        assertTrue(payment.isPaymentStatus());
        assertNotNull(payment.getStripePaymentIntentId());
    }

    @Test
    void testPaymentFailureStatus() {
        // Arrange & Act
        Payment payment = new Payment();
        payment.setPaymentStatus(false);

        // Assert
        assertFalse(payment.isPaymentStatus());
    }
}