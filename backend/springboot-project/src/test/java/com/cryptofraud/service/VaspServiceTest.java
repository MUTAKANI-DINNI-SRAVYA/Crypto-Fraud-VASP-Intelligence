package com.cryptofraud.service;

import com.cryptofraud.model.LastTraceablePoint;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.model.VaspInteractionResult;
import com.cryptofraud.model.VaspReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VaspServiceTest {

    private VaspService vaspService;

    @BeforeEach
    public void setUp() {
        vaspService = new VaspService();
        vaspService.init(); // Loads mock VASP registry
    }

    @Test
    @DisplayName("Scenario 1: Wallet -> Wallet -> Mock VASP interaction and Last Traceable Point")
    public void testWalletToWalletToMockVasp() {
        List<Transaction> txs = Arrays.asList(
            new Transaction("0x1", "0xWALLET_START", "0xWALLET_INTERMEDIATE", 2.0, "ETH", "2026-09-02T10:00:00Z"),
            new Transaction("0x2", "0xWALLET_INTERMEDIATE", "0xVASP888888888888888888888888888888888888", 1.95, "ETH", "2026-09-02T10:15:00Z")
        );

        VaspInteractionResult interaction = vaspService.checkVaspInteraction(txs);
        assertTrue(interaction.isVaspInteraction(), "Should detect interaction with mock VASP");
        assertEquals("0xVASP888888888888888888888888888888888888", interaction.getAddress());
        assertEquals("Mock reference dataset", interaction.getSource());

        LastTraceablePoint lastPoint = vaspService.determineLastTraceablePoint(txs, "0xWALLET_START");
        assertNotNull(lastPoint);
        assertEquals("0xVASP888888888888888888888888888888888888", lastPoint.getAddress());
        assertEquals("VASP-associated address", lastPoint.getType());
        assertTrue(lastPoint.isOffChainRequired(), "Off-chain record flag should be true for VASP endpoint");
        assertEquals("Further lawful off-chain records may be required.", lastPoint.getMessage());
    }

    @Test
    @DisplayName("Scenario 2: Wallet -> Wallet with no VASP interaction")
    public void testWalletToWalletNoVasp() {
        List<Transaction> txs = Arrays.asList(
            new Transaction("0x1", "0xWALLET_A", "0xWALLET_B", 5.0, "ETH", "2026-09-02T10:00:00Z"),
            new Transaction("0x2", "0xWALLET_B", "0xWALLET_C", 4.9, "ETH", "2026-09-02T10:20:00Z")
        );

        VaspInteractionResult interaction = vaspService.checkVaspInteraction(txs);
        assertFalse(interaction.isVaspInteraction(), "Should NOT detect VASP interaction for unlisted wallets");

        LastTraceablePoint lastPoint = vaspService.determineLastTraceablePoint(txs, "0xWALLET_A");
        assertNotNull(lastPoint);
        assertEquals("0xWALLET_C", lastPoint.getAddress());
        assertEquals("Unidentified Wallet", lastPoint.getType());
        assertFalse(lastPoint.isOffChainRequired(), "Off-chain record flag should be false for unlisted wallet endpoint");
        assertEquals("On-chain trail reaches an unlisted wallet address.", lastPoint.getMessage());
    }

    @Test
    @DisplayName("Scenario 3: Multiple transactions with UNORDERED input list")
    public void testUnorderedTransactionsInput() {
        // Transactions provided out of chronological order
        List<Transaction> unorderedTxs = Arrays.asList(
            new Transaction("0x3", "0xINTERMEDIATE_2", "0xVASP777777777777777777777777777777777777", 1.48, "ETH", "2026-09-02T10:30:00Z"),
            new Transaction("0x1", "0xORIGIN_WALLET", "0xINTERMEDIATE_1", 2.0, "ETH", "2026-09-02T10:00:00Z"),
            new Transaction("0x2", "0xINTERMEDIATE_1", "0xINTERMEDIATE_2", 1.5, "ETH", "2026-09-02T10:15:00Z")
        );

        VaspInteractionResult interaction = vaspService.checkVaspInteraction(unorderedTxs);
        assertTrue(interaction.isVaspInteraction(), "Should detect VASP interaction regardless of list order");

        LastTraceablePoint lastPoint = vaspService.determineLastTraceablePoint(unorderedTxs, "0xORIGIN_WALLET");
        assertNotNull(lastPoint);
        assertEquals("0xVASP777777777777777777777777777777777777", lastPoint.getAddress());
        assertEquals("VASP-associated address", lastPoint.getType());
        assertTrue(lastPoint.isOffChainRequired());
    }

    @Test
    @DisplayName("Scenario 4: Empty and null transaction list")
    public void testEmptyOrNullTransactions() {
        VaspInteractionResult nullInteraction = vaspService.checkVaspInteraction(null);
        assertFalse(nullInteraction.isVaspInteraction());

        VaspInteractionResult emptyInteraction = vaspService.checkVaspInteraction(Collections.emptyList());
        assertFalse(emptyInteraction.isVaspInteraction());

        LastTraceablePoint lastPointNull = vaspService.determineLastTraceablePoint(null, "0xTARGET_WALLET");
        assertNotNull(lastPointNull);
        assertEquals("0xTARGET_WALLET", lastPointNull.getAddress());
        assertEquals("Unidentified Wallet", lastPointNull.getType());

        LastTraceablePoint lastPointEmpty = vaspService.determineLastTraceablePoint(Collections.emptyList(), "0xTARGET_WALLET");
        assertNotNull(lastPointEmpty);
        assertEquals("0xTARGET_WALLET", lastPointEmpty.getAddress());
    }

    @Test
    @DisplayName("Reference VASP data loading")
    public void testReferenceVaspDataLoading() {
        List<VaspReference> vasps = vaspService.getReferenceVasps();
        assertNotNull(vasps);
        assertFalse(vasps.isEmpty(), "Mock VASP reference dataset must contain entries");
        assertTrue(vasps.stream().allMatch(v -> "Mock reference dataset".equals(v.getSource())));
    }
}
