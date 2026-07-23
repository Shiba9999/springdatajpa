package com.example.SpringDataJpaDemo.service;

import com.example.SpringDataJpaDemo.dto.CreateOrderDto;
import com.example.SpringDataJpaDemo.dto.OrderDto;
import com.example.SpringDataJpaDemo.dto.UserDto;
import com.example.SpringDataJpaDemo.entities.Order;
import com.example.SpringDataJpaDemo.entities.User;
import com.example.SpringDataJpaDemo.repository.OrderRepository;
import com.example.SpringDataJpaDemo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Business logic for creating and listing orders.
 *
 * Two styles:
 * 1) By userId in the path (OrderController)
 * 2) By email from JWT (MeController → createOrderByEmail / getOrdersByEmail)
 *
 * @Service — Spring service bean
 * @AllArgsConstructor — injects repositories
 */
@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    /** Create an order linked to the given user id. */
    public OrderDto createOrder(Long userId, CreateOrderDto createOrderDto) {
        User user = userRepository.findById(userId).orElseThrow();
        Order order = new Order();
        order.setUser(user);
        order.setProductName(createOrderDto.getProductName());
        Order savedOrder = orderRepository.save(order);
        return toDto(savedOrder);
    }

    /** List orders for a user id. */
    public List<OrderDto> getOrderByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderDto> orderDtos = new ArrayList<>();
        orders.forEach(order -> orderDtos.add(toDto(order)));
        return orderDtos;
    }

    /**
     * Create order for whoever owns the JWT (email = Security username).
     * Called from MeController — no userId in the URL.
     */
    public OrderDto createOrderByEmail(String email, CreateOrderDto createOrderDto) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return createOrder(user.getId(), createOrderDto);
    }

    /**
     * List orders for whoever owns the JWT.
     */
    public List<OrderDto> getOrdersByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return getOrderByUserId(user.getId());
    }

    /** Maps entity → DTO so password/internal fields never leave the API. */
    private OrderDto toDto(Order order) {
        User orderUser = order.getUser();
        return new OrderDto(
                order.getId(),
                order.getProductName(),
                new UserDto(orderUser.getId(), orderUser.getName(), orderUser.getEmail())
        );
    }
}
