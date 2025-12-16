// package com.example.springboot.service;

// import com.example.springboot.model.Payment;
// import com.example.springboot.model.Ticket;
// import com.google.api.core.ApiFuture;
// import com.google.cloud.firestore.*;
// import org.apache.pdfbox.pdmodel.PDDocument;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.mockito.junit.jupiter.MockitoSettings;
// import org.mockito.quality.Strictness;

// import java.util.concurrent.ExecutionException;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// /**
//  * Test class for PdfReportService
//  * 
//  * Tests Module: Staff Dashboard - PDF Report Generation
//  * Coverage Target: 90%+
//  * Coverage: All PDF generation methods, text truncation, data aggregation
//  */
// @ExtendWith(MockitoExtension.class)
// @MockitoSettings(strictness = Strictness.LENIENT)
// class PdfReportServiceTest {

//     @Mock
//     private Firestore firestore;

//     @Mock
//     private CollectionReference paymentsCollection;

//     @Mock
//     private CollectionReference ticketsCollection;

//     @Mock
//     private Query paymentsQuery;

//     @Mock
//     private Query ticketsQuery;

//     @Mock
//     private QuerySnapshot paymentsSnapshot;

//     @Mock
//     private QuerySnapshot ticketsSnapshot;

//     @Mock
//     private ApiFuture<QuerySnapshot> futurePayments;

//     @Mock
//     private ApiFuture<QuerySnapshot> futureTickets;

//     @InjectMocks
//     private PdfReportService pdfReportService;

//     @BeforeEach
//     void setUp() {
//         // Reset mocks for each test
//         reset(firestore, paymentsCollection, ticketsCollection, paymentsQuery, ticketsQuery);
//     }

//     // ========== Generate Sales Report PDF Tests ==========

//     @Test
//     void testGenerateSalesReportPdf_WithData_Success() throws Exception {
//         // Arrange
//         List<QueryDocumentSnapshot> paymentDocs = createMockPaymentDocuments(3);
//         List<QueryDocumentSnapshot> ticketDocs = createMockTicketDocuments(3);

//         setupFirestoreMocks(paymentDocs, ticketDocs);

//         // Act
//         byte[] result = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.length > 0);
        
//         // Verify it's a valid PDF
//         String header = new String(Arrays.copyOfRange(result, 0, Math.min(8, result.length)));
//         assertTrue(header.startsWith("%PDF-"), "Should be a valid PDF file");
        
//         // Verify PDF can be loaded
//         try (PDDocument doc = PDDocument.load(result)) {
//             assertNotNull(doc);
//             assertTrue(doc.getNumberOfPages() > 0);
//         }
        
//         verify(firestore, atLeastOnce()).collection("payments");
//         verify(firestore, atLeastOnce()).collection("tickets");
//     }

//     @Test
//     void testGenerateSalesReportPdf_NoData_Success() throws Exception {
//         // Arrange
//         setupFirestoreMocks(Collections.emptyList(), Collections.emptyList());

//         // Act
//         byte[] result = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.length > 0);
        
//         // Should still create valid PDF even with no data
//         String header = new String(Arrays.copyOfRange(result, 0, Math.min(8, result.length)));
//         assertTrue(header.startsWith("%PDF-"));
//     }

//     @Test
//     void testGenerateSalesReportPdf_MultipleFlights() throws Exception {
//         // Arrange - Create payments for different flights
//         List<QueryDocumentSnapshot> paymentDocs = new ArrayList<>();
//         paymentDocs.add(createPaymentDoc("pay1", "ticket1", 200.0));
//         paymentDocs.add(createPaymentDoc("pay2", "ticket2", 300.0));
//         paymentDocs.add(createPaymentDoc("pay3", "ticket3", 150.0));
//         paymentDocs.add(createPaymentDoc("pay4", "ticket4", 400.0));
//         paymentDocs.add(createPaymentDoc("pay5", "ticket5", 250.0));

