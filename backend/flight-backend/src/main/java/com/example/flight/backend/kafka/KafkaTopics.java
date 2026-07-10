package com.example.flight.backend.kafka;

import com.example.flight.backend.config.AppProperties;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopics {

    private final AppProperties appProperties;

    public KafkaTopics(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String targetUpdates() {
        return appProperties.getKafkaTopic();
    }
}
