package com.example.springboot.controller;

import com.example.springboot.dto.request.BookingRequestDTO;
import com.example.springboot.model.*;
import com.example.springboot.service.*;
import com.example.springboot.strategy.PricingContext;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for BookingController
 * 
 * Tests Modules:
 * - Customer Display Seat Module (getSeats)
 * - Customer Booking Ticket Module (confirmBooking)
 * - Customer Payment Module (initiatePayment)
 */
@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private TicketDocumentService ticketDocumentService;

    @Mock
    private PricingContext pricingContext;

    @InjectMocks
    private BookingController bookingController;

    private Seat testSeat;
    private Flight testFlight;
    private Ticket testTicket;
    private Passenger testPassenger;

    @BeforeEach
    void setUp() {
        // Create test seat
        testSeat = new Seat();
        testSeat.setDocumentId("seat123");
        testSeat.setSeatNumber(101);
        testSeat.setTypeOfSeat("Economy");
        testSeat.setStatusSeat("Empty");
        testSeat.setFlightId("F001");

        // Create test flight
        testFlight = new Flight();
        testFlight.setDocumentId("flight123");
        testFlight.setFlightId("F001");
        testFlight.setDepartureCountry("Malaysia");
        testFlight.setArrivalCountry("Japan");
        testFlight.setEconomyPrice(200.00);
        testFlight.setBusinessPrice(400.00);

        // Create test passenger
        testPassenger = new Passenger();
        testPassenger.setDocumentId("pass123");
        testPassenger.setFullName("John Doe");
        testPassenger.setPassportNo("A12345678");
        testPassenger.setEmail("john@example.com");
        testPassenger.setPhoneNumber("012-3456789");

        // Create test ticket
        testTicket = new Ticket();
        testTicket.setDocumentId("ticket123");
        testTicket.setBookingReference("ABC12345");
        testTicket.setCustomerId("cust123");
        testTicket.setPassengerId("pass123");
        testTicket.setSeatId("seat123");
        testTicket.setFlightId("F001");
    }

    // ========== Get Seats Tests (Display Seat Module) ==========

    @Test
    void testGetSeats_Success() throws Exception {
        // Arrange
        List<Seat> seats = Arrays.asList(testSeat);
        when(bookingService.getSeatsByFlightId("F001")).thenReturn(seats);

        // Act
        ResponseEntity<?> response = bookingController.getSeats("F001");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals(1, body.get("count"));

        @SuppressWarnings("unchecked")
        List<Seat> resultSeats = (List<Seat>) body.get("seats");
        assertEquals(1, resultSeats.size());
        assertEquals(101, resultSeats.get(0).getSeatNumber());

        verify(bookingService).getSeatsByFlightId("F001");
    }

    @Test
    void testGetSeats_NoSeatsAvailable() throws Exception {
        // Arrange
        when(bookingService.getSeatsByFlightId("F001")).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<?> response = bookingController.getSeats("F001");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals(0, body.get("count"));
    }

    @Test
    void testGetSeats_ServiceException() throws Exception {
        // Arrange
        when(bookingService.getSeatsByFlightId("F001"))
                .thenThrow(new RuntimeException("Failed to load seats"));

        // Act
        ResponseEntity<?> response = bookingController.getSeats("F001");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertTrue(body.get("message").toString().contains("Failed to load seats"));
    }

    // ========== Initiate Payment Tests (Payment Module) ==========

    @Test
    void testInitiatePayment_Success() throws Exception {
        // Arrange
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("amount", 200.0);

        PaymentIntent mockIntent = mock(PaymentIntent.class);
        when(mockIntent.getClientSecret()).thenReturn("pi_secret_12345");
        when(paymentService.createPaymentIntent(200.0, "myr")).thenReturn(mockIntent);

        // Act
        ResponseEntity<?> response = bookingController.initiatePayment(paymentData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("pi_secret_12345", body.get("clientSecret"));

        verify(paymentService).createPaymentIntent(200.0, "myr");
    }

    @Test
    void testInitiatePayment_InvalidAmount() {
        // Arrange
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("amount", "invalid");

        // Act
        ResponseEntity<?> response = bookingController.initiatePayment(paymentData);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testInitiatePayment_StripeException() throws Exception {
        // Arrange
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("amount", 200.0);

        when(paymentService.createPaymentIntent(anyDouble(), anyString()))
                .thenThrow(new RuntimeException("Stripe API error"));

        // Act
        ResponseEntity<?> response = bookingController.initiatePayment(paymentData);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ========== Confirm Booking Tests (Booking Ticket Module) ==========

    @Test
    void testConfirmBooking_Success() throws Exception {
        // Arrange
        BookingRequestDTO request = new BookingRequestDTO();
        request.setCustomerId("cust123");
        request.setSeatId("seat123");
        request.setAmount(200.0);
        request.setStripePaymentIntentId("pi_123");
        request.setPassenger(testPassenger);

        when(bookingService.processBooking(any(BookingRequestDTO.class)))
                .thenReturn(testTicket);

        // Act
        ResponseEntity<?> response = bookingController.confirmBooking(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Booking Successful", body.get("message"));
        assertEquals("ticket123", body.get("ticketId"));

        verify(bookingService).processBooking(request);
    }

    @Test
    void testConfirmBooking_ValidationFailure() throws Exception {
        // Arrange
        BookingRequestDTO request = new BookingRequestDTO();
        request.setCustomerId("cust123");
        request.setSeatId("seat123");
        // Missing required fields

        when(bookingService.processBooking(any(BookingRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid passenger data"));

        // Act
        ResponseEntity<?> response = bookingController.confirmBooking(request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Booking Failed"));
    }

    @Test
    void testConfirmBooking_SeatAlreadyBooked() throws Exception {
        // Arrange
        BookingRequestDTO request = new BookingRequestDTO();
        request.setCustomerId("cust123");
        request.setSeatId("seat123");
        request.setPassenger(testPassenger);

        when(bookingService.processBooking(any(BookingRequestDTO.class)))
                .thenThrow(new IllegalStateException("Seat already booked"));

        // Act
        ResponseEntity<?> response = bookingController.confirmBooking(request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ========== Download Ticket Tests ==========

    @Test
    void testDownloadTicket_Success() throws Exception {
        // Arrange
        byte[] pdfBytes = "PDF_CONTENT".getBytes();
        when(bookingService.getTicketDetails("ticket123")).thenReturn(testTicket);
        when(ticketDocumentService.generateTicketPdf(testTicket)).thenReturn(pdfBytes);

        // Act
        ResponseEntity<byte[]> response = bookingController.downloadTicket("ticket123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(pdfBytes, response.getBody());
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());

        verify(bookingService).getTicketDetails("ticket123");
        verify(ticketDocumentService).generateTicketPdf(testTicket);
    }

    @Test
    void testDownloadTicket_TicketNotFound() throws Exception {
        // Arrange
        when(bookingService.getTicketDetails("invalid"))
                .thenThrow(new RuntimeException("Ticket not found"));

        // Act
        ResponseEntity<byte[]> response = bookingController.downloadTicket("invalid");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testDownloadTicket_PDFGenerationFailure() throws Exception {
        // Arrange
        when(bookingService.getTicketDetails("ticket123")).thenReturn(testTicket);
        when(ticketDocumentService.generateTicketPdf(testTicket))
                .thenThrow(new RuntimeException("PDF generation failed"));

        // Act
        ResponseEntity<byte[]> response = bookingController.downloadTicket("ticket123");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ========== Calculate Price Tests ==========

    @Test
    void testCalculatePrice_Success() throws Exception {
        // Arrange
        when(bookingService.getSeatById("seat123")).thenReturn(testSeat);
        when(bookingService.getFlightBySeatId("seat123")).thenReturn(testFlight);
        when(bookingService.calculateSeatPrice(testSeat, testFlight)).thenReturn(200.0);
        when(pricingContext.getBenefits("Economy")).thenReturn("Standard seating, 20kg baggage");

        // Act
        ResponseEntity<?> response = bookingController.calculatePrice("seat123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals("seat123", body.get("seatId"));
        assertEquals("Economy", body.get("seatClass"));
        assertEquals(200.0, body.get("price"));

        verify(bookingService).getSeatById("seat123");
        verify(bookingService).getFlightBySeatId("seat123");
        verify(bookingService).calculateSeatPrice(testSeat, testFlight);
    }

    @Test
    void testCalculatePrice_SeatNotFound() throws Exception {
        // Arrange
        when(bookingService.getSeatById("invalid"))
                .thenThrow(new RuntimeException("Seat not found"));

        // Act
        ResponseEntity<?> response = bookingController.calculatePrice("invalid");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("success"));
    }

    // ========== Edge Case Tests ==========

    @Test
    void testGetSeats_NullFlightId() throws Exception {
        // Arrange
        when(bookingService.getSeatsByFlightId(null))
                .thenThrow(new IllegalArgumentException("Flight ID cannot be null"));

        // Act
        ResponseEntity<?> response = bookingController.getSeats(null);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testConfirmBooking_NullRequest() {
        // Act
        ResponseEntity<?> response = bookingController.confirmBooking(null);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testInitiatePayment_ZeroAmount() throws Exception {
        // Arrange
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("amount", 0.0);

        PaymentIntent mockIntent = mock(PaymentIntent.class);
        when(mockIntent.getClientSecret()).thenReturn("pi_secret_12345");
        when(paymentService.createPaymentIntent(0.0, "myr")).thenReturn(mockIntent);

        // Act
        ResponseEntity<?> response = bookingController.initiatePayment(paymentData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInitiatePayment_NegativeAmount() {
        // Arrange
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("amount", -100.0);

        // Act
        ResponseEntity<?> response = bookingController.initiatePayment(paymentData);

        // Assert
        // Should handle negative amount gracefully
        assertNotNull(response);
    }
}