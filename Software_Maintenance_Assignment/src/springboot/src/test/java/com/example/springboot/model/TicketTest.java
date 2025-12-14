package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    @Test
    void testTicketGettersAndSetters() {
        // Arrange & Act
        Ticket ticket = new Ticket();
        ticket.setDocumentId("ticket123");
        ticket.setBookingReference("ABC12345");
        ticket.setCustomerId("cust123");
        ticket.setPassengerId("pass123");
        ticket.setSeatId("seat123");
        ticket.setFlightId("F001");
        ticket.setSeatNumberDisplay("101");
        ticket.setSeatClassDisplay("Economy");

        // Assert
        assertEquals("ticket123", ticket.getDocumentId());
        assertEquals("ABC12345", ticket.getBookingReference());
        assertEquals("cust123", ticket.getCustomerId());
        assertEquals("pass123", ticket.getPassengerId());
        assertEquals("seat123", ticket.getSeatId());
        assertEquals("F001", ticket.getFlightId());
        assertEquals("101", ticket.getSeatNumberDisplay());
        assertEquals("Economy", ticket.getSeatClassDisplay());
    }

    @Test
    void testTicketDefaultConstructor() {
        // Act
        Ticket ticket = new Ticket();

        // Assert
        assertNull(ticket.getDocumentId());
        assertNull(ticket.getBookingReference());
        assertNull(ticket.getFlightDetails());
        assertNull(ticket.getPassengerDetails());
    }

    @Test
    void testTicketWithEnrichedData() {
        // Arrange
        Flight flight = new Flight();
        flight.setFlightId("F001");

        Passenger passenger = new Passenger();
        passenger.setFullName("John Doe");

        // Act
        Ticket ticket = new Ticket();
        ticket.setFlightDetails(flight);
        ticket.setPassengerDetails(passenger);

        // Assert
        assertNotNull(ticket.getFlightDetails());
        assertNotNull(ticket.getPassengerDetails());
        assertEquals("F001", ticket.getFlightDetails().getFlightId());
        assertEquals("John Doe", ticket.getPassengerDetails().getFullName());
    }

    @Test
    void testTicketBookingReference() {
        // Arrange & Act
        Ticket ticket = new Ticket();
        ticket.setBookingReference("XYZ98765");

        // Assert
        assertEquals("XYZ98765", ticket.getBookingReference());
        assertEquals(8, ticket.getBookingReference().length());
    }
}
