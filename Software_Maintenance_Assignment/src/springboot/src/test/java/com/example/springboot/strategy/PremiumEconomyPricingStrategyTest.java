package com.example.springboot.strategy;

import com.example.springboot.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PremiumEconomyPricingStrategyTest {

    private PremiumEconomyPricingStrategy strategy;
    private Flight testFlight;

    @BeforeEach
    void setUp() {
        strategy = new PremiumEconomyPricingStrategy();
        
        testFlight = new Flight();
        testFlight.setFlightId("F001");
        testFlight.setEconomyPrice(200.00);
        testFlight.setBusinessPrice(400.00);
    }

    @Test
    void testCalculatePrice_Returns30PercentMoreThanEconomy() {
        // Act
        double price = strategy.calculatePrice(testFlight);

        // Assert
        assertEquals(260.00, price, 0.01); // 200 * 1.30
    }

    @Test
    void testGetSeatClass_ReturnsPremiumEconomy() {
        // Act
        String seatClass = strategy.getSeatClass();

        // Assert
        assertEquals("Premium Economy", seatClass);
    }

    @Test
    void testGetBenefits_ReturnsPremiumEconomyBenefits() {
        // Act
        String benefits = strategy.getBenefits();

        // Assert
        assertNotNull(benefits);
        assertTrue(benefits.contains("Extra legroom"));
        assertTrue(benefits.contains("30kg baggage"));
        assertTrue(benefits.contains("priority boarding"));
    }

    @Test
    void testGetPremiumPercentage_Returns30Percent() {
        PremiumEconomyPricingStrategy strategy = new PremiumEconomyPricingStrategy();
        // NEW LINE (Passes - allows a 0.001 difference):
        assertEquals(30.0, strategy.getPremiumPercentage(), 0.001);
    }

    @Test
    void testCalculatePrice_DifferentEconomyPrice() {
        // Arrange
        testFlight.setEconomyPrice(150.00);

        // Act
        double price = strategy.calculatePrice(testFlight);

        // Assert
        assertEquals(195.00, price, 0.01); // 150 * 1.30
    }
}