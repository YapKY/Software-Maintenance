package com.example.springboot.service;

import com.example.springboot.dto.request.BookingRequestDTO;
import com.example.springboot.model.*;
import com.example.springboot.repository.FirestoreRepository;
import com.example.springboot.strategy.PricingContext;
import com.example.springboot.factory.PassengerFactory;
import com.example.springboot.factory.TicketFactory;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
//import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
    public class BookingService {

        @Autowired private FirestoreRepository repository;
        @Autowired private NotificationService notificationService;
        @Autowired private PricingContext pricingContext;
        @Autowired private TicketFactory ticketFactory;
        @Autowired private PassengerFactory passengerFactory;

        public double calculateSeatPrice(Seat seat, Flight flight) throws Exception {
        if (flight == null) {
            throw new IllegalArgumentException("Flight cannot be null");
        }
        
        // Use Strategy Pattern to calculate price based on seat type
        return pricingContext.calculatePrice(seat.getTypeOfSeat(), flight);
    }

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

    // Display Seats
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

    // Booking Logic
    public Ticket processBooking(BookingRequestDTO request) throws Exception {
        // 0. Validate seat and flight
        Seat selectedSeat = repository.findById("seats", request.getSeatId(), Seat.class);
        if (selectedSeat == null) {
            throw new IllegalArgumentException("Seat not found");
        }
        selectedSeat.setDocumentId(request.getSeatId());
        
        Flight flight = getFlightByFlightIdField(selectedSeat.getFlightId());
        if (flight == null) {
            throw new IllegalArgumentException("Flight not found for seat");
        }
        
        // Validate pricing using Strategy Pattern
        double expectedPrice = calculateSeatPrice(selectedSeat, flight);
        if (Math.abs(request.getAmount() - expectedPrice) > 0.01) {
            throw new IllegalArgumentException(
                String.format("Price mismatch! Expected: %.2f, Received: %.2f", 
                    expectedPrice, request.getAmount())
            );
        }

        // 1. ✅ Create Passenger using Factory Pattern
        Passenger passenger = passengerFactory.createPassenger(
            request.getPassenger().getFullName(),
            request.getPassenger().getPassportNo(),
            request.getPassenger().getEmail(),
            request.getPassenger().getPhoneNumber()
        );
        String passengerId = repository.save("passengers", passenger);
        passenger.setDocumentId(passengerId);

        // 2. ✅ Create Ticket using Factory Pattern
        Ticket ticket = ticketFactory.createTicket(
        request.getCustomerId(),
        passengerId,
        request.getSeatId(),  // ✅ Pass the seatId directly from request
        selectedSeat,         // Still pass seat for display fields
        flight
    );
        
        String ticketId = repository.save("tickets", ticket);
        ticket.setDocumentId(ticketId);

        // 3. Save Payment
        Payment payment = new Payment();
        payment.setAmount(expectedPrice);
        payment.setPaymentDate(LocalDateTime.now().toString());
        payment.setPaymentStatus(true);
        payment.setStripePaymentIntentId(request.getStripePaymentIntentId());
        payment.setTicketId(ticketId);
        repository.save("payments", payment);

        // 4. Update Seat Status
        repository.updateField("seats", request.getSeatId(), "statusSeat", "Booked");

        // 5. Send Notification
        notificationService.sendBookingSuccessEmail(
            passenger.getEmail(),
            ticket.getBookingReference()
        );

        return ticket;
    }

    // Module 4: Get Ticket Metadata (Mocking a SQL Join)
    public Ticket getTicketDetails(String ticketId) throws Exception {
        Ticket ticket = repository.findById("tickets", ticketId, Ticket.class);
        if (ticket == null) {
            throw new RuntimeException("Ticket not found");
        }
        
        ticket.setDocumentId(ticketId);
        
        // ✅ Enrich with Passenger Details
        try {
            Passenger passenger = repository.findById("passengers", ticket.getPassengerId(), Passenger.class);
            if (passenger != null) {
                passenger.setDocumentId(ticket.getPassengerId());
                ticket.setPassengerDetails(passenger);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to load passenger: " + e.getMessage());
        }
        
        // ✅ Enrich with Seat Details
        try {
            Seat seat = repository.findById("seats", ticket.getSeatId(), Seat.class);
            if (seat != null) {
                seat.setDocumentId(ticket.getSeatId());
                ticket.setSeatNumberDisplay(String.valueOf(seat.getSeatNumber()));
                ticket.setSeatClassDisplay(seat.getTypeOfSeat());
                
                // ✅ Enrich with Flight Details via Seat
                if (seat.getFlightId() != null && !seat.getFlightId().isEmpty()) {
                    Flight flight = getFlightByFlightIdField(seat.getFlightId());
                    if (flight != null) {
                        ticket.setFlightDetails(flight);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to load seat/flight: " + e.getMessage());
        }
        
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
            if (passenger != null) {
                passenger.setDocumentId(ticket.getPassengerId());
                ticket.setPassengerDetails(passenger);
            }

            // 2. Get Seat Details
            Seat seat = repository.findById("seats", ticket.getSeatId(), Seat.class);
            
            if (seat != null) {
                seat.setDocumentId(ticket.getSeatId());
                
                // Set seat display info
                ticket.setSeatNumberDisplay(String.valueOf(seat.getSeatNumber()));
                ticket.setSeatClassDisplay(seat.getTypeOfSeat());
                
                System.out.println("✅ Found Seat: " + seat.getSeatNumber() + ", FlightRef: " + seat.getFlightId());

                // 3. Get Flight Details using the flightId from seat
                if (seat.getFlightId() != null && !seat.getFlightId().isEmpty()) {
                    Flight flight = getFlightByFlightIdField(seat.getFlightId());
                    if (flight != null) {
                        ticket.setFlightDetails(flight);
                        System.out.println("✅ Enriched ticket with flight: " + flight.getFlightId());
                    }
                } else {
                    System.err.println("❌ Seat has no flightId: " + seat.getDocumentId());
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

    public Seat getSeatById(String seatId) throws Exception {
        Seat seat = repository.findById("seats", seatId, Seat.class);
        if (seat == null) {
            throw new RuntimeException("Seat not found with ID: " + seatId);
        }
        seat.setDocumentId(seatId);
        return seat;
    }
    
    public Flight getFlightBySeatId(String seatId) throws Exception {
        // First get the seat to find which flight it belongs to
        Seat seat = getSeatById(seatId);
        
        if (seat.getFlightId() == null || seat.getFlightId().isEmpty()) {
            throw new RuntimeException("Seat does not have an associated flight");
        }
        
        // Then get the flight using the flightId field from the seat
        Flight flight = getFlightByFlightIdField(seat.getFlightId());
        
        if (flight == null) {
            throw new RuntimeException("Flight not found with ID: " + seat.getFlightId());
        }
        
        return flight;
    }
}