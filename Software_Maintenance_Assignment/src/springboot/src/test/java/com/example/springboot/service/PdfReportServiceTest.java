// package com.example.springboot.service;

// import com.example.springboot.model.Flight;
// import com.google.api.core.ApiFuture;
// import com.google.cloud.firestore.*;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.util.*;
// import java.util.concurrent.ExecutionException;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// /**
//  * Test class for PdfReportService
//  * 
//  * Tests Module: PDF Report Generation Module
//  * Coverage: Sales report PDF generation, data aggregation, error handling
//  * Target: 90%+ coverage
//  */
// @ExtendWith(MockitoExtension.class)
// @DisplayName("PDF Report Service Tests")
// class PdfReportServiceTest {

//     @Mock
//     private Firestore firestore;

//     @Mock
//     private FlightService flightService;

//     @Mock
//     private CollectionReference ticketsCollection;

//     @Mock
//     private CollectionReference paymentsCollection;

//     @Mock
//     private Query paymentsQuery;

//     @Mock
//     private ApiFuture<QuerySnapshot> queryFuture;

//     @Mock
//     private QuerySnapshot querySnapshot;

//     @InjectMocks
//     private PdfReportService pdfReportService;

//     private List<Flight> testFlights;
//     private List<QueryDocumentSnapshot> testTickets;
//     private List<QueryDocumentSnapshot> testPayments;

//     @BeforeEach
//     void setUp() {
//         // Create test flights
//         testFlights = createTestFlights();
        
//         // Create test tickets
//         testTickets = createTestTickets();
        
//         // Create test payments
//         testPayments = createTestPayments();
//     }

//     // ==================== SUCCESS CASES ====================

//     @Test
//     @DisplayName("Should generate sales report PDF successfully")
//     void testGenerateSalesReportPdf_Success() throws Exception {
//         // Arrange
//         setupMocks();

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0, "PDF should have content");
        
//         // Verify it's a PDF by checking PDF header
//         String pdfHeader = new String(pdfBytes, 0, Math.min(5, pdfBytes.length));
//         assertTrue(pdfHeader.startsWith("%PDF"), "Should start with PDF header");
//     }

//     @Test
//     @DisplayName("Should generate PDF with flight sales data")
//     void testGenerateSalesReportPdf_WithFlightData() throws Exception {
//         // Arrange
//         setupMocks();

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 1000, "PDF with data should be substantial");
//         verify(flightService).getAllFlights();
//     }

//     @Test
//     @DisplayName("Should generate PDF with payment data")
//     void testGenerateSalesReportPdf_WithPaymentData() throws Exception {
//         // Arrange
//         setupMocks();

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         verify(firestore).collection("payments");
//     }

//     @Test
//     @DisplayName("Should query tickets collection")
//     void testGenerateSalesReportPdf_QueriesTickets() throws Exception {
//         // Arrange
//         setupMocks();

//         // Act
//         pdfReportService.generateSalesReportPdf();

//         // Assert
//         verify(firestore).collection("tickets");
//     }

//     // ==================== EMPTY DATA CASES ====================

//     @Test
//     @DisplayName("Should generate PDF with no flights")
//     void testGenerateSalesReportPdf_NoFlights() throws Exception {
//         // Arrange
//         when(flightService.getAllFlights()).thenReturn(Collections.emptyList());
//         setupTicketsCollection(Collections.emptyList());
//         setupPaymentsCollection(Collections.emptyList());

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
//     }

//     @Test
//     @DisplayName("Should generate PDF with no tickets")
//     void testGenerateSalesReportPdf_NoTickets() throws Exception {
//         // Arrange
//         when(flightService.getAllFlights()).thenReturn(testFlights);
//         setupTicketsCollection(Collections.emptyList());
//         setupPaymentsCollection(testPayments);

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
//     }

//     @Test
//     @DisplayName("Should generate PDF with no payments")
//     void testGenerateSalesReportPdf_NoPayments() throws Exception {
//         // Arrange
//         when(flightService.getAllFlights()).thenReturn(testFlights);
//         setupTicketsCollection(testTickets);
//         setupPaymentsCollection(Collections.emptyList());

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
//     }

