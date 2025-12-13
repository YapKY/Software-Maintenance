package com.example.springboot.strategy;

import com.example.springboot.model.Flight;
import org.springframework.stereotype.Component;

/**
 * Concrete Strategy for Economy Class Pricing
 * 
 * Implements the pricing logic specific to Economy class seats.
 * Part of the Strategy Pattern implementation.
 */
@Component("economyPricing")
public class EconomyClassPricingStrategy implements PricingStrategy {
    
    @Override
    public double calculatePrice(Flight flight) {
        // Economy class pricing logic
        double basePrice = flight.getEconomyPrice();
        
        // Economy class might have promotional discounts
        // This logic is isolated from Business class pricing
        
        return basePrice;
    }
    
    @Override
    public String getSeatClass() {
        return "Economy";
    }
    
    @Override
    public String getBenefits() {
        return "Standard seating, 20kg baggage allowance, basic meal service";
    }
    
    /**
     * Calculate price with early bird discount (future enhancement)
     */
    public double calculatePriceWithDiscount(Flight flight, int daysBeforeDeparture) {
        double price = calculatePrice(flight);
        
        // Early bird discount: 30+ days = 15% off, 15-29 days = 10% off
        if (daysBeforeDeparture >= 30) {
            price *= 0.85; // 15% discount
        } else if (daysBeforeDeparture >= 15) {
            price *= 0.90; // 10% discount
        }
        
        return price;
    }
}
