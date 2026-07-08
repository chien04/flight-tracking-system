package com.example.flight.backend.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.flight.backend.config.AppProperties;
import com.example.flight.backend.repository.clickhouse.TargetHistoryClickHouseRepository;
import com.example.flight.common.enums.TargetClassification;
import com.example.flight.common.event.TargetUpdateBatchEvent;
import com.example.flight.common.event.TargetUpdateEvent;
import java.util.List;
import org.junit.jupiter.api.Test;

class TargetHistoryServiceTest {

    @Test
    void savesBatchWhenHistoryIsEnabled() {
        TargetHistoryClickHouseRepository repository = mock(TargetHistoryClickHouseRepository.class);
        AppProperties appProperties = new AppProperties();
        TargetHistoryService service = new TargetHistoryService(repository, appProperties);
        TargetUpdateBatchEvent batchEvent = batch();

        service.saveBatch(batchEvent);

        verify(repository).saveBatch(batchEvent.targets());
    }

    @Test
    void skipsBatchWhenHistoryIsDisabled() {
        TargetHistoryClickHouseRepository repository = mock(TargetHistoryClickHouseRepository.class);
        AppProperties appProperties = new AppProperties();
        appProperties.setHistoryEnabled(false);
        TargetHistoryService service = new TargetHistoryService(repository, appProperties);
        TargetUpdateBatchEvent batchEvent = batch();

        service.saveBatch(batchEvent);

        verify(repository, never()).saveBatch(batchEvent.targets());
    }

    @Test
    void rejectsInvalidHistoryRange() {
        TargetHistoryClickHouseRepository repository = mock(TargetHistoryClickHouseRepository.class);
        AppProperties appProperties = new AppProperties();
        TargetHistoryService service = new TargetHistoryService(repository, appProperties);

        assertThrows(IllegalArgumentException.class, () -> service.findHistory("0001", 2000, 1000, 1000));
    }

    private static TargetUpdateBatchEvent batch() {
        long timestamp = 1000;
        return new TargetUpdateBatchEvent(timestamp, List.of(new TargetUpdateEvent(
                "0001",
                21.0285,
                105.8542,
                1000,
                TargetClassification.UNKNOWN,
                timestamp
        )));
    }
}
