package com.example.flight.simulator.publisher;

import com.example.flight.common.event.TargetUpdateBatchEvent;

public interface TargetUpdatePublisher {

    void publish(TargetUpdateBatchEvent batchEvent);
}
