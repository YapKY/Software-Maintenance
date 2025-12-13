package com.example.springboot.controller;

import com.example.springboot.model.*;
import com.example.springboot.service.*;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")
@CrossOrigin(origins = "*") // Needed for frontend communication
public class BookingController {

    @Autowired private BookingService bookingService;
    @Autowired private PaymentService paymentService;
    @Autowired private TicketDocumentService ticketDocumentService;

    // 1. Get Seats
    @GetMapping("/seats/{flightId}")
public ResponseEntity<?> getSeats(@PathVariable String flightId) {
    try {
        // Try to fetch by flightId field first (e.g., "F001")
        List<Seat> seats = bookingService.getSeatsByFlightId(flightId);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "count", seats.size(),
            "seats", seats
        ));
    } catch (Exception e) {
        return ResponseEntity.status(500).body(Map.of(
            "success", false,
            "message", "Failed to load seats: " + e.getMessage()
        ));
    }
}

    // 2. Initiate Stripe Payment
    @PostMapping("/payment/initiate")
    public ResponseEntity<?> initiatePayment(@RequestBody Map<String, Object> data) {
        try {
            Double amount = Double.valueOf(data.get("amount").toString());
            PaymentIntent intent = paymentService.createPaymentIntent(amount, "myr");
            return ResponseEntity.ok(Map.of("clientSecret", intent.getClientSecret()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Payment Init Failed");
        }
    }

    // 3. Confirm Booking
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(@RequestBody BookingRequestDTO request) {
        try {
            Ticket ticket = bookingService.processBooking(request);
            return ResponseEntity.ok(Map.of(
                "message", "Booking Successful",
                "ticketId", ticket.getDocumentId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Booking Failed: " + e.getMessage());
        }
    }

    // 4. Download Ticket PDF
    @GetMapping("/download/{ticketId}")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable String ticketId) {
        try {
            Ticket ticket = bookingService.getTicketDetails(ticketId);
            byte[] pdf = ticketDocumentService.generateTicketPdf(ticket);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "ticket.pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}