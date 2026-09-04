package com.cryptofraud.service.risk;

import com.cryptofraud.dto.RiskAnalysisRequest;
import com.cryptofraud.model.RiskResult;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.service.risk.rules.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RiskServiceTest {

    private RiskService riskService;
    private static final String TARGET_WALLET = "0xAAA0000000000000000000000000000000000001";
    private static final String FLAGGED_WALLET = "0xSCAM999999999999999999999999999999999999";
    private static final String VASP_WALLET = "0xVASP888888888888888888888888888888888888";

    @BeforeEach
    void setUp() {
        List<RiskRule> rules = Arrays.asList(
                new FundSplittingRule(3600),
                new RapidMovementRule(1800),
                new MultiHopRule(),
                new FlaggedAddressRule(),
                new UnusualAmountRule(),
                new VaspInteractionRule()
        );

        riskService = new RiskServiceImpl(
                rules,
                FLAGGED_WALLET + ",0xFLAGGED_SCAMPER_001",
                VASP_WALLET + ",0xVASP777777777777777777777777777777777777"
        );
    }

    @Test
    @DisplayName("Empty transaction list results in 0 score and LOW risk")
    void testEmptyTransactionList() {
        RiskResult result = riskService.analyzeRisk(TARGET_WALLET, Collections.emptyList());

        assertNotNull(result);
        assertEquals(TARGET_WALLET, result.getWallet());
        assertEquals(0, result.getRiskScore());
        assertEquals("LOW", result.getRiskLevel());
        assertTrue(result.getPatterns().isEmpty());
        assertTrue(result.getTriggeredRules().isEmpty());
        assertNotNull(result.getEvaluatedAt());
    }

    @Test
    @DisplayName("Null or blank wallet throws IllegalArgumentException")
    void testMissingWallet() {
        assertThrows(IllegalArgumentException.class, () -> riskService.analyzeRisk(null, Collections.emptyList()));
        assertThrows(IllegalArgumentException.class, () -> riskService.analyzeRisk("   ", Collections.emptyList()));
    }

    @Test
    @DisplayName("Rule 1: Fund Splitting (+20) detected when dispersing funds to multiple recipients")
    void testFundSplittingRule() {
        List<Transaction> txs = Arrays.asList(
                new Transaction("0x1", TARGET_WALLET, "0xBBB", 1.0, "ETH", "2026-09-02T10:00:00Z"),
                new Transaction("0x2", TARGET_WALLET, "0xCCC", 1.0, "ETH", "2026-09-02T10:05:00Z")
        );

        RiskResult result = riskService.analyzeRisk(TARGET_WALLET, txs);

        assertTrue(result.getPatterns().contains("Fund Splitting"));
        assertEquals(20, result.getRiskScore());
        assertEquals("LOW", result.getRiskLevel());
    }

    @Test
    @DisplayName("Rule 2: Rapid Movement (+20) detected when transfer occurs shortly after deposit")
    void testRapidMovementRule() {
        List<Transaction> txs = Arrays.asList(
                new Transaction("0x1", "0xSRC", TARGET_WALLET, 2.0, "ETH", "2026-09-02T10:00:00Z"),
                new Transaction("0x2", TARGET_WALLET, "0xDST", 1.95, "ETH", "2026-09-02T10:08:00Z") // 8 min delta
        );

        RiskResult result = riskService.analyzeRisk(TARGET_WALLET, txs);

        assertTrue(result.getPatterns().contains("Rapid Movement"));
        assertEquals(35, result.getRiskScore()); // Rapid Movement (+20) + MultiHop (+15)
        assertEquals("MEDIUM", result.getRiskLevel());
    }

    @Test
    @DisplayName("Rule 3: Multiple Hops (+15) detected in chained transactions")
    void testMultiHopRule() {
        List<Transaction> txs = Arrays.asList(
                new Transaction("0x1", TARGET_WALLET, "0xINTERMEDIATE", 1.0, "ETH", "2026-09-02T08:00:00Z"),
                new Transaction("0x2", "0xINTERMEDIATE", "0xFINAL", 0.98, "ETH", "2026-09-02T12:00:00Z")
        );

        RiskResult result = riskService.analyzeRisk(TARGET_WALLET, txs);

        assertTrue(result.getPatterns().contains("Multiple Hops"));
        assertEquals(15, result.getRiskScore());
        assertEquals("LOW", result.getRiskLevel());
    }

    @Test
    @DisplayName("Rule 4: Flagged Address Interaction (+20) detected from configured dataset")
    void testFlaggedAddressRuleConfigured() {
        List<Transaction> txs = Collections.singletonList(
                new Transaction("0x1", FLAGGED_WALLET, TARGET_WALLET, 3.0, "ETH", "2026-09-02T10:00:00Z")
        );

        RiskResult result = riskService.analyzeRisk(TARGET_WALLET, txs);

        assertTrue(result.getPatterns().contains("Flagged Address Interaction"));
        assertEquals(20, result.getRiskScore());
        assertEquals("LOW", result.getRiskLevel());
    }

    @Test
    @DisplayName("Rule 4: Flagged Address Interaction (+20) detected from dynamic request list")
    void testFlaggedAddressRuleDynamicRequest() {
        String dynamicFlagged = "0xCUSTOM_SCAM_ADDRESS";
        List<Transaction> txs = Collections.singletonList(
                new Transaction("0x1", TARGET_WALLET, dynamicFlagged, 1.0, "ETH", "2026-09-02T10:00:00Z")
        );

        RiskAnalysisRequest request = new RiskAnalysisRequest(TARGET_WALLET, txs);
        request.setFlaggedAddresses(Collections.singletonList(dynamicFlagged));

        RiskResult result = riskService.analyzeRisk(request);

        assertTrue(result.getPatterns().contains("Flagged Address Interaction"));
        assertEquals(20, result.getRiskScore());
    }

    @Test
    @DisplayName("Rule 5: Unusual Amount (+10) detected for statistical outlier")
    void testUnusualAmountRule() {
        List<Transaction> txs = Arrays.asList(
                new Transaction("0x1", "0xOTHER1", "0xOTHER2", 0.1, "ETH", "2026-09-02T10:00:00Z"),
                new Transaction("0x2", "0xOTHER2", "0xOTHER3", 0.15, "ETH", "2026-09-02T10:01:00Z"),
                new Transaction("0x3", "0xSRC", TARGET_WALLET, 50.0, "ETH", "2026-09-02T10:02:00Z") // Outlier
        );

        RiskResult result = riskService.analyzeRisk(TARGET_WALLET, txs);

        assertTrue(result.getPatterns().contains("Unusual Amount"));
        assertEquals(10, result.getRiskScore());
        assertEquals("LOW", result.getRiskLevel());
    }

    @Test
    @DisplayName("Rule 6: VASP Interaction (+15) detected via explicit signal or address match")
    void testVaspInteractionRule() {
        // Via explicit flag
        RiskAnalysisRequest request1 = new RiskAnalysisRequest(TARGET_WALLET, Collections.emptyList(), true);
        RiskResult result1 = riskService.analyzeRisk(request1);
        // Empty transactions returns 0
        assertEquals(0, result1.getRiskScore());

        // With transactions and explicit flag
        List<Transaction> normalTx = Collections.singletonList(
                new Transaction("0x1", TARGET_WALLET, "0xNORMAL", 1.0, "ETH", "2026-09-02T10:00:00Z")
        );
        RiskAnalysisRequest request2 = new RiskAnalysisRequest(TARGET_WALLET, normalTx, true);
        RiskResult result2 = riskService.analyzeRisk(request2);
        assertTrue(result2.getPatterns().contains("VASP Interaction"));
        assertEquals(15, result2.getRiskScore());

        // Via VASP address interaction
        List<Transaction> vaspTx = Collections.singletonList(
                new Transaction("0x2", TARGET_WALLET, VASP_WALLET, 1.0, "ETH", "2026-09-02T10:00:00Z")
        );
        RiskResult result3 = riskService.analyzeRisk(TARGET_WALLET, vaspTx);
        assertTrue(result3.getPatterns().contains("VASP Interaction"));
        assertEquals(15, result3.getRiskScore());
    }

    @Test
    @DisplayName("Score capping: Combined rules never exceed 100 and risk level is CRITICAL")
    void testCombinedRulesAndMaxScoreCap() {
        // Trigger all 6 rules:
        // 1. Fund Splitting (+20)
        // 2. Rapid Movement (+20)
        // 3. Multi Hop (+15)
        // 4. Flagged Address (+20)
        // 5. Unusual Amount (+10)
        // 6. VASP Interaction (+15)
        // Total sum = 100
        List<Transaction> txs = Arrays.asList(
                // Flagged source deposit: 50.0 ETH (Unusual amount) at 10:00
                new Transaction("0x1", FLAGGED_WALLET, TARGET_WALLET, 50.0, "ETH", "2026-09-02T10:00:00Z"),
                // Baseline txs for unusual amount calculation
                new Transaction("0x2", "0xOTHER1", "0xOTHER2", 0.5, "ETH", "2026-09-02T10:01:00Z"),
                new Transaction("0x3", "0xOTHER3", "0xOTHER4", 0.5, "ETH", "2026-09-02T10:02:00Z"),
                // Split 1 to VASP wallet within 5 mins (Rapid movement + Fund splitting + VASP interaction + MultiHop)
                new Transaction("0x4", TARGET_WALLET, VASP_WALLET, 1.0, "ETH", "2026-09-02T10:05:00Z"),
                // Split 2 to another address within 6 mins (Fund splitting)
                new Transaction("0x5", TARGET_WALLET, "0xRECEIVER_B", 1.0, "ETH", "2026-09-02T10:06:00Z")
        );

        RiskResult result = riskService.analyzeRisk(TARGET_WALLET, txs);

        assertTrue(result.getPatterns().contains("Fund Splitting"));
        assertTrue(result.getPatterns().contains("Rapid Movement"));
        assertTrue(result.getPatterns().contains("Multiple Hops"));
        assertTrue(result.getPatterns().contains("Flagged Address Interaction"));
        assertTrue(result.getPatterns().contains("Unusual Amount"));
        assertTrue(result.getPatterns().contains("VASP Interaction"));

        assertEquals(100, result.getRiskScore());
        assertTrue(result.getRiskScore() <= 100);
        assertTrue(result.getRiskScore() >= 0);
        assertEquals("CRITICAL", result.getRiskLevel());
    }

    @Test
    @DisplayName("Risk levels matrix calculation matches exact boundaries")
    void testRiskLevelMatrix() {
        assertEquals("LOW", riskService.determineRiskLevel(0));
        assertEquals("LOW", riskService.determineRiskLevel(30));
        assertEquals("MEDIUM", riskService.determineRiskLevel(31));
        assertEquals("MEDIUM", riskService.determineRiskLevel(60));
        assertEquals("HIGH", riskService.determineRiskLevel(61));
        assertEquals("HIGH", riskService.determineRiskLevel(80));
        assertEquals("CRITICAL", riskService.determineRiskLevel(81));
        assertEquals("CRITICAL", riskService.determineRiskLevel(100));
    }

    @Test
    @DisplayName("Malformed transactions and invalid timestamps are safely handled")
    void testMalformedTransactionsAndInvalidTimestamps() {
        List<Transaction> dirtyTxs = new ArrayList<>();
        dirtyTxs.add(null); // null transaction
        dirtyTxs.add(new Transaction(null, null, TARGET_WALLET, 1.0, "ETH", "not-a-timestamp")); // missing from & bad timestamp
        dirtyTxs.add(new Transaction("0x1", TARGET_WALLET, null, 1.0, "ETH", null)); // missing to
        dirtyTxs.add(new Transaction("0x2", TARGET_WALLET, "0xBBB", -5.0, "ETH", "2026-09-02T10:00:00Z")); // negative amount
        dirtyTxs.add(new Transaction("0x3", "0xSRC", TARGET_WALLET, 1.0, "ETH", "garbage_date")); // unparseable date
        dirtyTxs.add(new Transaction("0x4", TARGET_WALLET, "0xVALID_DEST", 1.0, "ETH", "1725273000")); // valid epoch date

        assertDoesNotThrow(() -> {
            RiskResult result = riskService.analyzeRisk(TARGET_WALLET, dirtyTxs);
            assertNotNull(result);
            assertTrue(result.getRiskScore() >= 0);
            assertTrue(result.getRiskScore() <= 100);
        });
    }
}
