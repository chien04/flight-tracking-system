package com.example.flight.common.event;

import java.util.List;

public record TargetUpdateBatchEvent(
        long timestamp,
        List<TargetUpdateEvent> targets
) {

    public TargetUpdateBatchEvent {
        targets = List.copyOf(targets);
    }
}
