package com.cryptofraud.service;

import com.cryptofraud.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

/**
 * Service that orchestrates transaction analysis, VASP detection, Last Traceable Point calculation,
 * AI briefing generation, and structured investigation report compilation.
 */
@Service
public class InvestigationService {

    private final VaspService vaspService;
    private final AiExplanationService aiExplanationService;

    @Autowired
    public InvestigationService(VaspService vaspService, AiExplanationService aiExplanationService) {
        this.vaspService = vaspService;
        this.aiExplanationService = aiExplanationService;
    }

    public InvestigationReport analyze(InvestigationRequest request) {
        if (request == null) {
            request = new InvestigationRequest();
        }

        String wallet = request.getWallet();
        if (wallet == null || wallet.trim().isEmpty()) {
            wallet = "0x0000000000000000000000000000000000000000";
        }

        List<Transaction> transactions = request.getTransactions() != null ? request.getTransactions() : Collections.emptyList();
        Integer riskScore = request.getRiskScore() != null ? request.getRiskScore() : 0;
        String riskLevel = request.getRiskLevel() != null ? request.getRiskLevel() : "LOW";
        List<String> patterns = request.getPatterns() != null ? request.getPatterns() : Collections.emptyList();

        // 1. Perform VASP Interaction Analysis
        VaspInteractionResult vaspInteraction = vaspService.checkVaspInteraction(transactions);

        // 2. Determine Last Traceable Point along transaction chain
        LastTraceablePoint lastTraceablePoint = vaspService.determineLastTraceablePoint(transactions, wallet);

        // 3. Generate AI or Fallback Executive Summary
        String aiExplanation = aiExplanationService.generateInvestigationExplanation(
            wallet, transactions, patterns, riskScore, vaspInteraction, lastTraceablePoint
        );

        // 4. Calculate Fund Summary Statistics
        Map<String, Object> fundSummary = calculateFundSummary(wallet, transactions);

        // 5. Compile Investigation Report
        InvestigationReport report = new InvestigationReport();
        report.setReportId("REP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        report.setGeneratedAt(Instant.now().toString());

        report.setWallet(wallet);
        report.setTargetAddress(wallet);

        report.setRiskScore(riskScore);
        report.setRiskLevel(riskLevel);
        report.setPatterns(patterns);
        report.setFundSummary(fundSummary);
        report.setVaspInteraction(vaspInteraction);
        report.setLastTraceablePoint(lastTraceablePoint);
        report.setAiExplanation(aiExplanation);
        report.setAiExecutiveSummary(aiExplanation);

        // Populate backward-compatible VaspCheckResult findings list
        if (vaspInteraction.isVaspInteraction()) {
            VaspCheckResult vaspResult = new VaspCheckResult(
                vaspInteraction.getAddress(),
                true,
                vaspInteraction.getVaspName(),
                vaspInteraction.getVaspType(),
                lastTraceablePoint.isOffChainRequired(),
                "LAST TRACEABLE POINT: Further lawful off-chain records required.",
                vaspInteraction.getCountry()
            );
            report.setVaspFindings(List.of(vaspResult));
        }

        report.setLimitations(List.of(
            "Blockchain data alone does not establish real-world identity.",
            "Further lawful off-chain records may be required.",
            "Prototype investigation assistant for hackathon testing only."
        ));

        report.setDisclaimer("This is a prototype heuristic report generated for hackathon demonstration purposes. No legal or criminal culpability is established.");

        return report;
    }

    private Map<String, Object> calculateFundSummary(String wallet, List<Transaction> transactions) {
        Map<String, Object> summary = new HashMap<>();
        double totalSent = 0.0;
        double totalReceived = 0.0;
        int txCount = transactions.size();
        Set<String> uniqueCounterparties = new HashSet<>();

        String walletLower = wallet.toLowerCase();
        for (Transaction tx : transactions) {
            if (tx.getFrom() != null && tx.getFrom().equalsIgnoreCase(walletLower)) {
                totalSent += tx.getAmount();
                if (tx.getTo() != null) uniqueCounterparties.add(tx.getTo());
            }
            if (tx.getTo() != null && tx.getTo().equalsIgnoreCase(walletLower)) {
                totalReceived += tx.getAmount();
                if (tx.getFrom() != null) uniqueCounterparties.add(tx.getFrom());
            }
        }

        summary.put("transactionCount", txCount);
        summary.put("totalSent", totalSent);
        summary.put("totalReceived", totalReceived);
        summary.put("netFlow", totalReceived - totalSent);
        summary.put("uniqueCounterpartiesCount", uniqueCounterparties.size());
        return summary;
    }
}
