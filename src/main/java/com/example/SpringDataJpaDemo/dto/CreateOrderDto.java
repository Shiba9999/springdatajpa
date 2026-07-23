package com.example.SpringDataJpaDemo.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Request body for creating an order — only product name needed.
 * Owner comes from path userId OR from JWT (/api/v1/me/orders).
 */
@Getter
@Setter
public class CreateOrderDto {

    private String productName;
}
