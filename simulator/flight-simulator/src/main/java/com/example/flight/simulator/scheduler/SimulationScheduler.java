package com.example.flight.simulator.scheduler;

import com.example.flight.simulator.service.SimulationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "simulator", name = "scheduling-enabled", havingValue = "true", matchIfMissing = true)
public class SimulationScheduler {

    private final SimulationService simulationService;

    public SimulationScheduler(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @Scheduled(fixedRateString = "#{@simulatorProperties.updateIntervalMs}")
    public void publishTick() {
        simulationService.tick();
    }
}
