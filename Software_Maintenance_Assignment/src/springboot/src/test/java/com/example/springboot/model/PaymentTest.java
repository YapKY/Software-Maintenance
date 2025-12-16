package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Payment model
 * 
 * Coverage: Getters, Setters, Constructors, Lombok methods
 * Target: >90% coverage
 */
@DisplayName("Payment Model Tests")
class PaymentTest {

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
    }

    // ==================== CONSTRUCTOR TESTS ====================

    @Test
    @DisplayName("Should create Payment with no-args constructor")
    void testNoArgsConstructor() {
        // Act
        Payment newPayment = new Payment();

        // Assert
        assertNotNull(newPayment);
        assertNull(newPayment.getDocumentId());
        assertNull(newPayment.getAmount());
        assertNull(newPayment.getPaymentDate());
        assertFalse(newPayment.isPaymentStatus());
        assertNull(newPayment.getStripePaymentIntentId());
        assertNull(newPayment.getTicketId());
    }

    // ==================== GETTER/SETTER TESTS ====================

    @Test
    @DisplayName("Should set and get documentId")
    void testSetGetDocumentId() {
        // Arrange
        String documentId = "payment-doc-123";

        // Act
        payment.setDocumentId(documentId);

        // Assert
        assertEquals(documentId, payment.getDocumentId());
    }

    @Test
    @DisplayName("Should set and get amount")
    void testSetGetAmount() {
        // Arrange
        Double amount = 250.50;

        // Act
        payment.setAmount(amount);

        // Assert
        assertEquals(amount, payment.getAmount());
    }

    @Test
    @DisplayName("Should set and get paymentDate")
    void testSetGetPaymentDate() {
        // Arrange
        String date = "2023-12-15T14:30:00Z";

        // Act
        payment.setPaymentDate(date);

        // Assert
        assertEquals(date, payment.getPaymentDate());
    }

    @Test
    @DisplayName("Should set and get paymentStatus - true")
    void testSetGetPaymentStatus_True() {
        // Arrange & Act
        payment.setPaymentStatus(true);

        // Assert
        assertTrue(payment.isPaymentStatus());
    }

    @Test
    @DisplayName("Should set and get paymentStatus - false")
    void testSetGetPaymentStatus_False() {
        // Arrange & Act
        payment.setPaymentStatus(false);

        // Assert
        assertFalse(payment.isPaymentStatus());
    }

    @Test
    @DisplayName("Should set and get stripePaymentIntentId")
    void testSetGetStripePaymentIntentId() {
        // Arrange
        String intentId = "pi_1234567890abcdef";

        // Act
        payment.setStripePaymentIntentId(intentId);

        // Assert
        assertEquals(intentId, payment.getStripePaymentIntentId());
    }

    @Test
    @DisplayName("Should set and get ticketId")
    void testSetGetTicketId() {
        // Arrange
        String ticketId = "ticket-456";

        // Act
        payment.setTicketId(ticketId);

        // Assert
        assertEquals(ticketId, payment.getTicketId());
    }

    // ==================== COMPLETE PAYMENT OBJECT TEST ====================

    @Test
    @DisplayName("Should create complete Payment object")
    void testCompletePaymentObject() {
        // Arrange & Act
        payment.setDocumentId("payment-789");
        payment.setAmount(450.00);
        payment.setPaymentDate("2023-12-20T10:00:00Z");
        payment.setPaymentStatus(true);
        payment.setStripePaymentIntentId("pi_abcdef123456");
        payment.setTicketId("ticket-999");

        // Assert
        assertEquals("payment-789", payment.getDocumentId());
        assertEquals(450.00, payment.getAmount());
        assertEquals("2023-12-20T10:00:00Z", payment.getPaymentDate());
        assertTrue(payment.isPaymentStatus());
        assertEquals("pi_abcdef123456", payment.getStripePaymentIntentId());
        assertEquals("ticket-999", payment.getTicketId());
    }

    // ==================== EQUALS AND HASHCODE TESTS ====================

    @Test
    @DisplayName("Should be equal when all fields are the same")
    void testEquals_SameFields() {
        // Arrange
        Payment payment1 = new Payment();
        payment1.setDocumentId("doc-1");
        payment1.setAmount(100.00);
        payment1.setPaymentDate("2023-12-15T10:00:00Z");
        payment1.setPaymentStatus(true);
        payment1.setStripePaymentIntentId("pi_123");
        payment1.setTicketId("ticket-1");

        Payment payment2 = new Payment();
        payment2.setDocumentId("doc-1");
        payment2.setAmount(100.00);
        payment2.setPaymentDate("2023-12-15T10:00:00Z");
        payment2.setPaymentStatus(true);
        payment2.setStripePaymentIntentId("pi_123");
        payment2.setTicketId("ticket-1");

        // Act & Assert
        assertEquals(payment1, payment2);
        assertEquals(payment1.hashCode(), payment2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when fields differ")
    void testEquals_DifferentFields() {
        // Arrange
        Payment payment1 = new Payment();
        payment1.setDocumentId("doc-1");
        payment1.setAmount(100.00);

        Payment payment2 = new Payment();
        payment2.setDocumentId("doc-2");
        payment2.setAmount(200.00);

        // Act & Assert
        assertNotEquals(payment1, payment2);
    }

    @Test
    @DisplayName("Should be equal to itself")
    void testEquals_SameObject() {
        // Arrange
        payment.setDocumentId("doc-1");

        // Act & Assert
        assertEquals(payment, payment);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void testEquals_Null() {
        // Arrange
        payment.setDocumentId("doc-1");

        // Act & Assert
        assertNotEquals(payment, null);
    }

    @Test
    @DisplayName("Should not be equal to different class")
    void testEquals_DifferentClass() {
        // Arrange
        payment.setDocumentId("doc-1");
        String differentObject = "Not a payment";

        // Act & Assert
        assertNotEquals(payment, differentObject);
    }

    // ==================== TOSTRING TEST ====================

    @Test
    @DisplayName("toString should contain key information")
    void testToString() {
        // Arrange
        payment.setDocumentId("payment-123");
        payment.setAmount(300.00);
        payment.setStripePaymentIntentId("pi_xyz");

        // Act
        String result = payment.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("payment-123"));
        assertTrue(result.contains("300.0") || result.contains("300.00"));
        assertTrue(result.contains("pi_xyz"));
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("Should handle null amount")
    void testNullAmount() {
        // Arrange & Act
        payment.setAmount(null);

        // Assert
        assertNull(payment.getAmount());
    }

    @Test
    @DisplayName("Should handle zero amount")
    void testZeroAmount() {
        // Arrange & Act
        payment.setAmount(0.0);

        // Assert
        assertEquals(0.0, payment.getAmount());
    }

    @Test
    @DisplayName("Should handle negative amount")
    void testNegativeAmount() {
        // Arrange & Act
        payment.setAmount(-50.00);

        // Assert
        assertEquals(-50.00, payment.getAmount());
    }

    @Test
    @DisplayName("Should handle very large amount")
    void testVeryLargeAmount() {
        // Arrange & Act
        Double largeAmount = 999999.99;
        payment.setAmount(largeAmount);

        // Assert
        assertEquals(largeAmount, payment.getAmount());
    }

    @Test
    @DisplayName("Should handle null payment date")
    void testNullPaymentDate() {
        // Arrange & Act
        payment.setPaymentDate(null);

        // Assert
        assertNull(payment.getPaymentDate());
    }

    @Test
    @DisplayName("Should handle empty payment date")
    void testEmptyPaymentDate() {
        // Arrange & Act
        payment.setPaymentDate("");

        // Assert
        assertEquals("", payment.getPaymentDate());
    }

    @Test
    @DisplayName("Should handle various date formats")
    void testVariousDateFormats() {
        // ISO 8601 format
        payment.setPaymentDate("2023-12-15T14:30:00Z");
        assertEquals("2023-12-15T14:30:00Z", payment.getPaymentDate());

        // Another format
        payment.setPaymentDate("2023-12-15 14:30:00");
        assertEquals("2023-12-15 14:30:00", payment.getPaymentDate());
    }

    @Test
    @DisplayName("Should handle null stripePaymentIntentId")
    void testNullStripePaymentIntentId() {
        // Arrange & Act
        payment.setStripePaymentIntentId(null);

        // Assert
        assertNull(payment.getStripePaymentIntentId());
    }

    @Test
    @DisplayName("Should handle empty stripePaymentIntentId")
    void testEmptyStripePaymentIntentId() {
        // Arrange & Act
        payment.setStripePaymentIntentId("");

        // Assert
        assertEquals("", payment.getStripePaymentIntentId());
    }

    @Test
    @DisplayName("Should handle null ticketId")
    void testNullTicketId() {
        // Arrange & Act
        payment.setTicketId(null);

        // Assert
        assertNull(payment.getTicketId());
    }

    @Test
    @DisplayName("Should handle empty ticketId")
    void testEmptyTicketId() {
        // Arrange & Act
        payment.setTicketId("");

        // Assert
        assertEquals("", payment.getTicketId());
    }

    @Test
    @DisplayName("Should handle payment status toggle")
    void testPaymentStatusToggle() {
        // Initially false
        assertFalse(payment.isPaymentStatus());

        // Set to true
        payment.setPaymentStatus(true);
        assertTrue(payment.isPaymentStatus());

        // Set back to false
        payment.setPaymentStatus(false);
        assertFalse(payment.isPaymentStatus());
    }

    // ==================== SCENARIO TESTS ====================

    @Test
    @DisplayName("Should represent successful payment")
    void testSuccessfulPayment() {
        // Arrange & Act
        payment.setDocumentId("payment-success-1");
        payment.setAmount(200.00);
        payment.setPaymentDate("2023-12-15T10:00:00Z");
        payment.setPaymentStatus(true);
        payment.setStripePaymentIntentId("pi_success123");
        payment.setTicketId("ticket-100");

        // Assert
        assertTrue(payment.isPaymentStatus());
        assertNotNull(payment.getStripePaymentIntentId());
        assertNotNull(payment.getTicketId());
    }

    @Test
    @DisplayName("Should represent failed payment")
    void testFailedPayment() {
        // Arrange & Act
        payment.setDocumentId("payment-failed-1");
        payment.setAmount(150.00);
        payment.setPaymentDate("2023-12-15T11:00:00Z");
        payment.setPaymentStatus(false);
        payment.setStripePaymentIntentId(null);
        payment.setTicketId("ticket-200");

        // Assert
        assertFalse(payment.isPaymentStatus());
        assertNull(payment.getStripePaymentIntentId());
    }

    @Test
    @DisplayName("Should represent pending payment")
    void testPendingPayment() {
        // Arrange & Act
        payment.setDocumentId("payment-pending-1");
        payment.setAmount(300.00);
        payment.setPaymentDate("2023-12-15T12:00:00Z");
        payment.setPaymentStatus(false);
        payment.setStripePaymentIntentId("pi_pending456");
        payment.setTicketId("ticket-300");

        // Assert
        assertFalse(payment.isPaymentStatus());
        assertNotNull(payment.getStripePaymentIntentId());
    }
}