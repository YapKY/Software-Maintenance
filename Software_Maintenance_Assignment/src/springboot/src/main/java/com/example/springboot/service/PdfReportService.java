package com.example.springboot.service;

import com.example.springboot.model.Flight;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PdfReportService {

    @Autowired
    private Firestore firestore;

    @Autowired
    private FlightService flightService;

    private static final String TICKETS_COLLECTION = "tickets";

    // Page layout constants
    private static final float MARGIN = 50;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();

    /**
     * Generate Sales Report PDF using Apache PDFBox
     * Contains only Flight Sales Summary table.
     */
    public byte[] generateSalesReportPdf() throws ExecutionException, InterruptedException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = PAGE_HEIGHT - MARGIN;

                // Add Title
                yPosition = addTitle(contentStream, yPosition);

                // Add Flight Sales Section
                yPosition = addFlightSalesSection(contentStream, yPosition);

                // Add Footer on the same page
                addFooter(contentStream, yPosition);
            }

            document.save(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF report: " + e.getMessage(), e);
        }
    }

    /**
     * Add report title and header
     */
    private float addTitle(PDPageContentStream contentStream, float yPosition) throws IOException {
        // Title
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 24);
        contentStream.setNonStrokingColor(Color.BLUE);
        String title = "AIRLINE MANAGEMENT SYSTEM";
        float titleWidth = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD).getStringWidth(title) / 1000 * 24;
        contentStream.beginText();
        contentStream.newLineAtOffset((PAGE_WIDTH - titleWidth) / 2, yPosition);
        contentStream.showText(title);
        contentStream.endText();
        yPosition -= 30;

        // Subtitle
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
        contentStream.setNonStrokingColor(Color.BLACK);
        String subtitle = "FLIGHT SALES SUMMARY"; // Changed subtitle to reflect content
        float subtitleWidth = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD).getStringWidth(subtitle) / 1000 * 18;
        contentStream.beginText();
        contentStream.newLineAtOffset((PAGE_WIDTH - subtitleWidth) / 2, yPosition);
        contentStream.showText(subtitle);
        contentStream.endText();
        yPosition -= 30;

        // Date
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        contentStream.setNonStrokingColor(Color.GRAY);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateStr = "Generated on: " + sdf.format(new Date());
        float dateWidth = new PDType1Font(Standard14Fonts.FontName.HELVETICA).getStringWidth(dateStr) / 1000 * 10;
        contentStream.beginText();
        contentStream.newLineAtOffset(PAGE_WIDTH - MARGIN - dateWidth, yPosition);
        contentStream.showText(dateStr);
        contentStream.endText();
        yPosition -= 30;

        // Separator line
        contentStream.setStrokingColor(Color.BLACK);
        contentStream.setLineWidth(1);
        contentStream.moveTo(MARGIN, yPosition);
        contentStream.lineTo(PAGE_WIDTH - MARGIN, yPosition);
        contentStream.stroke();
        yPosition -= 20;

        return yPosition;
    }

    /**
     * Add Flight Sales section to the PDF
     */
    private float addFlightSalesSection(PDPageContentStream contentStream, float yPosition) 
            throws IOException, ExecutionException, InterruptedException {
        
        // Section Header (Optional, redundant since subtitle covers it, but keeping for consistency)
        /* contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("FLIGHT SALES SUMMARY");
        contentStream.endText();
        yPosition -= 25;
        */

        // Get all flights
        List<Flight> flights = flightService.getAllFlights();
        Map<String, Integer> ticketCountByFlight = getTicketCountByFlight();

        // Table headers
        float tableWidth = PAGE_WIDTH - 2 * MARGIN;
        float[] columnWidths = {80, 80, 80, 120, 120}; // Flight ID, Seats, Tickets, From, To
        float rowHeight = 20;

        // Draw table header background
        contentStream.setNonStrokingColor(new Color(41, 128, 185));
        contentStream.addRect(MARGIN, yPosition - rowHeight, tableWidth, rowHeight);
        contentStream.fill();

        // Header text
        contentStream.setNonStrokingColor(Color.WHITE);
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        
        float xPosition = MARGIN + 5;
        contentStream.beginText();
        contentStream.newLineAtOffset(xPosition, yPosition - 14);
        contentStream.showText("Flight ID");
        contentStream.newLineAtOffset(columnWidths[0], 0);
        contentStream.showText("Total Seats");
        contentStream.newLineAtOffset(columnWidths[1], 0);
        contentStream.showText("Tickets Sold");
        contentStream.newLineAtOffset(columnWidths[2], 0);
        contentStream.showText("From");
        contentStream.newLineAtOffset(columnWidths[3], 0);
        contentStream.showText("To");
        contentStream.endText();

        yPosition -= rowHeight;

        // Table data
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
        contentStream.setNonStrokingColor(Color.BLACK);

        int totalTicketsSold = 0;
        int rowCount = 0;

        for (Flight flight : flights) {
            if (yPosition < 100) break; // Prevent overflow

            int ticketsSold = ticketCountByFlight.getOrDefault(flight.getFlightId(), 0);
            totalTicketsSold += ticketsSold;

            // Alternate row colors
            if (rowCount % 2 == 0) {
                contentStream.setNonStrokingColor(new Color(245, 245, 245));
                contentStream.addRect(MARGIN, yPosition - rowHeight, tableWidth, rowHeight);
                contentStream.fill();
            }

            contentStream.setNonStrokingColor(Color.BLACK);
            xPosition = MARGIN + 5;
            
            contentStream.beginText();
            contentStream.newLineAtOffset(xPosition, yPosition - 14);
            contentStream.showText(flight.getFlightId());
            contentStream.newLineAtOffset(columnWidths[0], 0);
            contentStream.showText(String.valueOf(flight.getTotalSeats()));
            contentStream.newLineAtOffset(columnWidths[1], 0);
            contentStream.showText(String.valueOf(ticketsSold));
            contentStream.newLineAtOffset(columnWidths[2], 0);
            contentStream.showText(truncateText(flight.getDepartureCountry(), 15));
            contentStream.newLineAtOffset(columnWidths[3], 0);
            contentStream.showText(truncateText(flight.getArrivalCountry(), 15));
            contentStream.endText();

            yPosition -= rowHeight;
            rowCount++;
        }

        yPosition -= 15;

        // Total tickets summary
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.setNonStrokingColor(Color.BLACK); // Changed to black for better contrast
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(String.format("Total Number of Tickets Sold: %d", totalTicketsSold));
        contentStream.endText();
        yPosition -= 30;

        return yPosition;
    }

    /**
     * Add Footer
     */
    private void addFooter(PDPageContentStream contentStream, float yPosition) throws IOException {
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 8);
        contentStream.setNonStrokingColor(Color.GRAY);
        String footer = "*** End of Report ***";
        float footerWidth = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE).getStringWidth(footer) / 1000 * 8;
        
        // Position footer at the bottom margin or current Y position if lower
        float footerY = Math.min(yPosition, MARGIN); 
        
        contentStream.beginText();
        contentStream.newLineAtOffset((PAGE_WIDTH - footerWidth) / 2, footerY);
        contentStream.showText(footer);
        contentStream.endText();
    }

    /**
     * Get ticket count grouped by flight ID
     */
    private Map<String, Integer> getTicketCountByFlight() throws ExecutionException, InterruptedException {
        Map<String, Integer> ticketCount = new HashMap<>();

        ApiFuture<QuerySnapshot> future = firestore.collection(TICKETS_COLLECTION).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        for (DocumentSnapshot doc : documents) {
            String flightId = doc.getString("flightId");
            if (flightId != null) {
                ticketCount.put(flightId, ticketCount.getOrDefault(flightId, 0) + 1);
            }
        }

        return ticketCount;
    }

    /**
     * Helper method to truncate text to fit in table cells
     */
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}