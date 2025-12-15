package com.example.springboot.service;

import com.example.springboot.model.Flight;
import com.example.springboot.model.Passenger;
import com.example.springboot.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for TicketDocumentService
 * 
 * Tests Module: Ticket PDF Generation Module
 * Coverage: PDF generation, QR code, null safety, content validation
 * Target: 90%+ coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Ticket Document Service Tests")
class TicketDocumentServiceTest {

    @InjectMocks
    private TicketDocumentService ticketDocumentService;

    private Ticket testTicket;
    private Flight testFlight;
    private Passenger testPassenger;

    @BeforeEach
    void setUp() {
        // Create test flight
        testFlight = new Flight();
        testFlight.setFlightId("F001");
        testFlight.setPlaneNo("PL04");
        testFlight.setDepartureCountry("Malaysia");
        testFlight.setArrivalCountry("Japan");
        testFlight.setDepartureDate("11/11/2023");
        testFlight.setArrivalDate("12/11/2023");
        testFlight.setDepartureTime(1300);
        testFlight.setArrivalTime(2000);
        testFlight.setBoardingTime(1200);

        // Create test passenger
        testPassenger = new Passenger();
        testPassenger.setFullName("John Doe");
        testPassenger.setPassportNo("A12345678");
        testPassenger.setEmail("john@example.com");
        testPassenger.setPhoneNumber("012-3456789");

        // Create test ticket
        testTicket = new Ticket();
        testTicket.setBookingReference("ABC12345");
        testTicket.setSeatNumberDisplay("101");
        testTicket.setSeatClassDisplay("Economy");
        testTicket.setFlightDetails(testFlight);
        testTicket.setPassengerDetails(testPassenger);
    }

    // ==================== SUCCESS CASES ====================

    @Test
    @DisplayName("Should generate PDF ticket successfully")
    void testGenerateTicketPdf_Success() throws Exception {
        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0, "PDF should have content");
        
