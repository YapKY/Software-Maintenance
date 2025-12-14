package com.example.springboot.dto;

import com.example.springboot.dto.request.BookingRequestDTO;
import com.example.springboot.model.Passenger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BookingRequestDTO
 * 
 * Tests Module: Customer Booking Ticket Module
 * Coverage: DTO validation, data transfer
 */
class BookingRequestDTOTest {

    @Test
    void testBookingRequestDTO_GettersAndSetters() {
        // Arrange
        Passenger passenger = new Passenger();
        passenger.setFullName("John Doe");
        passenger.setPassportNo("A12345678");

        // Act
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setCustomerId("cust123");
        dto.setSeatId("seat456");
        dto.setAmount(200.50);
        dto.setStripePaymentIntentId("pi_123456");
        dto.setPassenger(passenger);

        // Assert
        assertEquals("cust123", dto.getCustomerId());
        assertEquals("seat456", dto.getSeatId());
        assertEquals(200.50, dto.getAmount());
        assertEquals("pi_123456", dto.getStripePaymentIntentId());
        assertNotNull(dto.getPassenger());
        assertEquals("John Doe", dto.getPassenger().getFullName());
    }

    @Test
    void testBookingRequestDTO_DefaultValues() {
        // Act
        BookingRequestDTO dto = new BookingRequestDTO();

        // Assert
        assertNull(dto.getCustomerId());
        assertNull(dto.getSeatId());
        assertNull(dto.getAmount());
        assertNull(dto.getStripePaymentIntentId());
        assertNull(dto.getPassenger());
    }

    @Test
    void testBookingRequestDTO_WithNullPassenger() {
        // Act
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setCustomerId("cust123");
        dto.setSeatId("seat456");
        dto.setAmount(200.00);
        dto.setPassenger(null);

        // Assert
        assertNotNull(dto.getCustomerId());
        assertNull(dto.getPassenger());
    }

    @Test
    void testBookingRequestDTO_AllFieldsPopulated() {
        // Arrange
        Passenger passenger = new Passenger();
        passenger.setFullName("Jane Smith");
        passenger.setPassportNo("B98765432");
        passenger.setEmail("jane@example.com");
        passenger.setPhoneNumber("013-9876543");

        // Act
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setCustomerId("cust999");
        dto.setSeatId("seat789");
        dto.setAmount(450.75);
        dto.setStripePaymentIntentId("pi_abcdef");
        dto.setPassenger(passenger);

        // Assert
        assertEquals("cust999", dto.getCustomerId());
        assertEquals("seat789", dto.getSeatId());
        assertEquals(450.75, dto.getAmount());
        assertEquals("pi_abcdef", dto.getStripePaymentIntentId());
        assertEquals("Jane Smith", dto.getPassenger().getFullName());
        assertEquals("B98765432", dto.getPassenger().getPassportNo());
    }

    @Test
    void testBookingRequestDTO_AmountZero() {
        // Act
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setAmount(0.0);

        // Assert
        assertEquals(0.0, dto.getAmount());
    }

    @Test
    void testBookingRequestDTO_AmountNegative() {
        // Act
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setAmount(-100.00);

        // Assert
        assertEquals(-100.00, dto.getAmount());
    }

    @Test
    void testBookingRequestDTO_EmptyStrings() {
        // Act
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setCustomerId("");
        dto.setSeatId("");
        dto.setStripePaymentIntentId("");

        // Assert
        assertEquals("", dto.getCustomerId());
        assertEquals("", dto.getSeatId());
        assertEquals("", dto.getStripePaymentIntentId());
    }

    @Test
    void testBookingRequestDTO_LargeAmount() {
        // Act
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setAmount(99999.99);

        // Assert
        assertEquals(99999.99, dto.getAmount());
    }
}