package com.example.springboot.strategy;

import com.example.springboot.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EconomyClassPricingStrategyTest {

    private EconomyClassPricingStrategy strategy;
    private Flight testFlight;

    @BeforeEach
    void setUp() {
        strategy = new EconomyClassPricingStrategy();
        
        testFlight = new Flight();
        testFlight.setFlightId("F001");
        testFlight.setEconomyPrice(200.00);
        testFlight.setBusinessPrice(400.00);
    }

    @Test
    void testCalculatePrice_ReturnsEconomyPrice() {
        // Act
        double price = strategy.calculatePrice(testFlight);

        // Assert
        assertEquals(200.00, price);
    }

    @Test
    void testGetSeatClass_ReturnsEconomy() {
        // Act
        String seatClass = strategy.getSeatClass();

        // Assert
        assertEquals("Economy", seatClass);
    }

    @Test
    void testGetBenefits_ReturnsEconomyBenefits() {
        // Act
        String benefits = strategy.getBenefits();

        // Assert
        assertNotNull(benefits);
        assertTrue(benefits.contains("Standard seating"));
        assertTrue(benefits.contains("20kg baggage"));
    }

    @Test
    void testCalculatePriceWithDiscount_EarlyBird30Days() {
        // Act
        double discountedPrice = strategy.calculatePriceWithDiscount(testFlight, 30);

        // Assert
        assertEquals(170.00, discountedPrice, 0.01); // 15% discount
    }

    @Test
    void testCalculatePriceWithDiscount_EarlyBird15Days() {
        // Act
        double discountedPrice = strategy.calculatePriceWithDiscount(testFlight, 20);

        // Assert
        assertEquals(180.00, discountedPrice, 0.01); // 10% discount
    }

    @Test
    void testCalculatePriceWithDiscount_NoDiscount() {
        // Act
        double price = strategy.calculatePriceWithDiscount(testFlight, 5);

        // Assert
        assertEquals(200.00, price); // No discount
    }

    @Test
    void testCalculatePrice_DifferentFlightPrice() {
        // Arrange
        testFlight.setEconomyPrice(150.00);

        // Act
        double price = strategy.calculatePrice(testFlight);

        // Assert
        assertEquals(150.00, price);
    }
}