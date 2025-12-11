package com.fastconnect.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConnectionRequestAlreadySent extends RuntimeException {
    public ConnectionRequestAlreadySent(String msg) {
        super(msg);
    }
}
