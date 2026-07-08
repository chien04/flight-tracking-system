package com.example.flight.backend.dto.response;

import com.example.flight.common.enums.TargetClassification;

public record TargetCurrentResponse(
        String targetId,
        double latitude,
        double longitude,
        double altitude,
        TargetClassification classification,
        long updatedAt
) {
}