//         List<QueryDocumentSnapshot> ticketDocs = new ArrayList<>();
//         ticketDocs.add(createTicketDoc("ticket1", "F001"));
//         ticketDocs.add(createTicketDoc("ticket2", "F001"));
//         ticketDocs.add(createTicketDoc("ticket3", "F002"));
//         ticketDocs.add(createTicketDoc("ticket4", "F002"));
//         ticketDocs.add(createTicketDoc("ticket5", "F003"));

//         setupFirestoreMocks(paymentDocs, ticketDocs);

//         // Act
//         byte[] result = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.length > 0);
        
//         try (PDDocument doc = PDDocument.load(result)) {
//             assertTrue(doc.getNumberOfPages() > 0);
//         }
//     }

//     @Test
//     void testGenerateSalesReportPdf_LargeDataset() throws Exception {
//         // Arrange - Create 50 payments to test pagination/large dataset handling
//         List<QueryDocumentSnapshot> paymentDocs = createMockPaymentDocuments(50);
//         List<QueryDocumentSnapshot> ticketDocs = createMockTicketDocuments(50);

//         setupFirestoreMocks(paymentDocs, ticketDocs);

//         // Act
//         byte[] result = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.length > 0);
        
//         try (PDDocument doc = PDDocument.load(result)) {
//             assertTrue(doc.getNumberOfPages() > 0);
//         }
//     }

//     @Test
//     void testGenerateSalesReportPdf_WithLongText() throws Exception {
//         // Arrange - Create payments with very long IDs to test truncation
//         QueryDocumentSnapshot payDoc = createPaymentDoc("payment123", "ticket123", 200.0);
//         when(payDoc.getString("stripePaymentIntentId")).thenReturn("pi_" + "x".repeat(200));
        
//         QueryDocumentSnapshot ticketDoc = createTicketDoc("ticket123", "F001");
//         when(ticketDoc.getString("bookingReference")).thenReturn("BOOK" + "Y".repeat(100));

//         setupFirestoreMocks(Collections.singletonList(payDoc), Collections.singletonList(ticketDoc));

//         // Act
//         byte[] result = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.length > 0);
//     }

//     @Test
//     void testGenerateSalesReportPdf_WithNullValues() throws Exception {
//         // Arrange - Test null handling
//         QueryDocumentSnapshot payDoc = createPaymentDoc("pay1", "ticket1", 200.0);
//         when(payDoc.getString("stripePaymentIntentId")).thenReturn(null);
//         when(payDoc.getString("paymentDate")).thenReturn(null);
        
//         QueryDocumentSnapshot ticketDoc = createTicketDoc("ticket1", "F001");
//         when(ticketDoc.getString("seatClassDisplay")).thenReturn(null);
//         when(ticketDoc.getString("seatNumberDisplay")).thenReturn(null);

//         setupFirestoreMocks(Collections.singletonList(payDoc), Collections.singletonList(ticketDoc));

//         // Act
//         byte[] result = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.length > 0);
//     }

//     @Test
//     void testGenerateSalesReportPdf_WithZeroAmount() throws Exception {
//         // Arrange
//         QueryDocumentSnapshot payDoc = createPaymentDoc("pay1", "ticket1", 0.0);
//         QueryDocumentSnapshot ticketDoc = createTicketDoc("ticket1", "F001");

//         setupFirestoreMocks(Collections.singletonList(payDoc), Collections.singletonList(ticketDoc));

//         // Act
//         byte[] result = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.length > 0);
//     }

//     @Test
//     void testGenerateSalesReportPdf_FirestoreException() throws Exception {
//         // Arrange
//         when(firestore.collection("payments")).thenReturn(paymentsCollection);
//         when(paymentsCollection.whereEqualTo("paymentStatus", true)).thenReturn(paymentsQuery);
//         when(paymentsQuery.get()).thenReturn(futurePayments);
//         when(futurePayments.get()).thenThrow(new ExecutionException("Firestore error", new RuntimeException()));

//         // Act & Assert
//         assertThrows(RuntimeException.class, () -> pdfReportService.generateSalesReportPdf());
//     }

//     // ========== Get Successful Payments Tests ==========

