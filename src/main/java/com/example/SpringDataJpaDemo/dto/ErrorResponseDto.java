package com.example.SpringDataJpaDemo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Standard error JSON shape returned by GlobalExceptionHandler / security handlers.
 * Example: { "code": "UNAUTHORIZED", "message": "Authentication required" }
 */
@Getter
@AllArgsConstructor
public class ErrorResponseDto {

    private String code;
    private String message;
}
