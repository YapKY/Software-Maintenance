package com.example.springboot.service;

import com.example.springboot.model.*;
import com.example.springboot.repository.FirestoreRepository;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
    public class BookingService {

        @Autowired private FirestoreRepository repository;
        @Autowired private NotificationService notificationService;

        public List<Seat> getSeatsByFlightId(String flightId) throws ExecutionException, InterruptedException {
        List<Seat> seats = new ArrayList<>();
        var querySnapshot = repository.getCollectionByField("seats", "flightId", flightId).get().get();
        
        for (QueryDocumentSnapshot doc : querySnapshot.getDocuments()) {
            Seat seat = doc.toObject(Seat.class);
            seat.setDocumentId(doc.getId());
            seats.add(seat);
        }
        return seats;
    }   

    // Module 1: Display Seats
    public List<Seat> getSeatsForFlight(String flightId) throws ExecutionException, InterruptedException {
        List<Seat> seats = new ArrayList<>();
        var querySnapshot = repository.getCollectionByField("seats", "flightId", flightId).get().get();
        
        for (QueryDocumentSnapshot doc : querySnapshot.getDocuments()) {
            Seat seat = doc.toObject(Seat.class);
            seat.setDocumentId(doc.getId());
            seats.add(seat);
        }
        return seats;
    }

    // Module 2 & 3: Booking Logic
    public Ticket processBooking(BookingRequestDTO request) throws Exception {
        // 1. Create Passenger
        String passengerId = repository.save("passengers", request.getPassenger());

        // 2. Create Ticket
        Ticket ticket = new Ticket();
        ticket.setCustomerId(request.getCustomerId());
        ticket.setPassengerId(passengerId);
        ticket.setSeatId(request.getSeatId());
        ticket.setBookingReference(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        String ticketId = repository.save("tickets", ticket);
        ticket.setDocumentId(ticketId);

        // 3. Save Payment
        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(LocalDateTime.now().toString());
        payment.setPaymentStatus(true);
        payment.setStripePaymentIntentId(request.getStripePaymentIntentId());
        payment.setTicketId(ticketId);
        repository.save("payments", payment);

        // 4. Update Seat Status
        repository.updateField("seats", request.getSeatId(), "statusSeat", "Booked");

        // 5. Send Notification
        notificationService.sendBookingSuccessEmail(
            request.getPassenger().getEmail(),
            ticket.getBookingReference()
        );

        return ticket;
    }

    // Module 4: Get Ticket Metadata (Mocking a SQL Join)
    public Ticket getTicketDetails(String ticketId) throws Exception {
        Ticket ticket = repository.findById("tickets", ticketId, Ticket.class);
        if (ticket == null) throw new RuntimeException("Ticket not found");

        Seat seat = repository.findById("seats", ticket.getSeatId(), Seat.class);
        if (seat != null) {
            ticket.setSeatNumberDisplay(String.valueOf(seat.getSeatNumber()));
            ticket.setSeatClassDisplay(seat.getTypeOfSeat());
        }
        ticket.setDocumentId(ticketId);
        return ticket;
    }

    /**
     * Get all tickets for a customer
     */
    public List<Ticket> getCustomerTickets(String customerId) throws ExecutionException, InterruptedException {
        System.out.println("🔍 Loading tickets for customer: " + customerId);
        
        List<Ticket> customerTickets = new ArrayList<>();
        var querySnapshot = repository.getCollectionByField("tickets", "customerId", customerId).get().get();
        
        for (QueryDocumentSnapshot doc : querySnapshot.getDocuments()) {
            Ticket ticket = doc.toObject(Ticket.class);
            ticket.setDocumentId(doc.getId());
            
            // Enrich each ticket with details
            enrichTicketDetails(ticket);
            
            customerTickets.add(ticket);
        }
        
        System.out.println("✅ Found " + customerTickets.size() + " tickets");
        return customerTickets;
    }

    /**
     * Enrich ticket with flight and passenger details
     */
    private void enrichTicketDetails(Ticket ticket) {
        try {
            // 1. Get Passenger Details
            Passenger passenger = repository.findById("passengers", ticket.getPassengerId(), Passenger.class);
            ticket.setPassengerDetails(passenger);

            // 2. Get Seat Details (Crucial Step)
            // We MUST find the seat to know which flight this ticket belongs to.
            Seat seat = repository.findById("seats", ticket.getSeatId(), Seat.class);
            
            if (seat != null) {
                // Set seat display info
                ticket.setSeatNumberDisplay(String.valueOf(seat.getSeatNumber()));
                ticket.setSeatClassDisplay(seat.getTypeOfSeat());
                
                System.out.println("✅ Found Seat: " + seat.getSeatNumber() + ", FlightRef: " + seat.getFlightId());

                // 3. Get Flight Details using the ID found inside the SEAT
                if (seat.getFlightId() != null) {
                    Flight flight = getFlightByFlightIdField(seat.getFlightId());
                    ticket.setFlightDetails(flight);
                } else {
                    System.err.println("❌ Seat found, but it has no flightId: " + seat.getDocumentId());
                }
            } else {
                System.err.println("❌ Seat not found for ticket: " + ticket.getDocumentId());
            }

        } catch (Exception e) {
            System.err.println("⚠️ Error enriching ticket details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ✅ NEW METHOD: Fetch flight by flightId field (F001, F002, etc)
     * NOT by Firestore document ID
     */
    private Flight getFlightByFlightIdField(String flightId) throws ExecutionException, InterruptedException {
        System.out.println("🔍 Searching for flight with flightId: " + flightId);
        
        // Query: SELECT * FROM flights WHERE flightId = "F001"
        var querySnapshot = repository.getCollectionByField("flights", "flightId", flightId).get().get();
        
        if (querySnapshot.isEmpty()) {
            System.err.println("❌ No flight found with flightId: " + flightId);
            return null;
        }
        
        // Get first matching document
        QueryDocumentSnapshot doc = querySnapshot.getDocuments().get(0);
        Flight flight = doc.toObject(Flight.class);
        flight.setDocumentId(doc.getId());
        
        System.out.println("✅ Found flight: " + flight.getFlightId() + " (Document ID: " + doc.getId() + ")");
        return flight;
    }
    
}