//     @Test
//     void testGetSuccessfulPayments_WithData() throws Exception {
//         // Arrange
//         List<QueryDocumentSnapshot> paymentDocs = createMockPaymentDocuments(3);
//         List<QueryDocumentSnapshot> ticketDocs = createMockTicketDocuments(3);

//         setupFirestoreMocks(paymentDocs, ticketDocs);

//         // Act
//         List<Map<String, Object>> result = pdfReportService.getSuccessfulPayments();

//         // Assert
//         assertNotNull(result);
//         assertEquals(3, result.size());
        
//         // Verify first payment
//         Map<String, Object> firstPayment = result.get(0);
//         assertTrue(firstPayment.containsKey("amount"));
//         assertTrue(firstPayment.containsKey("ticketId"));
//         assertTrue(firstPayment.containsKey("flightId"));
        
//         verify(firestore, times(2)).collection("payments");
//     }

//     @Test
//     void testGetSuccessfulPayments_NoPayments() throws Exception {
//         // Arrange
//         setupFirestoreMocks(Collections.emptyList(), Collections.emptyList());

//         // Act
//         List<Map<String, Object>> result = pdfReportService.getSuccessfulPayments();

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.isEmpty());
//     }

//     @Test
//     void testGetSuccessfulPayments_TicketNotFound() throws Exception {
//         // Arrange - Payment exists but ticket doesn't
//         List<QueryDocumentSnapshot> paymentDocs = createMockPaymentDocuments(1);
        
//         when(firestore.collection("payments")).thenReturn(paymentsCollection);
//         when(paymentsCollection.whereEqualTo("paymentStatus", true)).thenReturn(paymentsQuery);
//         when(paymentsQuery.get()).thenReturn(futurePayments);
//         when(futurePayments.get()).thenReturn(paymentsSnapshot);
//         when(paymentsSnapshot.getDocuments()).thenReturn(paymentDocs);
        
//         // Mock empty ticket results
//         when(firestore.collection("tickets")).thenReturn(ticketsCollection);
//         when(ticketsCollection.whereEqualTo(eq("documentId"), anyString())).thenReturn(ticketsQuery);
//         when(ticketsQuery.get()).thenReturn(futureTickets);
//         when(futureTickets.get()).thenReturn(ticketsSnapshot);
//         when(ticketsSnapshot.getDocuments()).thenReturn(Collections.emptyList());

//         // Act
//         List<Map<String, Object>> result = pdfReportService.getSuccessfulPayments();

//         // Assert
//         assertNotNull(result);
//         assertEquals(1, result.size());
//         assertNull(result.get(0).get("flightId")); // Should be null when ticket not found
//     }

//     @Test
//     void testGetSuccessfulPayments_MultiplePaymentsSameFlight() throws Exception {
//         // Arrange
//         List<QueryDocumentSnapshot> paymentDocs = new ArrayList<>();
//         paymentDocs.add(createPaymentDoc("pay1", "ticket1", 200.0));
//         paymentDocs.add(createPaymentDoc("pay2", "ticket2", 300.0));
//         paymentDocs.add(createPaymentDoc("pay3", "ticket3", 150.0));

//         List<QueryDocumentSnapshot> ticketDocs = new ArrayList<>();
//         ticketDocs.add(createTicketDoc("ticket1", "F001"));
//         ticketDocs.add(createTicketDoc("ticket2", "F001"));
//         ticketDocs.add(createTicketDoc("ticket3", "F001"));

//         setupFirestoreMocks(paymentDocs, ticketDocs);

//         // Act
//         List<Map<String, Object>> result = pdfReportService.getSuccessfulPayments();

//         // Assert
//         assertNotNull(result);
//         assertEquals(3, result.size());
//         result.forEach(payment -> assertEquals("F001", payment.get("flightId")));
//     }

//     // ========== Get Ticket Count By Flight Tests ==========

//     @Test
//     void testGetTicketCountByFlight_Success() {
//         // Arrange
//         List<Map<String, Object>> payments = new ArrayList<>();
//         payments.add(createPaymentMap("F001"));
//         payments.add(createPaymentMap("F001"));
//         payments.add(createPaymentMap("F002"));
//         payments.add(createPaymentMap("F001"));
//         payments.add(createPaymentMap("F003"));

