package com.example.flight.backend.service;

import com.example.flight.backend.dto.websocket.TargetRealtimeBatchMessage;
import com.example.flight.backend.websocket.TargetWebSocketPublisher;
import com.example.flight.common.event.TargetUpdateBatchEvent;
import org.springframework.stereotype.Service;

@Service
public class TargetRealtimePushService {

    private final TargetWebSocketPublisher targetWebSocketPublisher;

    public TargetRealtimePushService(TargetWebSocketPublisher targetWebSocketPublisher) {
        this.targetWebSocketPublisher = targetWebSocketPublisher;
    }

    public void pushBatch(TargetUpdateBatchEvent batchEvent) {
        targetWebSocketPublisher.publish(TargetRealtimeBatchMessage.from(batchEvent));
    }
}
