package io.github.chrisvdalen.truckingmanager.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.chrisvdalen.truckingmanager.service.GameRuleException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GameRuleException.class)
    public ResponseEntity<Map<String, String>> handleGameRule(GameRuleException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " " + f.getDefaultMessage())
                .sorted()
                .toList()
                .toString();
        return error(HttpStatus.BAD_REQUEST, "Invalid request: " + message);
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}
