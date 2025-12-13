package com.example.springboot.strategy;

import com.example.springboot.model.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Context class for Strategy Pattern (Optional but recommended)
 * 
 * This class provides a clean way to select and use pricing strategies.
 * It encapsulates the strategy selection logic.
 */
@Component
public class PricingContext {
    
    private final Map<String, PricingStrategy> pricingStrategies;
    
    @Autowired
    public PricingContext(Map<String, PricingStrategy> pricingStrategies) {
        this.pricingStrategies = pricingStrategies;
    }
    
    /**
     * Calculate price using the appropriate strategy for the seat class
     * 
     * @param seatClass The seat class (Business, Economy, Premium Economy)
     * @param flight The flight to price
     * @return Calculated price
     */
    public double calculatePrice(String seatClass, Flight flight) {
        PricingStrategy strategy = getStrategy(seatClass);
        return strategy.calculatePrice(flight);
    }
    
    /**
     * Get the pricing strategy for a given seat class
     * 
     * @param seatClass The seat class
     * @return The appropriate pricing strategy
     */
    public PricingStrategy getStrategy(String seatClass) {
        String strategyKey = switch (seatClass.toLowerCase().trim()) {
            case "business" -> "businessPricing";
            case "economy" -> "economyPricing";
            case "premium economy" -> "premiumEconomyPricing";
            default -> "economyPricing"; // Default fallback
        };
        
        PricingStrategy strategy = pricingStrategies.get(strategyKey);
        
        if (strategy == null) {
            throw new IllegalArgumentException("No pricing strategy found for seat class: " + seatClass);
        }
        
        return strategy;
    }
    
    /**
     * Get benefits description for a seat class
     */
    public String getBenefits(String seatClass) {
        PricingStrategy strategy = getStrategy(seatClass);
        return strategy.getBenefits();
    }
}