package com.example.flight.backend.websocket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.flight.backend.config.AppProperties;
import com.example.flight.backend.dto.websocket.TargetRealtimeBatchMessage;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

class TargetWebSocketPublisherTest {

    @Test
    void sendsBatchToConfiguredTopic() {
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        AppProperties appProperties = new AppProperties();
        TargetWebSocketPublisher publisher = new TargetWebSocketPublisher(messagingTemplate, appProperties);
        TargetRealtimeBatchMessage message = new TargetRealtimeBatchMessage(
                WebSocketMessageType.TARGET_UPDATE_BATCH_COMPACT,
                123,
                new String[0],
                new double[0],
                new double[0],
                new double[0],
                new String[0]
        );

        publisher.publish(message);

        verify(messagingTemplate).convertAndSend("/topic/targets/realtime", message);
    }
}
