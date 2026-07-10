package com.example.flight.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.flight.backend.dto.response.IngestionMetricsResponse;
import org.junit.jupiter.api.Test;

class IngestionMetricsServiceTest {

    @Test
    void recordsLatestBatchMetrics() {
        IngestionMetricsService service = new IngestionMetricsService();

        service.recordBatch(1000, 1100, 1350, 10000, 10, 100, 20, 250);
        IngestionMetricsResponse snapshot = service.snapshot(300);

        assertEquals(1, snapshot.totalBatches());
        assertEquals(10000, snapshot.totalTargets());
        assertEquals(10000, snapshot.lastTargetCount());
        assertEquals(1100, snapshot.lastBatchReceivedAt());
        assertEquals(1350, snapshot.lastBatchCompletedAt());
        assertEquals(350, snapshot.lastEndToEndMs());
        assertEquals(250, snapshot.lastTotalIngestMs());
        assertEquals(100000, snapshot.lastClickHouseInsertRatePerSecond(), 0.001);
        assertFalse(snapshot.lastCycleWithinTarget());
    }
}
