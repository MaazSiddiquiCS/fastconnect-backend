package com.fastconnect.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConnectionRequestNotFoundException extends RuntimeException {
    public ConnectionRequestNotFoundException(Long requestId) {

        super("ConnectionRequest with requestId " + requestId + " not found");
    }
}
