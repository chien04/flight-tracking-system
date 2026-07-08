package com.example.flight.simulator.domain;

public record TrajectoryState(
        long startTimeMillis,
        double phaseOffsetRadians
) {
}
