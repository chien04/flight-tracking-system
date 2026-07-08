package com.example.flight.simulator.service;

import com.example.flight.common.event.TargetUpdateBatchEvent;
import com.example.flight.simulator.domain.SimulatedTarget;
import com.example.flight.simulator.generator.TargetFactory;
import com.example.flight.simulator.generator.TargetUpdateGenerator;
import com.example.flight.simulator.publisher.TargetUpdatePublisher;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SimulationService {

    private static final Logger log = LoggerFactory.getLogger(SimulationService.class);

    private final TargetFactory targetFactory;
    private final TargetUpdateGenerator targetUpdateGenerator;
    private final TargetUpdatePublisher targetUpdatePublisher;
    private final AtomicBoolean tickRunning = new AtomicBoolean(false);
    private List<SimulatedTarget> targets = List.of();

    public SimulationService(
            TargetFactory targetFactory,
            TargetUpdateGenerator targetUpdateGenerator,
            TargetUpdatePublisher targetUpdatePublisher
    ) {
        this.targetFactory = targetFactory;
        this.targetUpdateGenerator = targetUpdateGenerator;
        this.targetUpdatePublisher = targetUpdatePublisher;
    }

    @PostConstruct
    void initializeTargets() {
        targets = targetFactory.createTargets();
        log.info("Initialized {} simulated targets", targets.size());
    }

    public void tick() {
        if (!tickRunning.compareAndSet(false, true)) {
            log.warn("Previous simulation tick is still running; skipping this tick");
            return;
        }

        long startedAt = System.currentTimeMillis();
        try {
            TargetUpdateBatchEvent batchEvent = targetUpdateGenerator.generate(targets, startedAt);
            targetUpdatePublisher.publish(batchEvent);
            long durationMs = System.currentTimeMillis() - startedAt;
            log.info("Published simulation batch: targets={}, durationMs={}", batchEvent.targets().size(), durationMs);
        } finally {
            tickRunning.set(false);
        }
    }
}
