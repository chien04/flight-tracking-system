package com.example.flight.backend.service;

import com.example.flight.backend.config.AppProperties;
import com.example.flight.backend.dto.response.TargetHistoryPointResponse;
import com.example.flight.backend.repository.clickhouse.TargetHistoryClickHouseRepository;
import com.example.flight.backend.util.TimeRangeValidator;
import com.example.flight.common.event.TargetUpdateBatchEvent;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TargetHistoryService {

    private final TargetHistoryClickHouseRepository targetHistoryClickHouseRepository;
    private final AppProperties appProperties;

    public TargetHistoryService(
            TargetHistoryClickHouseRepository targetHistoryClickHouseRepository,
            AppProperties appProperties
    ) {
        this.targetHistoryClickHouseRepository = targetHistoryClickHouseRepository;
        this.appProperties = appProperties;
    }

    public void initializeSchema() {
        if (!appProperties.isHistoryEnabled()) {
            return;
        }

        targetHistoryClickHouseRepository.createTableIfNotExists();
    }

    public void saveBatch(TargetUpdateBatchEvent batchEvent) {
        if (!appProperties.isHistoryEnabled()) {
            return;
        }

        targetHistoryClickHouseRepository.saveBatch(batchEvent.targets());
    }

    public List<TargetHistoryPointResponse> findHistory(
            String targetId,
            long from,
            long to,
            long sampleMs
    ) {
        TimeRangeValidator.validateTargetId(targetId);
        TimeRangeValidator.validateRange(from, to);
        TimeRangeValidator.validateSampleMs(sampleMs);

        return targetHistoryClickHouseRepository.findHistory(
                targetId,
                Instant.ofEpochMilli(from),
                Instant.ofEpochMilli(to),
                sampleMs
        );
    }
}
