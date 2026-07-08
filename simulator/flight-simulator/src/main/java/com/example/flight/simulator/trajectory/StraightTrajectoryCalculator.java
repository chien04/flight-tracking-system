package com.example.flight.simulator.trajectory;

import com.example.flight.common.dto.PositionDto;
import com.example.flight.common.enums.TrajectoryType;
import com.example.flight.common.event.TargetUpdateEvent;
import com.example.flight.simulator.domain.SimulatedTarget;
import com.example.flight.simulator.util.GeoUtil;
import org.springframework.stereotype.Component;

@Component
public class StraightTrajectoryCalculator implements TrajectoryCalculator {

    @Override
    public TrajectoryType supports() {
        return TrajectoryType.STRAIGHT;
    }

    @Override
    public TargetUpdateEvent calculate(SimulatedTarget target, long currentTimeMillis) {
        double elapsedSeconds = Math.max(0, currentTimeMillis - target.state().startTimeMillis()) / 1000.0;
        double segmentMeters = target.radiusMeters() * 4;
        double cycleMeters = (elapsedSeconds * target.speedMetersPerSecond()) % (segmentMeters * 2);
        double signedDistance = cycleMeters <= segmentMeters ? cycleMeters : (segmentMeters * 2) - cycleMeters;
        signedDistance -= segmentMeters / 2;

        double headingRadians = Math.toRadians(target.headingDegrees());
        double northMeters = Math.cos(headingRadians) * signedDistance;
        double eastMeters = Math.sin(headingRadians) * signedDistance;
        PositionDto position = GeoUtil.offset(target.origin(), northMeters, eastMeters, target.origin().altitude());

        return new TargetUpdateEvent(
                target.targetId(),
                position.latitude(),
                position.longitude(),
                position.altitude(),
                target.classification(),
                currentTimeMillis
        );
    }
}
