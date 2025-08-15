package com.versionxd.lms.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class handles exceptions globally across the whole application.
 * It provides consistent, structured error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation exceptions from @Valid.
     * @param ex The exception thrown when validation fails.
     * @return A map containing structured error details.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());

        // Collect all validation error messages into a map of field -> message
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                                       .collect(Collectors.toMap(
                                               fieldError -> fieldError.getField(),
                                               fieldError -> fieldError.getDefaultMessage()
                                       ));
        response.put("errors", errors);

        return response;
    }

    /**
     * Handles the specific exception for when a user already exists.
     * @param ex The custom exception.
     * @return A ResponseEntity with a structured error message.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "Conflict");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
