// package com.example.springboot.service;

// import com.example.springboot.model.Ticket;
// import com.example.springboot.model.Flight;
// import com.example.springboot.model.Payment;
// import com.google.api.core.ApiFuture;
// import com.google.cloud.firestore.*;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.mockito.junit.jupiter.MockitoSettings;
// import org.mockito.quality.Strictness;

// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;
// import java.util.Map;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// /**
//  * Test class for ReportService
//  * 
//  * Tests Module: Staff Dashboard - Sales Reporting
//  * Coverage Target: 90%+
//  * Coverage: Sales reports, statistics, revenue calculations, PDF generation, edge cases
//  */
// @ExtendWith(MockitoExtension.class)
// @MockitoSettings(strictness = Strictness.LENIENT)
// class ReportServiceTest {

//     @Mock
//     private Firestore firestore;

//     @Mock
//     private CollectionReference collectionReference;

//     @Mock
//     private Query query;

//     @Mock
//     private QuerySnapshot querySnapshot;

//     @Mock
//     private ApiFuture<QuerySnapshot> future;

//     @Mock
//     private QueryDocumentSnapshot queryDocumentSnapshot;

//     @Mock
//     private BookingService bookingService;

//     @Mock
//     private FlightService flightService;

//     @InjectMocks
//     private ReportService reportService;

//     private Ticket testTicket;
//     private Payment testPayment;
//     private Flight testFlight;

//     @BeforeEach
//     void setUp() {
//         testTicket = new Ticket();
//         testTicket.setDocumentId("ticket123");
//         testTicket.setBookingReference("ABC12345");
//         testTicket.setFlightId("F001");
//         testTicket.setCustomerId("cust123");
//         testTicket.setSeatClassDisplay("Economy");

//         testPayment = new Payment();
//         testPayment.setDocumentId("payment123");
//         testPayment.setTicketId("ticket123");
//         testPayment.setAmount(200.00);
//         testPayment.setPaymentStatus(true);
//         testPayment.setPaymentDate("2023-11-11T13:00:00");

//         testFlight = new Flight();
//         testFlight.setFlightId("F001");
//         testFlight.setDepartureCountry("Malaysia");
//         testFlight.setArrivalCountry("Japan");
//         testFlight.setEconomyPrice(200.00);
//         testFlight.setBusinessPrice(400.00);
//         testFlight.setStatus("ACTIVE");
//     }

//     // ========== Get Sales Summary Tests ==========

//     @Test
//     void testGetSalesSummary_Success() throws Exception {
//         // Arrange
//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

//         mockPaymentDocumentConversion(queryDocumentSnapshot, testPayment);

//         // Act
//         Map<String, Object> summary = reportService.getSalesSummary();

//         // Assert
//         assertNotNull(summary);
//         assertTrue(summary.containsKey("totalRevenue"));
//         assertTrue(summary.containsKey("totalBookings"));
//         assertEquals(200.0, summary.get("totalRevenue"));
//         assertEquals(1, summary.get("totalBookings"));
//         verify(firestore).collection("payments");
//     }

//     @Test
//     void testGetSalesSummary_NoSales() throws Exception {
//         // Arrange
//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

//         // Act
//         Map<String, Object> summary = reportService.getSalesSummary();

//         // Assert
//         assertNotNull(summary);
//         assertEquals(0.0, summary.get("totalRevenue"));
//         assertEquals(0, summary.get("totalBookings"));
//     }

//     @Test
//     void testGetSalesSummary_MultipleSales() throws Exception {
//         // Arrange
//         Payment payment2 = new Payment();
//         payment2.setAmount(300.00);
//         payment2.setPaymentStatus(true);

//         Payment payment3 = new Payment();
//         payment3.setAmount(150.00);
//         payment3.setPaymentStatus(true);

//         QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);
//         QueryDocumentSnapshot doc3 = mock(QueryDocumentSnapshot.class);

//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(queryDocumentSnapshot, doc2, doc3));

//         mockPaymentDocumentConversion(queryDocumentSnapshot, testPayment);
//         mockPaymentDocumentConversion(doc2, payment2);
//         mockPaymentDocumentConversion(doc3, payment3);

//         // Act
//         Map<String, Object> summary = reportService.getSalesSummary();

//         // Assert
//         assertNotNull(summary);
//         assertEquals(650.0, summary.get("totalRevenue"));
//         assertEquals(3, summary.get("totalBookings"));
//     }

//     @Test
//     void testGetSalesSummary_WithAverageCalculation() throws Exception {
//         // Arrange
//         Payment payment2 = new Payment();
//         payment2.setAmount(400.00);
//         payment2.setPaymentStatus(true);

