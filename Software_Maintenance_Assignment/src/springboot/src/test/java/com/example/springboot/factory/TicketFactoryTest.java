package com.example.springboot.factory;

import com.example.springboot.model.Flight;
import com.example.springboot.model.Passenger;
import com.example.springboot.model.Seat;
import com.example.springboot.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicketFactoryTest {

    private TicketFactory factory;
    private Flight testFlight;
    private Seat testSeat;
    private Passenger testPassenger;

    @BeforeEach
    void setUp() {
        factory = new TicketFactory();

        testFlight = new Flight();
        testFlight.setFlightId("F001");
        testFlight.setDepartureCountry("Malaysia");
        testFlight.setArrivalCountry("Japan");

        testSeat = new Seat();
        testSeat.setDocumentId("seat123");
        testSeat.setSeatNumber(101);
        testSeat.setTypeOfSeat("Economy");
        testSeat.setStatusSeat("Empty");

        testPassenger = new Passenger();
        testPassenger.setDocumentId("pass123");
        testPassenger.setFullName("John Doe");
    }

    // ========== Valid Ticket Creation Tests ==========

    @Test
    void testCreateTicket_Success() {
        // Act
        Ticket ticket = factory.createTicket(
            "cust123",
            "pass123",
            "seat123",
            testSeat,
            testFlight
        );

        // Assert
        assertNotNull(ticket);
        assertEquals("cust123", ticket.getCustomerId());
        assertEquals("pass123", ticket.getPassengerId());
        assertEquals("seat123", ticket.getSeatId());
        assertEquals("F001", ticket.getFlightId());
        assertNotNull(ticket.getBookingReference());
        assertEquals(8, ticket.getBookingReference().length());
    }

    @Test
    void testCreateTicket_GeneratesUniqueBookingReference() {
        // Act
        Ticket ticket1 = factory.createTicket("cust1", "pass1", "seat1", testSeat, testFlight);
        Ticket ticket2 = factory.createTicket("cust2", "pass2", "seat2", testSeat, testFlight);

        // Assert
        assertNotEquals(ticket1.getBookingReference(), ticket2.getBookingReference());
    }

    @Test
    void testCreateTicket_BookingReferenceIsUppercase() {
        // Act
        Ticket ticket = factory.createTicket("cust123", "pass123", "seat123", testSeat, testFlight);

        // Assert
        assertEquals(ticket.getBookingReference(), ticket.getBookingReference().toUpperCase());
    }

    // ========== Validation Tests ==========

    @Test
    void testCreateTicket_NullCustomerId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.createTicket(null, "pass123", "seat123", testSeat, testFlight)
        );
        assertEquals("Customer ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCreateTicket_EmptyCustomerId_ThrowsException() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> factory.createTicket("", "pass123", "seat123", testSeat, testFlight)
        );
    }

    @Test
    void testCreateTicket_NullPassengerId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.createTicket("cust123", null, "seat123", testSeat, testFlight)
        );
        assertEquals("Passenger ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCreateTicket_NullSeat_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.createTicket("cust123", "pass123", "seat123", null, testFlight)
        );
        assertEquals("Seat cannot be null", exception.getMessage());
    }

    @Test
    void testCreateTicket_NullFlight_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.createTicket("cust123", "pass123", "seat123", testSeat, null)
        );
        assertEquals("Flight cannot be null", exception.getMessage());
    }

    @Test
    void testCreateTicket_SeatAlreadyBooked_ThrowsException() {
        // Arrange
        testSeat.setStatusSeat("Booked");

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> factory.createTicket("cust123", "pass123", "seat123", testSeat, testFlight)
        );
        assertEquals("Cannot create ticket for already booked seat", exception.getMessage());
    }

    // ========== Basic Ticket Creation Tests ==========

    @Test
    void testCreateBasicTicket_Success() {
        // Act
        Ticket ticket = factory.createBasicTicket("cust123", "pass123", "seat123", "F001");

        // Assert
        assertNotNull(ticket);
        assertEquals("cust123", ticket.getCustomerId());
        assertEquals("pass123", ticket.getPassengerId());
        assertEquals("seat123", ticket.getSeatId());
        assertEquals("F001", ticket.getFlightId());
        assertNotNull(ticket.getBookingReference());
    }

    // ========== Enriched Ticket Creation Tests ==========

    @Test
    void testCreateEnrichedTicket_Success() {
        // Act
        Ticket ticket = factory.createEnrichedTicket(
            "cust123",
            testPassenger,
            "seat123",
            testSeat,
            testFlight
        );

        // Assert
        assertNotNull(ticket);
        assertNotNull(ticket.getPassengerDetails());
        assertNotNull(ticket.getFlightDetails());
        assertEquals("John Doe", ticket.getPassengerDetails().getFullName());
        assertEquals("F001", ticket.getFlightDetails().getFlightId());
    }
}