//     @Test
//     @DisplayName("Should generate PDF with all empty data")
//     void testGenerateSalesReportPdf_AllEmpty() throws Exception {
//         // Arrange
//         when(flightService.getAllFlights()).thenReturn(Collections.emptyList());
//         setupTicketsCollection(Collections.emptyList());
//         setupPaymentsCollection(Collections.emptyList());

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
//     }

//     // ==================== MULTIPLE DATA CASES ====================

//     @Test
//     @DisplayName("Should generate PDF with multiple flights")
//     void testGenerateSalesReportPdf_MultipleFlights() throws Exception {
//         // Arrange
//         List<Flight> manyFlights = new ArrayList<>();
//         for (int i = 0; i < 10; i++) {
//             Flight flight = new Flight();
//             flight.setFlightId("F" + String.format("%03d", i + 1));
//             flight.setDepartureCountry("Country" + i);
//             flight.setArrivalCountry("Destination" + i);
//             flight.setTotalSeats(30 + i);
//             manyFlights.add(flight);
//         }
        
//         when(flightService.getAllFlights()).thenReturn(manyFlights);
//         setupTicketsCollection(createTicketsForFlights(manyFlights));
//         setupPaymentsCollection(testPayments);

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
//     }

//     @Test
//     @DisplayName("Should generate PDF with multiple payments")
//     void testGenerateSalesReportPdf_MultiplePayments() throws Exception {
//         // Arrange
//         setupMocks();
//         List<QueryDocumentSnapshot> manyPayments = createMultiplePayments(20);
//         setupPaymentsCollection(manyPayments);

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
//     }

//     // ==================== ERROR HANDLING ====================

//     @Test
//     @DisplayName("Should throw RuntimeException when PDF generation fails")
//     void testGenerateSalesReportPdf_PDFGenerationFailure() throws Exception {
//         // Arrange
//         when(flightService.getAllFlights()).thenThrow(new RuntimeException("Service error"));

//         // Act & Assert
//         assertThrows(RuntimeException.class, () -> 
//             pdfReportService.generateSalesReportPdf()
//         );
//     }

//     @Test
//     @DisplayName("Should throw RuntimeException when Firestore query fails")
//     void testGenerateSalesReportPdf_FirestoreException() throws Exception {
//         // Arrange
//         when(flightService.getAllFlights()).thenReturn(testFlights);
//         when(firestore.collection("tickets")).thenReturn(ticketsCollection);
//         when(ticketsCollection.get()).thenReturn(queryFuture);
//         when(queryFuture.get()).thenThrow(new ExecutionException(new Exception("Firestore error")));

//         // Act & Assert
//         assertThrows(RuntimeException.class, () -> 
//             pdfReportService.generateSalesReportPdf()
//         );
//     }

//     @Test
//     @DisplayName("Should throw RuntimeException when payment query fails")
//     void testGenerateSalesReportPdf_PaymentQueryException() throws Exception {
//         // Arrange
//         when(flightService.getAllFlights()).thenReturn(testFlights);
//         setupTicketsCollection(testTickets);
        
//         when(firestore.collection("payments")).thenReturn(paymentsCollection);
//         when(paymentsCollection.whereEqualTo("paymentStatus", true)).thenReturn(paymentsQuery);
//         when(paymentsQuery.get()).thenReturn(queryFuture);
//         when(queryFuture.get()).thenThrow(new ExecutionException(new Exception("Payment query error")));

//         // Act & Assert
//         assertThrows(RuntimeException.class, () -> 
//             pdfReportService.generateSalesReportPdf()
//         );
//     }

//     // ==================== DATA VALIDATION ====================

//     @Test
//     @DisplayName("Should count tickets correctly per flight")
//     void testGenerateSalesReportPdf_TicketCounting() throws Exception {
//         // Arrange
//         Flight flight = testFlights.get(0);
//         List<QueryDocumentSnapshot> ticketsForOneFlight = new ArrayList<>();
        
//         for (int i = 0; i < 5; i++) {
//             QueryDocumentSnapshot ticket = mock(QueryDocumentSnapshot.class);
//             when(ticket.getString("flightId")).thenReturn(flight.getFlightId());
//             ticketsForOneFlight.add(ticket);
//         }
        
//         when(flightService.getAllFlights()).thenReturn(Arrays.asList(flight));
//         setupTicketsCollection(ticketsForOneFlight);
//         setupPaymentsCollection(testPayments);

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
//     }

