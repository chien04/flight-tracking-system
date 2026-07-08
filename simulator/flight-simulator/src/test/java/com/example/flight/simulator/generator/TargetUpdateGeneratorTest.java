package com.example.flight.simulator.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import com.example.flight.simulator.config.SimulatorProperties;
import com.example.flight.simulator.trajectory.CircleTrajectoryCalculator;
import com.example.flight.simulator.trajectory.PolylineTrajectoryCalculator;
import com.example.flight.simulator.trajectory.StraightTrajectoryCalculator;
import com.example.flight.simulator.trajectory.TrajectoryCalculatorFactory;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TargetUpdateGeneratorTest {

    @ParameterizedTest
    @ValueSource(ints = {100, 1000, 10000})
    void generatesOneBatchForAllTargetsWithinTickBudget(int targetCount) {
        SimulatorProperties properties = properties(targetCount);
        TargetFactory targetFactory = new TargetFactory(properties);
        TargetUpdateGenerator generator = new TargetUpdateGenerator(new TrajectoryCalculatorFactory(List.of(
                new CircleTrajectoryCalculator(),
                new StraightTrajectoryCalculator(),
                new PolylineTrajectoryCalculator()
        )));

        var targets = targetFactory.createTargets();

        assertTimeout(Duration.ofMillis(300), () -> {
            var batch = generator.generate(targets, System.currentTimeMillis());
            assertEquals(targetCount, batch.targets().size());
            assertEquals(batch.timestamp(), batch.targets().getFirst().timestamp());
        });
    }

    private static SimulatorProperties properties(int targetCount) {
        SimulatorProperties properties = new SimulatorProperties();
        properties.setTargetCount(targetCount);
        properties.setCenterLatitude(21.0285);
        properties.setCenterLongitude(105.8542);
        properties.setDefaultAltitude(1000);
        return properties;
    }
}
