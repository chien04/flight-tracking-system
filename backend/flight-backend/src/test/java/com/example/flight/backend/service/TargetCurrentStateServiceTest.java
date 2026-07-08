package com.example.flight.backend.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.flight.backend.repository.redis.TargetCurrentStateRedisRepository;
import com.example.flight.common.enums.TargetClassification;
import com.example.flight.common.event.TargetUpdateBatchEvent;
import com.example.flight.common.event.TargetUpdateEvent;
import java.util.List;
import org.junit.jupiter.api.Test;

class TargetCurrentStateServiceTest {

    @Test
    void writesCurrentStateAsBatch() {
        TargetCurrentStateRedisRepository repository = mock(TargetCurrentStateRedisRepository.class);
        TargetCurrentStateService service = new TargetCurrentStateService(repository);
        TargetUpdateEvent event = new TargetUpdateEvent(
                "0001",
                21.0285,
                105.8542,
                1000,
                TargetClassification.FRIEND,
                123
        );
        TargetUpdateBatchEvent batchEvent = new TargetUpdateBatchEvent(123, List.of(event));

        service.updateBatch(batchEvent);

        verify(repository).saveBatch(batchEvent.targets());
    }
}
