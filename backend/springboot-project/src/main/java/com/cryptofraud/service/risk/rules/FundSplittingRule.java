package com.cryptofraud.service.risk.rules;

import com.cryptofraud.model.RiskResult;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.service.risk.RiskContext;
import com.cryptofraud.service.risk.RiskRule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Rule 1: FUND SPLITTING (+20)
 * Detects when funds move from the target wallet into multiple distinct destination
 * wallets within a reasonable time window (e.g., within 1 hour).
 */
@Component
@Order(1)
public class FundSplittingRule implements RiskRule {

    private final long windowSeconds;

    public FundSplittingRule(@Value("${app.risk.fund-splitting-window-seconds:3600}") long windowSeconds) {
        this.windowSeconds = windowSeconds;
    }

    @Override
    public String getRuleId() {
        return "RULE_FUND_SPLITTING";
    }

    @Override
    public String getPatternName() {
        return "Fund Splitting";
    }

    @Override
    public int getScoreDelta() {
        return 20;
    }

    @Override
    public boolean evaluate(RiskContext context) {
        List<Transaction> outgoing = context.getOutgoingTransactions();
        if (outgoing.size() < 2) {
            return false;
        }

        // Distinct recipient addresses
        Set<String> allRecipients = new HashSet<>();
        for (Transaction tx : outgoing) {
            allRecipients.add(tx.getTo().trim().toLowerCase());
        }

        if (allRecipients.size() < 2) {
            return false;
        }

        // Check sliding time window
        boolean hasTimestamps = false;
        List<Transaction> timedOutgoing = new ArrayList<>();
        for (Transaction tx : outgoing) {
            if (context.getTimestamp(tx) != null) {
                timedOutgoing.add(tx);
                hasTimestamps = true;
            }
        }

        if (!hasTimestamps) {
            // If no timestamps available, having 2+ distinct recipients in the set triggers splitting
            return allRecipients.size() >= 2;
        }

        timedOutgoing.sort(Comparator.comparing(context::getTimestamp));

        for (int i = 0; i < timedOutgoing.size(); i++) {
            Instant start = context.getTimestamp(timedOutgoing.get(i));
            Set<String> windowRecipients = new HashSet<>();
            windowRecipients.add(timedOutgoing.get(i).getTo().trim().toLowerCase());

            for (int j = i + 1; j < timedOutgoing.size(); j++) {
                Instant current = context.getTimestamp(timedOutgoing.get(j));
                long diff = Duration.between(start, current).getSeconds();
                if (Math.abs(diff) <= windowSeconds) {
                    windowRecipients.add(timedOutgoing.get(j).getTo().trim().toLowerCase());
                    if (windowRecipients.size() >= 2) {
                        return true;
                    }
                } else {
                    break;
                }
            }
        }

        return false;
    }

    @Override
    public RiskResult.TriggeredRule buildTriggeredRule(RiskContext context) {
        Set<String> recipients = new HashSet<>();
        for (Transaction tx : context.getOutgoingTransactions()) {
            recipients.add(tx.getTo());
        }
        return new RiskResult.TriggeredRule(
                getRuleId(),
                "Suspicious Fund Splitting",
                getScoreDelta(),
                String.format("Funds dispersed from target wallet into %d distinct destination addresses within the evaluation window.", recipients.size())
        );
    }
}
