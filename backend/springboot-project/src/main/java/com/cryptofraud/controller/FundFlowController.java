package com.cryptofraud.controller;

import com.cryptofraud.model.FundFlowGraph;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.service.graph.GraphService;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller providing Cytoscape money flow graph tracing endpoints.
 * Owned by Member 4 (Money Flow Graph Module) and Member 1 (Integration).
 */
@RestController
@RequestMapping("/api/funds")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class FundFlowController {

    private final GraphService graphService;

    public FundFlowController(GraphService graphService) {
        this.graphService = graphService;
    }

    public static class TraceRequest {
        @JsonAlias({"wallet", "targetAddress"})
        private String address;

        private Integer maxHops = 3;
        private List<Transaction> transactions;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Integer getMaxHops() {
            return maxHops;
        }

        public void setMaxHops(Integer maxHops) {
            this.maxHops = maxHops;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<Transaction> transactions) {
            this.transactions = transactions;
        }
    }

    /**
     * POST /api/funds/trace
     * Generates Cytoscape nodes and edges tracing money flow hops.
     */
    @PostMapping("/trace")
    public ResponseEntity<FundFlowGraph> traceFunds(@RequestBody(required = false) TraceRequest request) {
        String address = (request != null && request.getAddress() != null && !request.getAddress().isBlank())
                ? request.getAddress()
                : "0xSCAM999999999999999999999999999999999999";

        int hops = (request != null && request.getMaxHops() != null) ? request.getMaxHops() : 3;

        FundFlowGraph graph;
        if (request != null && request.getTransactions() != null && !request.getTransactions().isEmpty()) {
            graph = graphService.buildGraphFromTransactions(address, request.getTransactions());
        } else {
            graph = graphService.buildGraph(address, hops);
        }

        return ResponseEntity.ok(graph);
    }

    /**
     * GET /api/funds/trace/{address}
     * Convenience GET endpoint for graph tracing.
     */
    @GetMapping(value = {"/trace/{address}", "/trace"})
    public ResponseEntity<FundFlowGraph> getFundTrace(
            @PathVariable(value = "address", required = false) String pathAddress,
            @RequestParam(value = "address", required = false) String queryAddress) {

        String address = pathAddress != null && !pathAddress.isBlank() ? pathAddress : queryAddress;
        if (address == null || address.isBlank()) {
            address = "0xSCAM999999999999999999999999999999999999";
        }

        FundFlowGraph graph = graphService.buildGraph(address, 3);
        return ResponseEntity.ok(graph);
    }
}
