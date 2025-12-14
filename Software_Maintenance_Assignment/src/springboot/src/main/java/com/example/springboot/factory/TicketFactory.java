package com.example.springboot.factory;

import com.example.springboot.model.Flight;
import com.example.springboot.model.Passenger;
import com.example.springboot.model.Seat;
import com.example.springboot.model.Ticket;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TicketFactory {
    
    /**
     * ✅ UPDATED: Accept seatId directly instead of relying on seat.getDocumentId()
     */
    public Ticket createTicket(String customerId, String passengerId, String seatId, Seat seat, Flight flight) {
        validateInputs(customerId, passengerId, seat, flight);
        
        Ticket ticket = new Ticket();
        
        // Set core identifiers
        ticket.setCustomerId(customerId);
        ticket.setPassengerId(passengerId);
        ticket.setSeatId(seatId);  // ✅ Use the passed seatId directly
        ticket.setFlightId(flight.getFlightId());
        
        // Generate unique booking reference
        ticket.setBookingReference(generateBookingReference());
        
        // Set display fields from seat object
        //ticket.setSeatNumberDisplay(String.valueOf(seat.getSeatNumber()));
        //ticket.setSeatClassDisplay(seat.getTypeOfSeat());
        
        return ticket;
    }
    
    /**
     * Simplified ticket creation for minimum required fields
     */
    public Ticket createBasicTicket(String customerId, String passengerId, String seatId, String flightId) {
        Ticket ticket = new Ticket();
        ticket.setCustomerId(customerId);
        ticket.setPassengerId(passengerId);
        ticket.setSeatId(seatId);
        ticket.setFlightId(flightId);
        ticket.setBookingReference(generateBookingReference());
        return ticket;
    }
    
    private String generateBookingReference() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }
    
    private void validateInputs(String customerId, String passengerId, Seat seat, Flight flight) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        if (passengerId == null || passengerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger ID cannot be null or empty");
        }
        if (seat == null) {
            throw new IllegalArgumentException("Seat cannot be null");
        }
        if (flight == null) {
            throw new IllegalArgumentException("Flight cannot be null");
        }
        if (seat.getStatusSeat() != null && seat.getStatusSeat().equals("Booked")) {
            throw new IllegalStateException("Cannot create ticket for already booked seat");
        }
    }
    
    /**
     * Creates a ticket with enriched details
     */
    public Ticket createEnrichedTicket(String customerId, Passenger passenger, String seatId, Seat seat, Flight flight) {
        Ticket ticket = createTicket(customerId, passenger.getDocumentId(), seatId, seat, flight);
        
        ticket.setPassengerDetails(passenger);
        ticket.setFlightDetails(flight);
        
        return ticket;
    }
}