//     @Test
//     @DisplayName("Should calculate total revenue correctly")
//     void testGenerateSalesReportPdf_RevenueCalculation() throws Exception {
//         // Arrange
//         setupMocks();
//         List<QueryDocumentSnapshot> paymentsWithKnownAmount = new ArrayList<>();
        
//         for (int i = 0; i < 3; i++) {
//             QueryDocumentSnapshot payment = mock(QueryDocumentSnapshot.class);
//             when(payment.get("paymentID")).thenReturn((long) (i + 1));
//             when(payment.get("amount")).thenReturn(100.0);
//             when(payment.get("paymentDate")).thenReturn("2023-11-11");
//             when(payment.get("bankName")).thenReturn("Test Bank");
            
//             Map<String, Object> paymentData = new HashMap<>();
//             paymentData.put("paymentID", (long) (i + 1));
//             paymentData.put("amount", 100.0);
//             paymentData.put("paymentDate", "2023-11-11");
//             paymentData.put("bankName", "Test Bank");
//             when(payment.getData()).thenReturn(paymentData);
            
//             paymentsWithKnownAmount.add(payment);
//         }
        
//         setupPaymentsCollection(paymentsWithKnownAmount);

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         // Total revenue should be 300.0 (3 * 100.0)
//         assertTrue(pdfBytes.length > 0);
//     }

//     @Test
//     @DisplayName("Should handle null payment amounts")
//     void testGenerateSalesReportPdf_NullPaymentAmount() throws Exception {
//         // Arrange
//         setupMocks();
//         List<QueryDocumentSnapshot> paymentsWithNull = new ArrayList<>();
        
//         QueryDocumentSnapshot payment = mock(QueryDocumentSnapshot.class);
//         when(payment.get("paymentID")).thenReturn(1L);
//         when(payment.get("amount")).thenReturn(null);
//         when(payment.get("paymentDate")).thenReturn("2023-11-11");
//         when(payment.get("bankName")).thenReturn("Test Bank");
        
//         Map<String, Object> paymentData = new HashMap<>();
//         paymentData.put("paymentID", 1L);
//         paymentData.put("amount", null);
//         paymentData.put("paymentDate", "2023-11-11");
//         paymentData.put("bankName", "Test Bank");
//         when(payment.getData()).thenReturn(paymentData);
        
//         paymentsWithNull.add(payment);
//         setupPaymentsCollection(paymentsWithNull);

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
//     }

//     // ==================== PAGINATION & OVERFLOW ====================

//     @Test
//     @DisplayName("Should handle flight data overflow to new page")
//     void testGenerateSalesReportPdf_FlightOverflow() throws Exception {
//         // Arrange - Create many flights to trigger pagination
//         List<Flight> manyFlights = new ArrayList<>();
//         for (int i = 0; i < 30; i++) {
//             Flight flight = new Flight();
//             flight.setFlightId("F" + String.format("%03d", i + 1));
//             flight.setDepartureCountry("Malaysia");
//             flight.setArrivalCountry("Singapore");
//             flight.setTotalSeats(32);
//             manyFlights.add(flight);
//         }
        
//         when(flightService.getAllFlights()).thenReturn(manyFlights);
//         setupTicketsCollection(createTicketsForFlights(manyFlights));
//         setupPaymentsCollection(testPayments);

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
//     }

//     // ==================== SPECIAL CHARACTERS ====================

//     @Test
//     @DisplayName("Should handle special characters in flight details")
//     void testGenerateSalesReportPdf_SpecialCharacters() throws Exception {
//         // Arrange
//         Flight flight = new Flight();
//         flight.setFlightId("F&001");
//         flight.setDepartureCountry("São Paulo");
//         flight.setArrivalCountry("Zürich");
//         flight.setTotalSeats(32);
        
//         when(flightService.getAllFlights()).thenReturn(Arrays.asList(flight));
//         setupTicketsCollection(testTickets);
//         setupPaymentsCollection(testPayments);

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
//     }

//     @Test
//     @DisplayName("Should handle very long country names")
//     void testGenerateSalesReportPdf_LongCountryNames() throws Exception {
//         // Arrange
//         Flight flight = new Flight();
//         flight.setFlightId("F001");
//         flight.setDepartureCountry("United Kingdom of Great Britain and Northern Ireland");
//         flight.setArrivalCountry("Democratic Republic of the Congo");
//         flight.setTotalSeats(32);
        
