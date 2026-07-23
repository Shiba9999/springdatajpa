package com.example.SpringDataJpaDemo.exception;

/**
 * Thrown by AuthService when login email/password is wrong.
 * Intentionally NOT a Spring AuthenticationException, so ExceptionTranslationFilter
 * does not turn it into a generic "Authentication required" response.
 * Handled by GlobalExceptionHandler → HTTP 401 + INVALID_CREDENTIALS.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
