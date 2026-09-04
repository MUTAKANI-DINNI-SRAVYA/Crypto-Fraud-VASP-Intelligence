package com.cryptofraud.service.risk;

import com.cryptofraud.dto.RiskAnalysisRequest;
import com.cryptofraud.model.RiskResult;
import com.cryptofraud.model.Transaction;

import java.util.List;

/**
 * Service interface for evaluating cryptocurrency wallet risk.
 */
public interface RiskService {

    /**
     * Evaluates risk from a complete request payload.
     *
     * @param request The risk analysis request containing wallet and transactions
     * @return Calculated RiskResult
     */
    RiskResult analyzeRisk(RiskAnalysisRequest request);

    /**
     * Convenience overload for direct programmatic invocation by other backend modules.
     *
     * @param wallet Target wallet address
     * @param transactions List of transactions to analyze
     * @return Calculated RiskResult
     */
    RiskResult analyzeRisk(String wallet, List<Transaction> transactions);

    /**
     * Convenience overload for direct programmatic invocation with VASP signal.
     *
     * @param wallet Target wallet address
     * @param transactions List of transactions to analyze
     * @param hasVaspInteraction Flag indicating VASP interaction
     * @return Calculated RiskResult
     */
    RiskResult analyzeRisk(String wallet, List<Transaction> transactions, Boolean hasVaspInteraction);

    /**
     * Maps an integer score (0-100) to its corresponding risk level category.
     *
     * @param score Integer risk score
     * @return "LOW", "MEDIUM", "HIGH", or "CRITICAL"
     */
    String determineRiskLevel(int score);
}
