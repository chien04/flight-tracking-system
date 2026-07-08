package com.example.flight.simulator.trajectory;

import com.example.flight.common.enums.TrajectoryType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TrajectoryCalculatorFactory {

    private final Map<TrajectoryType, TrajectoryCalculator> calculators;

    public TrajectoryCalculatorFactory(List<TrajectoryCalculator> calculators) {
        this.calculators = new EnumMap<>(TrajectoryType.class);
        for (TrajectoryCalculator calculator : calculators) {
            this.calculators.put(calculator.supports(), calculator);
        }
    }

    public TrajectoryCalculator getCalculator(TrajectoryType trajectoryType) {
        TrajectoryCalculator calculator = calculators.get(trajectoryType);
        if (calculator == null) {
            throw new IllegalArgumentException("Unsupported trajectory type: " + trajectoryType);
        }
        return calculator;
    }
}
