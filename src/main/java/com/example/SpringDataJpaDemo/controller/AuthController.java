package com.example.SpringDataJpaDemo.controller;

import com.example.SpringDataJpaDemo.dto.*;
import com.example.SpringDataJpaDemo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public auth endpoints (no JWT required — see SecurityConfig permitAll).
 *
 * @RestController  — marks this class as a REST API controller (JSON in/out)
 * @RequestMapping  — base URL prefix for all methods in this class
 * @RequiredArgsConstructor — Lombok injects final AuthService via constructor
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    /** Business logic for register/login lives in the service layer. */
    private final AuthService authService;

    /**
     * POST /api/v1/auth/register
     * @RequestBody — maps JSON body to CreateUserDto
     * Returns 201 Created with name + id
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDto> signUp(@RequestBody CreateUserDto createUserDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(createUserDto));
    }

    /**
     * POST /api/v1/auth/login
     * Checks email/password, returns JWT string for later Authorization headers.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(authService.login(loginDto));
    }

}
