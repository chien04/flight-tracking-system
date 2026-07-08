package com.example.flight.backend.websocket;

import com.example.flight.backend.config.AppProperties;
import com.example.flight.backend.dto.websocket.TargetRealtimeBatchMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class TargetWebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private final AppProperties appProperties;

    public TargetWebSocketPublisher(SimpMessagingTemplate messagingTemplate, AppProperties appProperties) {
        this.messagingTemplate = messagingTemplate;
        this.appProperties = appProperties;
    }

    public void publish(TargetRealtimeBatchMessage message) {
        messagingTemplate.convertAndSend(appProperties.getWebsocketTopic(), message);
    }
}
