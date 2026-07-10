package com.example.flight.backend.repository.redis;

import com.example.flight.backend.config.AppProperties;
import com.example.flight.backend.dto.response.TargetCurrentResponse;
import com.example.flight.backend.mapper.TargetMapper;
import com.example.flight.common.event.TargetUpdateEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TargetCurrentStateRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    public TargetCurrentStateRedisRepository(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            AppProperties appProperties
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.appProperties = appProperties;
    }

    public void saveBatch(List<TargetUpdateEvent> targets) {
        Map<Object, Object> valuesByTargetId = new LinkedHashMap<>(targets.size());
        for (TargetUpdateEvent target : targets) {
            valuesByTargetId.put(target.targetId(), serialize(TargetMapper.toCurrentResponse(target)));
        }

        hashOperations().putAll(appProperties.getRedisCurrentStateKey(), valuesByTargetId);
    }

    public List<TargetCurrentResponse> findAll() {
        return hashOperations().entries(appProperties.getRedisCurrentStateKey())
                .values()
                .stream()
                .map(value -> deserialize(String.valueOf(value)))
                .sorted(Comparator.comparing(TargetCurrentResponse::targetId))
                .toList();
    }

    public Optional<TargetCurrentResponse> findById(String targetId) {
        Object value = hashOperations().get(appProperties.getRedisCurrentStateKey(), targetId);
        if (value == null) {
            return Optional.empty();
        }

        return Optional.of(deserialize(String.valueOf(value)));
    }

    private HashOperations<String, Object, Object> hashOperations() {
        return redisTemplate.opsForHash();
    }

    private String serialize(TargetCurrentResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize target current state", exception);
        }
    }

    private TargetCurrentResponse deserialize(String value) {
        try {
            return objectMapper.readValue(value, TargetCurrentResponse.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to deserialize target current state", exception);
        }
    }
}
