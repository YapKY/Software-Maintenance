package com.example.springboot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for NotificationService
 * 
 * Tests Module: Email Notification Module
 * Coverage: Email sending, error handling, message construction
 * Target: 90%+ coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Notification Service Tests")
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    private String testEmail;
    private String testBookingReference;

    @BeforeEach
    void setUp() {
        testEmail = "customer@example.com";
        testBookingReference = "ABC12345";
    }

    // ==================== SUCCESS CASES ====================

    @Test
    @DisplayName("Should send booking success email successfully")
    void testSendBookingSuccessEmail_Success() {
        // Arrange
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        notificationService.sendBookingSuccessEmail(testEmail, testBookingReference);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
        assertEquals("noreply@airline.com", sentMessage.getFrom());
        assertEquals(testEmail, sentMessage.getTo()[0]);
        assertEquals("Your Flight Confirmation: " + testBookingReference, sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains(testBookingReference));
        assertTrue(sentMessage.getText().contains("Thank you for booking with us"));
    }

    @Test
    @DisplayName("Should send email with correct message format")
    void testSendBookingSuccessEmail_MessageFormat() {
        // Arrange
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        String bookingRef = "XYZ98765";
        String email = "test@example.com";

        // Act
        notificationService.sendBookingSuccessEmail(email, bookingRef);

        // Assert
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        
        String expectedText = "Thank you for booking with us. Your booking reference is " + bookingRef + ".";
        assertEquals(expectedText, message.getText());
    }

    @Test
    @DisplayName("Should send email with different booking references")
    void testSendBookingSuccessEmail_DifferentReferences() {
        // Arrange
        String[] bookingRefs = {"ABC12345", "XYZ98765", "DEF54321"};
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        for (String ref : bookingRefs) {
            notificationService.sendBookingSuccessEmail(testEmail, ref);
        }

        // Assert
        verify(mailSender, times(3)).send(messageCaptor.capture());
        
        java.util.List<SimpleMailMessage> messages = messageCaptor.getAllValues();
        for (int i = 0; i < bookingRefs.length; i++) {
            assertTrue(messages.get(i).getSubject().contains(bookingRefs[i]));
            assertTrue(messages.get(i).getText().contains(bookingRefs[i]));
        }
    }

    @Test
    @DisplayName("Should send email to different recipients")
    void testSendBookingSuccessEmail_DifferentRecipients() {
        // Arrange
        String[] emails = {"user1@example.com", "user2@example.com", "user3@example.com"};
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        for (String email : emails) {
            notificationService.sendBookingSuccessEmail(email, testBookingReference);
        }

        // Assert
        verify(mailSender, times(3)).send(messageCaptor.capture());
        
        java.util.List<SimpleMailMessage> messages = messageCaptor.getAllValues();
        for (int i = 0; i < emails.length; i++) {
            assertEquals(emails[i], messages.get(i).getTo()[0]);
        }
    }

    // ==================== ERROR HANDLING CASES ====================

    @Test
    @DisplayName("Should handle MailSendException gracefully")
    void testSendBookingSuccessEmail_MailSendException() {
        // Arrange
        doThrow(new MailSendException("SMTP server not available"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act - Should not throw exception
        assertDoesNotThrow(() -> 
            notificationService.sendBookingSuccessEmail(testEmail, testBookingReference)
        );

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should handle general exception during email sending")
    void testSendBookingSuccessEmail_GeneralException() {
        // Arrange
        doThrow(new RuntimeException("Network error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act - Should not throw exception
        assertDoesNotThrow(() -> 
            notificationService.sendBookingSuccessEmail(testEmail, testBookingReference)
        );

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should handle NullPointerException during email sending")
    void testSendBookingSuccessEmail_NullPointerException() {
        // Arrange
        doThrow(new NullPointerException("Null configuration"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act - Should not throw exception
        assertDoesNotThrow(() -> 
            notificationService.sendBookingSuccessEmail(testEmail, testBookingReference)
        );

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // ==================== NULL/EMPTY PARAMETER CASES ====================

    // @Test
    // @DisplayName("Should handle null email address")
    // void testSendBookingSuccessEmail_NullEmail() {
    //     // Arrange
    //     ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

    //     // Act
    //     notificationService.sendBookingSuccessEmail(null, testBookingReference);

    //     // Assert
    //     verify(mailSender, times(1)).send(messageCaptor.capture());
    //     SimpleMailMessage message = messageCaptor.getValue();
    //     assertNull(message.getTo());
    // }

    @Test
    @DisplayName("Should handle empty email address")
    void testSendBookingSuccessEmail_EmptyEmail() {
        // Arrange
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        notificationService.sendBookingSuccessEmail("", testBookingReference);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals("", message.getTo()[0]);
    }

    @Test
    @DisplayName("Should handle null booking reference")
    void testSendBookingSuccessEmail_NullReference() {
        // Arrange
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        notificationService.sendBookingSuccessEmail(testEmail, null);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertTrue(message.getSubject().contains("null"));
        assertTrue(message.getText().contains("null"));
    }

    @Test
    @DisplayName("Should handle empty booking reference")
    void testSendBookingSuccessEmail_EmptyReference() {
        // Arrange
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        notificationService.sendBookingSuccessEmail(testEmail, "");

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertTrue(message.getSubject().contains(": "));
        assertTrue(message.getText().contains("is ."));
    }

    // @Test
    // @DisplayName("Should handle both null parameters")
    // void testSendBookingSuccessEmail_BothNull() {
    //     // Arrange
    //     ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

    //     // Act
    //     notificationService.sendBookingSuccessEmail(null, null);

    //     // Assert
    //     verify(mailSender, times(1)).send(messageCaptor.capture());
    //     SimpleMailMessage message = messageCaptor.getValue();
    //     assertNull(message.getTo());
    //     assertTrue(message.getText().contains("null"));
    // }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("Should handle very long booking reference")
    void testSendBookingSuccessEmail_LongReference() {
        // Arrange
        String longReference = "ABC123456789012345678901234567890";
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        notificationService.sendBookingSuccessEmail(testEmail, longReference);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertTrue(message.getText().contains(longReference));
        assertTrue(message.getSubject().contains(longReference));
    }

    @Test
    @DisplayName("Should handle email with special characters")
    void testSendBookingSuccessEmail_SpecialCharactersInEmail() {
        // Arrange
        String specialEmail = "test+user@example.co.uk";
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        notificationService.sendBookingSuccessEmail(specialEmail, testBookingReference);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(specialEmail, message.getTo()[0]);
    }

    @Test
    @DisplayName("Should handle booking reference with special characters")
    void testSendBookingSuccessEmail_SpecialCharactersInReference() {
        // Arrange
        String specialReference = "ABC-123@456";
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        notificationService.sendBookingSuccessEmail(testEmail, specialReference);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertTrue(message.getText().contains(specialReference));
    }

    @Test
    @DisplayName("Should handle whitespace in parameters")
    void testSendBookingSuccessEmail_WithWhitespace() {
        // Arrange
        String emailWithSpaces = "  test@example.com  ";
        String referenceWithSpaces = "  ABC123  ";
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        notificationService.sendBookingSuccessEmail(emailWithSpaces, referenceWithSpaces);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(emailWithSpaces, message.getTo()[0]);
        assertTrue(message.getText().contains(referenceWithSpaces));
    }

    // ==================== MULTIPLE INVOCATIONS ====================

    @Test
    @DisplayName("Should handle multiple sequential email sends")
    void testSendBookingSuccessEmail_MultipleSequential() {
        // Arrange
        int numberOfEmails = 5;

        // Act
        for (int i = 0; i < numberOfEmails; i++) {
            notificationService.sendBookingSuccessEmail(
                "user" + i + "@example.com", 
                "REF" + i
            );
        }

        // Assert
        verify(mailSender, times(numberOfEmails)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should handle email sending after previous failure")
    void testSendBookingSuccessEmail_AfterFailure() {
        // Arrange
        doThrow(new MailSendException("SMTP error"))
                .doNothing()
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        notificationService.sendBookingSuccessEmail(testEmail, "REF1"); // Fails
        notificationService.sendBookingSuccessEmail(testEmail, "REF2"); // Succeeds

        // Assert
        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }

    // ==================== MESSAGE CONTENT VALIDATION ====================

    @Test
    @DisplayName("Should create message with all required fields")
    void testSendBookingSuccessEmail_MessageHasAllFields() {
        // Arrange
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        notificationService.sendBookingSuccessEmail(testEmail, testBookingReference);

        // Assert
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        
        assertNotNull(message.getFrom(), "From address should not be null");
        assertNotNull(message.getTo(), "To address should not be null");
        assertNotNull(message.getSubject(), "Subject should not be null");
        assertNotNull(message.getText(), "Message text should not be null");
    }

    @Test
    @DisplayName("Should use correct sender address")
    void testSendBookingSuccessEmail_CorrectSender() {
        // Arrange
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        notificationService.sendBookingSuccessEmail(testEmail, testBookingReference);

        // Assert
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals("noreply@airline.com", message.getFrom());
    }

    @Test
    @DisplayName("Should include booking reference in subject")
    void testSendBookingSuccessEmail_SubjectContainsReference() {
        // Arrange
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        notificationService.sendBookingSuccessEmail(testEmail, testBookingReference);

        // Assert
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertTrue(message.getSubject().startsWith("Your Flight Confirmation: "));
        assertTrue(message.getSubject().endsWith(testBookingReference));
    }

    @Test
    @DisplayName("Should include thank you message in body")
    void testSendBookingSuccessEmail_BodyHasThankYou() {
        // Arrange
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        notificationService.sendBookingSuccessEmail(testEmail, testBookingReference);

        // Assert
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertTrue(message.getText().startsWith("Thank you for booking with us"));
    }

    @Test
    @DisplayName("Should not call mailSender when exception occurs in try block")
    void testSendBookingSuccessEmail_ExceptionInTryBlock() {
        // Arrange
        doThrow(new RuntimeException("Simulated error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        assertDoesNotThrow(() -> 
            notificationService.sendBookingSuccessEmail(testEmail, testBookingReference)
        );

        // Assert - Verify it was called once (the exception was caught)
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}