//         QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);

//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(queryDocumentSnapshot, doc2));

//         mockPaymentDocumentConversion(queryDocumentSnapshot, testPayment);
//         mockPaymentDocumentConversion(doc2, payment2);

//         // Act
//         Map<String, Object> summary = reportService.getSalesSummary();

//         // Assert
//         assertNotNull(summary);
//         assertEquals(600.0, summary.get("totalRevenue"));
//         assertTrue(summary.containsKey("averageBookingValue"));
//         assertEquals(300.0, summary.get("averageBookingValue"));
//     }

//     @Test
//     void testGetSalesSummary_Exception() throws Exception {
//         // Arrange
//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.get()).thenThrow(new RuntimeException("Database error"));

//         // Act & Assert
//         assertThrows(RuntimeException.class, () -> reportService.getSalesSummary());
//     }

//     // ========== Get Sales by Date Range Tests ==========

//     @Test
//     void testGetSalesByDateRange_Success() throws Exception {
//         // Arrange
//         String startDate = "2023-11-01";
//         String endDate = "2023-11-30";

//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.whereGreaterThanOrEqualTo("paymentDate", startDate)).thenReturn(query);
//         when(query.whereLessThanOrEqualTo("paymentDate", endDate)).thenReturn(query);
//         when(query.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

//         mockPaymentDocumentConversion(queryDocumentSnapshot, testPayment);

//         // Act
//         List<Payment> result = reportService.getSalesByDateRange(startDate, endDate);

//         // Assert
//         assertNotNull(result);
//         assertEquals(1, result.size());
//         assertEquals(200.00, result.get(0).getAmount());
//         verify(query).whereGreaterThanOrEqualTo("paymentDate", startDate);
//         verify(query).whereLessThanOrEqualTo("paymentDate", endDate);
//     }

//     @Test
//     void testGetSalesByDateRange_NoSales() throws Exception {
//         // Arrange
//         String startDate = "2023-12-01";
//         String endDate = "2023-12-31";

//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.whereGreaterThanOrEqualTo("paymentDate", startDate)).thenReturn(query);
//         when(query.whereLessThanOrEqualTo("paymentDate", endDate)).thenReturn(query);
//         when(query.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

//         // Act
//         List<Payment> result = reportService.getSalesByDateRange(startDate, endDate);

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.isEmpty());
//     }

//     @Test
//     void testGetSalesByDateRange_MultipleSales() throws Exception {
//         // Arrange
//         Payment payment2 = new Payment();
//         payment2.setAmount(300.00);
//         QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);

//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.whereGreaterThanOrEqualTo("paymentDate", anyString())).thenReturn(query);
//         when(query.whereLessThanOrEqualTo("paymentDate", anyString())).thenReturn(query);
//         when(query.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(queryDocumentSnapshot, doc2));

//         mockPaymentDocumentConversion(queryDocumentSnapshot, testPayment);
//         mockPaymentDocumentConversion(doc2, payment2);

//         // Act
//         List<Payment> result = reportService.getSalesByDateRange("2023-11-01", "2023-11-30");

//         // Assert
//         assertEquals(2, result.size());
//     }

//     @Test
//     void testGetSalesByDateRange_NullDates() {
//         // Act & Assert
//         assertThrows(Exception.class, () -> 
//             reportService.getSalesByDateRange(null, null)
//         );
//     }

//     @Test
//     void testGetSalesByDateRange_EmptyDates() {
//         // Act & Assert
//         assertThrows(Exception.class, () -> 
//             reportService.getSalesByDateRange("", "")
//         );
//     }

//     // ========== Get Sales by Flight Tests ==========

//     @Test
//     void testGetSalesByFlight_Success() throws Exception {
//         // Arrange
//         when(bookingService.getTicketsByFlightId("F001"))
//                 .thenReturn(Collections.singletonList(testTicket));
//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo("ticketId", "ticket123")).thenReturn(query);
//         when(query.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

//         mockPaymentDocumentConversion(queryDocumentSnapshot, testPayment);

//         // Act
//         Map<String, Object> result = reportService.getSalesByFlight("F001");

//         // Assert
//         assertNotNull(result);
//         assertEquals("F001", result.get("flightId"));
//         assertEquals(200.0, result.get("totalRevenue"));
//         assertEquals(1, result.get("bookingCount"));
//         verify(bookingService).getTicketsByFlightId("F001");
//     }

//     @Test
//     void testGetSalesByFlight_NoBookings() throws Exception {
//         // Arrange
//         when(bookingService.getTicketsByFlightId("F999"))
//                 .thenReturn(Collections.emptyList());

