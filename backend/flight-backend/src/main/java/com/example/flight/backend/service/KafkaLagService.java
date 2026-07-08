package com.example.flight.backend.service;

import com.example.flight.backend.config.AppProperties;
import com.example.flight.backend.dto.response.KafkaLagResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;

@Service
public class KafkaLagService {

    private static final int TIMEOUT_SECONDS = 2;

    private final KafkaProperties kafkaProperties;
    private final AppProperties appProperties;

    public KafkaLagService(KafkaProperties kafkaProperties, AppProperties appProperties) {
        this.kafkaProperties = kafkaProperties;
        this.appProperties = appProperties;
    }

    public KafkaLagResponse readLag() {
        String groupId = kafkaProperties.getConsumer().getGroupId();
        String topic = appProperties.getKafkaTopic();
        if (groupId == null || groupId.isBlank()) {
            return new KafkaLagResponse("", topic, 0, 0, "UNAVAILABLE", "Kafka consumer group id is not configured");
        }

        try (AdminClient adminClient = AdminClient.create(adminConfig())) {
            ListConsumerGroupOffsetsResult groupOffsetsResult = adminClient.listConsumerGroupOffsets(groupId);
            Map<TopicPartition, OffsetAndMetadata> committedOffsets = groupOffsetsResult
                    .partitionsToOffsetAndMetadata()
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            TopicDescription topicDescription = adminClient.describeTopics(List.of(topic))
                    .allTopicNames()
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .get(topic);

            Map<TopicPartition, OffsetSpec> latestOffsetRequests = new HashMap<>();
            for (var partitionInfo : topicDescription.partitions()) {
                latestOffsetRequests.put(new TopicPartition(topic, partitionInfo.partition()), OffsetSpec.latest());
            }

            ListOffsetsResult latestOffsetsResult = adminClient.listOffsets(latestOffsetRequests);
            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> latestOffsets = latestOffsetsResult
                    .all()
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            long totalLag = 0;
            for (Map.Entry<TopicPartition, OffsetSpec> entry : latestOffsetRequests.entrySet()) {
                TopicPartition partition = entry.getKey();
                OffsetAndMetadata committedOffset = committedOffsets.get(partition);
                long committed = committedOffset == null ? 0 : committedOffset.offset();
                long latest = latestOffsets.get(partition).offset();
                totalLag += Math.max(0, latest - committed);
            }

            return new KafkaLagResponse(groupId, topic, totalLag, latestOffsetRequests.size(), "OK", null);
        } catch (Exception exception) {
            return new KafkaLagResponse(groupId, topic, 0, 0, "ERROR", exception.getMessage());
        }
    }

    private Map<String, Object> adminConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", kafkaProperties.getBootstrapServers()));
        return config;
    }
}
