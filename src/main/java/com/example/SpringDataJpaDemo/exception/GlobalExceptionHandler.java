package com.example.SpringDataJpaDemo.exception;

import com.example.SpringDataJpaDemo.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Central place to turn exceptions into HTTP JSON responses.
 *
 * @RestControllerAdvice — applies to all @RestController classes
 * @ExceptionHandler(X.class) — runs when that exception type is thrown
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 404 when a user id/email is missing. */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(
                "USER_NOT_FOUND", ex.getMessage()
        ));
    }

    /** 400 when @Valid fails on a request DTO. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
                "INVALID_INPUT", errorMessage
        ));
    }

    /** 401 for wrong email/password on login. */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(
                "INVALID_CREDENTIALS", ex.getMessage()
        ));
    }
}