//         // Act
//         Map<String, Object> result = reportService.getSalesByFlight("F999");

//         // Assert
//         assertNotNull(result);
//         assertEquals("F999", result.get("flightId"));
//         assertEquals(0.0, result.get("totalRevenue"));
//         assertEquals(0, result.get("bookingCount"));
//     }

//     @Test
//     void testGetSalesByFlight_MultipleTickets() throws Exception {
//         // Arrange
//         Ticket ticket2 = new Ticket();
//         ticket2.setDocumentId("ticket456");
//         ticket2.setFlightId("F001");

//         Payment payment2 = new Payment();
//         payment2.setTicketId("ticket456");
//         payment2.setAmount(300.00);
//         payment2.setPaymentStatus(true);

//         QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);

//         when(bookingService.getTicketsByFlightId("F001"))
//                 .thenReturn(Arrays.asList(testTicket, ticket2));
//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo(eq("ticketId"), anyString())).thenReturn(query);
//         when(query.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments())
//                 .thenReturn(Collections.singletonList(queryDocumentSnapshot))
//                 .thenReturn(Collections.singletonList(doc2));

//         mockPaymentDocumentConversion(queryDocumentSnapshot, testPayment);
//         mockPaymentDocumentConversion(doc2, payment2);

//         // Act
//         Map<String, Object> result = reportService.getSalesByFlight("F001");

//         // Assert
//         assertEquals(500.0, result.get("totalRevenue"));
//         assertEquals(2, result.get("bookingCount"));
//     }

//     @Test
//     void testGetSalesByFlight_NullFlightId() {
//         // Act & Assert
//         assertThrows(Exception.class, () -> reportService.getSalesByFlight(null));
//     }

//     @Test
//     void testGetSalesByFlight_EmptyFlightId() {
//         // Act & Assert
//         assertThrows(Exception.class, () -> reportService.getSalesByFlight(""));
//     }

//     // ========== Get Revenue by Seat Class Tests ==========

//     @Test
//     void testGetRevenueByClass_Success() throws Exception {
//         // Arrange
//         when(firestore.collection("tickets")).thenReturn(collectionReference);
//         when(collectionReference.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

//         mockTicketDocumentConversion(queryDocumentSnapshot, testTicket);

//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo("ticketId", "ticket123")).thenReturn(query);
//         when(query.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));
        
//         mockPaymentDocumentConversion(queryDocumentSnapshot, testPayment);

//         // Act
//         Map<String, Double> result = reportService.getRevenueByClass();

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.containsKey("Economy"));
//         assertEquals(200.0, result.get("Economy"));
//     }

//     @Test
//     void testGetRevenueByClass_MultipleClasses() throws Exception {
//         // Arrange
//         Ticket businessTicket = new Ticket();
//         businessTicket.setDocumentId("ticket456");
//         businessTicket.setSeatClassDisplay("Business");

//         Payment businessPayment = new Payment();
//         businessPayment.setTicketId("ticket456");
//         businessPayment.setAmount(400.00);
//         businessPayment.setPaymentStatus(true);

//         QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);

//         when(firestore.collection("tickets")).thenReturn(collectionReference);
//         when(collectionReference.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(queryDocumentSnapshot, doc2));

//         mockTicketDocumentConversion(queryDocumentSnapshot, testTicket);
//         mockTicketDocumentConversion(doc2, businessTicket);

//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo(eq("ticketId"), anyString())).thenReturn(query);
//         when(query.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.get()).thenReturn(future);

//         QuerySnapshot economySnapshot = mock(QuerySnapshot.class);
//         QuerySnapshot businessSnapshot = mock(QuerySnapshot.class);
        
//         when(future.get())
//                 .thenReturn(economySnapshot)
//                 .thenReturn(businessSnapshot);
        
//         when(economySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));
//         when(businessSnapshot.getDocuments()).thenReturn(Collections.singletonList(doc2));

//         mockPaymentDocumentConversion(queryDocumentSnapshot, testPayment);
//         mockPaymentDocumentConversion(doc2, businessPayment);

//         // Act
//         Map<String, Double> result = reportService.getRevenueByClass();

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.containsKey("Economy"));
//         assertTrue(result.containsKey("Business"));
//     }

//     @Test
//     void testGetRevenueByClass_NoTickets() throws Exception {
//         // Arrange
//         when(firestore.collection("tickets")).thenReturn(collectionReference);
//         when(collectionReference.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

//         // Act
//         Map<String, Double> result = reportService.getRevenueByClass();

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.isEmpty());
//     }

