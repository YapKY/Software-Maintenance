package com.example.springboot.controller;

import com.example.springboot.model.*;
import com.example.springboot.service.BookingService;
import com.example.springboot.service.TicketDocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for TicketController
 * 
 * Tests Module: Customer Display Ticket Module
 * Coverage: Get tickets, view ticket details, download PDF
 */
@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private TicketDocumentService ticketDocumentService;

    @InjectMocks
    private TicketController ticketController;

    private Ticket testTicket;
    private Flight testFlight;
    private Passenger testPassenger;
    private List<Ticket> testTickets;

    @BeforeEach
    void setUp() {
        // Create test flight
        testFlight = new Flight();
        testFlight.setDocumentId("flight123");
        testFlight.setFlightId("F001");
        testFlight.setDepartureCountry("Malaysia");
        testFlight.setArrivalCountry("Japan");
        testFlight.setDepartureDate("11/11/2023");
        testFlight.setArrivalDate("12/11/2023");
        testFlight.setDepartureTime(1300);
        testFlight.setArrivalTime(2000);

        // Create test passenger
        testPassenger = new Passenger();
        testPassenger.setDocumentId("pass123");
        testPassenger.setFullName("John Doe");
        testPassenger.setPassportNo("A12345678");
        testPassenger.setEmail("john@example.com");

        // Create test ticket
        testTicket = new Ticket();
        testTicket.setDocumentId("ticket123");
        testTicket.setBookingReference("ABC12345");
        testTicket.setCustomerId("cust123");
        testTicket.setPassengerId("pass123");
        testTicket.setSeatId("seat123");
        testTicket.setFlightId("F001");
        testTicket.setSeatNumberDisplay("101");
        testTicket.setSeatClassDisplay("Economy");
        testTicket.setFlightDetails(testFlight);
        testTicket.setPassengerDetails(testPassenger);

        testTickets = Arrays.asList(testTicket);
    }

    // ========== Get Customer Tickets Tests ==========

    @Test
    void testGetCustomerTickets_Success() throws Exception {
        // Arrange
        when(bookingService.getCustomerTickets("cust123")).thenReturn(testTickets);

        // Act
        ResponseEntity<?> response = ticketController.getCustomerTickets("cust123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals(1, body.get("count"));

        @SuppressWarnings("unchecked")
        List<Ticket> tickets = (List<Ticket>) body.get("tickets");
        assertEquals(1, tickets.size());
        assertEquals("ABC12345", tickets.get(0).getBookingReference());

        verify(bookingService).getCustomerTickets("cust123");
    }

    @Test
    void testGetCustomerTickets_NoTickets() throws Exception {
        // Arrange
        when(bookingService.getCustomerTickets("cust999")).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<?> response = ticketController.getCustomerTickets("cust999");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals(0, body.get("count"));
        
        @SuppressWarnings("unchecked")
        List<Ticket> tickets = (List<Ticket>) body.get("tickets");
        assertTrue(tickets.isEmpty());
    }

    @Test
    void testGetCustomerTickets_MultipleTickets() throws Exception {
        // Arrange
        Ticket ticket2 = new Ticket();
        ticket2.setDocumentId("ticket456");
        ticket2.setBookingReference("XYZ98765");
        ticket2.setCustomerId("cust123");

        List<Ticket> multipleTickets = Arrays.asList(testTicket, ticket2);
        when(bookingService.getCustomerTickets("cust123")).thenReturn(multipleTickets);

        // Act
        ResponseEntity<?> response = ticketController.getCustomerTickets("cust123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(2, body.get("count"));
    }

    @Test
    void testGetCustomerTickets_ServiceException() throws Exception {
        // Arrange
        when(bookingService.getCustomerTickets("cust123"))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ResponseEntity<?> response = ticketController.getCustomerTickets("cust123");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertTrue(body.get("message").toString().contains("Failed to load tickets"));
    }

    // ========== Get Single Ticket Tests ==========

    @Test
    void testGetTicket_Success() throws Exception {
        // Arrange
        when(bookingService.getTicketDetails("ticket123")).thenReturn(testTicket);

        // Act
        ResponseEntity<?> response = ticketController.getTicket("ticket123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));

        Ticket ticket = (Ticket) body.get("ticket");
        assertEquals("ABC12345", ticket.getBookingReference());
        assertEquals("cust123", ticket.getCustomerId());

        verify(bookingService).getTicketDetails("ticket123");
    }

    @Test
    void testGetTicket_NotFound() throws Exception {
        // Arrange
        when(bookingService.getTicketDetails("invalid"))
                .thenThrow(new RuntimeException("Ticket not found"));

        // Act
        ResponseEntity<?> response = ticketController.getTicket("invalid");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertTrue(body.get("message").toString().contains("Ticket not found"));
    }

    @Test
    void testGetTicket_WithFullDetails() throws Exception {
        // Arrange - ticket with all enriched data
        when(bookingService.getTicketDetails("ticket123")).thenReturn(testTicket);

        // Act
        ResponseEntity<?> response = ticketController.getTicket("ticket123");

        // Assert
        Ticket ticket = (Ticket) ((Map<?, ?>) response.getBody()).get("ticket");
        assertNotNull(ticket.getFlightDetails());
        assertNotNull(ticket.getPassengerDetails());
        assertEquals("F001", ticket.getFlightDetails().getFlightId());
        assertEquals("John Doe", ticket.getPassengerDetails().getFullName());
    }

    // ========== Download Ticket PDF Tests ==========

    @Test
    void testDownloadTicket_Success() throws Exception {
        // Arrange
        byte[] pdfContent = "PDF_CONTENT_BYTES".getBytes();
        when(bookingService.getTicketDetails("ticket123")).thenReturn(testTicket);
        when(ticketDocumentService.generateTicketPdf(testTicket)).thenReturn(pdfContent);

        // Act
        ResponseEntity<byte[]> response = ticketController.downloadTicket("ticket123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(pdfContent, response.getBody());
        
        // Verify headers
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
        assertTrue(response.getHeaders().getContentDisposition().toString()
                .contains("ticket-ABC12345.pdf"));

        verify(bookingService).getTicketDetails("ticket123");
        verify(ticketDocumentService).generateTicketPdf(testTicket);
    }

    @Test
    void testDownloadTicket_TicketNotFound() throws Exception {
        // Arrange
        when(bookingService.getTicketDetails("invalid"))
                .thenThrow(new RuntimeException("Ticket not found"));

        // Act
        ResponseEntity<byte[]> response = ticketController.downloadTicket("invalid");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(ticketDocumentService, never()).generateTicketPdf(any());
    }

    @Test
    void testDownloadTicket_PDFGenerationFails() throws Exception {
        // Arrange
        when(bookingService.getTicketDetails("ticket123")).thenReturn(testTicket);
        when(ticketDocumentService.generateTicketPdf(testTicket))
                .thenThrow(new RuntimeException("PDF generation failed"));

        // Act
        ResponseEntity<byte[]> response = ticketController.downloadTicket("ticket123");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testDownloadTicket_EmptyPDF() throws Exception {
        // Arrange
        byte[] emptyPdf = new byte[0];
        when(bookingService.getTicketDetails("ticket123")).thenReturn(testTicket);
        when(ticketDocumentService.generateTicketPdf(testTicket)).thenReturn(emptyPdf);

        // Act
        ResponseEntity<byte[]> response = ticketController.downloadTicket("ticket123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length);
    }

    // ========== Edge Case Tests ==========

    @Test
    void testGetCustomerTickets_NullCustomerId() throws Exception {
        // Arrange
        when(bookingService.getCustomerTickets(null))
                .thenThrow(new IllegalArgumentException("Customer ID cannot be null"));

        // Act
        ResponseEntity<?> response = ticketController.getCustomerTickets(null);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetCustomerTickets_EmptyCustomerId() throws Exception {
        // Arrange
        when(bookingService.getCustomerTickets(""))
                .thenReturn(Arrays.asList());

        // Act
        ResponseEntity<?> response = ticketController.getCustomerTickets("");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetTicket_NullTicketId() throws Exception {
        // Arrange
        when(bookingService.getTicketDetails(null))
                .thenThrow(new IllegalArgumentException("Ticket ID cannot be null"));

        // Act
        ResponseEntity<?> response = ticketController.getTicket(null);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDownloadTicket_NullTicketId() throws Exception {
        // Arrange
        when(bookingService.getTicketDetails(null))
                .thenThrow(new IllegalArgumentException("Ticket ID cannot be null"));

        // Act
        ResponseEntity<byte[]> response = ticketController.downloadTicket(null);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetCustomerTickets_TicketsWithoutFlightDetails() throws Exception {
        // Arrange - ticket without enriched flight data
        Ticket basicTicket = new Ticket();
        basicTicket.setDocumentId("basic123");
        basicTicket.setBookingReference("BASIC123");
        basicTicket.setCustomerId("cust123");
        // No flight details

        when(bookingService.getCustomerTickets("cust123"))
                .thenReturn(Arrays.asList(basicTicket));

        // Act
        ResponseEntity<?> response = ticketController.getCustomerTickets("cust123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<Ticket> tickets = (List<Ticket>) ((Map<?, ?>) response.getBody()).get("tickets");
        assertNull(tickets.get(0).getFlightDetails());
    }
}