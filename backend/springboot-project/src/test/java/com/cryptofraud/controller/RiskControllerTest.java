package com.cryptofraud.controller;

import com.cryptofraud.dto.RiskAnalysisRequest;
import com.cryptofraud.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RiskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/risk/analyze returns 200 with expected JSON fields")
    void testAnalyzeEndpointSuccess() throws Exception {
        RiskAnalysisRequest request = new RiskAnalysisRequest(
                "0xAAA",
                Arrays.asList(
                        new Transaction("0x1", "0xSRC", "0xAAA", 5.0, "ETH", "2026-09-02T10:00:00Z"),
                        new Transaction("0x2", "0xAAA", "0xBBB", 2.5, "ETH", "2026-09-02T10:05:00Z"),
                        new Transaction("0x3", "0xAAA", "0xCCC", 2.4, "ETH", "2026-09-02T10:06:00Z")
                )
        );

        mockMvc.perform(post("/api/risk/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wallet", is("0xAAA")))
                .andExpect(jsonPath("$.riskScore", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.riskScore", lessThanOrEqualTo(100)))
                .andExpect(jsonPath("$.riskLevel", notNullValue()))
                .andExpect(jsonPath("$.patterns", hasItems("Fund Splitting", "Rapid Movement", "Multiple Hops")));
    }

    @Test
    @DisplayName("POST /api/risk/analyze with missing wallet returns 400 Bad Request")
    void testAnalyzeMissingWallet() throws Exception {
        RiskAnalysisRequest request = new RiskAnalysisRequest(
                "",
                Collections.emptyList()
        );

        mockMvc.perform(post("/api/risk/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("Wallet address is required")))
                .andExpect(jsonPath("$.path", is("/api/risk/analyze")));
    }

    @Test
    @DisplayName("POST /api/risk/analyze with empty transaction list returns 200 with LOW risk")
    void testAnalyzeEmptyTransactions() throws Exception {
        RiskAnalysisRequest request = new RiskAnalysisRequest(
                "0xAAA",
                Collections.emptyList()
        );

        mockMvc.perform(post("/api/risk/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wallet", is("0xAAA")))
                .andExpect(jsonPath("$.riskScore", is(0)))
                .andExpect(jsonPath("$.riskLevel", is("LOW")))
                .andExpect(jsonPath("$.patterns", hasSize(0)));
    }
}
