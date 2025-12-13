package com.example.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired private JavaMailSender mailSender;

    public void sendBookingSuccessEmail(String to, String ref) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@airline.com");
            message.setTo(to);
            message.setSubject("Your Flight Confirmation: " + ref);
            message.setText("Thank you for booking with us. Your booking reference is " + ref + ".");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Email failed: " + e.getMessage());
        }
    }
}