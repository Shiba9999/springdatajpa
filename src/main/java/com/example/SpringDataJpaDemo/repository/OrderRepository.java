package com.example.SpringDataJpaDemo.repository;

import com.example.SpringDataJpaDemo.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for Order.
 * findByUserId — derived query: WHERE user_id = ?
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);
}
