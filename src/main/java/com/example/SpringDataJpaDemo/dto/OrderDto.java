package com.example.SpringDataJpaDemo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Order response DTO including nested user summary.
 */
@Getter
@Setter
@AllArgsConstructor
public class OrderDto {

    private Long id;
    private String productName;
    private UserDto user;
}
