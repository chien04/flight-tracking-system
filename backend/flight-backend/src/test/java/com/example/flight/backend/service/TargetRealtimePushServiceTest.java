package com.example.flight.backend.service;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.flight.backend.dto.websocket.TargetRealtimeBatchMessage;
import com.example.flight.backend.websocket.TargetWebSocketPublisher;
import com.example.flight.backend.websocket.WebSocketMessageType;
import com.example.flight.common.enums.TargetClassification;
import com.example.flight.common.event.TargetUpdateBatchEvent;
import com.example.flight.common.event.TargetUpdateEvent;
import com.example.flight.common.util.TargetIdUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class TargetRealtimePushServiceTest {

    @Test
    void publishesOneWebSocketMessageForWholeBatch() {
        TargetWebSocketPublisher publisher = mock(TargetWebSocketPublisher.class);
        TargetRealtimePushService service = new TargetRealtimePushService(publisher);
        TargetUpdateBatchEvent batchEvent = batch(10000);

        service.pushBatch(batchEvent);

        verify(publisher).publish(argThat(message -> matchesBatch(message, batchEvent)));
    }

    private static boolean matchesBatch(TargetRealtimeBatchMessage message, TargetUpdateBatchEvent batchEvent) {
        return message.type() == WebSocketMessageType.TARGET_UPDATE_BATCH_COMPACT
                && message.timestamp() == batchEvent.timestamp()
                && message.targetCount() == 10000
                && message.targetIds()[0].equals("0000")
                && message.classifications()[0].equals(TargetClassification.UNKNOWN.name());
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
