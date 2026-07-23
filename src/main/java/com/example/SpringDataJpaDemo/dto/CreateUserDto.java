package com.example.SpringDataJpaDemo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for register (and also used as update/patch payload in UserController).
 * DTO = Data Transfer Object — API shape, not the DB entity.
 *
 * @Getter / @Setter — Lombok accessors for Jackson JSON binding
 * Validation annotations work when the controller uses @Valid
 */
@Getter
@Setter
public class CreateUserDto {

    @NotNull
    @NotBlank
    @Size(max = 100)
    private String name;

    @Email
    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    private String password;
}
