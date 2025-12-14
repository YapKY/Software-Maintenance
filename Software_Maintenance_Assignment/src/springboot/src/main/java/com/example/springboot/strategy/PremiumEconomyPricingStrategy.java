package com.example.springboot.strategy;


import com.example.springboot.model.Flight;
import org.springframework.stereotype.Component;

/**
 * Concrete Strategy for Premium Economy Class Pricing
 * 
 * Demonstrates the extensibility of the Strategy Pattern.
 * This strategy was added WITHOUT modifying BookingService or other strategies.
 * This is the Open-Closed Principle in action.
 */
@Component("premiumEconomyPricing")
public class PremiumEconomyPricingStrategy implements PricingStrategy {
    
    private static final double PREMIUM_MULTIPLIER = 1.30; // 30% more than economy
    
    @Override
    public double calculatePrice(Flight flight) {
        // Premium Economy is typically 30% more than Economy
        double economyPrice = flight.getEconomyPrice();
        return economyPrice * PREMIUM_MULTIPLIER;
    }
    
    @Override
    public String getSeatClass() {
        return "Premium Economy";
    }
    
    @Override
    public String getBenefits() {
        return "Extra legroom, priority boarding, premium meals, 30kg baggage allowance";
    }
    
    /**
     * Get the premium percentage over economy
     */
    public double getPremiumPercentage() {
        return (PREMIUM_MULTIPLIER - 1.0) * 100; // Returns 30.0
    }
}