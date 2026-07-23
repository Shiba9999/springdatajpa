package com.example.SpringDataJpaDemo.controller;

import com.example.SpringDataJpaDemo.dto.CreateOrderDto;
import com.example.SpringDataJpaDemo.dto.OrderDto;
import com.example.SpringDataJpaDemo.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Orders keyed by userId in the URL (older style).
 * Example: GET /api/v1/users/5/orders
 *
 * For the logged-in user without putting id in the URL, use MeController:
 *   GET/POST /api/v1/me/orders  (identity from JWT)
 *
 * @RestController — JSON API
 * @RequestMapping — {userId} is a path variable shared by all methods
 * @AllArgsConstructor — injects OrderService
 */
@RestController
@RequestMapping("/api/v1/users/{userId}/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * POST /api/v1/users/{userId}/orders
     * Creates an order for the given userId.
     */
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @PathVariable Long userId,
            @RequestBody CreateOrderDto createOrderDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(userId, createOrderDto));
    }

    /**
     * GET /api/v1/users/{userId}/orders
     * Lists all orders for that userId.
     */
    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrderByUserId(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderByUserId(userId));
    }

}
