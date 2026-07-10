package com.example.flight.backend.service;

import com.example.flight.backend.config.AppProperties;
import com.example.flight.common.event.TargetUpdateBatchEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TargetIngestionService {

    private static final Logger log = LoggerFactory.getLogger(TargetIngestionService.class);

    private final TargetCurrentStateService targetCurrentStateService;
    private final TargetHistoryService targetHistoryService;
    private final TargetRealtimePushService targetRealtimePushService;
    private final IngestionMetricsService ingestionMetricsService;
    private final AppProperties appProperties;

    public TargetIngestionService(
            TargetCurrentStateService targetCurrentStateService,
            TargetHistoryService targetHistoryService,
            TargetRealtimePushService targetRealtimePushService,
            IngestionMetricsService ingestionMetricsService,
            AppProperties appProperties
    ) {
        this.targetCurrentStateService = targetCurrentStateService;
        this.targetHistoryService = targetHistoryService;
        this.targetRealtimePushService = targetRealtimePushService;
        this.ingestionMetricsService = ingestionMetricsService;
        this.appProperties = appProperties;
    }

    public int ingest(TargetUpdateBatchEvent batchEvent) {
        int targetCount = batchEvent.targets().size();
        long receivedAt = System.currentTimeMillis();
        long totalStarted = System.nanoTime();

        long redisStarted = System.nanoTime();
        targetCurrentStateService.updateBatch(batchEvent);
        long redisMs = elapsedMillis(redisStarted);

        long historyStarted = System.nanoTime();
        try {
            targetHistoryService.saveBatch(batchEvent);
        } catch (RuntimeException exception) {
            log.error(
                    "Failed to persist target history: timestamp={}, targets={}",
                    batchEvent.timestamp(),
                    targetCount,
                    exception
            );
        }
        long historyMs = elapsedMillis(historyStarted);

        long webSocketStarted = System.nanoTime();
        targetRealtimePushService.pushBatch(batchEvent);
        long webSocketMs = elapsedMillis(webSocketStarted);

        long totalMs = elapsedMillis(totalStarted);
        long completedAt = System.currentTimeMillis();
        long endToEndMs = Math.max(0, completedAt - batchEvent.timestamp());
        ingestionMetricsService.recordBatch(
                batchEvent.timestamp(),
                receivedAt,
                completedAt,
                targetCount,
                redisMs,
                historyMs,
                webSocketMs,
                totalMs
        );

        long targetCycleMs = appProperties.getPerformanceTargetCycleMs();
        if (endToEndMs > targetCycleMs) {
            log.warn(
                    "Target update batch exceeded target cycle: timestamp={}, targets={}, targetMs={}, endToEndMs={}, backendMs={}, redisMs={}, clickHouseMs={}, webSocketMs={}",
                    batchEvent.timestamp(),
                    targetCount,
                    targetCycleMs,
                    endToEndMs,
                    totalMs,
                    redisMs,
                    historyMs,
                    webSocketMs
            );
        } else {
            log.info(
                    "Received target update batch: timestamp={}, targets={}, endToEndMs={}, backendMs={}",
                    batchEvent.timestamp(),
                    targetCount,
                    endToEndMs,
                    totalMs
            );
        }
        return targetCount;
    }

    private static long elapsedMillis(long startedNanos) {
        return (System.nanoTime() - startedNanos) / 1_000_000;
    }
}
