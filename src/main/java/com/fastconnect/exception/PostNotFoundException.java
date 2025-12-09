package com.fastconnect.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Long postId) {
        super("Post not found with ID: " + postId);
    }
}