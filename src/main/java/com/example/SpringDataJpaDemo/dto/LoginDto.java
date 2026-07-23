package com.example.SpringDataJpaDemo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for POST /api/v1/auth/login.
 *
 * @NoArgsConstructor — needed for Jackson to create the object
 * @AllArgsConstructor — convenience constructor
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    private String email;
    private String password;
}