//         // Act
//         Map<String, Long> result = pdfReportService.getTicketCountByFlight(payments);

//         // Assert
//         assertNotNull(result);
//         assertEquals(3L, result.get("F001"));
//         assertEquals(1L, result.get("F002"));
//         assertEquals(1L, result.get("F003"));
//     }

//     @Test
//     void testGetTicketCountByFlight_EmptyList() {
//         // Act
//         Map<String, Long> result = pdfReportService.getTicketCountByFlight(Collections.emptyList());

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.isEmpty());
//     }

//     @Test
//     void testGetTicketCountByFlight_NullFlightId() {
//         // Arrange
//         List<Map<String, Object>> payments = new ArrayList<>();
//         Map<String, Object> payment1 = new HashMap<>();
//         payment1.put("flightId", null);
//         payments.add(payment1);
//         payments.add(createPaymentMap("F001"));

//         // Act
//         Map<String, Long> result = pdfReportService.getTicketCountByFlight(payments);

//         // Assert
//         assertNotNull(result);
//         // Should only count non-null flightIds
//         assertEquals(1, result.size());
//     }

//     @Test
//     void testGetTicketCountByFlight_SingleFlight() {
//         // Arrange
//         List<Map<String, Object>> payments = new ArrayList<>();
//         for (int i = 0; i < 10; i++) {
//             payments.add(createPaymentMap("F001"));
//         }

//         // Act
//         Map<String, Long> result = pdfReportService.getTicketCountByFlight(payments);

//         // Assert
//         assertEquals(10L, result.get("F001"));
//         assertEquals(1, result.size());
//     }

//     @Test
//     void testGetTicketCountByFlight_ManyFlights() {
//         // Arrange
//         List<Map<String, Object>> payments = new ArrayList<>();
//         for (int i = 1; i <= 20; i++) {
//             payments.add(createPaymentMap("F" + String.format("%03d", i)));
//         }

//         // Act
//         Map<String, Long> result = pdfReportService.getTicketCountByFlight(payments);

//         // Assert
//         assertEquals(20, result.size());
//         result.values().forEach(count -> assertEquals(1L, count));
//     }

//     // ========== Truncate Text Tests ==========

//     @Test
//     void testTruncateText_ShortText() {
//         // Act
//         String result = pdfReportService.truncateText("Hello", 10);

//         // Assert
//         assertEquals("Hello", result);
//     }

//     @Test
//     void testTruncateText_ExactLength() {
//         // Act
//         String result = pdfReportService.truncateText("HelloWorld", 10);

//         // Assert
//         assertEquals("HelloWorld", result);
//     }

//     @Test
//     void testTruncateText_LongText() {
//         // Arrange
//         String longText = "This is a very long text that definitely needs to be truncated";

//         // Act
//         String result = pdfReportService.truncateText(longText, 20);

//         // Assert
//         assertNotNull(result);
//         assertTrue(result.length() <= 23); // 20 + "..."
//         assertTrue(result.endsWith("..."));
//         assertEquals("This is a very long ...", result);
//     }

//     @Test
//     void testTruncateText_NullText() {
//         // Act
//         String result = pdfReportService.truncateText(null, 10);

//         // Assert
//         assertEquals("", result);
//     }

//     @Test
//     void testTruncateText_EmptyText() {
//         // Act
//         String result = pdfReportService.truncateText("", 10);

//         // Assert
//         assertEquals("", result);
//     }

//     @Test
//     void testTruncateText_ZeroMaxLength() {
//         // Act
//         String result = pdfReportService.truncateText("Hello", 0);

//         // Assert
//         assertEquals("...", result);
//     }

//     @Test
//     void testTruncateText_NegativeMaxLength() {
//         // Act
//         String result = pdfReportService.truncateText("Hello", -5);

//         // Assert
//         assertEquals("...", result);
//     }

//     @Test
//     void testTruncateText_VeryLongText() {
//         // Arrange
//         String veryLongText = "a".repeat(1000);

//         // Act
//         String result = pdfReportService.truncateText(veryLongText, 50);

