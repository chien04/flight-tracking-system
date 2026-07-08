package com.example.flight.backend.service;

import com.example.flight.backend.dto.response.TargetCurrentResponse;
import com.example.flight.backend.dto.response.TargetDetailResponse;
import com.example.flight.backend.exception.TargetNotFoundException;
import com.example.flight.backend.mapper.TargetMapper;
import com.example.flight.backend.repository.redis.TargetCurrentStateRedisRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TargetQueryService {

    private final TargetCurrentStateRedisRepository targetCurrentStateRedisRepository;

    public TargetQueryService(TargetCurrentStateRedisRepository targetCurrentStateRedisRepository) {
        this.targetCurrentStateRedisRepository = targetCurrentStateRedisRepository;
    }

    public List<TargetCurrentResponse> findAll() {
        return targetCurrentStateRedisRepository.findAll();
    }

    public TargetDetailResponse findById(String targetId) {
        TargetCurrentResponse current = targetCurrentStateRedisRepository.findById(targetId)
                .orElseThrow(() -> new TargetNotFoundException(targetId));
        return TargetMapper.toDetailResponse(current);
    }
}
