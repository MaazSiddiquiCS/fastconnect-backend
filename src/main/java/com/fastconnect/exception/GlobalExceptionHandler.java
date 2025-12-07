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

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException ex,
            HttpServletRequest request) {

        String detailedMessage = ex.getMostSpecificCause().getMessage();
        String userFriendlyMessage = "A data integrity constraint was violated. This often means a required field is missing, or a unique value (like an email or roll number) is a duplicate.";

        // Optionally, you can try to parse the message to determine the specific constraint
        if (detailedMessage != null) {
            if (detailedMessage.toLowerCase().contains("unique")) {
                userFriendlyMessage = "A value you entered (e.g., email, roll number) already exists in the system.";
            } else if (detailedMessage.toLowerCase().contains("null")) {
                userFriendlyMessage = "A required field was left empty.";
            }
        }

        ApiError error = new ApiError(
                HttpStatus.CONFLICT, // Use 409 CONFLICT for business rule violations
                userFriendlyMessage,
                LocalDateTime.now(),
                request.getRequestURI(),
                "Data Constraint Violation"
        );
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(
            org.springframework.web.bind.MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        // Extract all field errors into a single, clean message
        String validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(java.util.stream.Collectors.joining("; "));

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST, // Use 400 Bad Request for client-side validation errors
                validationErrors,
                java.time.LocalDateTime.now(),
                request.getRequestURI(),
                "Input validation failed"
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

    @ExceptionHandler(UserEmailNotFoundException.class)
    public ResponseEntity<ApiError> handleUserEmailNotFound(UserNotFoundException ex, HttpServletRequest request) {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND,
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI(),
                "Bad Credentials: User with this email does not exist"
        );
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(ProfileAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleProfileAlreadyExist(ProfileAlreadyExistsException ex, HttpServletRequest request) {
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

    @ExceptionHandler
    public ResponseEntity<ApiError> handleEmailAlreadyExists(EmailAlreadyExistsException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI(),
                "Email already exist"
        );
        return new ResponseEntity<>(error, error.getStatus());
    }
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ApiError> handlePostNotFound(PostNotFoundException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI(),
                "The requested post does not exist"
        );
        return new ResponseEntity<>(error, error.getStatus());
    }
}
