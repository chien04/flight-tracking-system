package com.example.flight.common.event;

import com.example.flight.common.enums.TargetClassification;

public record TargetUpdateEvent(
        String targetId,
        double latitude,
        double longitude,
        double altitude,
        TargetClassification classification,
        long timestamp
) {
}
