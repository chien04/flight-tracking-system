package com.example.flight.backend.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TargetHistorySchemaInitializer implements ApplicationRunner {

    private final TargetHistoryService targetHistoryService;

    public TargetHistorySchemaInitializer(TargetHistoryService targetHistoryService) {
        this.targetHistoryService = targetHistoryService;
    }

    @Override
    public void run(ApplicationArguments args) {
        targetHistoryService.initializeSchema();
    }
}