//     @Test
//     void testGetRevenueByClass_WithUnpaidTickets() throws Exception {
//         // Arrange
//         when(firestore.collection("tickets")).thenReturn(collectionReference);
//         when(collectionReference.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

//         mockTicketDocumentConversion(queryDocumentSnapshot, testTicket);

//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo("ticketId", "ticket123")).thenReturn(query);
//         when(query.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

//         // Act
//         Map<String, Double> result = reportService.getRevenueByClass();

//         // Assert
//         assertNotNull(result);
//         assertEquals(0.0, result.getOrDefault("Economy", 0.0));
//     }

//     // ========== Generate Sales Report PDF Tests ==========

//     @Test
//     void testGenerateSalesReportPDF_Success() throws Exception {
//         // Arrange
//         String startDate = "2023-11-01";
//         String endDate = "2023-11-30";

//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.whereGreaterThanOrEqualTo("paymentDate", startDate)).thenReturn(query);
//         when(query.whereLessThanOrEqualTo("paymentDate", endDate)).thenReturn(query);
//         when(query.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

//         mockPaymentDocumentConversion(queryDocumentSnapshot, testPayment);

//         // Act
//         byte[] pdfBytes = reportService.generateSalesReportPDF(startDate, endDate);

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
//     }

//     @Test
//     void testGenerateSalesReportPDF_NoData() throws Exception {
//         // Arrange
//         String startDate = "2023-12-01";
//         String endDate = "2023-12-31";

//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.whereGreaterThanOrEqualTo("paymentDate", startDate)).thenReturn(query);
//         when(query.whereLessThanOrEqualTo("paymentDate", endDate)).thenReturn(query);
//         when(query.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

//         // Act
//         byte[] pdfBytes = reportService.generateSalesReportPDF(startDate, endDate);

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
//     }

//     @Test
//     void testGenerateSalesReportPDF_NullDates() {
//         // Act & Assert
//         assertThrows(Exception.class, () -> 
//             reportService.generateSalesReportPDF(null, null)
//         );
//     }

//     // ========== Get Flight Statistics Tests ==========

//     @Test
//     void testGetFlightStatistics_Success() throws Exception {
//         // Arrange
//         when(flightService.getAllFlights()).thenReturn(Collections.singletonList(testFlight));
//         when(bookingService.getTicketsByFlightId("F001"))
//                 .thenReturn(Collections.singletonList(testTicket));

//         // Act
//         Map<String, Object> result = reportService.getFlightStatistics();

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.containsKey("totalFlights"));
//         assertTrue(result.containsKey("flightDetails"));
//         assertEquals(1, result.get("totalFlights"));
//     }

//     @Test
//     void testGetFlightStatistics_NoFlights() throws Exception {
//         // Arrange
//         when(flightService.getAllFlights()).thenReturn(Collections.emptyList());

//         // Act
//         Map<String, Object> result = reportService.getFlightStatistics();

//         // Assert
//         assertNotNull(result);
//         assertEquals(0, result.get("totalFlights"));
//     }

//     @Test
//     void testGetFlightStatistics_MultipleFlights() throws Exception {
//         // Arrange
//         Flight flight2 = new Flight();
//         flight2.setFlightId("F002");

//         when(flightService.getAllFlights()).thenReturn(Arrays.asList(testFlight, flight2));
//         when(bookingService.getTicketsByFlightId(anyString()))
//                 .thenReturn(Collections.singletonList(testTicket));

//         // Act
//         Map<String, Object> result = reportService.getFlightStatistics();

//         // Assert
//         assertEquals(2, result.get("totalFlights"));
//     }

//     // ========== Get Top Routes Tests ==========

//     @Test
//     void testGetTopRoutes_Success() throws Exception {
//         // Arrange
//         when(firestore.collection("tickets")).thenReturn(collectionReference);
//         when(collectionReference.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

//         mockTicketDocumentConversion(queryDocumentSnapshot, testTicket);

//         when(flightService.getFlightByFlightId("F001")).thenReturn(testFlight);

//         // Act
//         List<Map<String, Object>> result = reportService.getTopRoutes(5);

//         // Assert
//         assertNotNull(result);
//         assertFalse(result.isEmpty());
//         verify(firestore).collection("tickets");
//         verify(flightService).getFlightByFlightId("F001");
//     }

//     @Test
//     void testGetTopRoutes_EmptyDatabase() throws Exception {
//         // Arrange
//         when(firestore.collection("tickets")).thenReturn(collectionReference);
//         when(collectionReference.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

