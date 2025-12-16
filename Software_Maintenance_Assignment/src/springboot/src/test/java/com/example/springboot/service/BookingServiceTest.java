package com.example.springboot.service;

import com.example.springboot.dto.request.BookingRequestDTO;
import com.example.springboot.factory.PassengerFactory;
import com.example.springboot.factory.TicketFactory;
import com.example.springboot.model.*;
import com.example.springboot.repository.FirestoreRepository;
import com.example.springboot.strategy.PricingContext;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for BookingService
 * 
 * Tests Module: Booking Management Module
 * Coverage: Booking processing, seat selection, ticket generation, customer bookings
 * Target: 90%+ coverage
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Booking Service Tests")
class BookingServiceTest {

    @Mock
    private FirestoreRepository repository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PricingContext pricingContext;

    @Mock
    private TicketFactory ticketFactory;

    @Mock
    private PassengerFactory passengerFactory;

    @Mock
    private ApiFuture<QuerySnapshot> queryFuture;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private com.google.cloud.firestore.Query query;

    @InjectMocks
    private BookingService bookingService;

    private Flight testFlight;
    private Seat testSeat;
    private Passenger testPassenger;
    private Ticket testTicket;
    private BookingRequestDTO testBookingRequest;

    @BeforeEach
    void setUp() {
        // Setup test flight
        testFlight = new Flight();
        testFlight.setDocumentId("flight-doc-1");
        testFlight.setFlightId("F001");
        testFlight.setDepartureCountry("Malaysia");
        testFlight.setArrivalCountry("Japan");
        testFlight.setEconomyPrice(200.00);
        testFlight.setBusinessPrice(400.00);

        // Setup test seat
        testSeat = new Seat();
        testSeat.setDocumentId("seat-doc-1");
        testSeat.setSeatNumber(101);
        testSeat.setTypeOfSeat("Economy");
        testSeat.setStatusSeat("Available");
        testSeat.setFlightId("F001");

        // Setup test passenger
        testPassenger = new Passenger();
        testPassenger.setDocumentId("passenger-doc-1");
        testPassenger.setFullName("John Doe");
        testPassenger.setPassportNo("A12345678");
        testPassenger.setEmail("john@example.com");
        testPassenger.setPhoneNumber("012-3456789");

        // Setup test ticket
        testTicket = new Ticket();
        testTicket.setDocumentId("ticket-doc-1");
        testTicket.setBookingReference("ABC12345");
        testTicket.setCustomerId("customer-1");
        testTicket.setPassengerId("passenger-doc-1");
        testTicket.setSeatId("seat-doc-1");
        testTicket.setFlightId("F001");

        // Setup test booking request
        testBookingRequest = createTestBookingRequest();
    }

    // ==================== CALCULATE SEAT PRICE TESTS ====================

    @Test
    @DisplayName("Should calculate seat price successfully")
    void testCalculateSeatPrice_Success() throws Exception {
        // Arrange
        when(pricingContext.calculatePrice("Economy", testFlight)).thenReturn(200.00);

        // Act
        double price = bookingService.calculateSeatPrice(testSeat, testFlight);

        // Assert
        assertEquals(200.00, price);
        verify(pricingContext).calculatePrice("Economy", testFlight);
    }

