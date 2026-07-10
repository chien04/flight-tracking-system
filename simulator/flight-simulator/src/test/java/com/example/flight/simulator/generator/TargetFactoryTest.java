package com.example.flight.simulator.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.flight.common.enums.TargetClassification;
import com.example.flight.common.enums.TrajectoryType;
import com.example.flight.simulator.config.SimulatorProperties;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class TargetFactoryTest {

    @Test
    void createsTenThousandUniqueFourDigitTargets() {
        SimulatorProperties properties = properties(10000);
        TargetFactory targetFactory = new TargetFactory(properties);

        var targets = targetFactory.createTargets();

        assertEquals(10000, targets.size());
        assertEquals(10000, targets.stream().map(target -> target.targetId()).collect(Collectors.toSet()).size());
        assertTrue(targets.stream().allMatch(target -> target.targetId().matches("\\d{4}")));
        assertEquals("0000", targets.getFirst().targetId());
        assertEquals("9999", targets.getLast().targetId());
    }

    @Test
    void rejectsTargetCountOutsideFourDigitIdSpace() {
        TargetFactory targetFactory = new TargetFactory(properties(10001));

        assertThrows(IllegalArgumentException.class, targetFactory::createTargets);
    }

    @Test
    void distributesClassificationsAndTrajectoriesIndependently() {
        TargetFactory targetFactory = new TargetFactory(properties(10000));

        var targets = targetFactory.createTargets();

        assertEquals(
                TargetClassification.values().length,
                targets.stream().map(target -> target.classification()).collect(Collectors.toSet()).size()
        );
        assertEquals(
                TrajectoryType.values().length,
                targets.stream().map(target -> target.trajectoryType()).collect(Collectors.toSet()).size()
        );
        assertEquals(
                TargetClassification.values().length * TrajectoryType.values().length,
                targets.stream()
                        .map(target -> target.classification() + ":" + target.trajectoryType())
                        .collect(Collectors.toSet())
                        .size()
        );
    }

    private static SimulatorProperties properties(int targetCount) {
        SimulatorProperties properties = new SimulatorProperties();
        properties.setTargetCount(targetCount);
        properties.setCenterLatitude(39.8283);
        properties.setCenterLongitude(-98.5795);
        properties.setDefaultAltitude(1000);
        properties.setTargetLatitudeSpanDegrees(24);
        properties.setTargetLongitudeSpanDegrees(58);
        return properties;
    }
}
