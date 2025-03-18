package com.example.busticket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Catch NullPointerException and return a proper response
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        // Log the exception at a lower level if needed
        System.err.println("NullPointerException caught: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid login request or missing data");
    }
    
    // Optionally, catch any other exceptions to avoid exposing details
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        System.err.println("Exception caught: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred. Please try again later.");
    }
}
