package com.example.flight.backend.dto.response;

public record KafkaLagResponse(
        String groupId,
        String topic,
        long totalLag,
        int partitions,
        String status,
        String error
) {
}
