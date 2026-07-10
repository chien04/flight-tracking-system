package com.example.flight.backend.kafka;

import com.example.flight.backend.service.TargetIngestionService;
import com.example.flight.common.event.TargetUpdateBatchEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "flight", name = "kafka-consumer-enabled", havingValue = "true", matchIfMissing = true)
public class TargetUpdateConsumer {

    private final TargetIngestionService targetIngestionService;

    public TargetUpdateConsumer(TargetIngestionService targetIngestionService) {
        this.targetIngestionService = targetIngestionService;
    }

    @KafkaListener(topics = "${flight.kafka-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(TargetUpdateBatchEvent batchEvent) {
        targetIngestionService.ingest(batchEvent);
    }
}
