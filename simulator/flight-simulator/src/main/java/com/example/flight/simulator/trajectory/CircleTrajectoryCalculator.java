package com.example.flight.simulator.trajectory;

import com.example.flight.common.dto.PositionDto;
import com.example.flight.common.enums.TrajectoryType;
import com.example.flight.common.event.TargetUpdateEvent;
import com.example.flight.simulator.domain.SimulatedTarget;
import com.example.flight.simulator.util.GeoUtil;
import org.springframework.stereotype.Component;

@Component
public class CircleTrajectoryCalculator implements TrajectoryCalculator {

    @Override
    public TrajectoryType supports() {
        return TrajectoryType.CIRCLE;
    }

    @Override
    public TargetUpdateEvent calculate(SimulatedTarget target, long currentTimeMillis) {
        double elapsedSeconds = elapsedSeconds(target, currentTimeMillis);
        double angularSpeed = target.speedMetersPerSecond() / target.radiusMeters();
        double angle = target.state().phaseOffsetRadians() + angularSpeed * elapsedSeconds;
        double northMeters = Math.cos(angle) * target.radiusMeters();
        double eastMeters = Math.sin(angle) * target.radiusMeters();
        PositionDto position = GeoUtil.offset(target.origin(), northMeters, eastMeters, target.origin().altitude());

        return toEvent(target, position, currentTimeMillis);
    }

    private static double elapsedSeconds(SimulatedTarget target, long currentTimeMillis) {
        return Math.max(0, currentTimeMillis - target.state().startTimeMillis()) / 1000.0;
    }

    private static TargetUpdateEvent toEvent(SimulatedTarget target, PositionDto position, long timestamp) {
        return new TargetUpdateEvent(
                target.targetId(),
                position.latitude(),
                position.longitude(),
                position.altitude(),
                target.classification(),
                timestamp
        );
    }
}
