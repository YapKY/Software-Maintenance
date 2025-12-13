package com.example.springboot.controller;

import com.example.springboot.model.Ticket;
import com.example.springboot.service.BookingService;
import com.example.springboot.service.TicketDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TicketDocumentService ticketDocumentService;

    /**
     * Get all tickets for a customer
     * GET /api/tickets/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerTickets(@PathVariable String customerId) {
        try {
            List<Ticket> tickets = bookingService.getCustomerTickets(customerId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "count", tickets.size(),
                "tickets", tickets
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Failed to load tickets: " + e.getMessage()
            ));
        }
    }

    /**
     * Get ticket by ID
     * GET /api/tickets/{ticketId}
     */
    @GetMapping("/{ticketId}")
    public ResponseEntity<?> getTicket(@PathVariable String ticketId) {
        try {
            Ticket ticket = bookingService.getTicketDetails(ticketId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "ticket", ticket
            ));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Download ticket as PDF
     * GET /api/tickets/{ticketId}/download
     */
    @GetMapping("/{ticketId}/download")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable String ticketId) {
        try {
            Ticket ticket = bookingService.getTicketDetails(ticketId);
            byte[] pdf = ticketDocumentService.generateTicketPdf(ticket);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(
                "attachment", 
                "ticket-" + ticket.getBookingReference() + ".pdf"
            );

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}