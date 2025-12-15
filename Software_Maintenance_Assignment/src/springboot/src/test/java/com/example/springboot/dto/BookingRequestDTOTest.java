package com.example.springboot.dto;

import com.example.springboot.dto.request.BookingRequestDTO;
import com.example.springboot.model.Passenger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;

/**
 * Test class for BookingRequestDTO
 * 
 * Tests Module: Customer Booking Ticket Module
 * Coverage: DTO validation, data transfer
 */
class BookingRequestDTOTest {

    
    @DisplayName("Field Accessor Tests")
    class FieldAccessorTests {

        @Test
        @DisplayName("Should correctly set and retrieve all simple fields")
        void testSimpleFields_HappyPath() {
            // Arrange
            BookingRequestDTO dto = new BookingRequestDTO();
            String expectedCustId = "cust_12345";
            String expectedSeatId = "seat_A1";
            Double expectedAmount = 150.75;
            String expectedPaymentId = "pi_stripe_999";

            // Act
            dto.setCustomerId(expectedCustId);
            dto.setSeatId(expectedSeatId);
            dto.setAmount(expectedAmount);
            dto.setStripePaymentIntentId(expectedPaymentId);

            // Assert
            assertAll("Verify all simple properties",
                () -> assertEquals(expectedCustId, dto.getCustomerId(), "Customer ID should match"),
                () -> assertEquals(expectedSeatId, dto.getSeatId(), "Seat ID should match"),
                () -> assertEquals(expectedAmount, dto.getAmount(), "Amount should match"),
                () -> assertEquals(expectedPaymentId, dto.getStripePaymentIntentId(), "Payment ID should match")
            );
        }

        @Test
        @DisplayName("Should handle default null state (New Object)")
        void testDefaultConstructor_State() {
            BookingRequestDTO dto = new BookingRequestDTO();

            assertAll("Verify default nulls",
                () -> assertNull(dto.getCustomerId()),
                () -> assertNull(dto.getSeatId()),
                () -> assertNull(dto.getAmount()),
                () -> assertNull(dto.getStripePaymentIntentId()),
                () -> assertNull(dto.getPassenger())
            );
        }
    }

    
    @DisplayName("Nested Passenger Object Tests")
    class NestedObjectTests {

        @Test
        @DisplayName("Should correctly embed and retrieve Passenger object")
        void testPassengerAssociation() {
            // Arrange
            Passenger passenger = new Passenger();
            passenger.setFullName("John Doe");
            passenger.setPassportNo("P1234567");

            BookingRequestDTO dto = new BookingRequestDTO();

            // Act
            dto.setPassenger(passenger);

            // Assert
            assertNotNull(dto.getPassenger(), "Passenger object should not be null");
            assertAll("Verify nested passenger details",
                () -> assertEquals("John Doe", dto.getPassenger().getFullName()),
                () -> assertEquals("P1234567", dto.getPassenger().getPassportNo())
            );
        }

        @Test
        @DisplayName("Should handle explicit null Passenger")
        void testSetPassengerNull() {
            // Arrange
            BookingRequestDTO dto = new BookingRequestDTO();
            dto.setPassenger(new Passenger()); // Set initial value

            // Act
            dto.setPassenger(null); // Explicitly nullify

            // Assert
            assertNull(dto.getPassenger(), "Passenger should be null after setting to null");
        }
    }

    
    @DisplayName("Boundary & Edge Case Tests")
    class BoundaryTests {

        @Test
        @DisplayName("Should accept empty strings")
        void testEmptyStringFields() {
            BookingRequestDTO dto = new BookingRequestDTO();
            dto.setCustomerId("");
            dto.setSeatId("");
            dto.setStripePaymentIntentId("");

            assertAll("Verify empty strings",
                () -> assertEquals("", dto.getCustomerId()),
                () -> assertEquals("", dto.getSeatId()),
                () -> assertEquals("", dto.getStripePaymentIntentId())
            );
        }

        @Test
        @DisplayName("Should accept zero and negative amounts (DTO data carrier behavior)")
        void testAmountBoundaries() {
            BookingRequestDTO dto = new BookingRequestDTO();
            
            // Test Zero
            dto.setAmount(0.0);
            assertEquals(0.0, dto.getAmount());

            // Test Negative
            dto.setAmount(-100.50);
            assertEquals(-100.50, dto.getAmount());
            
            // Test High Precision
            dto.setAmount(99999.9999);
            assertEquals(99999.9999, dto.getAmount());
        }
    }

