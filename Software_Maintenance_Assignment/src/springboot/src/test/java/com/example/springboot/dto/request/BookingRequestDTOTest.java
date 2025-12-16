package com.example.springboot.dto.request;

import com.example.springboot.model.Passenger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
    void testBookingRequestDTO_EqualityAndToString() {
        Passenger passenger = new Passenger();
        passenger.setFullName("Test");

        BookingRequestDTO dto1 = new BookingRequestDTO();
        dto1.setCustomerId("1");
        dto1.setAmount(100.0);
        dto1.setPassenger(passenger);

        BookingRequestDTO dto2 = new BookingRequestDTO();
        dto2.setCustomerId("1");
        dto2.setAmount(100.0);
        dto2.setPassenger(passenger);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }
}