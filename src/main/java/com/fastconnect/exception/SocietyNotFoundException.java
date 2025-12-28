package com.fastconnect.exception;

public class SocietyNotFoundException extends RuntimeException {

    public SocietyNotFoundException(Long societyId) {
        super("Society not found with ID: " + societyId);
    }

    public SocietyNotFoundException(String societyName) {
        super("Society not found with name: " + societyName);
    }
}