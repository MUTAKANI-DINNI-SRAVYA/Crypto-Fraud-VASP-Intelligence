package com.cryptofraud.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object (DTO) for requesting an investigative analysis.
 */
public class InvestigationRequest {
    private String wallet;
    private List<Transaction> transactions = new ArrayList<>();
    private Integer riskScore;
    private String riskLevel;
    private List<String> patterns = new ArrayList<>();

    public InvestigationRequest() {
    }

    public InvestigationRequest(String wallet, List<Transaction> transactions, Integer riskScore, String riskLevel, List<String> patterns) {
        this.wallet = wallet;
        this.transactions = transactions;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.patterns = patterns;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }
}
