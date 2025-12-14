package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SeatTest {

    @Test
    void testSeatGettersAndSetters() {
        // Arrange & Act
        Seat seat = new Seat();
        seat.setDocumentId("seat123");
        seat.setSeatNumber(101);
        seat.setTypeOfSeat("Economy");
        seat.setStatusSeat("Empty");
        seat.setFlightId("F001");

        // Assert
        assertEquals("seat123", seat.getDocumentId());
        assertEquals(101, seat.getSeatNumber());
        assertEquals("Economy", seat.getTypeOfSeat());
        assertEquals("Empty", seat.getStatusSeat());
        assertEquals("F001", seat.getFlightId());
    }

    @Test
    void testSeatDefaultConstructor() {
        // Act
        Seat seat = new Seat();

        // Assert
        assertNull(seat.getDocumentId());
        assertEquals(0, seat.getSeatNumber());
        assertNull(seat.getTypeOfSeat());
    }

    @Test
    void testSeatBusinessClass() {
        // Arrange & Act
        Seat seat = new Seat();
        seat.setTypeOfSeat("Business");
        seat.setStatusSeat("Empty");

        // Assert
        assertEquals("Business", seat.getTypeOfSeat());
        assertEquals("Empty", seat.getStatusSeat());
    }

    @Test
    void testSeatBookedStatus() {
        // Arrange & Act
        Seat seat = new Seat();
        seat.setStatusSeat("Booked");

        // Assert
        assertEquals("Booked", seat.getStatusSeat());
    }
}
