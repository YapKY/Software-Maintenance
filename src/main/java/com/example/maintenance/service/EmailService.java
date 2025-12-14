package com.example.maintenance.service;

import com.example.maintenance.domain.model.User;
import com.example.maintenance.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.UUID;

/**
 * EmailService - Production email sending with JavaMailSender
 * Supports Gmail SMTP, SendGrid, AWS SES, etc.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    
    @Value("${app.email.from}")
    private String fromEmail;
    
    @Value("${app.email.name}")
    private String fromName;
    
    @Value("${app.frontend.url}")
    private String frontendUrl;
    
    /**
     * Send email verification link
     */
    @Async
    public void sendVerificationEmail(User user, String verificationToken) {
        try {
            String verificationUrl = String.format(
                "%s/pages/verify-email.html?token=%s", 
                frontendUrl, 
                verificationToken
            );
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, fromName);
            helper.setTo(user.getEmail());
            helper.setSubject("Verify Your Email - Airline Ticketing");
            
            String htmlContent = buildVerificationEmailHtml(
                user.getName(), 
                verificationUrl
            );
            
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            log.info("Verification email sent successfully to: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", 
                user.getEmail(), e.getMessage());
            // Don't throw exception - email failure shouldn't block registration
        }
    }
    
    /**
     * Send password reset email
     */
    @Async
    public void sendPasswordResetEmail(String email, String resetToken) {
        try {
            String resetUrl = String.format(
                "%s/pages/reset-password.html?token=%s", 
                frontendUrl, 
                resetToken
            );
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, fromName);
            helper.setTo(email);
            helper.setSubject("Password Reset Request - Airline Ticketing");
            
            String htmlContent = buildPasswordResetEmailHtml(resetUrl);
            
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            log.info("Password reset email sent successfully to: {}", email);
            
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", 
                email, e.getMessage());
        }
    }
    
    /**
     * Send welcome email after verification
     */
    @Async
    public void sendWelcomeEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, fromName);
            helper.setTo(user.getEmail());
            helper.setSubject("Welcome to Airline Ticketing!");
            
            String htmlContent = buildWelcomeEmailHtml(user.getName());
            
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            log.info("Welcome email sent to: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage());
        }
    }
    
    /**
     * Build HTML content for verification email
     */
    private String buildVerificationEmailHtml(String userName, String verificationUrl) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                              color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .button { display: inline-block; padding: 15px 30px; background: #667eea; 
                              color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; color: #999; font-size: 12px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✈️ Airline Ticketing</h1>
                        <p>Email Verification</p>
                    </div>
                    <div class="content">
                        <h2>Hello %s!</h2>
                        <p>Thank you for registering with Airline Ticketing. Please verify your email address to complete your registration.</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">Verify Email Address</a>
                        </p>
                        <p>Or copy and paste this link in your browser:</p>
                        <p style="word-break: break-all; color: #667eea;">%s</p>
                        <p><strong>This link will expire in 24 hours.</strong></p>
                        <p>If you didn't create an account, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2024 Airline Ticketing. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, verificationUrl, verificationUrl);
    }
    
    /**
     * Build HTML content for password reset email
     */
    private String buildPasswordResetEmailHtml(String resetUrl) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                              color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .button { display: inline-block; padding: 15px 30px; background: #667eea; 
                              color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; color: #999; font-size: 12px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✈️ Airline Ticketing</h1>
                        <p>Password Reset Request</p>
                    </div>
                    <div class="content">
                        <h2>Reset Your Password</h2>
                        <p>We received a request to reset your password. Click the button below to create a new password:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">Reset Password</a>
                        </p>
                        <p>Or copy and paste this link in your browser:</p>
                        <p style="word-break: break-all; color: #667eea;">%s</p>
                        <p><strong>This link will expire in 1 hour.</strong></p>
                        <p>If you didn't request a password reset, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2024 Airline Ticketing. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, resetUrl, resetUrl);
    }
    
    /**
     * Build HTML content for welcome email
     */
    private String buildWelcomeEmailHtml(String userName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                              color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .button { display: inline-block; padding: 15px 30px; background: #667eea; 
                              color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; color: #999; font-size: 12px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✈️ Welcome to Airline Ticketing!</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s!</h2>
                        <p>Your email has been verified successfully. Welcome to Airline Ticketing!</p>
                        <p>You can now:</p>
                        <ul>
                            <li>Search and book flights</li>
                            <li>Manage your bookings</li>
                            <li>Set up two-factor authentication</li>
                            <li>Update your profile</li>
                        </ul>
                        <p style="text-align: center;">
                            <a href="%s/pages/login.html" class="button">Login to Your Account</a>
                        </p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2024 Airline Ticketing. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, frontendUrl);
    }
}
