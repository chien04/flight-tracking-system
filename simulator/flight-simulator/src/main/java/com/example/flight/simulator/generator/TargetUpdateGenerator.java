package com.example.flight.simulator.generator;

import com.example.flight.common.event.TargetUpdateBatchEvent;
import com.example.flight.common.event.TargetUpdateEvent;
import com.example.flight.simulator.domain.SimulatedTarget;
import com.example.flight.simulator.trajectory.TrajectoryCalculatorFactory;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TargetUpdateGenerator {

    private final TrajectoryCalculatorFactory trajectoryCalculatorFactory;

    public TargetUpdateGenerator(TrajectoryCalculatorFactory trajectoryCalculatorFactory) {
        this.trajectoryCalculatorFactory = trajectoryCalculatorFactory;
    }

    public TargetUpdateBatchEvent generate(List<SimulatedTarget> targets, long currentTimeMillis) {
        List<TargetUpdateEvent> updates = targets.stream()
                .map(target -> trajectoryCalculatorFactory
                        .getCalculator(target.trajectoryType())
                        .calculate(target, currentTimeMillis))
                .toList();

        return new TargetUpdateBatchEvent(currentTimeMillis, updates);
    }
}
