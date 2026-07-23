package com.example.SpringDataJpaDemo.exception;

/**
 * Thrown when a user cannot be found by id or email.
 * Handled by GlobalExceptionHandler → HTTP 404 + USER_NOT_FOUND.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String errorMsg) {
        super(errorMsg); // RuntimeException stores the message
    }
}