    @Test
    void testBookingRequestDTO_GettersAndSetters() {
        // Arrange
        Passenger passenger = new Passenger();
        passenger.setFullName("John Doe");
        passenger.setPassportNo("A12345678");

        // Act
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setCustomerId("cust123");
        dto.setSeatId("seat456");
        dto.setAmount(200.50);
        dto.setStripePaymentIntentId("pi_123456");
        dto.setPassenger(passenger);

        // Assert
        assertEquals("cust123", dto.getCustomerId());
        assertEquals("seat456", dto.getSeatId());
        assertEquals(200.50, dto.getAmount());
        assertEquals("pi_123456", dto.getStripePaymentIntentId());
        assertNotNull(dto.getPassenger());
        assertEquals("John Doe", dto.getPassenger().getFullName());
    }

    @Test
    void testBookingRequestDTO_DefaultValues() {
        // Act
        BookingRequestDTO dto = new BookingRequestDTO();

        // Assert
        assertNull(dto.getCustomerId());
        assertNull(dto.getSeatId());
        assertNull(dto.getAmount());
        assertNull(dto.getStripePaymentIntentId());
        assertNull(dto.getPassenger());
    }

    @Test
    void testBookingRequestDTO_WithNullPassenger() {
        // Act
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setCustomerId("cust123");
        dto.setSeatId("seat456");
        dto.setAmount(200.00);
        dto.setPassenger(null);

        // Assert
        assertNotNull(dto.getCustomerId());
        assertNull(dto.getPassenger());
    }

    @Test
    void testBookingRequestDTO_AllFieldsPopulated() {
        // Arrange
        Passenger passenger = new Passenger();
        passenger.setFullName("Jane Smith");
        passenger.setPassportNo("B98765432");
        passenger.setEmail("jane@example.com");
        passenger.setPhoneNumber("013-9876543");

        // Act
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setCustomerId("cust999");
        dto.setSeatId("seat789");
        dto.setAmount(450.75);
        dto.setStripePaymentIntentId("pi_abcdef");
        dto.setPassenger(passenger);

        // Assert
        assertEquals("cust999", dto.getCustomerId());
        assertEquals("seat789", dto.getSeatId());
        assertEquals(450.75, dto.getAmount());
        assertEquals("pi_abcdef", dto.getStripePaymentIntentId());
        assertEquals("Jane Smith", dto.getPassenger().getFullName());
        assertEquals("B98765432", dto.getPassenger().getPassportNo());
    }

    @Test
    void testBookingRequestDTO_AmountZero() {
        // Act
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setAmount(0.0);

        // Assert
        assertEquals(0.0, dto.getAmount());
    }

    @Test
    void testBookingRequestDTO_AmountNegative() {
        // Act
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setAmount(-100.00);

        // Assert
        assertEquals(-100.00, dto.getAmount());
    }

    @Test
    void testBookingRequestDTO_EmptyStrings() {
        // Act
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setCustomerId("");
        dto.setSeatId("");
        dto.setStripePaymentIntentId("");

        // Assert
        assertEquals("", dto.getCustomerId());
        assertEquals("", dto.getSeatId());
        assertEquals("", dto.getStripePaymentIntentId());
    }

    @Test
    void testBookingRequestDTO_LargeAmount() {
        // Act
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setAmount(99999.99);

        // Assert
        assertEquals(99999.99, dto.getAmount());
    }

    
    @DisplayName("Boilerplate & Object Identity Tests")
    class BoilerplateTests {

        @Test
        @DisplayName("Should verify equals() and hashCode() contracts")
        void testEqualsAndHashCode() {
            // Arrange: Create two identical objects
            BookingRequestDTO dto1 = new BookingRequestDTO();
            dto1.setCustomerId("cust1");
            dto1.setSeatId("seat1");

            BookingRequestDTO dto2 = new BookingRequestDTO();
            dto2.setCustomerId("cust1");
            dto2.setSeatId("seat1");

            // Arrange: Create a different object
            BookingRequestDTO dto3 = new BookingRequestDTO();
            dto3.setCustomerId("cust2");
            dto3.setSeatId("seat2");

            // Assert: Equality consistency
            assertEquals(dto1, dto2, "Identical objects must be equal");
            assertEquals(dto1.hashCode(), dto2.hashCode(), "Identical objects must have same hash code");
            
            // Assert: Inequality
            assertNotEquals(dto1, dto3, "Different objects must not be equal");
            assertNotEquals(null, dto1, "Object must not be equal to null");
            assertNotEquals(dto1, new Object(), "Object must not be equal to different type");
        }

        @Test
        @DisplayName("Should verify toString() output")
        void testToString() {
            // Arrange
            BookingRequestDTO dto = new BookingRequestDTO();
            dto.setCustomerId("TEST_ID");
            dto.setAmount(100.0);

            // Act
            String stringResult = dto.toString();

            // Assert
            assertNotNull(stringResult);
            // Verify it contains key data (Lombok/Standard IDE generation usually includes field values)
            assertTrue(stringResult.contains("TEST_ID")); 
            assertTrue(stringResult.contains("100.0"));
        }
    }
}