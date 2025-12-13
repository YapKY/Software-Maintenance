package com.example.springboot.service;

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

        // 1. Add Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("E-TICKET CONFIRMATION", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // 2. Add Details
        document.add(new Paragraph("Booking Reference: " + ticket.getBookingReference()));
        document.add(new Paragraph("Seat: " + ticket.getSeatNumberDisplay()));
        document.add(new Paragraph("Class: " + ticket.getSeatClassDisplay()));
        document.add(new Paragraph("------------------------------------------------"));

        // 3. Generate & Add QR Code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(ticket.getBookingReference(), BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        
        Image qrImage = Image.getInstance(pngOutputStream.toByteArray());
        qrImage.setAlignment(Element.ALIGN_CENTER);
        document.add(new Paragraph("Scan at Gate:"));
        document.add(qrImage);

        document.close();
        return out.toByteArray();
    }
}