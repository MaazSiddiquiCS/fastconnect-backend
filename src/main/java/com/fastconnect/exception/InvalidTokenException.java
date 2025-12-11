package com.fastconnect.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }

    // Optional: Include constructors for wrapping other exceptions
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
