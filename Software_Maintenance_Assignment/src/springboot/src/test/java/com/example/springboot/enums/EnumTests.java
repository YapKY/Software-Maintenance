package com.example.springboot.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnumTests {

    @Test
    void testRoleEnum() {
        assertEquals(3, Role.values().length);
        assertNotNull(Role.valueOf("USER"));
        assertNotNull(Role.valueOf("ADMIN"));
        assertNotNull(Role.valueOf("SUPERADMIN"));
    }

    @Test
    void testAuthProviderEnum() {
        assertEquals(3, AuthProvider.values().length);
        assertNotNull(AuthProvider.valueOf("EMAIL"));
        assertNotNull(AuthProvider.valueOf("GOOGLE"));
        assertNotNull(AuthProvider.valueOf("FACEBOOK"));
    }

    @Test
    void testMFATypeEnum() {
        assertEquals(3, MFAType.values().length);
        assertNotNull(MFAType.valueOf("TOTP"));
        assertNotNull(MFAType.valueOf("SMS"));
        assertNotNull(MFAType.valueOf("EMAIL"));
    }

    @Test
    void testGenderEnum() {
        assertEquals(2, Gender.values().length);
        assertNotNull(Gender.valueOf("MALE"));
        assertNotNull(Gender.valueOf("FEMALE"));
    }
}