package com.example.flight.simulator.domain;

import com.example.flight.common.dto.PositionDto;
import com.example.flight.common.enums.TargetClassification;
import com.example.flight.common.enums.TrajectoryType;
import java.util.List;

public record SimulatedTarget(
        String targetId,
        TargetClassification classification,
        TrajectoryType trajectoryType,
        PositionDto origin,
        double radiusMeters,
        double speedMetersPerSecond,
        double headingDegrees,
        List<Waypoint> waypoints,
        TrajectoryState state
) {

    public SimulatedTarget {
        waypoints = List.copyOf(waypoints);
    }
}
