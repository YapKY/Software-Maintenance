package com.example.springboot.service;

import com.example.springboot.model.Flight;
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
        
        if (ticket.getPassengerDetails() != null) {
            document.add(new Paragraph("Name: " + ticket.getPassengerDetails().getFullName(), normalFont));
            document.add(new Paragraph("Passport: " + ticket.getPassengerDetails().getPassportNo(), normalFont));
            document.add(new Paragraph("Email: " + ticket.getPassengerDetails().getEmail(), normalFont));
            document.add(new Paragraph("Phone: " + ticket.getPassengerDetails().getPhoneNumber(), normalFont));
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", smallFont));
        document.add(new Paragraph(" "));

        // Flight Information
        document.add(new Paragraph("FLIGHT INFORMATION", headerFont));
        document.add(new Paragraph(" "));

        if (ticket.getFlightDetails() != null) {
            Flight flight = ticket.getFlightDetails();
            
            document.add(new Paragraph("Flight: " + flight.getFlightId() + " (" + flight.getPlaneNo() + ")", normalFont));
            document.add(new Paragraph("Route: " + flight.getDepartureCountry() + " → " + flight.getArrivalCountry(), normalFont));
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("Departure: " + flight.getDepartureDate() + " at " + formatTime(flight.getDepartureTime()), normalFont));
            document.add(new Paragraph("Arrival: " + flight.getArrivalDate() + " at " + formatTime(flight.getArrivalTime()), normalFont));
            document.add(new Paragraph("Boarding Time: " + formatTime(flight.getBoardingTime()), normalFont));
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", smallFont));
        document.add(new Paragraph(" "));

        // Seat Information
        document.add(new Paragraph("SEAT INFORMATION", headerFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Seat Number: " + ticket.getSeatNumberDisplay(), normalFont));
        document.add(new Paragraph("Class: " + ticket.getSeatClassDisplay(), normalFont));

        document.add(new Paragraph(" "));
        document.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", smallFont));
        document.add(new Paragraph(" "));

        // QR Code
        document.add(new Paragraph("SCAN AT GATE", headerFont));
        document.add(new Paragraph(" "));

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

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // Footer
        Paragraph footer = new Paragraph("Thank you for flying with us!", smallFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return out.toByteArray();
    }

    private String formatTime(int time) {
        int hours = time / 100;
        int minutes = time % 100;
        String period = hours >= 12 ? "PM" : "AM";
        int displayHours = hours % 12;
        if (displayHours == 0) displayHours = 12;
        return String.format("%d:%02d %s", displayHours, minutes, period);
    }
}