package com.cryptofraud.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Result model representing explainable risk assessment for a cryptocurrency wallet.
 */
@JsonPropertyOrder({ "wallet", "riskScore", "riskLevel", "patterns", "address", "triggeredRules", "evaluatedAt", "disclaimer" })
public class RiskResult {
    private String wallet;
    private String address;
    private int riskScore;
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
    private List<String> patterns = new ArrayList<>();
    private List<TriggeredRule> triggeredRules = new ArrayList<>();
    private String evaluatedAt;
    private String disclaimer = "This is a prototype heuristic score for demonstration purposes and is NOT a real AML/compliance decision engine.";

    public static class TriggeredRule {
        private String ruleId;
        private String ruleName;
        private int scoreDelta;
        private String description;

        public TriggeredRule() {
        }

        public TriggeredRule(String ruleId, String ruleName, int scoreDelta, String description) {
            this.ruleId = ruleId;
            this.ruleName = ruleName;
            this.scoreDelta = scoreDelta;
            this.description = description;
        }

        public String getRuleId() {
            return ruleId;
        }

        public void setRuleId(String ruleId) {
            this.ruleId = ruleId;
        }

        public String getRuleName() {
            return ruleName;
        }

        public void setRuleName(String ruleName) {
            this.ruleName = ruleName;
        }

        public int getScoreDelta() {
            return scoreDelta;
        }

        public void setScoreDelta(int scoreDelta) {
            this.scoreDelta = scoreDelta;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public RiskResult() {
    }

    public RiskResult(String wallet, int riskScore, String riskLevel, List<String> patterns) {
        this.wallet = wallet;
        this.address = wallet;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.patterns = patterns != null ? patterns : new ArrayList<>();
    }

    public RiskResult(String address, int riskScore, String riskLevel, List<TriggeredRule> triggeredRules, String evaluatedAt) {
        this.address = address;
        this.wallet = address;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.triggeredRules = triggeredRules != null ? triggeredRules : new ArrayList<>();
        this.evaluatedAt = evaluatedAt;
    }

    public RiskResult(String wallet, int riskScore, String riskLevel, List<String> patterns, List<TriggeredRule> triggeredRules, String evaluatedAt) {
        this.wallet = wallet;
        this.address = wallet;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.patterns = patterns != null ? patterns : new ArrayList<>();
        this.triggeredRules = triggeredRules != null ? triggeredRules : new ArrayList<>();
        this.evaluatedAt = evaluatedAt;
    }

    public String getWallet() {
        return wallet != null ? wallet : address;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
        if (this.address == null) {
            this.address = wallet;
        }
    }

    public String getAddress() {
        return address != null ? address : wallet;
    }

    public void setAddress(String address) {
        this.address = address;
        if (this.wallet == null) {
            this.wallet = address;
        }
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    public void addPattern(String pattern) {
        if (this.patterns == null) {
            this.patterns = new ArrayList<>();
        }
        if (!this.patterns.contains(pattern)) {
            this.patterns.add(pattern);
        }
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public List<TriggeredRule> getTriggeredRules() {
        return triggeredRules;
    }

    public void setTriggeredRules(List<TriggeredRule> triggeredRules) {
        this.triggeredRules = triggeredRules;
    }

    public String getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(String evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
