package com.example.flight.backend.dto.response;

public record IngestionMetricsResponse(
        long totalBatches,
        long totalTargets,
        int lastTargetCount,
        long lastBatchEventTimestamp,
        long lastBatchReceivedAt,
        long lastBatchAgeMs,
        long lastRedisWriteMs,
        long lastClickHouseInsertMs,
        long lastWebSocketPushMs,
        long lastTotalIngestMs,
        double lastClickHouseInsertRatePerSecond,
        boolean lastCycleWithinTarget
) {
}
