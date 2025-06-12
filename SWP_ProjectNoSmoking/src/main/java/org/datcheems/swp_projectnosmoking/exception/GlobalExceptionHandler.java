package org.datcheems.swp_projectnosmoking.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle 404 Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Handle 400 Bad Request (invalid JSON format)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = "Invalid request body. Please check your JSON format.";

        // Optional: Nếu muốn chi tiết hơn:
        if (ex.getCause() instanceof InvalidFormatException) {
            message = "Invalid value in request body: " + ex.getCause().getMessage();
        }

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    // Handle 500 Internal Server Error (catch all other exceptions)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleGenericException(Exception ex) {
        ex.printStackTrace(); // Optional: log lỗi ra console để dễ debug khi dev
        String message = "Internal server error: " + ex.getMessage();
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
