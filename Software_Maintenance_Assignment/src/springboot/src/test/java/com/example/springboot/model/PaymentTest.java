package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void testGettersAndSetters() {
        Payment payment = new Payment();
        payment.setDocumentId("doc1");
        payment.setAmount(100.50);
        payment.setPaymentDate("2023-12-01");
        payment.setPaymentStatus(true);
        payment.setStripePaymentIntentId("pi_123");
        payment.setTicketId("t1");

        assertEquals("doc1", payment.getDocumentId());
        assertEquals(100.50, payment.getAmount());
        assertEquals("2023-12-01", payment.getPaymentDate());
        assertTrue(payment.isPaymentStatus());
        assertEquals("pi_123", payment.getStripePaymentIntentId());
        assertEquals("t1", payment.getTicketId());
    }

    @Test
    void testEqualsAndHashCode() {
        Payment p1 = new Payment();
        p1.setTicketId("T1");
        Payment p2 = new Payment();
        p2.setTicketId("T1");
        
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void testToString() {
        Payment p = new Payment();
        p.setStripePaymentIntentId("STRIPE_ID");
        assertTrue(p.toString().contains("STRIPE_ID"));
    }
}