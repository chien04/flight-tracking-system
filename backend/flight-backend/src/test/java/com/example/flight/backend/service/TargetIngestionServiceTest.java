package com.example.flight.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.flight.backend.config.AppProperties;
import com.example.flight.common.enums.TargetClassification;
import com.example.flight.common.event.TargetUpdateBatchEvent;
import com.example.flight.common.event.TargetUpdateEvent;
import com.example.flight.common.util.TargetIdUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class TargetIngestionServiceTest {

    @Test
    void logsBatchCountForTenThousandTargets() {
        TargetCurrentStateService targetCurrentStateService = mock(TargetCurrentStateService.class);
        TargetHistoryService targetHistoryService = mock(TargetHistoryService.class);
        TargetRealtimePushService targetRealtimePushService = mock(TargetRealtimePushService.class);
        IngestionMetricsService ingestionMetricsService = mock(IngestionMetricsService.class);
        AppProperties appProperties = new AppProperties();
        TargetIngestionService targetIngestionService = new TargetIngestionService(
                targetCurrentStateService,
                targetHistoryService,
                targetRealtimePushService,
                ingestionMetricsService,
                appProperties
        );
        TargetUpdateBatchEvent batchEvent = batch(10000);

        int count = targetIngestionService.ingest(batchEvent);

        assertEquals(10000, count);
        verify(targetCurrentStateService).updateBatch(batchEvent);
        verify(targetHistoryService).saveBatch(batchEvent);
        verify(targetRealtimePushService).pushBatch(batchEvent);
        verify(ingestionMetricsService).recordBatch(
                org.mockito.Mockito.eq(batchEvent.timestamp()),
                org.mockito.Mockito.eq(10000),
                org.mockito.Mockito.anyLong(),
                org.mockito.Mockito.anyLong(),
                org.mockito.Mockito.anyLong(),
                org.mockito.Mockito.anyLong()
        );
    }

    private static TargetUpdateBatchEvent batch(int targetCount) {
        long timestamp = System.currentTimeMillis();
        List<TargetUpdateEvent> targets = new ArrayList<>(targetCount);
        for (int index = 0; index < targetCount; index++) {
            targets.add(new TargetUpdateEvent(
                    TargetIdUtil.format(index),
                    21.0285,
                    105.8542,
                    1000,
                    TargetClassification.UNKNOWN,
                    timestamp
            ));
        }
        return new TargetUpdateBatchEvent(timestamp, targets);
    }
}
