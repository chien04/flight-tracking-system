package com.example.flight.backend.controller;

import com.example.flight.backend.dto.response.TargetCurrentResponse;
import com.example.flight.backend.dto.response.TargetDetailResponse;
import com.example.flight.backend.dto.response.TargetHistoryPointResponse;
import com.example.flight.backend.service.TargetHistoryService;
import com.example.flight.backend.service.TargetQueryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/targets")
public class TargetQueryController {

    private final TargetQueryService targetQueryService;
    private final TargetHistoryService targetHistoryService;

    public TargetQueryController(
            TargetQueryService targetQueryService,
            TargetHistoryService targetHistoryService
    ) {
        this.targetQueryService = targetQueryService;
        this.targetHistoryService = targetHistoryService;
    }

    @GetMapping
    public List<TargetCurrentResponse> findAll() {
        return targetQueryService.findAll();
    }

    @GetMapping("/{targetId}/history")
    public List<TargetHistoryPointResponse> findHistory(
            @PathVariable("targetId") String targetId,
            @RequestParam("from") long from,
            @RequestParam("to") long to,
            @RequestParam(name = "sampleMs", defaultValue = "1000") long sampleMs
    ) {
        return targetHistoryService.findHistory(targetId, from, to, sampleMs);
    }

    @GetMapping("/{targetId}")
    public TargetDetailResponse findById(@PathVariable("targetId") String targetId) {
        return targetQueryService.findById(targetId);
    }
}