//         // Assert
//         assertEquals(53, result.length()); // 50 + "..."
//         assertTrue(result.endsWith("..."));
//     }

//     @Test
//     void testTruncateText_SpecialCharacters() {
//         // Act
//         String result = pdfReportService.truncateText("Hello@#$%^&*()", 10);

//         // Assert
//         assertEquals("Hello@#$%^&*()", result);
//     }

//     @Test
//     void testTruncateText_UnicodeCharacters() {
//         // Act
//         String result = pdfReportService.truncateText("你好世界这是测试", 5);

//         // Assert
//         assertTrue(result.length() <= 8); // 5 + "..."
//         assertTrue(result.endsWith("..."));
//     }

//     @Test
//     void testTruncateText_WhitespaceOnly() {
//         // Act
//         String result = pdfReportService.truncateText("     ", 10);

//         // Assert
//         assertEquals("     ", result);
//     }

//     @Test
//     void testTruncateText_MaxLengthOne() {
//         // Act
//         String result = pdfReportService.truncateText("Hello", 1);

//         // Assert
//         assertEquals("H...", result);
//     }

//     @Test
//     void testTruncateText_ExactlyAtBoundary() {
//         // Arrange
//         String text = "0123456789"; // Exactly 10 chars

//         // Act
//         String result = pdfReportService.truncateText(text, 10);

//         // Assert
//         assertEquals("0123456789", result);
//     }

//     @Test
//     void testTruncateText_OnePastBoundary() {
//         // Arrange
//         String text = "01234567890"; // 11 chars

//         // Act
//         String result = pdfReportService.truncateText(text, 10);

//         // Assert
//         assertEquals("0123456789...", result);
//     }

//     // ========== Integration Tests ==========

//     @Test
//     void testFullPdfGeneration_WithCompleteData() throws Exception {
//         // Arrange - Create realistic data
//         List<QueryDocumentSnapshot> paymentDocs = new ArrayList<>();
//         paymentDocs.add(createPaymentDoc("pay1", "tick1", 500.00));
//         paymentDocs.add(createPaymentDoc("pay2", "tick2", 750.00));
//         paymentDocs.add(createPaymentDoc("pay3", "tick3", 1200.00));
        
//         List<QueryDocumentSnapshot> ticketDocs = new ArrayList<>();
//         ticketDocs.add(createTicketDocWithDetails("tick1", "F001", "ABC123", "12A", "Economy"));
//         ticketDocs.add(createTicketDocWithDetails("tick2", "F001", "ABC124", "13B", "Business"));
//         ticketDocs.add(createTicketDocWithDetails("tick3", "F002", "ABC125", "14C", "Business"));

//         setupFirestoreMocks(paymentDocs, ticketDocs);

//         // Act
//         byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

//         // Assert
//         assertNotNull(pdfBytes);
//         assertTrue(pdfBytes.length > 0);
        
//         // Verify PDF structure
//         try (PDDocument doc = PDDocument.load(pdfBytes)) {
//             assertNotNull(doc);
//             assertEquals(1, doc.getNumberOfPages());
//         }
        
//         // Verify all methods were called
//         verify(firestore, atLeast(2)).collection("payments");
//         verify(firestore, atLeast(1)).collection("tickets");
//     }

//     // ========== Helper Methods ==========

//     private void setupFirestoreMocks(List<QueryDocumentSnapshot> paymentDocs, 
//                                      List<QueryDocumentSnapshot> ticketDocs) throws Exception {
//         // Mock payments
//         when(firestore.collection("payments")).thenReturn(paymentsCollection);
//         when(paymentsCollection.whereEqualTo("paymentStatus", true)).thenReturn(paymentsQuery);
//         when(paymentsQuery.get()).thenReturn(futurePayments);
//         when(futurePayments.get()).thenReturn(paymentsSnapshot);
//         when(paymentsSnapshot.getDocuments()).thenReturn(paymentDocs);
        
