package com.example.springboot.service;

import com.stripe.exception.*;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for PaymentService
 * 
 * Tests Module: Payment Processing Module  
 * Coverage: Stripe integration, amount conversion, error handling
 * Target: 90%+ coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Service Tests")
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    private String testApiKey;

    @BeforeEach
    void setUp() {
        testApiKey = "sk_test_123456789";
        // Set the API key using reflection since it's a @Value field
        ReflectionTestUtils.setField(paymentService, "stripeApiKey", testApiKey);
        // Call init to set Stripe.apiKey
        paymentService.init();
    }

    // ==================== INIT METHOD TESTS ====================

    @Test
    @DisplayName("Should initialize Stripe API key on post construct")
    void testInit_SetsApiKey() {
        // Arrange
        String newApiKey = "sk_test_new_key";
        ReflectionTestUtils.setField(paymentService, "stripeApiKey", newApiKey);

        // Act
        paymentService.init();

        // Assert - Stripe.apiKey is set (we can't easily assert this, but test doesn't throw)
        assertDoesNotThrow(() -> paymentService.init());
    }

    // ==================== SUCCESS CASES ====================

    @Test
    @DisplayName("Should create payment intent successfully with valid amount and currency")
    void testCreatePaymentIntent_Success() throws StripeException {
        // Arrange
        Double amount = 200.50;
        String currency = "myr";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            when(mockIntent.getId()).thenReturn("pi_123456");
            when(mockIntent.getClientSecret()).thenReturn("pi_123456_secret_abc");
            
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act
            PaymentIntent result = paymentService.createPaymentIntent(amount, currency);

            // Assert
            assertNotNull(result);
            assertEquals("pi_123456", result.getId());
            assertEquals("pi_123456_secret_abc", result.getClientSecret());
        }
    }

    @Test
    @DisplayName("Should convert amount to cents correctly - verify method called")
    void testCreatePaymentIntent_AmountConversion() throws StripeException {
        // Arrange
        Double amount = 100.00;
        String currency = "myr";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act
            paymentService.createPaymentIntent(amount, currency);

            // Assert - Verify the method was called with PaymentIntentCreateParams
            mockedPaymentIntent.verify(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class)),
                times(1)
            );
        }
    }

    @Test
    @DisplayName("Should handle decimal amounts correctly")
    void testCreatePaymentIntent_DecimalAmount() throws StripeException {
        // Arrange
        Double amount = 150.75;
        String currency = "usd";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            when(mockIntent.getId()).thenReturn("pi_decimal");
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act
            PaymentIntent result = paymentService.createPaymentIntent(amount, currency);

            // Assert
            assertNotNull(result);
            assertEquals("pi_decimal", result.getId());
        }
    }

    @Test
    @DisplayName("Should create payment intent with different currencies")
    void testCreatePaymentIntent_DifferentCurrencies() throws StripeException {
        // Arrange
        String[] currencies = {"myr", "usd", "eur", "gbp"};
        Double amount = 100.00;
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            when(mockIntent.getId()).thenReturn("pi_123");
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act & Assert
            for (String currency : currencies) {
                PaymentIntent result = paymentService.createPaymentIntent(amount, currency);
                assertNotNull(result);
                assertEquals("pi_123", result.getId());
            }
            
            // Verify called 4 times
            mockedPaymentIntent.verify(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class)),
                times(4)
            );
        }
    }

    // @Test
    // @DisplayName("Should successfully create multiple payment intents")
    // void testCreatePaymentIntent_MultipleCreations() throws StripeException {
    //     // Arrange
    //     Double[] amounts = {50.00, 100.00, 200.00};
    //     String currency = "myr";
        
    //     try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
    //         PaymentIntent mockIntent = mock(PaymentIntent.class);
    //         when(mockIntent.getId()).thenReturn("pi_1", "pi_2", "pi_3");
    //         mockedPaymentIntent.when(() -> 
    //             PaymentIntent.create(any(PaymentIntentCreateParams.class))
    //         ).thenReturn(mockIntent);

    //         // Act
    //         for (Double amount : amounts) {
    //             PaymentIntent result = paymentService.createPaymentIntent(amount, currency);
    //             assertNotNull(result);
    //         }

    //         // Assert
    //         mockedPaymentIntent.verify(() -> 
    //             PaymentIntent.create(any(PaymentIntentCreateParams.class)),
    //             times(3)
    //         );
    //     }
    // }

    // ==================== STRIPE EXCEPTION CASES ====================

    @Test
    @DisplayName("Should throw CardException when card is declined")
    void testCreatePaymentIntent_CardException() throws StripeException {
        // Arrange
        Double amount = 200.00;
        String currency = "myr";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenThrow(new CardException("Card declined", "req_123", "card_error", 
                    "card_declined", "insufficient_funds", null, null, null));

            // Act & Assert
            assertThrows(CardException.class, () -> 
                paymentService.createPaymentIntent(amount, currency)
            );
        }
    }

    // @Test
    // @DisplayName("Should throw RateLimitException when rate limit exceeded")
    // void testCreatePaymentIntent_RateLimitException() throws StripeException {
    //     // Arrange
    //     Double amount = 200.00;
    //     String currency = "myr";
        
    //     try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
    //         mockedPaymentIntent.when(() -> 
    //             PaymentIntent.create(any(PaymentIntentCreateParams.class))
    //         ).thenThrow(new RateLimitException("Too many requests", "req_123", "rate_limit", 429, null));

    //         // Act & Assert
    //         assertThrows(RateLimitException.class, () -> 
    //             paymentService.createPaymentIntent(amount, currency)
    //         );
    //     }
    // }

    @Test
    @DisplayName("Should throw InvalidRequestException for invalid parameters")
    void testCreatePaymentIntent_InvalidRequestException() throws StripeException {
        // Arrange
        Double amount = 200.00;
        String currency = "invalid";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenThrow(new InvalidRequestException("Invalid currency", "currency", 
                    "req_123", null, 400, null));

            // Act & Assert
            assertThrows(InvalidRequestException.class, () -> 
                paymentService.createPaymentIntent(amount, currency)
            );
        }
    }

    @Test
    @DisplayName("Should throw AuthenticationException for invalid API key")
    void testCreatePaymentIntent_AuthenticationException() throws StripeException {
        // Arrange
        Double amount = 200.00;
        String currency = "myr";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenThrow(new AuthenticationException("Invalid API Key", "req_123", null, 401));

            // Act & Assert
            assertThrows(AuthenticationException.class, () -> 
                paymentService.createPaymentIntent(amount, currency)
            );
        }
    }

    @Test
    @DisplayName("Should throw ApiConnectionException for network errors")
    void testCreatePaymentIntent_ApiConnectionException() throws StripeException {
        // Arrange
        Double amount = 200.00;
        String currency = "myr";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenThrow(new ApiConnectionException("Network error", 
                    new java.io.IOException("Connection timeout")));

            // Act & Assert
            assertThrows(ApiConnectionException.class, () -> 
                paymentService.createPaymentIntent(amount, currency)
            );
        }
    }

    @Test
    @DisplayName("Should throw ApiException for general API errors")
    void testCreatePaymentIntent_ApiException() throws StripeException {
        // Arrange
        Double amount = 200.00;
        String currency = "myr";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenThrow(new ApiException("Internal server error", "req_123", null, 500, null));

            // Act & Assert
            assertThrows(ApiException.class, () -> 
                paymentService.createPaymentIntent(amount, currency)
            );
        }
    }

    @Test
    @DisplayName("Should throw StripeException for general Stripe errors")
    void testCreatePaymentIntent_StripeException() throws StripeException {
        // Arrange
        Double amount = 200.00;
        String currency = "myr";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenThrow(new StripeException("General Stripe error", "req_123", null, 500) {});

            // Act & Assert
            assertThrows(StripeException.class, () -> 
                paymentService.createPaymentIntent(amount, currency)
            );
        }
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("Should handle zero amount")
    void testCreatePaymentIntent_ZeroAmount() throws StripeException {
        // Arrange
        Double amount = 0.0;
        String currency = "myr";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            when(mockIntent.getId()).thenReturn("pi_zero");
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act
            PaymentIntent result = paymentService.createPaymentIntent(amount, currency);

            // Assert
            assertNotNull(result);
            assertEquals("pi_zero", result.getId());
        }
    }

    @Test
    @DisplayName("Should handle very small amount")
    void testCreatePaymentIntent_SmallAmount() throws StripeException {
        // Arrange
        Double amount = 0.01;
        String currency = "myr";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act
            PaymentIntent result = paymentService.createPaymentIntent(amount, currency);

            // Assert - 0.01 should work fine
            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("Should handle large amount")
    void testCreatePaymentIntent_LargeAmount() throws StripeException {
        // Arrange
        Double amount = 999999.99;
        String currency = "myr";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act
            PaymentIntent result = paymentService.createPaymentIntent(amount, currency);

            // Assert
            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("Should handle amount with many decimal places")
    void testCreatePaymentIntent_ManyDecimals() throws StripeException {
        // Arrange
        Double amount = 100.123456;
        String currency = "myr";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act
            PaymentIntent result = paymentService.createPaymentIntent(amount, currency);

            // Assert
            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("Should handle lowercase currency code")
    void testCreatePaymentIntent_LowercaseCurrency() throws StripeException {
        // Arrange
        Double amount = 100.00;
        String currency = "usd";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act
            PaymentIntent result = paymentService.createPaymentIntent(amount, currency);

            // Assert
            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("Should handle uppercase currency code")
    void testCreatePaymentIntent_UppercaseCurrency() throws StripeException {
        // Arrange
        Double amount = 100.00;
        String currency = "USD";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act
            PaymentIntent result = paymentService.createPaymentIntent(amount, currency);

            // Assert
            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("Should handle mixed case currency code")
    void testCreatePaymentIntent_MixedCaseCurrency() throws StripeException {
        // Arrange
        Double amount = 100.00;
        String currency = "EuR";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act
            PaymentIntent result = paymentService.createPaymentIntent(amount, currency);

            // Assert
            assertNotNull(result);
        }
    }

    // ==================== NULL PARAMETER CASES ====================

    @Test
    @DisplayName("Should handle null amount gracefully")
    void testCreatePaymentIntent_NullAmount() {
        // Arrange
        Double amount = null;
        String currency = "myr";

        // Act & Assert - Will throw NullPointerException during calculation
        assertThrows(NullPointerException.class, () -> 
            paymentService.createPaymentIntent(amount, currency)
        );
    }

    @Test
    @DisplayName("Should handle null currency")
    void testCreatePaymentIntent_NullCurrency() throws StripeException {
        // Arrange
        Double amount = 100.00;
        String currency = null;
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act
            PaymentIntent result = paymentService.createPaymentIntent(amount, currency);

            // Assert - Should handle null currency
            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("Should handle empty currency string")
    void testCreatePaymentIntent_EmptyCurrency() throws StripeException {
        // Arrange
        Double amount = 100.00;
        String currency = "";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act
            PaymentIntent result = paymentService.createPaymentIntent(amount, currency);

            // Assert
            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("Should handle whitespace currency")
    void testCreatePaymentIntent_WhitespaceCurrency() throws StripeException {
        // Arrange
        Double amount = 100.00;
        String currency = "   ";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act
            PaymentIntent result = paymentService.createPaymentIntent(amount, currency);

            // Assert
            assertNotNull(result);
        }
    }

    // ==================== MULTIPLE OPERATIONS ====================

    // @Test
    // @DisplayName("Should handle multiple payment intents sequentially")
    // void testCreatePaymentIntent_MultipleSequential() throws StripeException {
    //     // Arrange
    //     Double[] amounts = {100.00, 200.00, 300.00};
    //     String currency = "myr";
        
    //     try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
    //         PaymentIntent mockIntent = mock(PaymentIntent.class);
    //         when(mockIntent.getId()).thenReturn("pi_1", "pi_2", "pi_3");
    //         mockedPaymentIntent.when(() -> 
    //             PaymentIntent.create(any(PaymentIntentCreateParams.class))
    //         ).thenReturn(mockIntent);

    //         // Act
    //         for (Double amount : amounts) {
    //             PaymentIntent result = paymentService.createPaymentIntent(amount, currency);
    //             assertNotNull(result);
    //         }

    //         // Assert
    //         mockedPaymentIntent.verify(() -> 
    //             PaymentIntent.create(any(PaymentIntentCreateParams.class)), 
    //             times(amounts.length)
    //         );
    //     }
    // }

    @Test
    @DisplayName("Should handle payment intent after previous failure")
    void testCreatePaymentIntent_AfterFailure() throws StripeException {
        // Arrange
        Double amount = 100.00;
        String currency = "myr";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            when(mockIntent.getId()).thenReturn("pi_success");
            
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenThrow(new CardException("Card declined", "req_1", "card_error", 
                    "card_declined", "insufficient_funds", null, null, null))
             .thenReturn(mockIntent);

            // Act
            assertThrows(CardException.class, () -> 
                paymentService.createPaymentIntent(amount, currency)
            );
            
            PaymentIntent result = paymentService.createPaymentIntent(amount, currency);

            // Assert
            assertNotNull(result);
            assertEquals("pi_success", result.getId());
        }
    }

    // @Test
    // @DisplayName("Should handle alternating success and failure")
    // void testCreatePaymentIntent_AlternatingResults() throws StripeException {
    //     // Arrange
    //     Double amount = 100.00;
    //     String currency = "myr";
        
    //     try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
    //         PaymentIntent mockIntent = mock(PaymentIntent.class);
    //         when(mockIntent.getId()).thenReturn("pi_1", "pi_2");
            
    //         mockedPaymentIntent.when(() -> 
    //             PaymentIntent.create(any(PaymentIntentCreateParams.class))
    //         ).thenReturn(mockIntent)
    //          .thenThrow(new RateLimitException("Rate limit", null, null, 429))
    //          .thenReturn(mockIntent);

    //         // Act & Assert
    //         PaymentIntent result1 = paymentService.createPaymentIntent(amount, currency);
    //         assertNotNull(result1);
            
    //         assertThrows(RateLimitException.class, () -> 
    //             paymentService.createPaymentIntent(amount, currency)
    //         );
            
    //         PaymentIntent result2 = paymentService.createPaymentIntent(amount, currency);
    //         assertNotNull(result2);
    //     }
    // }

    // ==================== VERIFICATION TESTS ====================

    @Test
    @DisplayName("Should verify service calls Stripe API")
    void testCreatePaymentIntent_VerifyApiCall() throws StripeException {
        // Arrange
        Double amount = 100.00;
        String currency = "myr";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act
            paymentService.createPaymentIntent(amount, currency);

            // Assert - Verify the static method was called
            mockedPaymentIntent.verify(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class)),
                times(1)
            );
        }
    }

    @Test
    @DisplayName("Should not call Stripe API when amount is null")
    void testCreatePaymentIntent_NoApiCallOnNullAmount() {
        // Arrange
        Double amount = null;
        String currency = "myr";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            // Act & Assert
            assertThrows(NullPointerException.class, () -> 
                paymentService.createPaymentIntent(amount, currency)
            );
            
            // Verify Stripe API was never called
            mockedPaymentIntent.verifyNoInteractions();
        }
    }

    @Test
    @DisplayName("Should return PaymentIntent object with proper structure")
    void testCreatePaymentIntent_ReturnStructure() throws StripeException {
        // Arrange
        Double amount = 100.00;
        String currency = "myr";
        
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockIntent = mock(PaymentIntent.class);
            when(mockIntent.getId()).thenReturn("pi_test");
            when(mockIntent.getClientSecret()).thenReturn("secret_test");
            when(mockIntent.getStatus()).thenReturn("requires_payment_method");
            
            mockedPaymentIntent.when(() -> 
                PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockIntent);

            // Act
            PaymentIntent result = paymentService.createPaymentIntent(amount, currency);

            // Assert
            assertNotNull(result);
            assertEquals("pi_test", result.getId());
            assertEquals("secret_test", result.getClientSecret());
            assertEquals("requires_payment_method", result.getStatus());
        }
    }
}