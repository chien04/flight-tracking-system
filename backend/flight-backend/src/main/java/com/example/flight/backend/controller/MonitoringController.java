package com.example.flight.backend.controller;

import com.example.flight.backend.config.AppProperties;
import com.example.flight.backend.dto.response.RuntimeMonitoringResponse;
import com.example.flight.backend.service.IngestionMetricsService;
import com.example.flight.backend.service.KafkaLagService;
import com.example.flight.backend.service.RedisMemoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

    private final IngestionMetricsService ingestionMetricsService;
    private final RedisMemoryService redisMemoryService;
    private final KafkaLagService kafkaLagService;
    private final AppProperties appProperties;

    public MonitoringController(
            IngestionMetricsService ingestionMetricsService,
            RedisMemoryService redisMemoryService,
            KafkaLagService kafkaLagService,
            AppProperties appProperties
    ) {
        this.ingestionMetricsService = ingestionMetricsService;
        this.redisMemoryService = redisMemoryService;
        this.kafkaLagService = kafkaLagService;
        this.appProperties = appProperties;
    }

    @GetMapping("/runtime")
    public RuntimeMonitoringResponse runtime() {
        long targetCycleMs = appProperties.getPerformanceTargetCycleMs();
        return new RuntimeMonitoringResponse(
                System.currentTimeMillis(),
                targetCycleMs,
                ingestionMetricsService.snapshot(targetCycleMs),
                redisMemoryService.readMemory(),
                kafkaLagService.readLag()
        );
    }
}
