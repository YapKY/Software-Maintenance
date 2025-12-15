package com.example.springboot.strategy;

import com.example.springboot.model.Flight;
/**
 * Strategy Pattern Interface for Pricing Calculation
 * 
 * This interface defines the contract for different pricing strategies
 * based on seat class. Following the Gang of Four Strategy Pattern,
 * this allows the pricing algorithm to vary independently from the
 * clients that use it.
 * 
 * Benefits:
 * - Open-Closed Principle: Add new pricing strategies without modifying existing code
 * - Single Responsibility: Each strategy handles its own pricing logic
 * - Testability: Each strategy can be unit tested independently
 */
public interface PricingStrategy {
    
    /**
     * Calculate the price for a given flight based on the seat class
     * 
     * @param flight The flight for which to calculate price
     * @return The calculated price
     */
    double calculatePrice(Flight flight);
    
    /**
     * Get the seat class this strategy applies to
     * 
     * @return Seat class name (e.g., "Business", "Economy")
     */
    String getSeatClass();
    
    /**
     * Get additional benefits/features included with this seat class
     * 
     * @return Description of benefits
     */
    String getBenefits();
}
