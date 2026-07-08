package com.example.flight.simulator.publisher;

import com.example.flight.common.event.TargetUpdateBatchEvent;
import com.example.flight.simulator.config.SimulatorProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaTargetUpdatePublisher implements TargetUpdatePublisher {

    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final SimulatorProperties properties;

    public KafkaTargetUpdatePublisher(KafkaTemplate<Object, Object> kafkaTemplate, SimulatorProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    @Override
    public void publish(TargetUpdateBatchEvent batchEvent) {
        kafkaTemplate.send(properties.getKafkaTopic(), Long.toString(batchEvent.timestamp()), batchEvent);
    }
}
