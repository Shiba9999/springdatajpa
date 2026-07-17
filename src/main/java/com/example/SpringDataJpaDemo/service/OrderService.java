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
import java.util.function.Consumer;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;


    public OrderDto createOrder(Long userId, CreateOrderDto createOrderDto) {

        User user = userRepository.findById(userId).orElseThrow();
        Order order = new Order();
        order.setUser(user);
        order.setProductName(createOrderDto.getProductName());
        Order savedOrder = orderRepository.save(order);

        return new OrderDto(savedOrder.getId(), savedOrder.getProductName(),

                new UserDto(savedOrder.getUser().getId(), savedOrder.getUser().getName(),savedOrder.getUser().getEmail())
                );


    }


    public List<OrderDto> getOrderByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderDto> orderDtos = new ArrayList<>();

        orders.forEach(order ->{
            User orderUser = order.getUser();
            OrderDto orderDto = new OrderDto(
                    order.getId(),
                    order.getProductName(),
                    new UserDto(orderUser.getId(), orderUser.getName(), orderUser.getEmail())
            );
            orderDtos.add(orderDto);
        });
        return orderDtos ;

    }
}
