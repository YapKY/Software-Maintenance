package com.example.springboot.strategy;

import com.example.springboot.model.Flight;
import org.springframework.stereotype.Component;

/**
 * Concrete Strategy for Business Class Pricing
 * 
 * Implements the pricing logic specific to Business class seats.
 * Part of the Strategy Pattern implementation.
 */
@Component("businessPricing")
public class BusinessClassPricingStrategy implements PricingStrategy {
    
    @Override
    public double calculatePrice(Flight flight) {
        // Business class pricing logic
        double basePrice = flight.getBusinessPrice();
        
        // You can add dynamic pricing logic here in the future
        // For example:
        // - Peak hour surcharge (6am-9am, 5pm-8pm)
        // - Weekend surcharge
        // - Holiday pricing
        // - Last-minute booking fees
        
        return basePrice;
    }
    
    @Override
    public String getSeatClass() {
        return "Business";
    }
    
    @Override
    public String getBenefits() {
        return "Priority boarding, lounge access, premium meals, 40kg baggage allowance, extra legroom";
    }
    
    /**
     * Calculate price with dynamic adjustments (future enhancement)
     */
    public double calculatePriceWithAdjustments(Flight flight, boolean isPeakHour, boolean isWeekend) {
        double price = calculatePrice(flight);
        
        if (isPeakHour) {
            price *= 1.15; // 15% peak hour surcharge
        }
        
        if (isWeekend) {
            price *= 1.10; // 10% weekend surcharge
        }
        
        return price;
    }
}
