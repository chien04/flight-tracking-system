package com.example.flight.backend.dto.response;

import com.example.flight.common.enums.TargetClassification;

public record TargetDetailResponse(
        String targetId,
        double latitude,
        double longitude,
        double altitude,
        TargetClassification classification,
        long updatedAt
) {
}
