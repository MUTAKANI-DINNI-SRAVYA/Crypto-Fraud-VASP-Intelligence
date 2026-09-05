package com.cryptofraud.controller;

import com.cryptofraud.model.InvestigationRequest;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.service.AiExplanationService;
import com.cryptofraud.service.InvestigationService;
import com.cryptofraud.service.VaspService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({InvestigationController.class, VaspController.class})
public class InvestigationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private InvestigationService investigationService;

    @SpyBean
    private VaspService vaspService;

    @SpyBean
    private AiExplanationService aiExplanationService;

    @Test
    @DisplayName("GET /api/vasp/reference returns list of mock VASP reference data")
    public void testGetVaspReference() throws Exception {
        mockMvc.perform(get("/api/vasp/reference"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].source").value("Mock reference dataset"));
    }

    @Test
    @DisplayName("POST /api/investigation/analyze generates complete investigation report")
    public void testAnalyzeInvestigation() throws Exception {
        List<Transaction> txs = Arrays.asList(
            new Transaction("0x1", "0xSCAM999999999999999999999999999999999999", "0xAAAA111111111111111111111111111111111111", 2.0, "ETH", "2026-09-02T10:05:00Z"),
            new Transaction("0x2", "0xAAAA111111111111111111111111111111111111", "0xVASP888888888888888888888888888888888888", 1.95, "ETH", "2026-09-02T10:22:00Z")
        );

        InvestigationRequest req = new InvestigationRequest(
            "0xSCAM999999999999999999999999999999999999",
            txs,
            85,
            "HIGH",
            Arrays.asList("Suspicious Fund Splitting", "Rapid Asset Relayering (< 15m)")
        );

        mockMvc.perform(post("/api/investigation/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.wallet").value("0xSCAM999999999999999999999999999999999999"))
            .andExpect(jsonPath("$.riskScore").value(85))
            .andExpect(jsonPath("$.riskLevel").value("HIGH"))
            .andExpect(jsonPath("$.vaspInteraction.vaspInteraction").value(true))
            .andExpect(jsonPath("$.lastTraceablePoint.address").value("0xVASP888888888888888888888888888888888888"))
            .andExpect(jsonPath("$.lastTraceablePoint.offChainRequired").value(true))
            .andExpect(jsonPath("$.limitations").isArray());
    }
}
