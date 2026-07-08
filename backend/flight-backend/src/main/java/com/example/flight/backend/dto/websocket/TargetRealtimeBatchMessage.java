package com.example.flight.backend.dto.websocket;

import com.example.flight.backend.websocket.WebSocketMessageType;
import com.example.flight.common.event.TargetUpdateBatchEvent;
import com.example.flight.common.event.TargetUpdateEvent;
import java.util.Arrays;
import java.util.List;

public record TargetRealtimeBatchMessage(
        WebSocketMessageType type,
        long timestamp,
        String[] targetIds,
        double[] latitudes,
        double[] longitudes,
        double[] altitudes,
        String[] classifications
) {

    public TargetRealtimeBatchMessage {
        targetIds = Arrays.copyOf(targetIds, targetIds.length);
        latitudes = Arrays.copyOf(latitudes, latitudes.length);
        longitudes = Arrays.copyOf(longitudes, longitudes.length);
        altitudes = Arrays.copyOf(altitudes, altitudes.length);
        classifications = Arrays.copyOf(classifications, classifications.length);
        if (
                latitudes.length != targetIds.length
                        || longitudes.length != targetIds.length
                        || altitudes.length != targetIds.length
                        || classifications.length != targetIds.length
        ) {
            throw new IllegalArgumentException("Compact realtime arrays must have the same length");
        }
    }

    public static TargetRealtimeBatchMessage from(TargetUpdateBatchEvent batchEvent) {
        List<TargetUpdateEvent> targets = batchEvent.targets();
        String[] targetIds = new String[targets.size()];
        double[] latitudes = new double[targets.size()];
        double[] longitudes = new double[targets.size()];
        double[] altitudes = new double[targets.size()];
        String[] classifications = new String[targets.size()];

        for (int index = 0; index < targets.size(); index++) {
            TargetUpdateEvent target = targets.get(index);
            targetIds[index] = target.targetId();
            latitudes[index] = target.latitude();
            longitudes[index] = target.longitude();
            altitudes[index] = target.altitude();
            classifications[index] = target.classification().name();
        }

        return new TargetRealtimeBatchMessage(
                WebSocketMessageType.TARGET_UPDATE_BATCH_COMPACT,
                batchEvent.timestamp(),
                targetIds,
                latitudes,
                longitudes,
                altitudes,
                classifications
        );
    }

    public int targetCount() {
        return targetIds.length;
    }

    @Override
    public String[] targetIds() {
        return Arrays.copyOf(targetIds, targetIds.length);
    }

    @Override
    public double[] latitudes() {
        return Arrays.copyOf(latitudes, latitudes.length);
    }

    @Override
    public double[] longitudes() {
        return Arrays.copyOf(longitudes, longitudes.length);
    }

    @Override
    public double[] altitudes() {
        return Arrays.copyOf(altitudes, altitudes.length);
    }

    @Override
    public String[] classifications() {
        return Arrays.copyOf(classifications, classifications.length);
    }
}
