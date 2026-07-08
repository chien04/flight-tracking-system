package com.example.flight.backend.exception;

public class TargetNotFoundException extends RuntimeException {

    public TargetNotFoundException(String targetId) {
        super("Target not found: " + targetId);
    }
}
