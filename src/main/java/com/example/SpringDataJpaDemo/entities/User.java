package com.example.SpringDataJpaDemo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA entity mapped to table "users".
 *
 * @Entity — this class is a persistent DB table row
 * @Table(name = "users") — table name (user is often reserved in SQL)
 * @Getter / @Setter — Lombok generates accessors
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    /** Primary key, auto-increment (IDENTITY = DB serial/identity column). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /** unique + not null → DB constraint; used as login identity / JWT subject. */
    @Column(unique = true, nullable = false)
    private String email;

    /** Stored as BCrypt hash, never plain text after register. */
    @Column(nullable = false)
    private String password;

    /**
     * @Enumerated(EnumType.STRING) — store "USER" / "ADMIN" as text, not 0/1.
     * Used by CustomUserDetailsService → Spring roles.
     */
    @Enumerated(EnumType.STRING)
    private Role role;
}
