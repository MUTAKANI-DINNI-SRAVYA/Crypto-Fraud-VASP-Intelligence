package com.cryptofraud.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model representing a compiled investigative dossier.
 * Produced by Member 6 (VASP + AI + Report Engineer).
 * Preserves backward compatibility for all existing fields while adding structured Member 6 intelligence properties.
 */
public class InvestigationReport {
    // Existing fields for backward compatibility
    private String reportId;
    private String generatedAt;
    private String targetAddress;
    private WalletSummary walletSummary;
    private RiskResult riskEvaluation;
    private List<VaspCheckResult> vaspFindings = new ArrayList<>();
    private String aiExecutiveSummary;
    private String disclaimer = "This is a prototype heuristic report generated for hackathon demonstration purposes. No legal or criminal culpability is established.";

    // Required Member 6 properties
    private String wallet;
    private int riskScore;
    private String riskLevel;
    private List<String> patterns = new ArrayList<>();
    private Map<String, Object> fundSummary = new HashMap<>();
    private VaspInteractionResult vaspInteraction;
    private LastTraceablePoint lastTraceablePoint;
    private String aiExplanation;
    private List<String> limitations = new ArrayList<>();

    public InvestigationReport() {
        initDefaultLimitations();
    }

    public InvestigationReport(String reportId, String generatedAt, String targetAddress, WalletSummary walletSummary, RiskResult riskEvaluation, List<VaspCheckResult> vaspFindings, String aiExecutiveSummary) {
        this.reportId = reportId;
        this.generatedAt = generatedAt;
        this.targetAddress = targetAddress;
        this.wallet = targetAddress;
        this.walletSummary = walletSummary;
        this.riskEvaluation = riskEvaluation;
        this.vaspFindings = vaspFindings;
        this.aiExecutiveSummary = aiExecutiveSummary;
        this.aiExplanation = aiExecutiveSummary;
        if (riskEvaluation != null) {
            this.riskScore = riskEvaluation.getRiskScore();
            this.riskLevel = riskEvaluation.getRiskLevel();
        }
        initDefaultLimitations();
    }

    private void initDefaultLimitations() {
        this.limitations = List.of(
            "Blockchain data alone does not establish real-world identity.",
            "Further lawful off-chain records may be required.",
            "Prototype investigation assistant for hackathon testing only."
        );
    }

    // Standard Getters and Setters
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
        return targetAddress != null ? targetAddress : wallet;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
        if (this.wallet == null) {
            this.wallet = targetAddress;
        }
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
        if (riskEvaluation != null) {
            this.riskScore = riskEvaluation.getRiskScore();
            this.riskLevel = riskEvaluation.getRiskLevel();
        }
    }

    public List<VaspCheckResult> getVaspFindings() {
        return vaspFindings;
    }

    public void setVaspFindings(List<VaspCheckResult> vaspFindings) {
        this.vaspFindings = vaspFindings;
    }

    public String getAiExecutiveSummary() {
        return aiExecutiveSummary != null ? aiExecutiveSummary : aiExplanation;
    }

    public void setAiExecutiveSummary(String aiExecutiveSummary) {
        this.aiExecutiveSummary = aiExecutiveSummary;
        if (this.aiExplanation == null) {
            this.aiExplanation = aiExecutiveSummary;
        }
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    // Member 6 Specific Getters and Setters
    public String getWallet() {
        return wallet != null ? wallet : targetAddress;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
        if (this.targetAddress == null) {
            this.targetAddress = wallet;
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

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    public Map<String, Object> getFundSummary() {
        return fundSummary;
    }

    public void setFundSummary(Map<String, Object> fundSummary) {
        this.fundSummary = fundSummary;
    }

    public VaspInteractionResult getVaspInteraction() {
        return vaspInteraction;
    }

    public void setVaspInteraction(VaspInteractionResult vaspInteraction) {
        this.vaspInteraction = vaspInteraction;
    }

    public LastTraceablePoint getLastTraceablePoint() {
        return lastTraceablePoint;
    }

    public void setLastTraceablePoint(LastTraceablePoint lastTraceablePoint) {
        this.lastTraceablePoint = lastTraceablePoint;
    }

    public String getAiExplanation() {
        return aiExplanation != null ? aiExplanation : aiExecutiveSummary;
    }

    public void setAiExplanation(String aiExplanation) {
        this.aiExplanation = aiExplanation;
        if (this.aiExecutiveSummary == null) {
            this.aiExecutiveSummary = aiExplanation;
        }
    }

    public List<String> getLimitations() {
        return limitations;
    }

    public void setLimitations(List<String> limitations) {
        this.limitations = limitations;
    }
}
