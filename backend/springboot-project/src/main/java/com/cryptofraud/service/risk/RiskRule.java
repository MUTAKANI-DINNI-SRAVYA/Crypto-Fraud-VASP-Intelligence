package com.cryptofraud.service.risk;

import com.cryptofraud.model.RiskResult;

/**
 * Interface defining a heuristic risk detection rule.
 * Rules are deterministic, explainable, and produce an individual score delta and pattern identifier.
 */
public interface RiskRule {

    /**
     * Unique technical identifier for the rule (e.g., RULE_FUND_SPLITTING).
     */
    String getRuleId();

    /**
     * Human-readable pattern name matching user-facing pattern terminology
     * (e.g., "Fund Splitting", "Rapid Movement").
     */
    String getPatternName();

    /**
     * Fixed score increment contributed when this rule is triggered.
     */
    int getScoreDelta();

    /**
     * Evaluates the pre-processed context to determine if this pattern is present.
     *
     * @param context Pre-indexed transaction analysis context
     * @return true if the pattern heuristic is matched, false otherwise
     */
    boolean evaluate(RiskContext context);

    /**
     * Builds the detailed TriggeredRule metadata describing why the rule fired.
     *
     * @param context Pre-indexed transaction analysis context
     * @return TriggeredRule instance with explanatory details
     */
    RiskResult.TriggeredRule buildTriggeredRule(RiskContext context);
}
