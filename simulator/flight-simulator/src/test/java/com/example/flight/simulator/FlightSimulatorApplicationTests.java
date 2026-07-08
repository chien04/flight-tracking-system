package com.example.flight.simulator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "simulator.scheduling-enabled=false",
        "simulator.target-count=10"
})
class FlightSimulatorApplicationTests {

    @Test
    void contextLoads() {
    }
}
