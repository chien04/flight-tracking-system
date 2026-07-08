package com.example.flight.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.flight.backend.dto.response.TargetCurrentResponse;
import com.example.flight.backend.exception.TargetNotFoundException;
import com.example.flight.backend.repository.redis.TargetCurrentStateRedisRepository;
import com.example.flight.common.enums.TargetClassification;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TargetQueryServiceTest {

    @Test
    void returnsAllCurrentTargets() {
        TargetCurrentStateRedisRepository repository = mock(TargetCurrentStateRedisRepository.class);
        TargetQueryService service = new TargetQueryService(repository);
        TargetCurrentResponse current = current("0001");
        when(repository.findAll()).thenReturn(List.of(current));

        assertEquals(List.of(current), service.findAll());
    }

    @Test
    void returnsTargetDetailById() {
        TargetCurrentStateRedisRepository repository = mock(TargetCurrentStateRedisRepository.class);
        TargetQueryService service = new TargetQueryService(repository);
        when(repository.findById("0001")).thenReturn(Optional.of(current("0001")));

        var detail = service.findById("0001");

        assertEquals("0001", detail.targetId());
    }

    @Test
    void throwsWhenTargetIsMissing() {
        TargetCurrentStateRedisRepository repository = mock(TargetCurrentStateRedisRepository.class);
        TargetQueryService service = new TargetQueryService(repository);
        when(repository.findById("9999")).thenReturn(Optional.empty());

        assertThrows(TargetNotFoundException.class, () -> service.findById("9999"));
    }

    private static TargetCurrentResponse current(String targetId) {
        return new TargetCurrentResponse(targetId, 21.0285, 105.8542, 1000, TargetClassification.UNKNOWN, 123);
    }
}
