package com.example.flight.backend.exception;

public record ErrorResponse(
        long timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
