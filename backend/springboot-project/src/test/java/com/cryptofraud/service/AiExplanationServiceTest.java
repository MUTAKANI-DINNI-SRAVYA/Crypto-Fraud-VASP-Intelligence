package com.cryptofraud.service;

import com.cryptofraud.model.LastTraceablePoint;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.model.VaspInteractionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AiExplanationServiceTest {

    private AiExplanationService aiService;

    @BeforeEach
    public void setUp() {
        aiService = new AiExplanationService();
    }

    @Test
    @DisplayName("Scenario 5: AI disabled mode (app.ai.enabled=false) generates deterministic fallback")
    public void testAiDisabledMode() {
        aiService.setAiEnabled(false);

        VaspInteractionResult vaspRes = new VaspInteractionResult(true, "ApexExchange (Fictional Demo VASP)", "Exchange", "Demo Island", "0xVASP8888");
        LastTraceablePoint lastPoint = new LastTraceablePoint("0xVASP8888", "VASP-associated address", "Deposit point", true, "Further lawful off-chain records may be required.");

        String explanation = aiService.generateInvestigationExplanation(
            "0xTARGET_WALLET",
            Collections.emptyList(),
            Arrays.asList("Suspicious Fund Splitting"),
            85,
            vaspRes,
            lastPoint
        );

        assertNotNull(explanation);
        assertFalse(explanation.isEmpty());
        assertTrue(explanation.contains("Funds interacted with a VASP-associated address"));
        assertTrue(explanation.contains("Further lawful off-chain records may be required."));
        assertFalse(explanation.toLowerCase().contains("criminal organization"), "Must NEVER accuse VASP of criminality");
    }

    @Test
    @DisplayName("Scenario 6: AI failure / unavailable fallback mode")
    public void testAiFailureFallback() {
        aiService.setAiEnabled(true);
        aiService.setApiKey("invalid-fake-api-key"); // Will cause HTTP connection timeout/error or failure

        VaspInteractionResult vaspRes = new VaspInteractionResult(false, null, null, null, null);
        LastTraceablePoint lastPoint = new LastTraceablePoint("0xWALLET_END", "Unidentified Wallet", "Terminal node", false, "On-chain trail reaches an unlisted wallet address.");

        String explanation = aiService.generateInvestigationExplanation(
            "0xTARGET_WALLET",
            Collections.emptyList(),
            Collections.emptyList(),
            40,
            vaspRes,
            lastPoint
        );

        assertNotNull(explanation);
        assertTrue(explanation.contains("Further lawful off-chain records may be required."));
        assertFalse(explanation.toLowerCase().contains("criminal organization"));
    }

    @Test
    @DisplayName("Safety phrasing enforcement: No accusations of VASP criminality allowed")
    public void testSanitizationAndSafetyRules() {
        aiService.setAiEnabled(false);

        VaspInteractionResult vaspRes = new VaspInteractionResult(true, "Test VASP", "Exchange", "India", "0xVASP001");
        LastTraceablePoint lastPoint = new LastTraceablePoint("0xVASP001", "VASP-associated address", "Endpoint", true, "Further lawful off-chain records may be required.");

        String summary = aiService.generateFallbackExplanation(
            "0xWALLET",
            Collections.emptyList(),
            List.of("Rapid Relayering"),
            75,
            vaspRes,
            lastPoint
        );

        assertTrue(summary.contains("Funds interacted with a VASP-associated address"));
        assertTrue(summary.contains("Further lawful off-chain records may be required."));
        assertFalse(summary.toLowerCase().contains("criminal organization"));
    }
}