//         // Act
//         List<Map<String, Object>> result = reportService.getTopRoutes(5);

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.isEmpty());
//     }

//     @Test
//     void testGetTopRoutes_LimitedResults() throws Exception {
//         // Arrange
//         when(firestore.collection("tickets")).thenReturn(collectionReference);
//         when(collectionReference.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

//         mockTicketDocumentConversion(queryDocumentSnapshot, testTicket);
//         when(flightService.getFlightByFlightId("F001")).thenReturn(testFlight);

//         // Act
//         List<Map<String, Object>> result = reportService.getTopRoutes(1);

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.size() <= 1);
//     }

//     @Test
//     void testGetTopRoutes_InvalidLimit() {
//         // Act & Assert
//         assertThrows(Exception.class, () -> reportService.getTopRoutes(-1));
//     }

//     @Test
//     void testGetTopRoutes_ZeroLimit() {
//         // Act & Assert
//         assertThrows(Exception.class, () -> reportService.getTopRoutes(0));
//     }

//     // ========== Get Monthly Revenue Tests ==========

//     @Test
//     void testGetMonthlyRevenue_Success() throws Exception {
//         // Arrange
//         int year = 2023;
        
//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.whereGreaterThanOrEqualTo(eq("paymentDate"), anyString())).thenReturn(query);
//         when(query.whereLessThanOrEqualTo(eq("paymentDate"), anyString())).thenReturn(query);
//         when(query.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(queryDocumentSnapshot));

//         mockPaymentDocumentConversion(queryDocumentSnapshot, testPayment);

//         // Act
//         Map<String, Double> result = reportService.getMonthlyRevenue(year);

//         // Assert
//         assertNotNull(result);
//         assertEquals(12, result.size());
//     }

//     @Test
//     void testGetMonthlyRevenue_NoData() throws Exception {
//         // Arrange
//         int year = 2024;
        
//         when(firestore.collection("payments")).thenReturn(collectionReference);
//         when(collectionReference.whereEqualTo("paymentStatus", true)).thenReturn(query);
//         when(query.whereGreaterThanOrEqualTo(eq("paymentDate"), anyString())).thenReturn(query);
//         when(query.whereLessThanOrEqualTo(eq("paymentDate"), anyString())).thenReturn(query);
//         when(query.get()).thenReturn(future);
//         when(future.get()).thenReturn(querySnapshot);
//         when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

//         // Act
//         Map<String, Double> result = reportService.getMonthlyRevenue(year);

//         // Assert
//         assertNotNull(result);
//         assertEquals(12, result.size());
//         result.values().forEach(value -> assertEquals(0.0, value));
//     }

//     @Test
//     void testGetMonthlyRevenue_InvalidYear() {
//         // Act & Assert
//         assertThrows(Exception.class, () -> reportService.getMonthlyRevenue(1900));
//     }

//     @Test
//     void testGetMonthlyRevenue_FutureYear() {
//         // Act & Assert
//         assertThrows(Exception.class, () -> reportService.getMonthlyRevenue(2100));
//     }

//     // ========== Helper Methods ==========

//     private void mockPaymentDocumentConversion(DocumentSnapshot mockDoc, Payment payment) {
//         when(mockDoc.getId()).thenReturn(payment.getDocumentId());
//         when(mockDoc.getString("ticketId")).thenReturn(payment.getTicketId());
//         when(mockDoc.getDouble("amount")).thenReturn(payment.getAmount());
//         when(mockDoc.getBoolean("paymentStatus")).thenReturn(payment.isPaymentStatus());
//         when(mockDoc.getString("paymentDate")).thenReturn(payment.getPaymentDate());
//         when(mockDoc.getString("stripePaymentIntentId")).thenReturn(payment.getStripePaymentIntentId());
//     }

//     private void mockTicketDocumentConversion(DocumentSnapshot mockDoc, Ticket ticket) {
//         when(mockDoc.getId()).thenReturn(ticket.getDocumentId());
//         when(mockDoc.getString("bookingReference")).thenReturn(ticket.getBookingReference());
//         when(mockDoc.getString("flightId")).thenReturn(ticket.getFlightId());
//         when(mockDoc.getString("customerId")).thenReturn(ticket.getCustomerId());
//         when(mockDoc.getString("passengerId")).thenReturn(ticket.getPassengerId());
//         when(mockDoc.getString("seatId")).thenReturn(ticket.getSeatId());
//         when(mockDoc.getString("seatClassDisplay")).thenReturn(ticket.getSeatClassDisplay());
//         when(mockDoc.getString("seatNumberDisplay")).thenReturn(ticket.getSeatNumberDisplay());
//     }
// }