package com.example.flight.backend.kafka;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.flight.backend.service.TargetIngestionService;
import com.example.flight.common.enums.TargetClassification;
import com.example.flight.common.event.TargetUpdateBatchEvent;
import com.example.flight.common.event.TargetUpdateEvent;
import java.util.List;
import org.junit.jupiter.api.Test;

class TargetUpdateConsumerTest {

    @Test
    void delegatesBatchToIngestionService() {
        TargetIngestionService targetIngestionService = mock(TargetIngestionService.class);
        TargetUpdateConsumer consumer = new TargetUpdateConsumer(targetIngestionService);
        TargetUpdateBatchEvent batchEvent = new TargetUpdateBatchEvent(
                123,
                List.of(new TargetUpdateEvent("0001", 21.0285, 105.8542, 1000, TargetClassification.FRIEND, 123))
        );

        consumer.consume(batchEvent);

        verify(targetIngestionService).ingest(batchEvent);
    }
}
