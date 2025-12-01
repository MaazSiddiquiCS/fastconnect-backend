package com.fastconnect.exception;

import com.fastconnect.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI(),
                "An unexpected error occurred"
        );
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND,
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI(),
                "User does not exist"
                );
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(ProfileAlreadyExistException.class)
    public ResponseEntity<ApiError> handleProfileAlreadyExist(ProfileAlreadyExistException ex, HttpServletRequest request) {
        ApiError error = new ApiError(HttpStatus.CONFLICT,
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI(),
                "Profile already exist"
        );
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ApiError> handleProfileNotFound(ProfileNotFoundException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI(),
                "Profile does not exist"
        );
        return new ResponseEntity<>(error, error.getStatus());
    }
}
