package com.example.flight.backend.dto.response;

public record IngestionMetricsResponse(
        long totalBatches,
        long totalTargets,
        int lastTargetCount,
        long lastBatchEventTimestamp,
        long lastBatchReceivedAt,
        long lastBatchCompletedAt,
        long lastEndToEndMs,
        long lastRedisWriteMs,
        long lastClickHouseInsertMs,
        long lastWebSocketPushMs,
        long lastTotalIngestMs,
        double lastClickHouseInsertRatePerSecond,
        boolean lastCycleWithinTarget
) {
}