    @Test
    @DisplayName("Should throw exception when flight is null")
    void testCalculateSeatPrice_NullFlight() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookingService.calculateSeatPrice(testSeat, null)
        );
        
        assertEquals("Flight cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should calculate business class price")
    void testCalculateSeatPrice_BusinessClass() throws Exception {
        // Arrange
        testSeat.setTypeOfSeat("Business");
        when(pricingContext.calculatePrice("Business", testFlight)).thenReturn(400.00);

        // Act
        double price = bookingService.calculateSeatPrice(testSeat, testFlight);

        // Assert
        assertEquals(400.00, price);
    }

    @Test
    @DisplayName("Should propagate exception from pricing context")
    void testCalculateSeatPrice_PricingException() throws Exception {
        // Arrange
        when(pricingContext.calculatePrice(anyString(), any(Flight.class)))
            .thenThrow(new RuntimeException("Pricing error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            bookingService.calculateSeatPrice(testSeat, testFlight)
        );
    }

    @Test
    @DisplayName("Should calculate price for null seat type")
    void testCalculateSeatPrice_NullSeatType() throws Exception {
        // Arrange
        testSeat.setTypeOfSeat(null);
        when(pricingContext.calculatePrice(null, testFlight)).thenReturn(0.0);

        // Act
        double price = bookingService.calculateSeatPrice(testSeat, testFlight);

        // Assert
        assertEquals(0.0, price);
    }

    // ==================== GET SEATS BY FLIGHT ID TESTS ====================

    @Test
    @DisplayName("Should get seats by flight ID successfully")
    void testGetSeatsByFlightId_Success() throws Exception {
        // Arrange
        List<QueryDocumentSnapshot> docs = createMockSeatDocuments(3);
        setupRepositoryForSeats(docs);

        // Act
        List<Seat> seats = bookingService.getSeatsByFlightId("F001");

        // Assert
        assertEquals(3, seats.size());
        verify(repository).getCollectionByField("seats", "flightId", "F001");
    }

    @Test
    @DisplayName("Should return empty list when no seats found")
    void testGetSeatsByFlightId_NoSeats() throws Exception {
        // Arrange
        setupRepositoryForSeats(new ArrayList<>());

        // Act
        List<Seat> seats = bookingService.getSeatsByFlightId("F001");

        // Assert
        assertTrue(seats.isEmpty());
    }

    @Test
    @DisplayName("Should set document IDs on retrieved seats")
    void testGetSeatsByFlightId_SetsDocumentIds() throws Exception {
        // Arrange
        List<QueryDocumentSnapshot> docs = createMockSeatDocuments(2);
        setupRepositoryForSeats(docs);

        // Act
        List<Seat> seats = bookingService.getSeatsByFlightId("F001");

        // Assert
        assertEquals("seat-doc-0", seats.get(0).getDocumentId());
        assertEquals("seat-doc-1", seats.get(1).getDocumentId());
    }

    // ==================== GET SEATS FOR FLIGHT TESTS ====================

    @Test
    @DisplayName("Should get seats for flight successfully")
    void testGetSeatsForFlight_Success() throws Exception {
        // Arrange
        List<QueryDocumentSnapshot> docs = createMockSeatDocuments(5);
        setupRepositoryForSeats(docs);

        // Act
        List<Seat> seats = bookingService.getSeatsForFlight("F001");

        // Assert
        assertEquals(5, seats.size());
    }

    @Test
    @DisplayName("Should handle empty seats for flight")
    void testGetSeatsForFlight_Empty() throws Exception {
        // Arrange
        setupRepositoryForSeats(new ArrayList<>());

        // Act
        List<Seat> seats = bookingService.getSeatsForFlight("F001");

        // Assert
        assertTrue(seats.isEmpty());
    }

    // ==================== PROCESS BOOKING TESTS ====================

    @Test
    @DisplayName("Should process booking successfully")
    void testProcessBooking_Success() throws Exception {
        // Arrange
        setupSuccessfulBooking();

        // Act
        Ticket result = bookingService.processBooking(testBookingRequest);

        // Assert
        assertNotNull(result);
        assertEquals("ticket-doc-1", result.getDocumentId());
        verify(repository).findById("seats", "seat-doc-1", Seat.class);
        verify(repository).save(eq("passengers"), any(Passenger.class));
        verify(repository).save(eq("tickets"), any(Ticket.class));
        verify(repository).save(eq("payments"), any(Payment.class));
        verify(repository).updateField("seats", "seat-doc-1", "statusSeat", "Booked");
        verify(notificationService).sendBookingSuccessEmail("john@example.com", "ABC12345");
    }

    @Test
    @DisplayName("Should throw exception when seat not found")
    void testProcessBooking_SeatNotFound() throws Exception {
        // Arrange
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookingService.processBooking(testBookingRequest)
        );
        
        assertEquals("Seat not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when flight not found")
    void testProcessBooking_FlightNotFound() throws Exception {
        // Arrange
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(testSeat);
        when(repository.getCollectionByField("flights", "flightId", "F001")).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookingService.processBooking(testBookingRequest)
        );
        
        assertEquals("Flight not found for seat", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception on price mismatch")
    void testProcessBooking_PriceMismatch() throws Exception {
        // Arrange
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(testSeat);
        setupFlightQuery();
        when(pricingContext.calculatePrice("Economy", testFlight)).thenReturn(200.00);
        testBookingRequest.setAmount(150.00);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookingService.processBooking(testBookingRequest)
        );
        
        assertTrue(exception.getMessage().contains("Price mismatch"));
    }

    @Test
    @DisplayName("Should accept price within tolerance")
    void testProcessBooking_PriceWithinTolerance() throws Exception {
        // Arrange
        setupSuccessfulBooking();
        testBookingRequest.setAmount(200.005);

        // Act
        Ticket result = bookingService.processBooking(testBookingRequest);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should save payment with correct amount")
    void testProcessBooking_PaymentAmount() throws Exception {
        // Arrange
        setupSuccessfulBooking();

        // Act
        bookingService.processBooking(testBookingRequest);

        // Assert
        verify(repository).save(eq("payments"), argThat(payment -> {
            Payment p = (Payment) payment;
            return p.getAmount() == 200.00;
        }));
    }

    @Test
    @DisplayName("Should set passenger document ID after saving")
    void testProcessBooking_PassengerIdSet() throws Exception {
        // Arrange
        setupSuccessfulBooking();

        // Act
        bookingService.processBooking(testBookingRequest);

        // Assert
        verify(repository).save(eq("passengers"), any(Passenger.class));
        verify(passengerFactory).createPassenger("John Doe", "A12345678", "john@example.com", "012-3456789");
    }

    // ==================== GET TICKET DETAILS TESTS ====================

    @Test
    @DisplayName("Should get ticket details with full enrichment")
    void testGetTicketDetails_FullEnrichment() throws Exception {
        // Arrange
        when(repository.findById("tickets", "ticket-doc-1", Ticket.class)).thenReturn(testTicket);
        when(repository.findById("passengers", "passenger-doc-1", Passenger.class)).thenReturn(testPassenger);
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(testSeat);
        setupFlightQuery();

        // Act
        Ticket result = bookingService.getTicketDetails("ticket-doc-1");

        // Assert
        assertNotNull(result);
        assertNotNull(result.getPassengerDetails());
        assertEquals("101", result.getSeatNumberDisplay());
        assertEquals("Economy", result.getSeatClassDisplay());
        assertNotNull(result.getFlightDetails());
    }

    @Test
    @DisplayName("Should throw exception when ticket not found")
    void testGetTicketDetails_TicketNotFound() throws Exception {
        // Arrange
        when(repository.findById("tickets", "ticket-doc-1", Ticket.class)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> bookingService.getTicketDetails("ticket-doc-1")
        );
        
        assertEquals("Ticket not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle missing passenger gracefully")
    void testGetTicketDetails_NoPassenger() throws Exception {
        // Arrange
        when(repository.findById("tickets", "ticket-doc-1", Ticket.class)).thenReturn(testTicket);
        when(repository.findById("passengers", "passenger-doc-1", Passenger.class)).thenReturn(null);
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(testSeat);
        setupFlightQuery();

        // Act
        Ticket result = bookingService.getTicketDetails("ticket-doc-1");

        // Assert
        assertNotNull(result);
        assertNull(result.getPassengerDetails());
    }

    @Test
    @DisplayName("Should handle missing seat gracefully")
    void testGetTicketDetails_NoSeat() throws Exception {
        // Arrange
        when(repository.findById("tickets", "ticket-doc-1", Ticket.class)).thenReturn(testTicket);
        when(repository.findById("passengers", "passenger-doc-1", Passenger.class)).thenReturn(testPassenger);
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(null);

        // Act
        Ticket result = bookingService.getTicketDetails("ticket-doc-1");

        // Assert
        assertNotNull(result);
        assertNull(result.getSeatNumberDisplay());
    }

    @Test
    @DisplayName("Should handle null flight ID")
    void testGetTicketDetails_NullFlightId() throws Exception {
        // Arrange
        testSeat.setFlightId(null);
        when(repository.findById("tickets", "ticket-doc-1", Ticket.class)).thenReturn(testTicket);
        when(repository.findById("passengers", "passenger-doc-1", Passenger.class)).thenReturn(testPassenger);
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(testSeat);

        // Act
        Ticket result = bookingService.getTicketDetails("ticket-doc-1");

        // Assert
        assertNotNull(result);
        assertNull(result.getFlightDetails());
    }

    @Test
    @DisplayName("Should handle empty flight ID")
    void testGetTicketDetails_EmptyFlightId() throws Exception {
        // Arrange
        testSeat.setFlightId("");
        when(repository.findById("tickets", "ticket-doc-1", Ticket.class)).thenReturn(testTicket);
        when(repository.findById("passengers", "passenger-doc-1", Passenger.class)).thenReturn(testPassenger);
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(testSeat);

        // Act
        Ticket result = bookingService.getTicketDetails("ticket-doc-1");

        // Assert
        assertNotNull(result);
        assertNull(result.getFlightDetails());
    }

    @Test
    @DisplayName("Should handle exception when loading passenger")
    void testGetTicketDetails_PassengerException() throws Exception {
        // Arrange
        when(repository.findById("tickets", "ticket-doc-1", Ticket.class)).thenReturn(testTicket);
        when(repository.findById("passengers", "passenger-doc-1", Passenger.class))
            .thenThrow(new RuntimeException("Database error"));
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(testSeat);
        setupFlightQuery();

        // Act
        Ticket result = bookingService.getTicketDetails("ticket-doc-1");

        // Assert - Should not throw
        assertNotNull(result);
        assertNull(result.getPassengerDetails());
    }

    @Test
    @DisplayName("Should handle exception when loading seat")
    void testGetTicketDetails_SeatException() throws Exception {
        // Arrange
        when(repository.findById("tickets", "ticket-doc-1", Ticket.class)).thenReturn(testTicket);
        when(repository.findById("passengers", "passenger-doc-1", Passenger.class)).thenReturn(testPassenger);
        when(repository.findById("seats", "seat-doc-1", Seat.class))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        Ticket result = bookingService.getTicketDetails("ticket-doc-1");

        // Assert - Should not throw
        assertNotNull(result);
        assertNotNull(result.getPassengerDetails());
    }

    // ==================== GET CUSTOMER TICKETS TESTS ====================

    // @Test
    // @DisplayName("Should get all customer tickets with enrichment")
    // void testGetCustomerTickets_Success() throws Exception {
    //     // Arrange
    //     List<QueryDocumentSnapshot> ticketDocs = createMockTicketDocuments(3);
    //     setupCustomerTicketsQuery(ticketDocs);
    //     when(repository.findById(eq("passengers"), anyString(), eq(Passenger.class))).thenReturn(testPassenger);
    //     when(repository.findById(eq("seats"), anyString(), eq(Seat.class))).thenReturn(testSeat);
    //     setupFlightQuery();

    //     // Act
    //     List<Ticket> tickets = bookingService.getCustomerTickets("customer-1");

    //     // Assert
    //     assertEquals(3, tickets.size());
    //     for (Ticket ticket : tickets) {
    //         assertNotNull(ticket.getDocumentId());
    //     }
    // }

    @Test
    @DisplayName("Should return empty list when no tickets found")
    void testGetCustomerTickets_NoTickets() throws Exception {
        // Arrange
        setupCustomerTicketsQuery(new ArrayList<>());

        // Act
        List<Ticket> tickets = bookingService.getCustomerTickets("customer-1");

        // Assert
        assertTrue(tickets.isEmpty());
    }

    @Test
    @DisplayName("Should handle enrichment errors gracefully")
    void testGetCustomerTickets_EnrichmentError() throws Exception {
        // Arrange
        List<QueryDocumentSnapshot> ticketDocs = createMockTicketDocuments(1);
        setupCustomerTicketsQuery(ticketDocs);
        when(repository.findById(eq("passengers"), anyString(), eq(Passenger.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act - Should not throw
        List<Ticket> tickets = bookingService.getCustomerTickets("customer-1");

        // Assert
        assertEquals(1, tickets.size());
    }

    @Test
    @DisplayName("Should handle seat without flight ID")
    void testGetCustomerTickets_SeatWithoutFlight() throws Exception {
        // Arrange
        List<QueryDocumentSnapshot> ticketDocs = createMockTicketDocuments(1);
        setupCustomerTicketsQuery(ticketDocs);
        
        Seat seatWithoutFlight = new Seat();
        seatWithoutFlight.setDocumentId("seat-no-flight");
        seatWithoutFlight.setSeatNumber(101);
        seatWithoutFlight.setTypeOfSeat("Economy");
        seatWithoutFlight.setFlightId(null);
        
        when(repository.findById(eq("passengers"), anyString(), eq(Passenger.class))).thenReturn(testPassenger);
        when(repository.findById(eq("seats"), anyString(), eq(Seat.class))).thenReturn(seatWithoutFlight);

        // Act
        List<Ticket> tickets = bookingService.getCustomerTickets("customer-1");

        // Assert
        assertEquals(1, tickets.size());
        assertNull(tickets.get(0).getFlightDetails());
    }

    @Test
    @DisplayName("Should handle empty flight ID in seat")
    void testGetCustomerTickets_EmptyFlightId() throws Exception {
        // Arrange
        List<QueryDocumentSnapshot> ticketDocs = createMockTicketDocuments(1);
        setupCustomerTicketsQuery(ticketDocs);
        
        Seat seatEmptyFlight = new Seat();
        seatEmptyFlight.setDocumentId("seat-empty-flight");
        seatEmptyFlight.setSeatNumber(102);
        seatEmptyFlight.setTypeOfSeat("Business");
        seatEmptyFlight.setFlightId("");
        
        when(repository.findById(eq("passengers"), anyString(), eq(Passenger.class))).thenReturn(testPassenger);
        when(repository.findById(eq("seats"), anyString(), eq(Seat.class))).thenReturn(seatEmptyFlight);

        // Act
        List<Ticket> tickets = bookingService.getCustomerTickets("customer-1");

        // Assert
        assertEquals(1, tickets.size());
        assertNull(tickets.get(0).getFlightDetails());
    }

    // ==================== GET SEAT BY ID TESTS ====================

    @Test
    @DisplayName("Should get seat by ID successfully")
    void testGetSeatById_Success() throws Exception {
        // Arrange
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(testSeat);

        // Act
        Seat result = bookingService.getSeatById("seat-doc-1");

        // Assert
        assertNotNull(result);
        assertEquals("seat-doc-1", result.getDocumentId());
    }

    @Test
    @DisplayName("Should throw exception when seat not found")
    void testGetSeatById_NotFound() throws Exception {
        // Arrange
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> bookingService.getSeatById("seat-doc-1")
        );
        
        assertTrue(exception.getMessage().contains("Seat not found"));
    }

    // ==================== GET FLIGHT BY SEAT ID TESTS ====================

    @Test
    @DisplayName("Should get flight by seat ID successfully")
    void testGetFlightBySeatId_Success() throws Exception {
        // Arrange
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(testSeat);
        setupFlightQuery();

        // Act
        Flight result = bookingService.getFlightBySeatId("seat-doc-1");

        // Assert
        assertNotNull(result);
        assertEquals("F001", result.getFlightId());
    }

    @Test
    @DisplayName("Should throw exception when seat has no flight ID")
    void testGetFlightBySeatId_NoFlightId() throws Exception {
        // Arrange
        testSeat.setFlightId(null);
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(testSeat);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> bookingService.getFlightBySeatId("seat-doc-1")
        );
        
        assertTrue(exception.getMessage().contains("does not have an associated flight"));
    }

    @Test
    @DisplayName("Should throw exception when seat has empty flight ID")
    void testGetFlightBySeatId_EmptyFlightId() throws Exception {
        // Arrange
        testSeat.setFlightId("");
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(testSeat);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> bookingService.getFlightBySeatId("seat-doc-1")
        );
        
        assertTrue(exception.getMessage().contains("does not have an associated flight"));
    }

    @Test
    @DisplayName("Should throw exception when flight not found")
    void testGetFlightBySeatId_FlightNotFound() throws Exception {
        // Arrange
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(testSeat);
        when(repository.getCollectionByField("flights", "flightId", "F001")).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> bookingService.getFlightBySeatId("seat-doc-1")
        );
        
        assertTrue(exception.getMessage().contains("Flight not found"));
    }

    // ==================== HELPER METHODS ====================

    private BookingRequestDTO createTestBookingRequest() {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setSeatId("seat-doc-1");
        request.setCustomerId("customer-1");
        request.setAmount(200.00);
        request.setStripePaymentIntentId("pi_123456");
        request.setPassenger(testPassenger);
        return request;
    }

    private void setupSuccessfulBooking() throws Exception {
        when(repository.findById("seats", "seat-doc-1", Seat.class)).thenReturn(testSeat);
        setupFlightQuery();
        when(pricingContext.calculatePrice("Economy", testFlight)).thenReturn(200.00);
        when(passengerFactory.createPassenger("John Doe", "A12345678", "john@example.com", "012-3456789"))
            .thenReturn(testPassenger);
        when(repository.save(eq("passengers"), any(Passenger.class))).thenReturn("passenger-doc-1");
        when(ticketFactory.createTicket(anyString(), anyString(), anyString(), any(Seat.class), any(Flight.class)))
            .thenReturn(testTicket);
        when(repository.save(eq("tickets"), any(Ticket.class))).thenReturn("ticket-doc-1");
        when(repository.save(eq("payments"), any(Payment.class))).thenReturn("payment-doc-1");
        doNothing().when(repository).updateField(anyString(), anyString(), anyString(), any());
        doNothing().when(notificationService).sendBookingSuccessEmail(anyString(), anyString());
    }

    private void setupFlightQuery() throws Exception {
        QueryDocumentSnapshot flightDoc = mock(QueryDocumentSnapshot.class);
        when(flightDoc.toObject(Flight.class)).thenReturn(testFlight);
        when(flightDoc.getId()).thenReturn("flight-doc-1");
        
        List<QueryDocumentSnapshot> flightDocs = new ArrayList<>();
        flightDocs.add(flightDoc);
        
        when(repository.getCollectionByField("flights", "flightId", "F001")).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(false);
        when(querySnapshot.getDocuments()).thenReturn(flightDocs);
    }

    private void setupRepositoryForSeats(List<QueryDocumentSnapshot> docs) throws Exception {
        when(repository.getCollectionByField(eq("seats"), eq("flightId"), anyString())).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(docs);
    }

    private List<QueryDocumentSnapshot> createMockSeatDocuments(int count) {
        List<QueryDocumentSnapshot> docs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            QueryDocumentSnapshot doc = mock(QueryDocumentSnapshot.class);
            Seat seat = new Seat();
            seat.setSeatNumber(100 + i);
            seat.setTypeOfSeat(i < 4 ? "Business" : "Economy");
            seat.setStatusSeat("Available");
            seat.setFlightId("F001");
            
            when(doc.toObject(Seat.class)).thenReturn(seat);
            when(doc.getId()).thenReturn("seat-doc-" + i);
            docs.add(doc);
        }
        return docs;
    }

    private void setupCustomerTicketsQuery(List<QueryDocumentSnapshot> docs) throws Exception {
        when(repository.getCollectionByField("tickets", "customerId", "customer-1")).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(docs);
    }

    private List<QueryDocumentSnapshot> createMockTicketDocuments(int count) {
        List<QueryDocumentSnapshot> docs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            QueryDocumentSnapshot doc = mock(QueryDocumentSnapshot.class);
            Ticket ticket = new Ticket();
            ticket.setBookingReference("REF" + i);
            ticket.setCustomerId("customer-1");
            ticket.setPassengerId("passenger-doc-" + i);
            ticket.setSeatId("seat-doc-" + i);
            ticket.setFlightId("F001");
            
            when(doc.toObject(Ticket.class)).thenReturn(ticket);
            when(doc.getId()).thenReturn("ticket-doc-" + i);
            docs.add(doc);
        }
        return docs;
    }
}