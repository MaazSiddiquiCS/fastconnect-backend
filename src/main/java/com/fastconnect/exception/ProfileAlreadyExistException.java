package com.fastconnect.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProfileAlreadyExistException extends RuntimeException {

    public ProfileAlreadyExistException(Long userId) {
        super("A profile already exists for User ID: " + userId);
    }
}