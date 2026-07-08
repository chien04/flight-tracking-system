package com.example.flight.common.dto;

import java.util.List;

public record TargetUpdateBatchDto(
        long timestamp,
        List<TargetUpdateDto> targets
) {

    public TargetUpdateBatchDto {
        targets = List.copyOf(targets);
    }
}
