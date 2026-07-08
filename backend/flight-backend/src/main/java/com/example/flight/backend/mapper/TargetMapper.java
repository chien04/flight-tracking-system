package com.example.flight.backend.mapper;

import com.example.flight.backend.dto.response.TargetCurrentResponse;
import com.example.flight.backend.dto.response.TargetDetailResponse;
import com.example.flight.common.event.TargetUpdateEvent;

public final class TargetMapper {

    private TargetMapper() {
    }

    public static TargetCurrentResponse toCurrentResponse(TargetUpdateEvent event) {
        return new TargetCurrentResponse(
                event.targetId(),
                event.latitude(),
                event.longitude(),
                event.altitude(),
                event.classification(),
                event.timestamp()
        );
    }

    public static TargetDetailResponse toDetailResponse(TargetCurrentResponse current) {
        return new TargetDetailResponse(
                current.targetId(),
                current.latitude(),
                current.longitude(),
                current.altitude(),
                current.classification(),
                current.updatedAt()
        );
    }
}