//         // Mock tickets - need to handle multiple calls
//         when(firestore.collection("tickets")).thenReturn(ticketsCollection);
//         when(ticketsCollection.whereEqualTo(eq("documentId"), anyString())).thenReturn(ticketsQuery);
//         when(ticketsQuery.get()).thenReturn(futureTickets);
        
//         if (ticketDocs.isEmpty()) {
//             when(futureTickets.get()).thenReturn(ticketsSnapshot);
//             when(ticketsSnapshot.getDocuments()).thenReturn(Collections.emptyList());
//         } else {
//             // Return one ticket at a time for each query
//             List<QuerySnapshot> snapshots = new ArrayList<>();
//             for (QueryDocumentSnapshot ticketDoc : ticketDocs) {
//                 QuerySnapshot snapshot = mock(QuerySnapshot.class);
//                 when(snapshot.getDocuments()).thenReturn(Collections.singletonList(ticketDoc));
//                 snapshots.add(snapshot);
//             }
            
//             if (snapshots.size() == 1) {
//                 when(futureTickets.get()).thenReturn(snapshots.get(0));
//             } else {
//                 QuerySnapshot[] snapshotArray = snapshots.toArray(new QuerySnapshot[0]);
//                 when(futureTickets.get()).thenReturn(snapshotArray[0], Arrays.copyOfRange(snapshotArray, 1, snapshotArray.length));
//             }
//         }
//     }

//     private List<QueryDocumentSnapshot> createMockPaymentDocuments(int count) {
//         List<QueryDocumentSnapshot> docs = new ArrayList<>();
//         for (int i = 0; i < count; i++) {
//             docs.add(createPaymentDoc("payment" + i, "ticket" + i, 100.0 + (i * 50)));
//         }
//         return docs;
//     }

//     private List<QueryDocumentSnapshot> createMockTicketDocuments(int count) {
//         List<QueryDocumentSnapshot> docs = new ArrayList<>();
//         for (int i = 0; i < count; i++) {
//             docs.add(createTicketDoc("ticket" + i, "F" + String.format("%03d", (i % 5) + 1)));
//         }
//         return docs;
//     }

//     private QueryDocumentSnapshot createPaymentDoc(String id, String ticketId, double amount) {
//         QueryDocumentSnapshot doc = mock(QueryDocumentSnapshot.class);
//         when(doc.getId()).thenReturn(id);
//         when(doc.getString("ticketId")).thenReturn(ticketId);
//         when(doc.getDouble("amount")).thenReturn(amount);
//         when(doc.getBoolean("paymentStatus")).thenReturn(true);
//         when(doc.getString("paymentDate")).thenReturn("2023-11-15T10:30:00");
//         when(doc.getString("stripePaymentIntentId")).thenReturn("pi_" + id);
//         return doc;
//     }

//     private QueryDocumentSnapshot createTicketDoc(String id, String flightId) {
//         QueryDocumentSnapshot doc = mock(QueryDocumentSnapshot.class);
//         when(doc.getId()).thenReturn(id);
//         when(doc.getString("flightId")).thenReturn(flightId);
//         when(doc.getString("bookingReference")).thenReturn("BOOK" + id);
//         when(doc.getString("seatClassDisplay")).thenReturn("Economy");
//         when(doc.getString("seatNumberDisplay")).thenReturn("12A");
//         return doc;
//     }

//     private QueryDocumentSnapshot createTicketDocWithDetails(String id, String flightId, 
//                                                              String bookingRef, String seatNo, String seatClass) {
//         QueryDocumentSnapshot doc = mock(QueryDocumentSnapshot.class);
//         when(doc.getId()).thenReturn(id);
//         when(doc.getString("flightId")).thenReturn(flightId);
//         when(doc.getString("bookingReference")).thenReturn(bookingRef);
//         when(doc.getString("seatClassDisplay")).thenReturn(seatClass);
//         when(doc.getString("seatNumberDisplay")).thenReturn(seatNo);
//         return doc;
//     }

//     private Map<String, Object> createPaymentMap(String flightId) {
//         Map<String, Object> payment = new HashMap<>();
//         payment.put("flightId", flightId);
//         payment.put("amount", 200.0);
//         return payment;
//     }
// }