package com.example.SpringDataJpaDemo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Response after register — confirms creation without returning the password.
 */
@Getter
@Setter
@AllArgsConstructor
public class RegisterUserResponseDto {

    private String name;
    private Long id;
}