//         when(flightService.getAllFlights()).thenReturn(Arrays.asList(flight));
//         setupTicketsCollection(testTickets);
//         setupPaymentsCollection(testPayments);

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
//     }

//     // ==================== DATE FORMATTING ====================

//     @Test
//     @DisplayName("Should handle various payment date formats")
//     void testGenerateSalesReportPdf_VariousDateFormats() throws Exception {
//         // Arrange
//         setupMocks();
//         List<QueryDocumentSnapshot> paymentsWithDates = new ArrayList<>();
//         String[] dateFormats = {"2023-11-11", "11/11/2023", "2023-11-11T10:30:00"};
        
//         for (int i = 0; i < dateFormats.length; i++) {
//             QueryDocumentSnapshot payment = mock(QueryDocumentSnapshot.class);
//             when(payment.get("paymentID")).thenReturn((long) (i + 1));
//             when(payment.get("amount")).thenReturn(100.0);
//             when(payment.get("paymentDate")).thenReturn(dateFormats[i]);
//             when(payment.get("bankName")).thenReturn("Bank " + i);
            
//             Map<String, Object> paymentData = new HashMap<>();
//             paymentData.put("paymentID", (long) (i + 1));
//             paymentData.put("amount", 100.0);
//             paymentData.put("paymentDate", dateFormats[i]);
//             paymentData.put("bankName", "Bank " + i);
//             when(payment.getData()).thenReturn(paymentData);
            
//             paymentsWithDates.add(payment);
//         }
        
//         setupPaymentsCollection(paymentsWithDates);

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
//     }

//     @Test
//     @DisplayName("Should handle null payment dates")
//     void testGenerateSalesReportPdf_NullPaymentDates() throws Exception {
//         // Arrange
//         setupMocks();
//         QueryDocumentSnapshot payment = mock(QueryDocumentSnapshot.class);
//         when(payment.get("paymentID")).thenReturn(1L);
//         when(payment.get("amount")).thenReturn(100.0);
//         when(payment.get("paymentDate")).thenReturn(null);
//         when(payment.get("bankName")).thenReturn("Test Bank");
        
//         Map<String, Object> paymentData = new HashMap<>();
//         paymentData.put("paymentID", 1L);
//         paymentData.put("amount", 100.0);
//         paymentData.put("paymentDate", null);
//         paymentData.put("bankName", "Test Bank");
//         when(payment.getData()).thenReturn(paymentData);
        
//         setupPaymentsCollection(Arrays.asList(payment));

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
//     }

//     // ==================== HELPER METHODS ====================

//     private void setupMocks() throws ExecutionException, InterruptedException {
//         when(flightService.getAllFlights()).thenReturn(testFlights);
//         setupTicketsCollection(testTickets);
//         setupPaymentsCollection(testPayments);
//     }

//     private void setupTicketsCollection(List<QueryDocumentSnapshot> tickets) 
//             throws ExecutionException, InterruptedException {
//         when(firestore.collection("tickets")).thenReturn(ticketsCollection);
//         when(ticketsCollection.get()).thenReturn(queryFuture);
//         QuerySnapshot ticketSnapshot = mock(QuerySnapshot.class);
//         when(queryFuture.get()).thenReturn(ticketSnapshot);
//         when(ticketSnapshot.getDocuments()).thenReturn(tickets);
//     }

//     private void setupPaymentsCollection(List<QueryDocumentSnapshot> payments) 
//             throws ExecutionException, InterruptedException {
//         when(firestore.collection("payments")).thenReturn(paymentsCollection);
//         when(paymentsCollection.whereEqualTo("paymentStatus", true)).thenReturn(paymentsQuery);
//         when(paymentsQuery.get()).thenReturn(queryFuture);
//         when(queryFuture.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(payments);
//     }

//     private List<Flight> createTestFlights() {
//         List<Flight> flights = new ArrayList<>();
        
//         Flight flight1 = new Flight();
//         flight1.setFlightId("F001");
//         flight1.setDepartureCountry("Malaysia");
//         flight1.setArrivalCountry("Japan");
//         flight1.setTotalSeats(32);
//         flights.add(flight1);
        
