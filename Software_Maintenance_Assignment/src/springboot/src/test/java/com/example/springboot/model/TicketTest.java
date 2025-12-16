package com.example.springboot.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Ticket model
 * 
 * Coverage: Getters, Setters, Constructors, Lombok methods
 * Target: >90% coverage
 */
@DisplayName("Ticket Model Tests")
class TicketTest {

    private Ticket ticket;
    private Flight testFlight;
    private Passenger testPassenger;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
        
        // Setup test flight
        testFlight = new Flight();
        testFlight.setFlightId("F001");
        testFlight.setDepartureCountry("Malaysia");
        testFlight.setArrivalCountry("Japan");
        
        // Setup test passenger
        testPassenger = new Passenger();
        testPassenger.setFullName("John Doe");
        testPassenger.setEmail("john@example.com");
    }

    // ==================== CONSTRUCTOR TESTS ====================

    @Test
    @DisplayName("Should create Ticket with no-args constructor")
    void testNoArgsConstructor() {
        // Act
        Ticket newTicket = new Ticket();

        // Assert
        assertNotNull(newTicket);
        assertNull(newTicket.getDocumentId());
        assertNull(newTicket.getBookingReference());
        assertNull(newTicket.getCustomerId());
        assertNull(newTicket.getPassengerId());
        assertNull(newTicket.getSeatId());
        assertNull(newTicket.getFlightId());
        assertNull(newTicket.getSeatNumberDisplay());
        assertNull(newTicket.getSeatClassDisplay());
        assertNull(newTicket.getFlightDetails());
        assertNull(newTicket.getPassengerDetails());
    }

    // ==================== GETTER/SETTER TESTS ====================

    @Test
    @DisplayName("Should set and get documentId")
    void testSetGetDocumentId() {
        // Arrange
        String documentId = "ticket-doc-123";

        // Act
        ticket.setDocumentId(documentId);

        // Assert
        assertEquals(documentId, ticket.getDocumentId());
    }

    @Test
    @DisplayName("Should set and get bookingReference")
    void testSetGetBookingReference() {
        // Arrange
        String reference = "ABC12345";

        // Act
        ticket.setBookingReference(reference);

        // Assert
        assertEquals(reference, ticket.getBookingReference());
    }

    @Test
    @DisplayName("Should set and get customerId")
    void testSetGetCustomerId() {
        // Arrange
        String customerId = "customer-456";

        // Act
        ticket.setCustomerId(customerId);

        // Assert
        assertEquals(customerId, ticket.getCustomerId());
    }

    @Test
    @DisplayName("Should set and get passengerId")
    void testSetGetPassengerId() {
        // Arrange
        String passengerId = "passenger-789";

        // Act
        ticket.setPassengerId(passengerId);

        // Assert
        assertEquals(passengerId, ticket.getPassengerId());
    }

    @Test
    @DisplayName("Should set and get seatId")
    void testSetGetSeatId() {
        // Arrange
        String seatId = "seat-101";

        // Act
        ticket.setSeatId(seatId);

        // Assert
        assertEquals(seatId, ticket.getSeatId());
    }

    @Test
    @DisplayName("Should set and get flightId")
    void testSetGetFlightId() {
        // Arrange
        String flightId = "F001";

        // Act
        ticket.setFlightId(flightId);

        // Assert
        assertEquals(flightId, ticket.getFlightId());
    }

    @Test
    @DisplayName("Should set and get seatNumberDisplay")
    void testSetGetSeatNumberDisplay() {
        // Arrange
        String seatNumber = "12A";

        // Act
        ticket.setSeatNumberDisplay(seatNumber);

        // Assert
        assertEquals(seatNumber, ticket.getSeatNumberDisplay());
    }

    @Test
    @DisplayName("Should set and get seatClassDisplay")
    void testSetGetSeatClassDisplay() {
        // Arrange
        String seatClass = "Business";

        // Act
        ticket.setSeatClassDisplay(seatClass);

        // Assert
        assertEquals(seatClass, ticket.getSeatClassDisplay());
    }

    @Test
    @DisplayName("Should set and get flightDetails")
    void testSetGetFlightDetails() {
        // Act
        ticket.setFlightDetails(testFlight);

        // Assert
        assertNotNull(ticket.getFlightDetails());
        assertEquals("F001", ticket.getFlightDetails().getFlightId());
        assertEquals("Malaysia", ticket.getFlightDetails().getDepartureCountry());
    }

    @Test
    @DisplayName("Should set and get passengerDetails")
    void testSetGetPassengerDetails() {
        // Act
        ticket.setPassengerDetails(testPassenger);

        // Assert
        assertNotNull(ticket.getPassengerDetails());
        assertEquals("John Doe", ticket.getPassengerDetails().getFullName());
        assertEquals("john@example.com", ticket.getPassengerDetails().getEmail());
    }

    // ==================== COMPLETE TICKET OBJECT TEST ====================

    @Test
    @DisplayName("Should create complete Ticket object with all references")
    void testCompleteTicketObject() {
        // Arrange & Act
        ticket.setDocumentId("ticket-complete-1");
        ticket.setBookingReference("REF123ABC");
        ticket.setCustomerId("customer-999");
        ticket.setPassengerId("passenger-888");
        ticket.setSeatId("seat-777");
        ticket.setFlightId("F100");
        ticket.setSeatNumberDisplay("15B");
        ticket.setSeatClassDisplay("Economy");

        // Assert
        assertEquals("ticket-complete-1", ticket.getDocumentId());
        assertEquals("REF123ABC", ticket.getBookingReference());
        assertEquals("customer-999", ticket.getCustomerId());
        assertEquals("passenger-888", ticket.getPassengerId());
        assertEquals("seat-777", ticket.getSeatId());
        assertEquals("F100", ticket.getFlightId());
        assertEquals("15B", ticket.getSeatNumberDisplay());
        assertEquals("Economy", ticket.getSeatClassDisplay());
    }

    @Test
    @DisplayName("Should create complete Ticket with enriched data")
    void testCompleteTicketWithEnrichedData() {
        // Arrange & Act
        ticket.setDocumentId("ticket-enriched-1");
        ticket.setBookingReference("XYZ789DEF");
        ticket.setCustomerId("customer-111");
        ticket.setPassengerId("passenger-222");
        ticket.setSeatId("seat-333");
        ticket.setFlightId("F200");
        ticket.setSeatNumberDisplay("5A");
        ticket.setSeatClassDisplay("Business");
        ticket.setFlightDetails(testFlight);
        ticket.setPassengerDetails(testPassenger);

        // Assert - References
        assertEquals("ticket-enriched-1", ticket.getDocumentId());
        assertEquals("XYZ789DEF", ticket.getBookingReference());
        
        // Assert - Display fields
        assertEquals("5A", ticket.getSeatNumberDisplay());
        assertEquals("Business", ticket.getSeatClassDisplay());
        
        // Assert - Enriched objects
        assertNotNull(ticket.getFlightDetails());
        assertNotNull(ticket.getPassengerDetails());
        assertEquals("F001", ticket.getFlightDetails().getFlightId());
        assertEquals("John Doe", ticket.getPassengerDetails().getFullName());
    }

    // ==================== EQUALS AND HASHCODE TESTS ====================

    @Test
    @DisplayName("Should be equal when all fields are the same")
    void testEquals_SameFields() {
        // Arrange
        Ticket ticket1 = new Ticket();
        ticket1.setDocumentId("ticket-1");
        ticket1.setBookingReference("REF001");
        ticket1.setCustomerId("customer-1");
        ticket1.setPassengerId("passenger-1");
        ticket1.setSeatId("seat-1");
        ticket1.setFlightId("F001");

        Ticket ticket2 = new Ticket();
        ticket2.setDocumentId("ticket-1");
        ticket2.setBookingReference("REF001");
        ticket2.setCustomerId("customer-1");
        ticket2.setPassengerId("passenger-1");
        ticket2.setSeatId("seat-1");
        ticket2.setFlightId("F001");

        // Act & Assert
        assertEquals(ticket1, ticket2);
        assertEquals(ticket1.hashCode(), ticket2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when fields differ")
    void testEquals_DifferentFields() {
        // Arrange
        Ticket ticket1 = new Ticket();
        ticket1.setDocumentId("ticket-1");
        ticket1.setBookingReference("REF001");

        Ticket ticket2 = new Ticket();
        ticket2.setDocumentId("ticket-2");
        ticket2.setBookingReference("REF002");

        // Act & Assert
        assertNotEquals(ticket1, ticket2);
    }

    @Test
    @DisplayName("Should be equal to itself")
    void testEquals_SameObject() {
        // Arrange
        ticket.setDocumentId("ticket-1");

        // Act & Assert
        assertEquals(ticket, ticket);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void testEquals_Null() {
        // Arrange
        ticket.setDocumentId("ticket-1");

        // Act & Assert
        assertNotEquals(ticket, null);
    }

    @Test
    @DisplayName("Should not be equal to different class")
    void testEquals_DifferentClass() {
        // Arrange
        ticket.setDocumentId("ticket-1");
        String differentObject = "Not a ticket";

        // Act & Assert
        assertNotEquals(ticket, differentObject);
    }

    // ==================== TOSTRING TEST ====================

    @Test
    @DisplayName("toString should contain key information")
    void testToString() {
        // Arrange
        ticket.setDocumentId("ticket-123");
        ticket.setBookingReference("ABC123");
        ticket.setCustomerId("customer-456");
        ticket.setFlightId("F001");

        // Act
        String result = ticket.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("ticket-123"));
        assertTrue(result.contains("ABC123"));
        assertTrue(result.contains("customer-456"));
        assertTrue(result.contains("F001"));
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("Should handle null bookingReference")
    void testNullBookingReference() {
        // Arrange & Act
        ticket.setBookingReference(null);

        // Assert
        assertNull(ticket.getBookingReference());
    }

    @Test
    @DisplayName("Should handle empty bookingReference")
    void testEmptyBookingReference() {
        // Arrange & Act
        ticket.setBookingReference("");

        // Assert
        assertEquals("", ticket.getBookingReference());
    }

    @Test
    @DisplayName("Should handle UUID format bookingReference")
    void testUUIDBookingReference() {
        // Arrange
        String uuid = "550e8400-e29b-41d4-a716-446655440000";

        // Act
        ticket.setBookingReference(uuid);

        // Assert
        assertEquals(uuid, ticket.getBookingReference());
    }

    @Test
    @DisplayName("Should handle null IDs")
    void testNullIds() {
        // Arrange & Act
        ticket.setCustomerId(null);
        ticket.setPassengerId(null);
        ticket.setSeatId(null);
        ticket.setFlightId(null);

        // Assert
        assertNull(ticket.getCustomerId());
        assertNull(ticket.getPassengerId());
        assertNull(ticket.getSeatId());
        assertNull(ticket.getFlightId());
    }

    @Test
    @DisplayName("Should handle empty IDs")
    void testEmptyIds() {
        // Arrange & Act
        ticket.setCustomerId("");
        ticket.setPassengerId("");
        ticket.setSeatId("");
        ticket.setFlightId("");

        // Assert
        assertEquals("", ticket.getCustomerId());
        assertEquals("", ticket.getPassengerId());
        assertEquals("", ticket.getSeatId());
        assertEquals("", ticket.getFlightId());
    }

    @Test
    @DisplayName("Should handle null display fields")
    void testNullDisplayFields() {
        // Arrange & Act
        ticket.setSeatNumberDisplay(null);
        ticket.setSeatClassDisplay(null);

        // Assert
        assertNull(ticket.getSeatNumberDisplay());
        assertNull(ticket.getSeatClassDisplay());
    }

    @Test
    @DisplayName("Should handle null enriched objects")
    void testNullEnrichedObjects() {
        // Arrange & Act
        ticket.setFlightDetails(null);
        ticket.setPassengerDetails(null);

        // Assert
        assertNull(ticket.getFlightDetails());
        assertNull(ticket.getPassengerDetails());
    }

    // ==================== SEAT NUMBER DISPLAY TESTS ====================

    @Test
    @DisplayName("Should handle numeric seat number display")
    void testNumericSeatNumberDisplay() {
        // Arrange & Act
        ticket.setSeatNumberDisplay("101");

        // Assert
        assertEquals("101", ticket.getSeatNumberDisplay());
    }

    @Test
    @DisplayName("Should handle alphanumeric seat number display")
    void testAlphanumericSeatNumberDisplay() {
        // Arrange & Act
        ticket.setSeatNumberDisplay("12A");

        // Assert
        assertEquals("12A", ticket.getSeatNumberDisplay());
    }

    @Test
    @DisplayName("Should handle various seat number formats")
    void testVariousSeatNumberFormats() {
        // Format: row + letter
        ticket.setSeatNumberDisplay("15C");
        assertEquals("15C", ticket.getSeatNumberDisplay());

        // Format: numeric only
        ticket.setSeatNumberDisplay("250");
        assertEquals("250", ticket.getSeatNumberDisplay());

        // Format: with dash
        ticket.setSeatNumberDisplay("Row-12A");
        assertEquals("Row-12A", ticket.getSeatNumberDisplay());
    }

    // ==================== SEAT CLASS DISPLAY TESTS ====================

    @Test
    @DisplayName("Should handle Economy class display")
    void testEconomyClassDisplay() {
        // Arrange & Act
        ticket.setSeatClassDisplay("Economy");

        // Assert
        assertEquals("Economy", ticket.getSeatClassDisplay());
    }

    @Test
    @DisplayName("Should handle Business class display")
    void testBusinessClassDisplay() {
        // Arrange & Act
        ticket.setSeatClassDisplay("Business");

        // Assert
        assertEquals("Business", ticket.getSeatClassDisplay());
    }

    @Test
    @DisplayName("Should handle First Class display")
    void testFirstClassDisplay() {
        // Arrange & Act
        ticket.setSeatClassDisplay("First Class");

        // Assert
        assertEquals("First Class", ticket.getSeatClassDisplay());
    }

    @Test
    @DisplayName("Should handle Premium Economy class display")
    void testPremiumEconomyClassDisplay() {
        // Arrange & Act
        ticket.setSeatClassDisplay("Premium Economy");

        // Assert
        assertEquals("Premium Economy", ticket.getSeatClassDisplay());
    }

    // ==================== SCENARIO TESTS ====================

    @Test
    @DisplayName("Should represent economy ticket")
    void testEconomyTicket() {
        // Arrange & Act
        ticket.setDocumentId("ticket-economy-1");
        ticket.setBookingReference("ECO123");
        ticket.setCustomerId("customer-eco");
        ticket.setSeatNumberDisplay("301");
        ticket.setSeatClassDisplay("Economy");
        ticket.setFlightId("F100");

        // Assert
        assertEquals("Economy", ticket.getSeatClassDisplay());
        assertEquals("ECO123", ticket.getBookingReference());
    }

    @Test
    @DisplayName("Should represent business ticket")
    void testBusinessTicket() {
        // Arrange & Act
        ticket.setDocumentId("ticket-business-1");
        ticket.setBookingReference("BUS456");
        ticket.setCustomerId("customer-bus");
        ticket.setSeatNumberDisplay("5A");
        ticket.setSeatClassDisplay("Business");
        ticket.setFlightId("F200");

        // Assert
        assertEquals("Business", ticket.getSeatClassDisplay());
        assertEquals("BUS456", ticket.getBookingReference());
    }

    @Test
    @DisplayName("Should represent ticket with minimal information")
    void testMinimalTicket() {
        // Arrange & Act
        ticket.setDocumentId("ticket-minimal");
        ticket.setBookingReference("MIN999");

        // Assert
        assertEquals("ticket-minimal", ticket.getDocumentId());
        assertEquals("MIN999", ticket.getBookingReference());
        assertNull(ticket.getCustomerId());
        assertNull(ticket.getFlightDetails());
        assertNull(ticket.getPassengerDetails());
    }

    @Test
    @DisplayName("Should represent fully enriched ticket")
    void testFullyEnrichedTicket() {
        // Arrange & Act
        ticket.setDocumentId("ticket-full");
        ticket.setBookingReference("FULL001");
        ticket.setCustomerId("customer-full");
        ticket.setPassengerId("passenger-full");
        ticket.setSeatId("seat-full");
        ticket.setFlightId("F300");
        ticket.setSeatNumberDisplay("10B");
        ticket.setSeatClassDisplay("Business");
        ticket.setFlightDetails(testFlight);
        ticket.setPassengerDetails(testPassenger);

        // Assert
        assertNotNull(ticket.getDocumentId());
        assertNotNull(ticket.getBookingReference());
        assertNotNull(ticket.getCustomerId());
        assertNotNull(ticket.getPassengerId());
        assertNotNull(ticket.getSeatId());
        assertNotNull(ticket.getFlightId());
        assertNotNull(ticket.getSeatNumberDisplay());
        assertNotNull(ticket.getSeatClassDisplay());
        assertNotNull(ticket.getFlightDetails());
        assertNotNull(ticket.getPassengerDetails());
    }

    @Test
    @DisplayName("Should update booking reference")
    void testUpdateBookingReference() {
        // Initial
        ticket.setBookingReference("OLD123");
        assertEquals("OLD123", ticket.getBookingReference());

        // Update
        ticket.setBookingReference("NEW456");
        assertEquals("NEW456", ticket.getBookingReference());
    }

    @Test
    @DisplayName("Should handle ticket with flight but no passenger")
    void testTicketWithFlightNoPassenger() {
        // Arrange & Act
        ticket.setFlightDetails(testFlight);
        ticket.setPassengerDetails(null);

        // Assert
        assertNotNull(ticket.getFlightDetails());
        assertNull(ticket.getPassengerDetails());
    }

    @Test
    @DisplayName("Should handle ticket with passenger but no flight")
    void testTicketWithPassengerNoFlight() {
        // Arrange & Act
        ticket.setFlightDetails(null);
        ticket.setPassengerDetails(testPassenger);

        // Assert
        assertNull(ticket.getFlightDetails());
        assertNotNull(ticket.getPassengerDetails());
    }

    @Test
    @DisplayName("Should handle long booking reference")
    void testLongBookingReference() {
        // Arrange
        String longRef = "BOOKING-REF-1234567890-ABCDEFGHIJ-LONG";

        // Act
        ticket.setBookingReference(longRef);

        // Assert
        assertEquals(longRef, ticket.getBookingReference());
    }

    @Test
    @DisplayName("Should handle special characters in booking reference")
    void testSpecialCharactersBookingReference() {
        // Arrange
        String specialRef = "REF#2023-12-15@FLIGHT-F001";

        // Act
        ticket.setBookingReference(specialRef);

        // Assert
        assertEquals(specialRef, ticket.getBookingReference());
    }
}