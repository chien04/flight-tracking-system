package com.example.flight.backend.dto.response;

public record RedisMemoryResponse(
        long usedMemoryBytes,
        String usedMemoryHuman,
        String status,
        String error
) {
}
