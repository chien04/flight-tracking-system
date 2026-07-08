package com.example.flight.backend.service;

import com.example.flight.common.event.TargetUpdateBatchEvent;
import com.example.flight.backend.repository.redis.TargetCurrentStateRedisRepository;
import org.springframework.stereotype.Service;

@Service
public class TargetCurrentStateService {

    private final TargetCurrentStateRedisRepository targetCurrentStateRedisRepository;

    public TargetCurrentStateService(TargetCurrentStateRedisRepository targetCurrentStateRedisRepository) {
        this.targetCurrentStateRedisRepository = targetCurrentStateRedisRepository;
    }

    public void updateBatch(TargetUpdateBatchEvent batchEvent) {
        targetCurrentStateRedisRepository.saveBatch(batchEvent.targets());
    }
}
