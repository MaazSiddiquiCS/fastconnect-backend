package com.fastconnect.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserEmailNotFoundException extends RuntimeException {
    public UserEmailNotFoundException(String email) {

        super("User with " + email + " not found");
    }
}
