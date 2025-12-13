package com.example.springboot.service;

import com.example.springboot.model.Flight;
import com.example.springboot.repository.FirestoreRepository;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FlightService {

    @Autowired
    private FirestoreRepository repository;

    /**
     * Search flights by departure country, arrival country, and departure date
     * Matches legacy system's searchFlight() method
     */
    public List<Flight> searchFlights(String departureCountry, String arrivalCountry, String departureDate) 
            throws ExecutionException, InterruptedException {
        
        List<Flight> matchingFlights = new ArrayList<>();
        
        // Query Firestore for matching flights
        Query query = repository.getFirestore()
                .collection("flights")
                .whereEqualTo("departureCountry", departureCountry)
                .whereEqualTo("arrivalCountry", arrivalCountry)
                .whereEqualTo("departureDate", departureDate);
        
        var snapshot = query.get().get();
        
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Flight flight = doc.toObject(Flight.class);
            flight.setDocumentId(doc.getId());
            matchingFlights.add(flight);
        }
        
        return matchingFlights;
    }

    /**
     * Get all available flights (for dropdown/listing)
     */
    public List<Flight> getAllFlights() throws ExecutionException, InterruptedException {
        List<Flight> flights = new ArrayList<>();
        
        var snapshot = repository.getFirestore()
                .collection("flights")
                .get()
                .get();
        
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Flight flight = doc.toObject(Flight.class);
            flight.setDocumentId(doc.getId());
            flights.add(flight);
        }
        
        return flights;
    }

    /**
     * Get flight by ID
     */
    public Flight getFlightById(String flightId) throws Exception {
        Flight flight = repository.findById("flights", flightId, Flight.class);
        if (flight == null) {
            throw new RuntimeException("Flight not found with ID: " + flightId);
        }
        flight.setDocumentId(flightId);
        return flight;
    }

    /**
     * Get flight by flightId field (e.g., "F001")
     */
    public Flight getFlightByFlightId(String flightId) throws ExecutionException, InterruptedException {
        Query query = repository.getFirestore()
                .collection("flights")
                .whereEqualTo("flightId", flightId)
                .limit(1);
        
        var snapshot = query.get().get();
        
        if (snapshot.isEmpty()) {
            throw new RuntimeException("Flight not found with flightId: " + flightId);
        }
        
        QueryDocumentSnapshot doc = snapshot.getDocuments().get(0);
        Flight flight = doc.toObject(Flight.class);
        flight.setDocumentId(doc.getId());
        return flight;
    }
}