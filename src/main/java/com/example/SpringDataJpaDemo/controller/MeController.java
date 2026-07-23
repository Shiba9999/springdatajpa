package com.example.SpringDataJpaDemo.controller;

import com.example.SpringDataJpaDemo.dto.CreateOrderDto;
import com.example.SpringDataJpaDemo.dto.OrderDto;
import com.example.SpringDataJpaDemo.dto.UserDto;
import com.example.SpringDataJpaDemo.service.OrderService;
import com.example.SpringDataJpaDemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controllers for the *currently logged-in* user.
 * User id is NOT in the URL — identity comes from the JWT (Authorization: Bearer ...).
 *
 * JwtAuthFilter already validated the token and put UserDetails into SecurityContext.
 * @AuthenticationPrincipal injects that UserDetails into the method.
 *
 * Example:
 *   GET  /api/v1/me
 *   GET  /api/v1/me/orders
 *   POST /api/v1/me/orders
 * Header: Authorization: Bearer <jwt>
 */
@RestController // REST JSON controller (combines @Controller + @ResponseBody)
@RequestMapping("/api/v1/me") // base path for "my" resources
@RequiredArgsConstructor // Lombok: generates constructor for final fields (DI)
public class MeController {

    private final UserService userService;
    private final OrderService orderService;

    /**
     * Returns the profile of the user who owns the JWT.
     * email = userDetails.getUsername() because we store email as the Security username.
     */
    @GetMapping
    public ResponseEntity<UserDto> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserByEmail(userDetails.getUsername()));
    }

    /**
     * Lists orders that belong to the logged-in user only.
     */
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.getOrdersByEmail(userDetails.getUsername()));
    }

    /**
     * Creates a new order for the logged-in user (no userId in path/body).
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderDto> createMyOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateOrderDto createOrderDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrderByEmail(userDetails.getUsername(), createOrderDto));
    }
}
