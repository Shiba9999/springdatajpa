package com.example.SpringDataJpaDemo.entities;

/**
 * Application roles stored on User and mapped to Spring Security authorities.
 * USER  → ROLE_USER
 * ADMIN → ROLE_ADMIN
 * (Spring's hasRole("USER") adds the ROLE_ prefix automatically.)
 */
public enum Role {
    USER,
    ADMIN
}
