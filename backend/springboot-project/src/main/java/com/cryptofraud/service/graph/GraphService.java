package com.cryptofraud.service.graph;

import com.cryptofraud.model.FundFlowGraph;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.model.VaspReference;
import com.cryptofraud.service.VaspService;
import com.cryptofraud.service.blockchain.BlockchainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service responsible for constructing directed fund flow graphs for Cytoscape.js / React Flow.
 * Owned by Member 4 (Money Flow Graph Module).
 */
@Service
public class GraphService {

    private static final Logger log = LoggerFactory.getLogger(GraphService.class);

    private final BlockchainService blockchainService;
    private final VaspService vaspService;

    public GraphService(BlockchainService blockchainService, VaspService vaspService) {
        this.blockchainService = blockchainService;
        this.vaspService = vaspService;
    }

    /**
     * Builds a FundFlowGraph for the specified target wallet by retrieving its blockchain transaction chain.
     */
    public FundFlowGraph buildGraph(String targetAddress, int maxHops) {
        String cleanAddress = targetAddress != null ? targetAddress.trim() : "0x0000000000000000000000000000000000000000";
        List<Transaction> transactions;
        try {
            transactions = blockchainService.getTransactions(cleanAddress);
        } catch (Exception e) {
            log.warn("Could not retrieve transactions for graph trace: {}", e.getMessage());
            transactions = Collections.emptyList();
        }

        return buildGraphFromTransactions(cleanAddress, transactions);
    }

    /**
     * Transforms an existing list of transactions into Cytoscape-compatible FundFlowGraph nodes and edges.
     */
    public FundFlowGraph buildGraphFromTransactions(String targetAddress, List<Transaction> transactions) {
        FundFlowGraph graph = new FundFlowGraph(targetAddress);
        if (transactions == null || transactions.isEmpty()) {
            return graph;
        }

        Map<String, FundFlowGraph.NodeData> nodeMap = new LinkedHashMap<>();
        List<FundFlowGraph.EdgeWrapper> edgeWrappers = new ArrayList<>();

        int edgeCounter = 1;
        for (Transaction tx : transactions) {
            String from = tx.getFrom();
            String to = tx.getTo();

            if (from != null && !nodeMap.containsKey(from.toLowerCase())) {
                nodeMap.put(from.toLowerCase(), createNodeData(from, targetAddress));
            }
            if (to != null && !nodeMap.containsKey(to.toLowerCase())) {
                nodeMap.put(to.toLowerCase(), createNodeData(to, targetAddress));
            }

            String edgeId = tx.getHash() != null && !tx.getHash().isBlank() ? tx.getHash() : "e" + (edgeCounter++);
            FundFlowGraph.EdgeData edgeData = new FundFlowGraph.EdgeData(
                    edgeId,
                    from,
                    to,
                    tx.getAmount(),
                    tx.getAsset() != null ? tx.getAsset() : "ETH",
                    tx.getTimestamp(),
                    false
            );
            edgeWrappers.add(new FundFlowGraph.EdgeWrapper(edgeData));
        }

        List<FundFlowGraph.NodeWrapper> nodeWrappers = new ArrayList<>();
        for (FundFlowGraph.NodeData nodeData : nodeMap.values()) {
            nodeWrappers.add(new FundFlowGraph.NodeWrapper(nodeData));
        }

        graph.setNodes(nodeWrappers);
        graph.setEdges(edgeWrappers);

        log.info("Constructed FundFlowGraph for {} with {} nodes and {} edges",
                targetAddress, nodeWrappers.size(), edgeWrappers.size());

        return graph;
    }

    private FundFlowGraph.NodeData createNodeData(String address, String targetAddress) {
        String lower = address.toLowerCase();
        VaspReference vasp = vaspService.findVaspByAddress(address);

        String type;
        String label;
        boolean isLastTraceablePoint = false;

        if (vasp != null) {
            type = "vasp";
            label = vasp.getVaspName();
            isLastTraceablePoint = vasp.isLastTraceablePoint();
        } else if (lower.contains("scam")) {
            type = "scam";
            label = "Scam Distributor";
        } else if (lower.contains("vic")) {
            type = "victim";
            label = "Victim Wallet";
        } else if (targetAddress != null && lower.equalsIgnoreCase(targetAddress.toLowerCase())) {
            type = "scam";
            label = "Target Wallet";
        } else {
            type = "hop";
            label = shortenAddress(address);
        }

        return new FundFlowGraph.NodeData(address, label, type, 0.0, isLastTraceablePoint);
    }

    private String shortenAddress(String addr) {
        if (addr == null || addr.length() <= 12) return addr != null ? addr : "";
        return addr.substring(0, 6) + "..." + addr.substring(addr.length() - 4);
    }
}
