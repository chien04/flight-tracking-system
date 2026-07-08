package com.example.flight.simulator.trajectory;

import com.example.flight.common.enums.TrajectoryType;
import com.example.flight.common.event.TargetUpdateEvent;
import com.example.flight.simulator.domain.SimulatedTarget;

public interface TrajectoryCalculator {

    TrajectoryType supports();

    TargetUpdateEvent calculate(SimulatedTarget target, long currentTimeMillis);
}
