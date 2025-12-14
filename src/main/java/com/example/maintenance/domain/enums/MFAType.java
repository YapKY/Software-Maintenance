package com.example.maintenance.domain.enums;

/**
 * MFA types supported
 */
public enum MFAType {
    TOTP,       // Time-based OTP (Google Authenticator)
    SMS,        // SMS-based OTP
    EMAIL       // Email-based OTP
}