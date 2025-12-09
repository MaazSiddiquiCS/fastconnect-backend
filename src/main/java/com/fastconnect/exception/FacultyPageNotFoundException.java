package com.fastconnect.exception;

public class FacultyPageNotFoundException extends RuntimeException {
    public FacultyPageNotFoundException(Long userId) {
        super("Faculty Page not found for user with id: " + userId);
    }
}