package com.example.springboot.security.mfa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * TOTPGenerator - Generates and validates Time-based One-Time Passwords
 */
@Slf4j
@Component
public class TOTPGenerator {
    
    // Google Authenticator uses 20 bytes (160 bits) -> 32 characters in Base32
    private static final int SECRET_SIZE = 20; 
    private static final String HMAC_ALGORITHM = "HmacSHA1";
    private static final int CODE_DIGITS = 6;
    private static final long TIME_STEP = 30; // seconds
    private static final int WINDOW = 1; // Allow 1 time step before/after
    
    public String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[SECRET_SIZE];
        random.nextBytes(bytes);
        return Base32.encode(bytes);
    }
    
    /**
     * Generate QR code URL using api.qrserver.com
     */
    public String generateQRCodeUrl(String secret, String accountName) {
        String issuer = "AirlineTicketing";
        
        try {
            // URL Encode the label components
            String label = URLEncoder.encode(issuer + ":" + accountName, StandardCharsets.UTF_8.toString());
            String issuerParam = URLEncoder.encode(issuer, StandardCharsets.UTF_8.toString());
            
            // Format: otpauth://totp/Label?secret=SECRET&issuer=ISSUER
            String otpauthUrl = String.format(
                "otpauth://totp/%s?secret=%s&issuer=%s",
                label,
                secret,
                issuerParam
            );
            
            // URL Encode the ENTIRE otpauth URL for the API parameter
            String encodedOtpUrl = URLEncoder.encode(otpauthUrl, StandardCharsets.UTF_8.toString());
            
            // [FIX] Switched to api.qrserver.com as Google Charts is deprecated
            return String.format(
                "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=%s",
                encodedOtpUrl
            );
        } catch (UnsupportedEncodingException e) {
            log.error("Error generating QR code URL: {}", e.getMessage());
            return "";
        }
    }
    
    public boolean validateCode(String secret, String code) {
        try {
            long currentTime = System.currentTimeMillis() / 1000L;
            long currentTimeStep = currentTime / TIME_STEP;
            
            for (int i = -WINDOW; i <= WINDOW; i++) {
                String generatedCode = generateCode(secret, currentTimeStep + i);
                if (generatedCode.equals(code)) {
                    log.info("TOTP code validated successfully");
                    return true;
                }
            }
            
            log.warn("TOTP code validation failed");
            return false;
            
        } catch (Exception e) {
            log.error("Error validating TOTP code: {}", e.getMessage());
            return false;
        }
    }
    
    private String generateCode(String secret, long timeStep) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] decodedSecret = Base32.decode(secret);
        
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(timeStep);
        byte[] timeBytes = buffer.array();
        
        Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(decodedSecret, HMAC_ALGORITHM);
        hmac.init(keySpec);
        byte[] hash = hmac.doFinal(timeBytes);
        
        int offset = hash[hash.length - 1] & 0x0F;
        int binary = ((hash[offset] & 0x7F) << 24) |
                     ((hash[offset + 1] & 0xFF) << 16) |
                     ((hash[offset + 2] & 0xFF) << 8) |
                     (hash[offset + 3] & 0xFF);
        
        int otp = binary % (int) Math.pow(10, CODE_DIGITS);
        
        return String.format("%0" + CODE_DIGITS + "d", otp);
    }
    
    /**
     * Internal Base32 implementation
     */
    private static class Base32 {
        private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        private static final int[] DECODE_TABLE;

        static {
            DECODE_TABLE = new int[128];
            java.util.Arrays.fill(DECODE_TABLE, -1);
            for (int i = 0; i < ALPHABET.length(); i++) {
                DECODE_TABLE[ALPHABET.charAt(i)] = i;
            }
        }

        public static String encode(byte[] data) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.length; i += 5) {
                long val = 0;
                int count = 0;
                for (int j = 0; j < 5 && i + j < data.length; j++) {
                    val = (val << 8) | (data[i + j] & 0xFF);
                    count++;
                }
                int bits = count * 8;
                while (bits > 0) {
                    int chunk = (int) ((val >> (bits - 5)) & 0x1F);
                    if (bits < 5) chunk = (int) (val << (5 - bits)) & 0x1F;
                    sb.append(ALPHABET.charAt(chunk));
                    bits -= 5;
                }
            }
            return sb.toString();
        }

        public static byte[] decode(String data) {
            data = data.trim().replaceAll("=", "").toUpperCase();
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            int val = 0;
            int bits = 0;
            for (char c : data.toCharArray()) {
                if (c >= DECODE_TABLE.length || DECODE_TABLE[c] < 0) continue;
                val = (val << 5) | DECODE_TABLE[c];
                bits += 5;
                if (bits >= 8) {
                    bos.write((val >> (bits - 8)) & 0xFF);
                    bits -= 8;
                }
            }
            return bos.toByteArray();
        }
    }
}