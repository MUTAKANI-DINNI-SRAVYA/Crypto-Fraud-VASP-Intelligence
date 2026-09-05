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
import java.util.List;

/**
 * Rule 2: RAPID MOVEMENT (+20)
 * Detects unusually short time intervals between incoming deposit and outgoing transfer
 * for the target wallet (e.g., within 30 minutes).
 */
@Component
@Order(2)
public class RapidMovementRule implements RiskRule {

    private final long rapidIntervalSeconds;

    public RapidMovementRule(@Value("${app.risk.rapid-movement-window-seconds:1800}") long rapidIntervalSeconds) {
        this.rapidIntervalSeconds = rapidIntervalSeconds;
    }

    @Override
    public String getRuleId() {
        return "RULE_RAPID_MOVEMENT";
    }

    @Override
    public String getPatternName() {
        return "Rapid Movement";
    }

    @Override
    public int getScoreDelta() {
        return 20;
    }

    @Override
    public boolean evaluate(RiskContext context) {
        List<Transaction> incoming = context.getIncomingTransactions();
        List<Transaction> outgoing = context.getOutgoingTransactions();

        if (incoming.isEmpty() || outgoing.isEmpty()) {
            return false;
        }

        for (Transaction inTx : incoming) {
            Instant inTime = context.getTimestamp(inTx);
            if (inTime == null) {
                continue;
            }

            for (Transaction outTx : outgoing) {
                Instant outTime = context.getTimestamp(outTx);
                if (outTime == null) {
                    continue;
                }

                long seconds = Duration.between(inTime, outTime).getSeconds();
                // Outgoing occurs shortly after or concurrently with deposit
                if (seconds >= 0 && seconds <= rapidIntervalSeconds) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public RiskResult.TriggeredRule buildTriggeredRule(RiskContext context) {
        return new RiskResult.TriggeredRule(
                getRuleId(),
                "Rapid Asset Relayering",
                getScoreDelta(),
                String.format("Outgoing transfer dispatched within %d minutes of incoming deposit.", rapidIntervalSeconds / 60)
        );
    }
}
