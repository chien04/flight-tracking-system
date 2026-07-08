package com.example.flight.backend.dto.request;

public record TargetHistoryQueryRequest(
        String targetId,
        long from,
        long to,
        long sampleMs
) {
}
