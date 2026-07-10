package com.example.flight.backend.repository.redis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.flight.backend.config.AppProperties;
import com.example.flight.common.enums.TargetClassification;
import com.example.flight.common.event.TargetUpdateEvent;
import com.example.flight.common.util.TargetIdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

class TargetCurrentStateRedisRepositoryTest {

    @Test
    void savesTenThousandTargetsWithOneHashPutAll() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        HashOperations<String, Object, Object> hashOperations = hashOperations();
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        AppProperties appProperties = new AppProperties();
        TargetCurrentStateRedisRepository repository = new TargetCurrentStateRedisRepository(
                redisTemplate,
                new ObjectMapper(),
                appProperties
        );

        repository.saveBatch(events(10000));

        ArgumentCaptor<Map<Object, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(hashOperations).putAll(eq("target:current:all"), captor.capture());
        assertEquals(10000, captor.getValue().size());
    }

    @Test
    void readsSingleTargetFromHash() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        HashOperations<String, Object, Object> hashOperations = hashOperations();
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(eq("target:current:all"), eq("0001"))).thenReturn("""
                {"targetId":"0001","latitude":21.0285,"longitude":105.8542,"altitude":1000.0,"classification":"FRIEND","updatedAt":123}
                """);
        TargetCurrentStateRedisRepository repository = new TargetCurrentStateRedisRepository(
                redisTemplate,
                new ObjectMapper(),
                new AppProperties()
        );

        var target = repository.findById("0001").orElseThrow();

        assertEquals("0001", target.targetId());
        assertEquals(TargetClassification.FRIEND, target.classification());
    }

    @SuppressWarnings("unchecked")
    private static HashOperations<String, Object, Object> hashOperations() {
        return mock(HashOperations.class);
    }

    private static List<TargetUpdateEvent> events(int targetCount) {
        long timestamp = System.currentTimeMillis();
        List<TargetUpdateEvent> events = new ArrayList<>(targetCount);
        for (int index = 0; index < targetCount; index++) {
            events.add(new TargetUpdateEvent(
                    TargetIdUtil.format(index),
                    21.0285,
                    105.8542,
                    1000,
                    TargetClassification.UNKNOWN,
                    timestamp
            ));
        }
        return events;
    }
}
