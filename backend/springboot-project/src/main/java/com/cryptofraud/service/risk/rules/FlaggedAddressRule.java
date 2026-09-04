package com.cryptofraud.service.risk.rules;

import com.cryptofraud.model.RiskResult;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.service.risk.RiskContext;
import com.cryptofraud.service.risk.RiskRule;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Rule 4: FLAGGED ADDRESS INTERACTION (+20)
 * Checks whether transactions interact with known suspicious or flagged addresses
 * defined in configuration or provided in request parameters.
 */
@Component
@Order(4)
public class FlaggedAddressRule implements RiskRule {

    @Override
    public String getRuleId() {
        return "RULE_FLAGGED_INTERACTION";
    }

    @Override
    public String getPatternName() {
        return "Flagged Address Interaction";
    }

    @Override
    public int getScoreDelta() {
        return 20;
    }

    @Override
    public boolean evaluate(RiskContext context) {
        Set<String> flagged = context.getFlaggedAddresses();
        if (flagged.isEmpty()) {
            return false;
        }

        String target = context.getTargetWallet();
        if (flagged.contains(target)) {
            return true;
        }

        for (Transaction tx : context.getIncomingTransactions()) {
            if (flagged.contains(tx.getFrom().trim())) {
                return true;
            }
        }

        for (Transaction tx : context.getOutgoingTransactions()) {
            if (flagged.contains(tx.getTo().trim())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public RiskResult.TriggeredRule buildTriggeredRule(RiskContext context) {
        Set<String> flagged = context.getFlaggedAddresses();
        Set<String> matched = new HashSet<>();

        if (flagged.contains(context.getTargetWallet())) {
            matched.add(context.getTargetWallet());
        }
        for (Transaction tx : context.getIncomingTransactions()) {
            if (flagged.contains(tx.getFrom().trim())) {
                matched.add(tx.getFrom().trim());
            }
        }
        for (Transaction tx : context.getOutgoingTransactions()) {
            if (flagged.contains(tx.getTo().trim())) {
                matched.add(tx.getTo().trim());
            }
        }

        return new RiskResult.TriggeredRule(
                getRuleId(),
                "Flagged Address Interaction",
                getScoreDelta(),
                String.format("Direct transaction interaction detected with flagged address(es): %s", matched)
        );
    }
}
