package com.example.flight.simulator.generator;

import com.example.flight.common.dto.PositionDto;
import com.example.flight.common.enums.TargetClassification;
import com.example.flight.common.enums.TrajectoryType;
import com.example.flight.common.util.TargetIdUtil;
import com.example.flight.simulator.config.SimulatorProperties;
import com.example.flight.simulator.domain.SimulatedTarget;
import com.example.flight.simulator.domain.TrajectoryState;
import com.example.flight.simulator.domain.Waypoint;
import com.example.flight.simulator.util.GeoUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import org.springframework.stereotype.Component;

@Component
public class TargetFactory {

    private static final long TARGET_RANDOM_SEED = 0x5EED_2026_0709L;
    private static final long TARGET_RANDOM_STEP = 0x9E37_79B9_7F4A_7C15L;

    private final SimulatorProperties properties;

    public TargetFactory(SimulatorProperties properties) {
        this.properties = properties;
    }

    public List<SimulatedTarget> createTargets() {
        validateTargetCount(properties.getTargetCount());

        long startTimeMillis = System.currentTimeMillis();
        List<SimulatedTarget> targets = new ArrayList<>(properties.getTargetCount());
        for (int index = 0; index < properties.getTargetCount(); index++) {
            targets.add(createTarget(index, startTimeMillis));
        }
        return List.copyOf(targets);
    }

    private SimulatedTarget createTarget(int index, long startTimeMillis) {
        SplittableRandom random = randomFor(index);
        PositionDto origin = originFor(random);
        double radiusMeters = 800 + random.nextDouble(0, 1_600);
        double speedMetersPerSecond = 160 + random.nextDouble(0, 140);
        double headingDegrees = random.nextDouble(0, 360);
        TrajectoryType trajectoryType = randomValue(TrajectoryType.values(), random);
        TargetClassification classification = randomValue(TargetClassification.values(), random);

        return new SimulatedTarget(
                TargetIdUtil.format(index),
                classification,
                trajectoryType,
                origin,
                radiusMeters,
                speedMetersPerSecond,
                headingDegrees,
                waypointsFor(origin, radiusMeters, properties.getDefaultAltitude()),
                new TrajectoryState(startTimeMillis, random.nextDouble(0, Math.PI * 2))
        );
    }

    private static SplittableRandom randomFor(int index) {
        return new SplittableRandom(TARGET_RANDOM_SEED + TARGET_RANDOM_STEP * index);
    }

    private PositionDto originFor(SplittableRandom random) {
        double radiusRatio = Math.sqrt(random.nextDouble());
        double angle = random.nextDouble(0, Math.PI * 2);
        double latitudeOffset = Math.cos(angle) * radiusRatio * properties.getTargetLatitudeSpanDegrees() / 2;
        double longitudeOffset = Math.sin(angle) * radiusRatio * properties.getTargetLongitudeSpanDegrees() / 2;

        return new PositionDto(
                properties.getCenterLatitude() + latitudeOffset,
                properties.getCenterLongitude() + longitudeOffset,
                properties.getDefaultAltitude()
        );
    }

    private static <T> T randomValue(T[] values, SplittableRandom random) {
        return values[random.nextInt(values.length)];
    }

    private static List<Waypoint> waypointsFor(PositionDto origin, double radiusMeters, double altitude) {
        PositionDto north = GeoUtil.offset(origin, radiusMeters, 0, altitude);
        PositionDto east = GeoUtil.offset(origin, 0, radiusMeters, altitude + 250);
        PositionDto south = GeoUtil.offset(origin, -radiusMeters, 0, altitude + 100);
        PositionDto west = GeoUtil.offset(origin, 0, -radiusMeters, altitude + 200);

        return List.of(
                new Waypoint(north.latitude(), north.longitude(), north.altitude()),
                new Waypoint(east.latitude(), east.longitude(), east.altitude()),
                new Waypoint(south.latitude(), south.longitude(), south.altitude()),
                new Waypoint(west.latitude(), west.longitude(), west.altitude())
        );
    }

    private static void validateTargetCount(int targetCount) {
        if (targetCount < 1 || targetCount > TargetIdUtil.MAX_TARGET_ID + 1) {
            throw new IllegalArgumentException("Target count must be between 1 and 10000");
        }
    }
}
