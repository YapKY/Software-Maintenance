package com.example.springboot.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Role Enum Tests")
class RoleTest {

    @Test
    @DisplayName("Should have all expected values")
    void testAllValues() {
        Role[] roles = Role.values();
        assertEquals(3, roles.length);
        
        assertEquals(Role.USER, Role.valueOf("USER"));
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
        assertEquals(Role.SUPERADMIN, Role.valueOf("SUPERADMIN"));
    }

    @Test
    @DisplayName("Should test valueOf")
    void testValueOf() {
        assertEquals(Role.USER, Role.valueOf("USER"));
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
        assertEquals(Role.SUPERADMIN, Role.valueOf("SUPERADMIN"));
    }

    @Test
    @DisplayName("Should throw exception for invalid value")
    void testInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            Role.valueOf("INVALID");
        });
    }

    @Test
    @DisplayName("Should test enum equality")
    void testEnumEquality() {
        Role role1 = Role.USER;
        Role role2 = Role.USER;
        
        assertSame(role1, role2);
        assertEquals(role1, role2);
    }

    @Test
    @DisplayName("Should test enum name")
    void testEnumName() {
        assertEquals("USER", Role.USER.name());
        assertEquals("ADMIN", Role.ADMIN.name());
        assertEquals("SUPERADMIN", Role.SUPERADMIN.name());
    }

    @Test
    @DisplayName("Should test enum ordinal")
    void testEnumOrdinal() {
        assertEquals(0, Role.USER.ordinal());
        assertEquals(1, Role.ADMIN.ordinal());
        assertEquals(2, Role.SUPERADMIN.ordinal());
    }

    @Test
    @DisplayName("Should test hierarchy")
    void testHierarchy() {
        assertTrue(Role.USER.ordinal() < Role.ADMIN.ordinal());
        assertTrue(Role.ADMIN.ordinal() < Role.SUPERADMIN.ordinal());
    }
}