package com.example.SpringDataJpaDemo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Response body after successful login — contains the JWT string.
 * Client must send it as: Authorization: Bearer <jwt>
 */
@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {

    private String jwt;
}
