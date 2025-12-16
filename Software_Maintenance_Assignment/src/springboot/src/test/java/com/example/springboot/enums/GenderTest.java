package com.example.springboot.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Gender Enum Tests")
class GenderTest {

    @Test
    @DisplayName("Should have all expected values")
    void testAllValues() {
        Gender[] genders = Gender.values();
        assertEquals(2, genders.length);
        
        assertEquals(Gender.MALE, Gender.valueOf("MALE"));
        assertEquals(Gender.FEMALE, Gender.valueOf("FEMALE"));
    }

    @Test
    @DisplayName("Should test valueOf")
    void testValueOf() {
        assertEquals(Gender.MALE, Gender.valueOf("MALE"));
        assertEquals(Gender.FEMALE, Gender.valueOf("FEMALE"));
    }

    @Test
    @DisplayName("Should throw exception for invalid value")
    void testInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            Gender.valueOf("UNKNOWN");
        });
    }

    @Test
    @DisplayName("Should test enum equality")
    void testEnumEquality() {
        Gender gender1 = Gender.MALE;
        Gender gender2 = Gender.MALE;
        
        assertSame(gender1, gender2);
        assertEquals(gender1, gender2);
    }

    @Test
    @DisplayName("Should test enum name")
    void testEnumName() {
        assertEquals("MALE", Gender.MALE.name());
        assertEquals("FEMALE", Gender.FEMALE.name());
    }

    @Test
    @DisplayName("Should test enum ordinal")
    void testEnumOrdinal() {
        assertEquals(0, Gender.MALE.ordinal());
        assertEquals(1, Gender.FEMALE.ordinal());
    }
}