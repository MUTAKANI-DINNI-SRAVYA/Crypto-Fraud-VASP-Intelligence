package com.cryptofraud.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Result model representing explainable risk assessment for a cryptocurrency wallet.
 */
public class RiskResult {
    private String address;
    private int riskScore;
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
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

    public RiskResult(String address, int riskScore, String riskLevel, List<TriggeredRule> triggeredRules, String evaluatedAt) {
        this.address = address;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.triggeredRules = triggeredRules;
        this.evaluatedAt = evaluatedAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
