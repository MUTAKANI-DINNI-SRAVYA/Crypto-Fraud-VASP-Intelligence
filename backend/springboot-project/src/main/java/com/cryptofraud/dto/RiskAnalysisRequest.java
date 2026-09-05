package com.cryptofraud.dto;

import com.cryptofraud.model.Transaction;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * Request payload for POST /api/risk/analyze.
 * Contains the target wallet address and the list of transactions to evaluate.
 */
public class RiskAnalysisRequest {

    @JsonAlias({"address", "targetAddress"})
    private String wallet;

    private List<Transaction> transactions = new ArrayList<>();

    // Optional signal hooks allowing the VASP module or external callers to inject signals
    private Boolean hasVaspInteraction;
    private List<String> vaspAddresses;
    private List<String> flaggedAddresses;

    public RiskAnalysisRequest() {
    }

    public RiskAnalysisRequest(String wallet, List<Transaction> transactions) {
        this.wallet = wallet;
        this.transactions = transactions != null ? transactions : new ArrayList<>();
    }

    public RiskAnalysisRequest(String wallet, List<Transaction> transactions, Boolean hasVaspInteraction) {
        this.wallet = wallet;
        this.transactions = transactions != null ? transactions : new ArrayList<>();
        this.hasVaspInteraction = hasVaspInteraction;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    // Support getAddress / setAddress for backwards compatibility
    public String getAddress() {
        return wallet;
    }

    public void setAddress(String address) {
        this.wallet = address;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions != null ? transactions : new ArrayList<>();
    }

    public Boolean getHasVaspInteraction() {
        return hasVaspInteraction;
    }

    public void setHasVaspInteraction(Boolean hasVaspInteraction) {
        this.hasVaspInteraction = hasVaspInteraction;
    }

    public List<String> getVaspAddresses() {
        return vaspAddresses;
    }

    public void setVaspAddresses(List<String> vaspAddresses) {
        this.vaspAddresses = vaspAddresses;
    }

    public List<String> getFlaggedAddresses() {
        return flaggedAddresses;
    }

    public void setFlaggedAddresses(List<String> flaggedAddresses) {
        this.flaggedAddresses = flaggedAddresses;
    }
}
