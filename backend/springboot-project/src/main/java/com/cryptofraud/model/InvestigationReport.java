package com.cryptofraud.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a compiled investigative dossier.
 * Produced by Member 6 (VASP + AI + Report Engineer).
 */
public class InvestigationReport {
    private String reportId;
    private String generatedAt;
    private String targetAddress;
    private WalletSummary walletSummary;
    private RiskResult riskEvaluation;
    private List<VaspCheckResult> vaspFindings = new ArrayList<>();
    private String aiExecutiveSummary;
    private String disclaimer = "This is a prototype heuristic report generated for hackathon demonstration purposes. No legal or criminal culpability is established.";

    public InvestigationReport() {
    }

    public InvestigationReport(String reportId, String generatedAt, String targetAddress, WalletSummary walletSummary, RiskResult riskEvaluation, List<VaspCheckResult> vaspFindings, String aiExecutiveSummary) {
        this.reportId = reportId;
        this.generatedAt = generatedAt;
        this.targetAddress = targetAddress;
        this.walletSummary = walletSummary;
        this.riskEvaluation = riskEvaluation;
        this.vaspFindings = vaspFindings;
        this.aiExecutiveSummary = aiExecutiveSummary;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
    }

    public WalletSummary getWalletSummary() {
        return walletSummary;
    }

    public void setWalletSummary(WalletSummary walletSummary) {
        this.walletSummary = walletSummary;
    }

    public RiskResult getRiskEvaluation() {
        return riskEvaluation;
    }

    public void setRiskEvaluation(RiskResult riskEvaluation) {
        this.riskEvaluation = riskEvaluation;
    }

    public List<VaspCheckResult> getVaspFindings() {
        return vaspFindings;
    }

    public void setVaspFindings(List<VaspCheckResult> vaspFindings) {
        this.vaspFindings = vaspFindings;
    }

    public String getAiExecutiveSummary() {
        return aiExecutiveSummary;
    }

    public void setAiExecutiveSummary(String aiExecutiveSummary) {
        this.aiExecutiveSummary = aiExecutiveSummary;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
