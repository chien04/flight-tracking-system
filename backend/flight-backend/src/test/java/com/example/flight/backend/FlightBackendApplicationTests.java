package com.example.flight.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "flight.kafka-consumer-enabled=false",
        "flight.history-enabled=false"
})
class FlightBackendApplicationTests {

    @Test
    void contextLoads() {
    }
}
