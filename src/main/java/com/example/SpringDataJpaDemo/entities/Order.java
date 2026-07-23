package com.example.SpringDataJpaDemo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA entity mapped to table "orders".
 * Many orders belong to one user (ManyToOne).
 *
 * @Entity / @Table — persistence mapping
 * @Getter / @Setter — Lombok accessors
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    /**
     * @ManyToOne — many orders → one user
     * FetchType.LAZY — load user only when accessed (performance)
     * @JoinColumn(name = "user_id") — FK column on orders table
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
