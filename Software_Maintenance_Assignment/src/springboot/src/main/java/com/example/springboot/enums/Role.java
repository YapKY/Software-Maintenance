package com.example.springboot.enums;

/**
 * User roles with clear hierarchy
 */
public enum Role {
    USER,       // Self-registered users
    ADMIN,      // Created by Superadmin only
    SUPERADMIN  // Highest privilege
}
