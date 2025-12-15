package com.example.springboot.strategy;

import com.example.springboot.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PricingContextTest {

    private PricingContext pricingContext;
    private Flight testFlight;
    private Map<String, PricingStrategy> strategyMap;

    @BeforeEach
    void setUp() {
        // Create strategy instances
        EconomyClassPricingStrategy economyStrategy = new EconomyClassPricingStrategy();
        BusinessClassPricingStrategy businessStrategy = new BusinessClassPricingStrategy();
        PremiumEconomyPricingStrategy premiumStrategy = new PremiumEconomyPricingStrategy();

        // Create strategy map
        strategyMap = new HashMap<>();
        strategyMap.put("economyPricing", economyStrategy);
        strategyMap.put("businessPricing", businessStrategy);
        strategyMap.put("premiumEconomyPricing", premiumStrategy);

        pricingContext = new PricingContext(strategyMap);

        // Create test flight
        testFlight = new Flight();
        testFlight.setEconomyPrice(200.00);
        testFlight.setBusinessPrice(400.00);
    }

    @Test
    void testCalculatePrice_EconomyClass() {
        // Act
        double price = pricingContext.calculatePrice("Economy", testFlight);

        // Assert
        assertEquals(200.00, price);
    }

    @Test
    void testCalculatePrice_BusinessClass() {
        // Act
        double price = pricingContext.calculatePrice("Business", testFlight);

        // Assert
        assertEquals(400.00, price);
    }

    @Test
    void testCalculatePrice_PremiumEconomy() {
        // Act
        double price = pricingContext.calculatePrice("Premium Economy", testFlight);

        // Assert
        assertEquals(260.00, price, 0.01);
    }

    @Test
    void testCalculatePrice_CaseInsensitive() {
        // Act
        double priceUpper = pricingContext.calculatePrice("ECONOMY", testFlight);
        double priceLower = pricingContext.calculatePrice("economy", testFlight);
        double priceMixed = pricingContext.calculatePrice("EcOnOmY", testFlight);

        // Assert
        assertEquals(200.00, priceUpper);
        assertEquals(200.00, priceLower);
        assertEquals(200.00, priceMixed);
    }

    @Test
    void testCalculatePrice_HandlesExtraWhitespace() {
        // Act
        double price = pricingContext.calculatePrice("  Economy  ", testFlight);

        // Assert
        assertEquals(200.00, price);
    }

    @Test
    void testCalculatePrice_InvalidSeatClass_UsesDefaultEconomy() {
        // Act
        double price = pricingContext.calculatePrice("FirstClass", testFlight);

        // Assert
        assertEquals(200.00, price); // Falls back to economy
    }

    @Test
    void testGetStrategy_EconomyClass() {
        // Act
        PricingStrategy strategy = pricingContext.getStrategy("Economy");

        // Assert
        assertNotNull(strategy);
        assertEquals("Economy", strategy.getSeatClass());
    }

    @Test
    void testGetStrategy_BusinessClass() {
        // Act
        PricingStrategy strategy = pricingContext.getStrategy("Business");

        // Assert
        assertNotNull(strategy);
        assertEquals("Business", strategy.getSeatClass());
    }

    @Test
    void testGetStrategy_PremiumEconomy() {
        // Act
        PricingStrategy strategy = pricingContext.getStrategy("Premium Economy");

        // Assert
        assertNotNull(strategy);
        assertEquals("Premium Economy", strategy.getSeatClass());
    }

    @Test
    void testGetBenefits_EconomyClass() {
        // Act
        String benefits = pricingContext.getBenefits("Economy");

        // Assert
        assertNotNull(benefits);
        assertTrue(benefits.contains("20kg baggage"));
    }

    @Test
    void testGetBenefits_BusinessClass() {
        // Act
        String benefits = pricingContext.getBenefits("Business");

        // Assert
        assertNotNull(benefits);
        assertTrue(benefits.contains("40kg baggage"));
        assertTrue(benefits.contains("Priority boarding"));
    }

    @Test
    void testGetBenefits_PremiumEconomy() {
        // Act
        String benefits = pricingContext.getBenefits("Premium Economy");

        // Assert
        assertNotNull(benefits);
        assertTrue(benefits.contains("30kg baggage"));
        assertTrue(benefits.contains("Extra legroom"));
    }

    @Test
    void testGetStrategy_NullSeatClass_ThrowsException() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            pricingContext.getStrategy(null);
        });
    }

    @Test
    void testCalculatePrice_MultipleCallsSameClass() {
        // Act
        double price1 = pricingContext.calculatePrice("Economy", testFlight);
        double price2 = pricingContext.calculatePrice("Economy", testFlight);

        // Assert
        assertEquals(price1, price2);
    }
}