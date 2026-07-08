package com.example.flight.simulator.trajectory;

import com.example.flight.common.dto.PositionDto;
import com.example.flight.common.enums.TrajectoryType;
import com.example.flight.common.event.TargetUpdateEvent;
import com.example.flight.simulator.domain.SimulatedTarget;
import com.example.flight.simulator.domain.Waypoint;
import com.example.flight.simulator.util.GeoUtil;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PolylineTrajectoryCalculator implements TrajectoryCalculator {

    @Override
    public TrajectoryType supports() {
        return TrajectoryType.POLYLINE;
    }

    @Override
    public TargetUpdateEvent calculate(SimulatedTarget target, long currentTimeMillis) {
        List<Waypoint> waypoints = target.waypoints();
        double elapsedSeconds = Math.max(0, currentTimeMillis - target.state().startTimeMillis()) / 1000.0;
        double pathLength = pathLength(waypoints);
        double distance = (elapsedSeconds * target.speedMetersPerSecond()) % pathLength;
        PositionDto position = positionAtDistance(waypoints, distance);

        return new TargetUpdateEvent(
                target.targetId(),
                position.latitude(),
                position.longitude(),
                position.altitude(),
                target.classification(),
                currentTimeMillis
        );
    }

    private static double pathLength(List<Waypoint> waypoints) {
        double length = 0;
        for (int i = 0; i < waypoints.size(); i++) {
            Waypoint current = waypoints.get(i);
            Waypoint next = waypoints.get((i + 1) % waypoints.size());
            length += GeoUtil.distanceMeters(current, next);
        }
        return length;
    }

    private static PositionDto positionAtDistance(List<Waypoint> waypoints, double distance) {
        double remaining = distance;
        for (int i = 0; i < waypoints.size(); i++) {
            Waypoint current = waypoints.get(i);
            Waypoint next = waypoints.get((i + 1) % waypoints.size());
            double segmentLength = GeoUtil.distanceMeters(current, next);
            if (remaining <= segmentLength) {
                return GeoUtil.interpolate(current, next, remaining / segmentLength);
            }
            remaining -= segmentLength;
        }

        Waypoint fallback = waypoints.get(0);
        return new PositionDto(fallback.latitude(), fallback.longitude(), fallback.altitude());
    }
}
