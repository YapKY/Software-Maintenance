package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SeatTest {

    @Test
    void testGettersAndSetters() {
        Seat seat = new Seat();
        seat.setDocumentId("docS");
        seat.setSeatNumber(10);
        seat.setTypeOfSeat("Economy");
        seat.setStatusSeat("Booked");
        seat.setFlightId("F1");

        assertEquals("docS", seat.getDocumentId());
        assertEquals(10, seat.getSeatNumber());
        assertEquals("Economy", seat.getTypeOfSeat());
        assertEquals("Booked", seat.getStatusSeat());
        assertEquals("F1", seat.getFlightId());
    }

    @Test
    void testEqualsAndHashCode() {
        Seat s1 = new Seat();
        s1.setSeatNumber(5);
        Seat s2 = new Seat();
        s2.setSeatNumber(5);
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }
    
    @Test
    void testToString() {
        Seat s = new Seat();
        s.setTypeOfSeat("Business");
        assertTrue(s.toString().contains("Business"));
    }
}