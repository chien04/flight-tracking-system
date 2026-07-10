package com.example.flight.backend.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.flight.backend.dto.response.TargetCurrentResponse;
import com.example.flight.backend.dto.response.TargetDetailResponse;
import com.example.flight.backend.dto.response.TargetHistoryPointResponse;
import com.example.flight.backend.exception.GlobalExceptionHandler;
import com.example.flight.backend.exception.TargetNotFoundException;
import com.example.flight.backend.service.TargetHistoryService;
import com.example.flight.backend.service.TargetQueryService;
import com.example.flight.common.enums.TargetClassification;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class TargetQueryControllerTest {

    @Test
    void returnsAllTargets() throws Exception {
        TargetQueryService service = org.mockito.Mockito.mock(TargetQueryService.class);
        TargetHistoryService historyService = org.mockito.Mockito.mock(TargetHistoryService.class);
        TargetQueryController controller = new TargetQueryController(service, historyService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        when(service.findAll()).thenReturn(List.of(current("0001")));

        mockMvc.perform(get("/api/targets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].targetId").value("0001"));
    }

    @Test
    void returnsTargetById() throws Exception {
        TargetQueryService service = org.mockito.Mockito.mock(TargetQueryService.class);
        TargetHistoryService historyService = org.mockito.Mockito.mock(TargetHistoryService.class);
        TargetQueryController controller = new TargetQueryController(service, historyService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        when(service.findById("0001")).thenReturn(detail("0001"));

        mockMvc.perform(get("/api/targets/0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetId").value("0001"));
    }

    @Test
    void returnsNotFoundForMissingTarget() throws Exception {
        TargetQueryService service = org.mockito.Mockito.mock(TargetQueryService.class);
        TargetHistoryService historyService = org.mockito.Mockito.mock(TargetHistoryService.class);
        TargetQueryController controller = new TargetQueryController(service, historyService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        when(service.findById("9999")).thenThrow(new TargetNotFoundException("9999"));

        mockMvc.perform(get("/api/targets/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Target not found: 9999"));
    }

    @Test
    void returnsTargetHistory() throws Exception {
        TargetQueryService service = org.mockito.Mockito.mock(TargetQueryService.class);
        TargetHistoryService historyService = org.mockito.Mockito.mock(TargetHistoryService.class);
        TargetQueryController controller = new TargetQueryController(service, historyService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        when(historyService.findHistory("0001", 1000, 2000, 500))
                .thenReturn(List.of(historyPoint("0001", 1000)));

        mockMvc.perform(get("/api/targets/0001/history")
                        .param("from", "1000")
                        .param("to", "2000")
                        .param("sampleMs", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].targetId").value("0001"))
                .andExpect(jsonPath("$[0].timestamp").value(1000))
                .andExpect(jsonPath("$[0].ingestedAt").value(1100));
    }

    private static TargetCurrentResponse current(String targetId) {
        return new TargetCurrentResponse(targetId, 21.0285, 105.8542, 1000, TargetClassification.UNKNOWN, 123);
    }

    private static TargetDetailResponse detail(String targetId) {
        return new TargetDetailResponse(targetId, 21.0285, 105.8542, 1000, TargetClassification.UNKNOWN, 123);
    }

    private static TargetHistoryPointResponse historyPoint(String targetId, long timestamp) {
        return new TargetHistoryPointResponse(
                targetId,
                21.0285,
                105.8542,
                1000,
                TargetClassification.UNKNOWN,
                timestamp,
                timestamp + 100
        );
    }
}