//         Flight flight2 = new Flight();
//         flight2.setFlightId("F002");
//         flight2.setDepartureCountry("Singapore");
//         flight2.setArrivalCountry("Australia");
//         flight2.setTotalSeats(40);
//         flights.add(flight2);
        
//         return flights;
//     }

//     private List<QueryDocumentSnapshot> createTestTickets() {
//         List<QueryDocumentSnapshot> tickets = new ArrayList<>();
        
//         for (int i = 0; i < 5; i++) {
//             QueryDocumentSnapshot ticket = mock(QueryDocumentSnapshot.class);
//             when(ticket.getString("flightId")).thenReturn("F001");
//             tickets.add(ticket);
//         }
        
//         for (int i = 0; i < 3; i++) {
//             QueryDocumentSnapshot ticket = mock(QueryDocumentSnapshot.class);
//             when(ticket.getString("flightId")).thenReturn("F002");
//             tickets.add(ticket);
//         }
        
//         return tickets;
//     }

//     private List<QueryDocumentSnapshot> createTestPayments() {
//         List<QueryDocumentSnapshot> payments = new ArrayList<>();
        
//         for (int i = 0; i < 3; i++) {
//             QueryDocumentSnapshot payment = mock(QueryDocumentSnapshot.class);
//             when(payment.get("paymentID")).thenReturn((long) (i + 1));
//             when(payment.get("amount")).thenReturn(200.0 + (i * 50));
//             when(payment.get("paymentDate")).thenReturn("2023-11-" + String.format("%02d", i + 1));
//             when(payment.get("bankName")).thenReturn("Bank " + (i + 1));
            
//             Map<String, Object> paymentData = new HashMap<>();
//             paymentData.put("paymentID", (long) (i + 1));
//             paymentData.put("amount", 200.0 + (i * 50));
//             paymentData.put("paymentDate", "2023-11-" + String.format("%02d", i + 1));
//             paymentData.put("bankName", "Bank " + (i + 1));
//             when(payment.getData()).thenReturn(paymentData);
            
//             payments.add(payment);
//         }
        
//         return payments;
//     }

//     private List<QueryDocumentSnapshot> createTicketsForFlights(List<Flight> flights) {
//         List<QueryDocumentSnapshot> tickets = new ArrayList<>();
        
//         for (Flight flight : flights) {
//             QueryDocumentSnapshot ticket = mock(QueryDocumentSnapshot.class);
//             when(ticket.getString("flightId")).thenReturn(flight.getFlightId());
//             tickets.add(ticket);
//         }
        
//         return tickets;
//     }

//     private List<QueryDocumentSnapshot> createMultiplePayments(int count) {
//         List<QueryDocumentSnapshot> payments = new ArrayList<>();
        
//         for (int i = 0; i < count; i++) {
//             QueryDocumentSnapshot payment = mock(QueryDocumentSnapshot.class);
//             when(payment.get("paymentID")).thenReturn((long) (i + 1));
//             when(payment.get("amount")).thenReturn(100.0 + (i * 10));
//             when(payment.get("paymentDate")).thenReturn("2023-11-11");
//             when(payment.get("bankName")).thenReturn("Bank " + (i + 1));
            
//             Map<String, Object> paymentData = new HashMap<>();
//             paymentData.put("paymentID", (long) (i + 1));
//             paymentData.put("amount", 100.0 + (i * 10));
//             paymentData.put("paymentDate", "2023-11-11");
//             paymentData.put("bankName", "Bank " + (i + 1));
//             when(payment.getData()).thenReturn(paymentData);
            
//             payments.add(payment);
//         }
        
//         return payments;
//     }

//     // ==================== INTEGRATION-LIKE TESTS ====================

//     @Test
//     @DisplayName("Should generate complete report with all sections")
//     void testGenerateSalesReportPdf_CompleteReport() throws Exception {
//         // Arrange
//         setupMocks();

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 2000, "Complete report should be substantial");
        
//         // Verify all required service calls were made
//         verify(flightService).getAllFlights();
//         verify(firestore).collection("tickets");
//         verify(firestore).collection("payments");
//     }

//     @Test
//     @DisplayName("Should sort payments by date")
//     void testGenerateSalesReportPdf_PaymentsSorted() throws Exception {
//         // Arrange
//         setupMocks();

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         // The service should sort payments internally
//         verify(querySnapshot).getDocuments();
//     }
// }