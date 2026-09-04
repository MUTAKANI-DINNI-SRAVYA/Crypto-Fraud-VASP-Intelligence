package com.cryptofraud.service.risk;

import com.cryptofraud.dto.RiskAnalysisRequest;
import com.cryptofraud.model.RiskResult;
import com.cryptofraud.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

/**
 * Implementation of RiskService providing deterministic, explainable heuristic evaluation.
 */
@Service
public class RiskServiceImpl implements RiskService {

    private final List<RiskRule> rules;
    private final Set<String> configuredFlaggedAddresses;
    private final Set<String> configuredVaspAddresses;

    public RiskServiceImpl(List<RiskRule> rules,
                           @Value("${app.risk.flagged-addresses:}") String flaggedAddressesStr,
                           @Value("${app.risk.vasp-addresses:}") String vaspAddressesStr) {
        this.rules = rules != null ? rules : Collections.emptyList();
        this.configuredFlaggedAddresses = parseAddressSet(flaggedAddressesStr);
        this.configuredVaspAddresses = parseAddressSet(vaspAddressesStr);
    }

    @Override
    public RiskResult analyzeRisk(RiskAnalysisRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Risk analysis request payload cannot be null.");
        }

        String wallet = request.getWallet();
        if (wallet == null || wallet.trim().isEmpty()) {
            throw new IllegalArgumentException("Wallet address is required and cannot be empty.");
        }

        String normalizedWallet = wallet.trim();
        List<Transaction> transactions = request.getTransactions();

        if (transactions == null || transactions.isEmpty()) {
            return new RiskResult(
                    normalizedWallet,
                    0,
                    determineRiskLevel(0),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    Instant.now().toString()
            );
        }

        RiskContext context = new RiskContext(
                normalizedWallet,
                transactions,
                configuredFlaggedAddresses,
                configuredVaspAddresses,
                request.getHasVaspInteraction(),
                request.getVaspAddresses(),
                request.getFlaggedAddresses()
        );

        int rawScore = 0;
        List<String> detectedPatterns = new ArrayList<>();
        List<RiskResult.TriggeredRule> triggeredRules = new ArrayList<>();

        for (RiskRule rule : rules) {
            try {
                if (rule.evaluate(context)) {
                    detectedPatterns.add(rule.getPatternName());
                    triggeredRules.add(rule.buildTriggeredRule(context));
                    rawScore += rule.getScoreDelta();
                }
            } catch (Exception e) {
                // Heuristic evaluation of an individual rule should not abort the entire analysis
                // In production this would be logged
            }
        }

        int finalScore = Math.min(100, Math.max(0, rawScore));
        String riskLevel = determineRiskLevel(finalScore);

        return new RiskResult(
                normalizedWallet,
                finalScore,
                riskLevel,
                detectedPatterns,
                triggeredRules,
                Instant.now().toString()
        );
    }

    @Override
    public RiskResult analyzeRisk(String wallet, List<Transaction> transactions) {
        return analyzeRisk(new RiskAnalysisRequest(wallet, transactions));
    }

    @Override
    public RiskResult analyzeRisk(String wallet, List<Transaction> transactions, Boolean hasVaspInteraction) {
        return analyzeRisk(new RiskAnalysisRequest(wallet, transactions, hasVaspInteraction));
    }

    @Override
    public String determineRiskLevel(int score) {
        if (score <= 30) {
            return "LOW";
        } else if (score <= 60) {
            return "MEDIUM";
        } else if (score <= 80) {
            return "HIGH";
        } else {
            return "CRITICAL";
        }
    }

    private Set<String> parseAddressSet(String commaSeparated) {
        Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (commaSeparated != null && !commaSeparated.isBlank()) {
            for (String part : commaSeparated.split(",")) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    set.add(trimmed);
                }
            }
        }
        return set;
    }
}
