package com.example.springboot.service;

import com.example.springboot.model.User;
import com.example.springboot.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@example.com");
        ReflectionTestUtils.setField(emailService, "fromName", "Airline Support");
        ReflectionTestUtils.setField(emailService, "frontendUrl", "http://localhost:8080");
        
        // Lenient stubbing for createMimeMessage because it's called in all methods
        lenient().when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSendVerificationEmail_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        String token = "verify-token-123";

        emailService.sendVerificationEmail(user, token);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendVerificationEmail_Exception() {
        User user = new User();
        user.setEmail("test@example.com");
        
        doThrow(new RuntimeException("Mail server down")).when(mailSender).send(any(MimeMessage.class));

        // Method catches exception, so this should not throw
        emailService.sendVerificationEmail(user, "token");

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendPasswordResetEmail_Success() {
        String email = "test@example.com";
        String token = "reset-token-123";

        emailService.sendPasswordResetEmail(email, token);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendPasswordResetEmail_Exception() {
        String email = "test@example.com";
        
        doThrow(new RuntimeException("Mail error")).when(mailSender).send(any(MimeMessage.class));

        emailService.sendPasswordResetEmail(email, "token");

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendWelcomeEmail_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");

        emailService.sendWelcomeEmail(user);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendWelcomeEmail_Exception() {
        User user = new User();
        user.setEmail("test@example.com");
        
        doThrow(new RuntimeException("Mail error")).when(mailSender).send(any(MimeMessage.class));

        emailService.sendWelcomeEmail(user);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}