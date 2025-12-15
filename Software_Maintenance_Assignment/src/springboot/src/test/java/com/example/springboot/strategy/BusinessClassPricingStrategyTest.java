package com.example.springboot.strategy;

import com.example.springboot.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BusinessClassPricingStrategyTest {

    private BusinessClassPricingStrategy strategy;
    private Flight testFlight;

    @BeforeEach
    void setUp() {
        strategy = new BusinessClassPricingStrategy();
        
        testFlight = new Flight();
        testFlight.setFlightId("F001");
        testFlight.setEconomyPrice(200.00);
        testFlight.setBusinessPrice(400.00);
    }

    @Test
    void testCalculatePrice_ReturnsBusinessPrice() {
        // Act
        double price = strategy.calculatePrice(testFlight);

        // Assert
        assertEquals(400.00, price);
    }

    @Test
    void testGetSeatClass_ReturnsBusiness() {
        // Act
        String seatClass = strategy.getSeatClass();

        // Assert
        assertEquals("Business", seatClass);
    }

    @Test
    void testGetBenefits_ReturnsBusinessBenefits() {
        // Act
        String benefits = strategy.getBenefits();

        // Assert
        assertNotNull(benefits);
        assertTrue(benefits.contains("Priority boarding"));
        assertTrue(benefits.contains("lounge access"));
        assertTrue(benefits.contains("40kg baggage"));
    }

    @Test
    void testCalculatePriceWithAdjustments_PeakHourOnly() {
        // Act
        double adjustedPrice = strategy.calculatePriceWithAdjustments(testFlight, true, false);

        // Assert
        assertEquals(460.00, adjustedPrice, 0.01); // 15% peak hour surcharge
    }

    @Test
    void testCalculatePriceWithAdjustments_WeekendOnly() {
        // Act
        double adjustedPrice = strategy.calculatePriceWithAdjustments(testFlight, false, true);

        // Assert
        assertEquals(440.00, adjustedPrice, 0.01); // 10% weekend surcharge
    }

    @Test
    void testCalculatePriceWithAdjustments_BothPeakAndWeekend() {
        // Act
        double adjustedPrice = strategy.calculatePriceWithAdjustments(testFlight, true, true);

        // Assert
        // 400 * 1.15 * 1.10 = 506.00
        assertEquals(506.00, adjustedPrice, 0.01);
    }

    @Test
    void testCalculatePriceWithAdjustments_NoAdjustments() {
        // Act
        double price = strategy.calculatePriceWithAdjustments(testFlight, false, false);

        // Assert
        assertEquals(400.00, price);
    }

    @Test
    void testCalculatePrice_DifferentFlightPrice() {
        // Arrange
        testFlight.setBusinessPrice(500.00);

        // Act
        double price = strategy.calculatePrice(testFlight);

        // Assert
        assertEquals(500.00, price);
    }
}