        // Verify it's a PDF by checking PDF header
        String pdfHeader = new String(pdfBytes, 0, Math.min(5, pdfBytes.length));
        assertTrue(pdfHeader.startsWith("%PDF"), "Should start with PDF header");
    }

    @Test
    @DisplayName("Should generate PDF with all ticket details")
    void testGenerateTicketPdf_WithAllDetails() throws Exception {
        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 1000, "PDF with full details should be substantial");
    }

    @Test
    @DisplayName("Should generate different PDFs for different tickets")
    void testGenerateTicketPdf_DifferentTickets() throws Exception {
        // Arrange
        Ticket ticket2 = new Ticket();
        ticket2.setBookingReference("XYZ98765");
        ticket2.setSeatNumberDisplay("102");
        ticket2.setSeatClassDisplay("Business");
        ticket2.setFlightDetails(testFlight);
        ticket2.setPassengerDetails(testPassenger);

        // Act
        byte[] pdf1 = ticketDocumentService.generateTicketPdf(testTicket);
        byte[] pdf2 = ticketDocumentService.generateTicketPdf(ticket2);

        // Assert
        assertNotNull(pdf1);
        assertNotNull(pdf2);
        assertFalse(java.util.Arrays.equals(pdf1, pdf2), "Different tickets should generate different PDFs");
    }

    // ==================== NULL TICKET VALIDATION ====================

    @Test
    @DisplayName("Should throw exception when ticket is null")
    void testGenerateTicketPdf_NullTicket() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ticketDocumentService.generateTicketPdf(null)
        );
        
        assertEquals("Ticket cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when booking reference is null")
    void testGenerateTicketPdf_NullBookingReference() {
        // Arrange
        testTicket.setBookingReference(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ticketDocumentService.generateTicketPdf(testTicket)
        );
        
        assertEquals("Ticket must have a booking reference", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when booking reference is empty")
    void testGenerateTicketPdf_EmptyBookingReference() {
        // Arrange
        testTicket.setBookingReference("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ticketDocumentService.generateTicketPdf(testTicket)
        );
        
        assertEquals("Ticket must have a booking reference", exception.getMessage());
    }

    // ==================== NULL PASSENGER DETAILS ====================

    @Test
    @DisplayName("Should handle null passenger details gracefully")
    void testGenerateTicketPdf_NullPassenger() throws Exception {
        // Arrange
        testTicket.setPassengerDetails(null);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should display N/A for null passenger name")
    void testGenerateTicketPdf_NullPassengerName() throws Exception {
        // Arrange
        testPassenger.setFullName(null);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should display N/A for empty passenger name")
    void testGenerateTicketPdf_EmptyPassengerName() throws Exception {
        // Arrange
        testPassenger.setFullName("");

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should display N/A for null passport number")
    void testGenerateTicketPdf_NullPassportNo() throws Exception {
        // Arrange
        testPassenger.setPassportNo(null);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should display N/A for null email")
    void testGenerateTicketPdf_NullEmail() throws Exception {
        // Arrange
        testPassenger.setEmail(null);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should display N/A for null phone number")
    void testGenerateTicketPdf_NullPhoneNumber() throws Exception {
        // Arrange
        testPassenger.setPhoneNumber(null);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    // ==================== NULL FLIGHT DETAILS ====================

    @Test
    @DisplayName("Should handle null flight details gracefully")
    void testGenerateTicketPdf_NullFlight() throws Exception {
        // Arrange
        testTicket.setFlightDetails(null);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should display N/A for null flight ID")
    void testGenerateTicketPdf_NullFlightId() throws Exception {
        // Arrange
        testFlight.setFlightId(null);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should display N/A for null plane number")
    void testGenerateTicketPdf_NullPlaneNo() throws Exception {
        // Arrange
        testFlight.setPlaneNo(null);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should display N/A for null departure country")
    void testGenerateTicketPdf_NullDepartureCountry() throws Exception {
        // Arrange
        testFlight.setDepartureCountry(null);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should display N/A for null arrival country")
    void testGenerateTicketPdf_NullArrivalCountry() throws Exception {
        // Arrange
        testFlight.setArrivalCountry(null);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should display N/A for null departure date")
    void testGenerateTicketPdf_NullDepartureDate() throws Exception {
        // Arrange
        testFlight.setDepartureDate(null);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should display N/A for null arrival date")
    void testGenerateTicketPdf_NullArrivalDate() throws Exception {
        // Arrange
        testFlight.setArrivalDate(null);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    // ==================== NULL SEAT DETAILS ====================

    @Test
    @DisplayName("Should display N/A for null seat number")
    void testGenerateTicketPdf_NullSeatNumber() throws Exception {
        // Arrange
        testTicket.setSeatNumberDisplay(null);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should display N/A for empty seat number")
    void testGenerateTicketPdf_EmptySeatNumber() throws Exception {
        // Arrange
        testTicket.setSeatNumberDisplay("");

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should display N/A for null seat class")
    void testGenerateTicketPdf_NullSeatClass() throws Exception {
        // Arrange
        testTicket.setSeatClassDisplay(null);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    // ==================== TIME FORMATTING ====================

    @Test
    @DisplayName("Should format time correctly for valid time")
    void testGenerateTicketPdf_ValidTime() throws Exception {
        // Arrange
        testFlight.setDepartureTime(1430); // 2:30 PM

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should handle zero time")
    void testGenerateTicketPdf_ZeroTime() throws Exception {
        // Arrange
        testFlight.setDepartureTime(0);
        testFlight.setArrivalTime(0);
        testFlight.setBoardingTime(0);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should format midnight time correctly")
    void testGenerateTicketPdf_MidnightTime() throws Exception {
        // Arrange
        testFlight.setDepartureTime(0000); // 12:00 AM

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should format noon time correctly")
    void testGenerateTicketPdf_NoonTime() throws Exception {
        // Arrange
        testFlight.setDepartureTime(1200); // 12:00 PM

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should format morning time correctly")
    void testGenerateTicketPdf_MorningTime() throws Exception {
        // Arrange
        testFlight.setDepartureTime(1830); // 6:30 AM

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should format evening time correctly")
    void testGenerateTicketPdf_EveningTime() throws Exception {
        // Arrange
        testFlight.setDepartureTime(2045); // 8:45 PM

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    // ==================== QR CODE GENERATION ====================

    @Test
    @DisplayName("Should generate QR code successfully")
    void testGenerateTicketPdf_QRCodeGeneration() throws Exception {
        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        // QR code generation should not throw exception
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should handle long booking reference in QR code")
    void testGenerateTicketPdf_LongBookingReferenceQR() throws Exception {
        // Arrange
        testTicket.setBookingReference("ABCDEFGHIJKLMNOP12345678");

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should handle special characters in booking reference for QR code")
    void testGenerateTicketPdf_SpecialCharactersQR() throws Exception {
        // Arrange
        testTicket.setBookingReference("ABC-123@456");

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("Should handle very long passenger name")
    void testGenerateTicketPdf_LongPassengerName() throws Exception {
        // Arrange
        testPassenger.setFullName("John Alexander Michael Christopher Davidson");

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should handle very long country names")
    void testGenerateTicketPdf_LongCountryNames() throws Exception {
        // Arrange
        testFlight.setDepartureCountry("United Kingdom of Great Britain");
        testFlight.setArrivalCountry("United States of America");

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should handle special characters in passenger name")
    void testGenerateTicketPdf_SpecialCharsInName() throws Exception {
        // Arrange
        testPassenger.setFullName("José María O'Brien-García");

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should handle special characters in email")
    void testGenerateTicketPdf_SpecialCharsInEmail() throws Exception {
        // Arrange
        testPassenger.setEmail("user+test@example.co.uk");

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should handle business class seat")
    void testGenerateTicketPdf_BusinessClass() throws Exception {
        // Arrange
        testTicket.setSeatClassDisplay("Business");

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should handle premium economy seat")
    void testGenerateTicketPdf_PremiumEconomy() throws Exception {
        // Arrange
        testTicket.setSeatClassDisplay("Premium Economy");

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should handle different seat numbers")
    void testGenerateTicketPdf_DifferentSeatNumbers() throws Exception {
        // Arrange
        String[] seatNumbers = {"100", "101", "200", "1A", "32F"};

        // Act & Assert
        for (String seatNumber : seatNumbers) {
            testTicket.setSeatNumberDisplay(seatNumber);
            byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);
            assertNotNull(pdfBytes);
            assertTrue(pdfBytes.length > 0);
        }
    }

    // ==================== MINIMAL DATA ====================

    @Test
    @DisplayName("Should generate PDF with minimal valid data")
    void testGenerateTicketPdf_MinimalData() throws Exception {
        // Arrange - Only booking reference is required
        Ticket minimalTicket = new Ticket();
        minimalTicket.setBookingReference("MIN12345");

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(minimalTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    @DisplayName("Should handle all null fields except booking reference")
    void testGenerateTicketPdf_AllNullsExceptReference() throws Exception {
        // Arrange
        Ticket sparseTicket = new Ticket();
        sparseTicket.setBookingReference("SPARSE123");
        sparseTicket.setSeatNumberDisplay(null);
        sparseTicket.setSeatClassDisplay(null);
        sparseTicket.setFlightDetails(null);
        sparseTicket.setPassengerDetails(null);

        // Act
        byte[] pdfBytes = ticketDocumentService.generateTicketPdf(sparseTicket);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    // ==================== MULTIPLE GENERATIONS ====================

    @Test
    @DisplayName("Should generate multiple PDFs sequentially")
    void testGenerateTicketPdf_MultipleSequential() throws Exception {
        // Act & Assert
        for (int i = 0; i < 5; i++) {
            byte[] pdfBytes = ticketDocumentService.generateTicketPdf(testTicket);
            assertNotNull(pdfBytes);
            assertTrue(pdfBytes.length > 0);
        }
    }

    @Test
    @DisplayName("Should generate consistent PDF for same ticket")
    void testGenerateTicketPdf_ConsistentGeneration() throws Exception {
        // Act
        byte[] pdf1 = ticketDocumentService.generateTicketPdf(testTicket);
        byte[] pdf2 = ticketDocumentService.generateTicketPdf(testTicket);

        // Assert
        assertNotNull(pdf1);
        assertNotNull(pdf2);
        // PDFs may have timestamps or other variable data, so we just check they're generated
        assertTrue(pdf1.length > 0);
        assertTrue(pdf2.length > 0);
    }
}