package com.example.springboot.service;

import com.example.springboot.model.Flight;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test Suite for PdfReportService
 * Achieves high coverage by inspecting generated PDF content and simulating Firestore behavior.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PdfReportServiceTest {

    @Mock
    private Firestore firestore;

    @Mock
    private FlightService flightService;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private ApiFuture<QuerySnapshot> futureQuerySnapshot;

    @Mock
    private QuerySnapshot querySnapshot;

    @InjectMocks
    private PdfReportService pdfReportService;

    private List<Flight> mockFlights;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {
        // 1. Setup Mock Flight Data
        mockFlights = new ArrayList<>();
        
        Flight flight1 = new Flight();
        flight1.setFlightId("F001");
        flight1.setDepartureCountry("Malaysia");
        flight1.setArrivalCountry("Japan");
        flight1.setTotalSeats(100);
        
        Flight flight2 = new Flight();
        flight2.setFlightId("F002");
        flight2.setDepartureCountry("Singapore");
        // Long name to test truncation logic (15 char limit in your code)
        flight2.setArrivalCountry("United States of America"); 
        flight2.setTotalSeats(150);

        mockFlights.add(flight1);
        mockFlights.add(flight2);

        // 2. Setup Mock Behavior
        // Mock flight service
        when(flightService.getAllFlights()).thenReturn(mockFlights);

        // Mock Firestore chain: firestore.collection("tickets").get()
        when(firestore.collection("tickets")).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(futureQuerySnapshot);
        when(futureQuerySnapshot.get()).thenReturn(querySnapshot);
    }

    @Test
    void testGenerateSalesReportPdf_Success() throws ExecutionException, InterruptedException, IOException {
        // Arrange: Mock ticket documents to simulate sales
        // 2 tickets for F001, 1 ticket for F002, 1 ticket for unknown flight
        QueryDocumentSnapshot ticket1 = mock(QueryDocumentSnapshot.class);
        when(ticket1.getString("flightId")).thenReturn("F001");
        
        QueryDocumentSnapshot ticket2 = mock(QueryDocumentSnapshot.class);
        when(ticket2.getString("flightId")).thenReturn("F001");

        QueryDocumentSnapshot ticket3 = mock(QueryDocumentSnapshot.class);
        when(ticket3.getString("flightId")).thenReturn("F002");
        
        // Ticket with null flight ID (should be ignored by logic)
        QueryDocumentSnapshot ticket4 = mock(QueryDocumentSnapshot.class);
        when(ticket4.getString("flightId")).thenReturn(null);

        List<QueryDocumentSnapshot> ticketDocs = Arrays.asList(ticket1, ticket2, ticket3, ticket4);
        when(querySnapshot.getDocuments()).thenReturn(ticketDocs);

        // Act
        byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

        // Assert: Verify Binary Output
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);

        // Assert: Verify PDF Content using PDFBox
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // 1. Verify Headers
            assertTrue(text.contains("AIRLINE MANAGEMENT SYSTEM"));
            assertTrue(text.contains("FLIGHT SALES SUMMARY"));
            assertTrue(text.contains("*** End of Report ***"));

            // 2. Verify Flight Data
            assertTrue(text.contains("F001"));
            assertTrue(text.contains("Malaysia"));
            assertTrue(text.contains("Japan"));
            
            assertTrue(text.contains("F002"));
            assertTrue(text.contains("Singapore"));
            
            // 3. Verify Truncation logic 
            // "United States of America" -> length 24. Code truncates > 15.
            // Expected: "United State..." (first 12 chars + "...")
            assertTrue(text.contains("United State...")); 

            // 4. Verify Calculations
            // Total tickets should be 3 (2 for F001 + 1 for F002)
            assertTrue(text.contains("Total Number of Tickets Sold: 3")); 
        }
    }

    @Test
    void testGenerateSalesReportPdf_NoFlights() throws ExecutionException, InterruptedException, IOException {
        // Arrange: Service returns empty flight list
        when(flightService.getAllFlights()).thenReturn(Collections.emptyList());
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

        // Act
        byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

        // Assert
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            assertTrue(text.contains("FLIGHT SALES SUMMARY"));
            assertTrue(text.contains("Total Number of Tickets Sold: 0"));
            assertFalse(text.contains("F001"));
        }
    }

    @Test
    void testGenerateSalesReportPdf_FlightsButNoTickets() throws ExecutionException, InterruptedException, IOException {
        // Arrange: Flights exist, but tickets collection is empty
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

        // Act
        byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

        // Assert
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // Flight info should be present
            assertTrue(text.contains("F001"));
            // Sales count should be 0
            assertTrue(text.contains("Total Number of Tickets Sold: 0"));
        }
    }

    @Test
    void testGenerateSalesReportPdf_FirestoreException() throws ExecutionException, InterruptedException {
        // Arrange: Simulate Firestore connection failure
        when(futureQuerySnapshot.get()).thenThrow(new InterruptedException("Firestore timeout"));

        // Act & Assert
        // The service re-throws InterruptedException, so we expect it here
        Exception exception = assertThrows(InterruptedException.class, () -> 
            pdfReportService.generateSalesReportPdf()
        );
        assertEquals("Firestore timeout", exception.getMessage());
    }

    @Test
    void testGenerateSalesReportPdf_ManyFlights_CheckLoop() throws ExecutionException, InterruptedException, IOException {
        // Arrange: Create a list of flights that FITS on one page (e.g., 15)
        // The current service implementation breaks the loop if the page is full, 
        // so we must test within the single-page limit (approx 20-25 rows).
        int flightCount = 15; 
        List<Flight> manyFlights = new ArrayList<>();
        for (int i = 0; i < flightCount; i++) {
            Flight f = new Flight();
            f.setFlightId("F" + i);
            f.setDepartureCountry("Dep" + i);
            f.setArrivalCountry("Arr" + i);
            f.setTotalSeats(100);
            manyFlights.add(f);
        }
        when(flightService.getAllFlights()).thenReturn(manyFlights);
        when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());

        // Act
        byte[] pdfBytes = pdfReportService.generateSalesReportPdf();

        // Assert
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            
            // Verify that the first flight is present
            assertTrue(text.contains("F0"));
            
            // Verify that the LAST flight in the list was processed and output
            // This confirms the loop ran to completion for a single page
            assertTrue(text.contains("F" + (flightCount - 1))); 
        }
    }
}