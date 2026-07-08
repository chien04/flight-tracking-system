package com.example.flight.common.dto;

import com.example.flight.common.enums.TargetClassification;

public record TargetUpdateDto(
        String targetId,
        double latitude,
        double longitude,
        double altitude,
        TargetClassification classification,
        long timestamp
) {
}
