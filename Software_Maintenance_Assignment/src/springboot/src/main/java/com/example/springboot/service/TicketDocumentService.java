package com.example.springboot.service;

import com.example.springboot.model.Flight;
import com.example.springboot.model.Passenger;
import com.example.springboot.model.Ticket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class TicketDocumentService {

    public byte[] generateTicketPdf(Ticket ticket) throws Exception {
        // ✅ VALIDATION: Check ticket has required data
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be null");
        }
        if (ticket.getBookingReference() == null || ticket.getBookingReference().isEmpty()) {
            throw new IllegalArgumentException("Ticket must have a booking reference");
        }
        
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        // Colors
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Font.BOLD);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        // Title
        Paragraph title = new Paragraph("E-TICKET CONFIRMATION", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        document.add(new Paragraph(" ")); // Spacer

        // Booking Reference (Large)
        Paragraph refPara = new Paragraph("Booking Reference: " + ticket.getBookingReference(), headerFont);
        refPara.setAlignment(Element.ALIGN_CENTER);
        document.add(refPara);

        document.add(new Paragraph(" ")); // Spacer
        document.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", smallFont));
        document.add(new Paragraph(" "));

        // Passenger Information
        document.add(new Paragraph("PASSENGER INFORMATION", headerFont));
        document.add(new Paragraph(" "));
        
        // ✅ NULL SAFETY: Check if passenger details exist
        if (ticket.getPassengerDetails() != null) {
            Passenger passenger = ticket.getPassengerDetails();
            document.add(new Paragraph("Name: " + getOrDefault(passenger.getFullName(), "N/A"), normalFont));
            document.add(new Paragraph("Passport: " + getOrDefault(passenger.getPassportNo(), "N/A"), normalFont));
            document.add(new Paragraph("Email: " + getOrDefault(passenger.getEmail(), "N/A"), normalFont));
            document.add(new Paragraph("Phone: " + getOrDefault(passenger.getPhoneNumber(), "N/A"), normalFont));
        } else {
            document.add(new Paragraph("Passenger information not available", normalFont));
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", smallFont));
        document.add(new Paragraph(" "));

        // Flight Information
        document.add(new Paragraph("FLIGHT INFORMATION", headerFont));
        document.add(new Paragraph(" "));

        // ✅ NULL SAFETY: Check if flight details exist
        if (ticket.getFlightDetails() != null) {
            Flight flight = ticket.getFlightDetails();
            
            document.add(new Paragraph("Flight: " + getOrDefault(flight.getFlightId(), "N/A") + 
                " (" + getOrDefault(flight.getPlaneNo(), "N/A") + ")", normalFont));
            document.add(new Paragraph("Route: " + getOrDefault(flight.getDepartureCountry(), "N/A") + 
                " → " + getOrDefault(flight.getArrivalCountry(), "N/A"), normalFont));
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("Departure: " + getOrDefault(flight.getDepartureDate(), "N/A") + 
                " at " + formatTime(flight.getDepartureTime()), normalFont));
            document.add(new Paragraph("Arrival: " + getOrDefault(flight.getArrivalDate(), "N/A") + 
                " at " + formatTime(flight.getArrivalTime()), normalFont));
            document.add(new Paragraph("Boarding Time: " + formatTime(flight.getBoardingTime()), normalFont));
        } else {
            document.add(new Paragraph("Flight information not available", normalFont));
            document.add(new Paragraph("Please contact customer service", normalFont));
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", smallFont));
        document.add(new Paragraph(" "));

        // Seat Information
        document.add(new Paragraph("SEAT INFORMATION", headerFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Seat Number: " + getOrDefault(ticket.getSeatNumberDisplay(), "N/A"), normalFont));
        document.add(new Paragraph("Class: " + getOrDefault(ticket.getSeatClassDisplay(), "N/A"), normalFont));

        document.add(new Paragraph(" "));
        document.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", smallFont));
        document.add(new Paragraph(" "));

        // QR Code
        document.add(new Paragraph("SCAN AT GATE", headerFont));
        document.add(new Paragraph(" "));

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                ticket.getBookingReference(), 
                BarcodeFormat.QR_CODE, 
                200, 
                200
            );
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            
            Image qrImage = Image.getInstance(pngOutputStream.toByteArray());
            qrImage.setAlignment(Element.ALIGN_CENTER);
            document.add(qrImage);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to generate QR code: " + e.getMessage());
            document.add(new Paragraph("QR Code: " + ticket.getBookingReference(), normalFont));
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // Footer
        Paragraph footer = new Paragraph("Thank you for flying with us!", smallFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return out.toByteArray();
    }

    /**
     * ✅ Helper method to provide default values for null strings
     */
    private String getOrDefault(String value, String defaultValue) {
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    /**
     * ✅ NULL SAFETY: Format time with validation
     */
    private String formatTime(int time) {
        if (time == 0) return "N/A";
        
        int hours = time / 100;
        int minutes = time % 100;
        String period = hours >= 12 ? "PM" : "AM";
        int displayHours = hours % 12;
        if (displayHours == 0) displayHours = 12;
        return String.format("%d:%02d %s", displayHours, minutes, period);
    }
}