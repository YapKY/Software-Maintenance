package com.example.springboot.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MFAType Enum Tests")
class MFATypeTest {

    @Test
    @DisplayName("Should have all expected values")
    void testAllValues() {
        MFAType[] types = MFAType.values();
        assertEquals(3, types.length);
        
        assertEquals(MFAType.TOTP, MFAType.valueOf("TOTP"));
        assertEquals(MFAType.SMS, MFAType.valueOf("SMS"));
        assertEquals(MFAType.EMAIL, MFAType.valueOf("EMAIL"));
    }

    @Test
    @DisplayName("Should test valueOf")
    void testValueOf() {
        assertEquals(MFAType.TOTP, MFAType.valueOf("TOTP"));
        assertEquals(MFAType.SMS, MFAType.valueOf("SMS"));
        assertEquals(MFAType.EMAIL, MFAType.valueOf("EMAIL"));
    }

    @Test
    @DisplayName("Should throw exception for invalid value")
    void testInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            MFAType.valueOf("INVALID");
        });
    }

    @Test
    @DisplayName("Should test enum equality")
    void testEnumEquality() {
        MFAType type1 = MFAType.TOTP;
        MFAType type2 = MFAType.TOTP;
        
        assertSame(type1, type2);
        assertEquals(type1, type2);
    }

    @Test
    @DisplayName("Should test enum name")
    void testEnumName() {
        assertEquals("TOTP", MFAType.TOTP.name());
        assertEquals("SMS", MFAType.SMS.name());
        assertEquals("EMAIL", MFAType.EMAIL.name());
    }

    @Test
    @DisplayName("Should test enum ordinal")
    void testEnumOrdinal() {
        assertEquals(0, MFAType.TOTP.ordinal());
        assertEquals(1, MFAType.SMS.ordinal());
        assertEquals(2, MFAType.EMAIL.ordinal());
    }
}