package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    @Test
    void testGettersAndSetters() {
        Flight flight = new Flight();
        Passenger passenger = new Passenger(); // Assuming Passenger has a no-args constructor
        
        Ticket ticket = new Ticket();
        ticket.setDocumentId("docT");
        ticket.setBookingReference("REF123");
        ticket.setCustomerId("C1");
        ticket.setPassengerId("P1");
        ticket.setSeatId("S1");
        ticket.setFlightId("F1");
        ticket.setSeatNumberDisplay("12A");
        ticket.setSeatClassDisplay("Economy");
        ticket.setFlightDetails(flight);
        ticket.setPassengerDetails(passenger);

        assertEquals("docT", ticket.getDocumentId());
        assertEquals("REF123", ticket.getBookingReference());
        assertEquals("C1", ticket.getCustomerId());
        assertEquals("12A", ticket.getSeatNumberDisplay());
        assertEquals("Economy", ticket.getSeatClassDisplay());
        assertSame(flight, ticket.getFlightDetails());
        assertSame(passenger, ticket.getPassengerDetails());
    }

    @Test
    void testEqualsAndHashCode() {
        Ticket t1 = new Ticket();
        t1.setBookingReference("ABC");
        Ticket t2 = new Ticket();
        t2.setBookingReference("ABC");
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    void testToString() {
        Ticket t = new Ticket();
        t.setBookingReference("XYZ");
        assertTrue(t.toString().contains("XYZ"));
    }
}