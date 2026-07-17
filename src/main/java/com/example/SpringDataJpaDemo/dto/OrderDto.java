package com.example.SpringDataJpaDemo.dto;


import com.example.SpringDataJpaDemo.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderDto {

    private Long id;
    private String productName;
    private UserDto user ;
}
