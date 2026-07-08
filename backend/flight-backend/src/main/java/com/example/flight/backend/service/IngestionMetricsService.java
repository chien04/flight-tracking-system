package com.example.flight.backend.service;

import com.example.flight.backend.dto.response.IngestionMetricsResponse;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class IngestionMetricsService {

    private final AtomicLong totalBatches = new AtomicLong();
    private final AtomicLong totalTargets = new AtomicLong();
    private final AtomicInteger lastTargetCount = new AtomicInteger();
    private final AtomicLong lastBatchEventTimestamp = new AtomicLong();
    private final AtomicLong lastBatchReceivedAt = new AtomicLong();
    private final AtomicLong lastRedisWriteMs = new AtomicLong();
    private final AtomicLong lastClickHouseInsertMs = new AtomicLong();
    private final AtomicLong lastWebSocketPushMs = new AtomicLong();
    private final AtomicLong lastTotalIngestMs = new AtomicLong();

    public void recordBatch(
            long batchEventTimestamp,
            int targetCount,
            long redisWriteMs,
            long clickHouseInsertMs,
            long webSocketPushMs,
            long totalIngestMs
    ) {
        totalBatches.incrementAndGet();
        totalTargets.addAndGet(targetCount);
        lastTargetCount.set(targetCount);
        lastBatchEventTimestamp.set(batchEventTimestamp);
        lastBatchReceivedAt.set(System.currentTimeMillis());
        lastRedisWriteMs.set(redisWriteMs);
        lastClickHouseInsertMs.set(clickHouseInsertMs);
        lastWebSocketPushMs.set(webSocketPushMs);
        lastTotalIngestMs.set(totalIngestMs);
    }

    public IngestionMetricsResponse snapshot(long performanceTargetCycleMs) {
        long eventTimestamp = lastBatchEventTimestamp.get();
        long ageMs = eventTimestamp == 0 ? 0 : Math.max(0, System.currentTimeMillis() - eventTimestamp);
        long clickHouseMs = lastClickHouseInsertMs.get();
        int targetCount = lastTargetCount.get();
        double insertRate = clickHouseMs == 0
                ? 0
                : targetCount / (clickHouseMs / 1000.0);

        return new IngestionMetricsResponse(
                totalBatches.get(),
                totalTargets.get(),
                targetCount,
                eventTimestamp,
                lastBatchReceivedAt.get(),
                ageMs,
                lastRedisWriteMs.get(),
                clickHouseMs,
                lastWebSocketPushMs.get(),
                lastTotalIngestMs.get(),
                insertRate,
                lastTotalIngestMs.get() <= performanceTargetCycleMs
        );
    }
}
