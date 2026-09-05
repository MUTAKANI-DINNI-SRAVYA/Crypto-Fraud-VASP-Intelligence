package com.cryptofraud.service.graph;

import com.cryptofraud.model.FundFlowGraph;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.service.VaspService;
import com.cryptofraud.service.blockchain.BlockchainService;
import com.cryptofraud.service.blockchain.EtherscanClient;
import com.cryptofraud.service.blockchain.MockTransactionLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphServiceTest {

    private GraphService graphService;
    private BlockchainService blockchainService;
    private VaspService vaspService;
    private MockTransactionLoader mockTransactionLoader;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        mockTransactionLoader = new MockTransactionLoader(objectMapper);
        mockTransactionLoader.init();

        EtherscanClient etherscanClient = new EtherscanClient(objectMapper);
        blockchainService = new BlockchainService(etherscanClient, mockTransactionLoader);
        blockchainService.setMockEnabled(true);

        vaspService = new VaspService();
        vaspService.init();

        graphService = new GraphService(blockchainService, vaspService);
    }

    @Test
    @DisplayName("Build graph transforms transactions into Cytoscape nodes and edges")
    void testBuildGraphFromTransactions() {
        List<Transaction> txs = Arrays.asList(
                new Transaction("0x1", "0xVIC1111111111111111111111111111111111111", "0xSCAM999999999999999999999999999999999999", 5.0, "ETH", "2026-09-02T10:00:00Z"),
                new Transaction("0x2", "0xSCAM999999999999999999999999999999999999", "0xVASP888888888888888888888888888888888888", 2.0, "ETH", "2026-09-02T10:05:00Z")
        );

        FundFlowGraph graph = graphService.buildGraphFromTransactions("0xSCAM999999999999999999999999999999999999", txs);

        assertNotNull(graph);
        assertEquals("0xSCAM999999999999999999999999999999999999", graph.getTargetAddress());
        assertEquals(3, graph.getNodes().size(), "Should have 3 unique nodes (VIC, SCAM, VASP)");
        assertEquals(2, graph.getEdges().size(), "Should have 2 edges");

        // Check VASP node details
        FundFlowGraph.NodeWrapper vaspNode = graph.getNodes().stream()
                .filter(n -> n.getData().getId().equalsIgnoreCase("0xVASP888888888888888888888888888888888888"))
                .findFirst()
                .orElse(null);

        assertNotNull(vaspNode, "VASP node should be present in graph");
        assertEquals("vasp", vaspNode.getData().getType());
        assertTrue(vaspNode.getData().isLastTraceablePoint(), "VASP should be marked as Last Traceable Point");
    }

    @Test
    @DisplayName("Build graph for demo scam wallet returns full multi-hop chain")
    void testBuildGraphForDemoScamWallet() {
        String scamWallet = "0xSCAM999999999999999999999999999999999999";
        FundFlowGraph graph = graphService.buildGraph(scamWallet, 3);

        assertNotNull(graph);
        assertFalse(graph.getNodes().isEmpty(), "Graph should contain nodes");
        assertEquals(9, graph.getEdges().size(), "Full 9-hop demo chain should be represented as edges");

        // Verify that terminal VASP node is present
        boolean hasVasp = graph.getNodes().stream()
                .anyMatch(n -> "vasp".equals(n.getData().getType()) && n.getData().isLastTraceablePoint());
        assertTrue(hasVasp, "Graph should include terminal VASP Last Traceable Point");
    }
}
