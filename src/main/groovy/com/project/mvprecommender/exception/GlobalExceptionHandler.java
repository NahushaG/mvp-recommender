package com.project.mvprecommender.exception;

import com.project.mvprecommender.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle @Valid validation errors
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<?> handleValidationExceptions(WebExchangeBindException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_FAILED", errors) {
        });
    }

    // Handle business-rule validation (IllegalArgumentException)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgs(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_REQUEST", ex.getMessage()));
    }

    // Handle business-rule validation (ServerWebInputException)
    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<?> handleIllegalArgs(ServerWebInputException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_REQUEST", ex.getMessage()));
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllOtherExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Unexpected server error"));
    }


}
