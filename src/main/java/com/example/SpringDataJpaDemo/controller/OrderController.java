package com.example.SpringDataJpaDemo.controller;

import com.example.SpringDataJpaDemo.dto.CreateOrderDto;
import com.example.SpringDataJpaDemo.dto.CreateUserDto;
import com.example.SpringDataJpaDemo.dto.OrderDto;
import com.example.SpringDataJpaDemo.dto.UserDto;
import com.example.SpringDataJpaDemo.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users/{userId}/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService ;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@PathVariable Long userId , @RequestBody CreateOrderDto createOrderDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(userId,createOrderDto));
    }

    //Get orders of a perticular User

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrderByUserId(@PathVariable Long userId ) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderByUserId(userId));
    }


}
