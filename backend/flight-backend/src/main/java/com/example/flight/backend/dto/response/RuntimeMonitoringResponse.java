package com.example.flight.backend.dto.response;

public record RuntimeMonitoringResponse(
        long timestamp,
        long performanceTargetCycleMs,
        IngestionMetricsResponse ingestion,
        RedisMemoryResponse redis,
        KafkaLagResponse kafka
) {
}
