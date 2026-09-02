package com.cryptofraud.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Graph data model formatted specifically for Cytoscape.js / React Flow rendering.
 * Used by Member 4 (Graph Engineer) and Member 5 (Frontend).
 */
public class FundFlowGraph {
    private String targetAddress;
    private List<NodeWrapper> nodes = new ArrayList<>();
    private List<EdgeWrapper> edges = new ArrayList<>();

    public static class NodeWrapper {
        private NodeData data;

        public NodeWrapper() {
        }

        public NodeWrapper(NodeData data) {
            this.data = data;
        }

        public NodeData getData() {
            return data;
        }

        public void setData(NodeData data) {
            this.data = data;
        }
    }

    public static class NodeData {
        private String id;
        private String label;
        private String type; // victim, scam, hop, vasp
        private double balance;
        private boolean isLastTraceablePoint;

        public NodeData() {
        }

        public NodeData(String id, String label, String type, double balance, boolean isLastTraceablePoint) {
            this.id = id;
            this.label = label;
            this.type = type;
            this.balance = balance;
            this.isLastTraceablePoint = isLastTraceablePoint;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public boolean isLastTraceablePoint() {
            return isLastTraceablePoint;
        }

        public void setLastTraceablePoint(boolean lastTraceablePoint) {
            isLastTraceablePoint = lastTraceablePoint;
        }
    }

    public static class EdgeWrapper {
        private EdgeData data;

        public EdgeWrapper() {
        }

        public EdgeWrapper(EdgeData data) {
            this.data = data;
        }

        public EdgeData getData() {
            return data;
        }

        public void setData(EdgeData data) {
            this.data = data;
        }
    }

    public static class EdgeData {
        private String id;
        private String source;
        private String target;
        private double amount;
        private String asset;
        private String timestamp;
        private boolean isRapid;

        public EdgeData() {
        }

        public EdgeData(String id, String source, String target, double amount, String asset, String timestamp, boolean isRapid) {
            this.id = id;
            this.source = source;
            this.target = target;
            this.amount = amount;
            this.asset = asset;
            this.timestamp = timestamp;
            this.isRapid = isRapid;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getAsset() {
            return asset;
        }

        public void setAsset(String asset) {
            this.asset = asset;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public boolean isRapid() {
            return isRapid;
        }

        public void setRapid(boolean rapid) {
            isRapid = rapid;
        }
    }

    public FundFlowGraph() {
    }

    public FundFlowGraph(String targetAddress) {
        this.targetAddress = targetAddress;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
    }

    public List<NodeWrapper> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeWrapper> nodes) {
        this.nodes = nodes;
    }

    public List<EdgeWrapper> getEdges() {
        return edges;
    }

    public void setEdges(List<EdgeWrapper> edges) {
        this.edges = edges;
    }
}
