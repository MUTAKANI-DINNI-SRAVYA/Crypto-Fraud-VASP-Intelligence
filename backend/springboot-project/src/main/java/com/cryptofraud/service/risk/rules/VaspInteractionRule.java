package com.cryptofraud.service.risk.rules;

import com.cryptofraud.model.RiskResult;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.service.risk.RiskContext;
import com.cryptofraud.service.risk.RiskRule;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Rule 6: VASP INTERACTION (+15)
 * Checks for interactions with Virtual Asset Service Providers (exchanges, custodial wallets).
 * Supports explicit external signals passed from Member 6's VASP module, request lists,
 * or configured VASP catalog addresses.
 */
@Component
@Order(6)
public class VaspInteractionRule implements RiskRule {

    @Override
    public String getRuleId() {
        return "RULE_VASP_INTERACTION";
    }

    @Override
    public String getPatternName() {
        return "VASP Interaction";
    }

    @Override
    public int getScoreDelta() {
        return 15;
    }

    @Override
    public boolean evaluate(RiskContext context) {
        // 1. Explicit signal provided by VASP module or caller
        if (context.isExplicitVaspSignal()) {
            return true;
        }

        Set<String> vaspAddresses = context.getVaspAddresses();

        // 2. Check incoming transactions
        for (Transaction tx : context.getIncomingTransactions()) {
            String from = tx.getFrom().trim();
            if (isVaspAddress(from, vaspAddresses)) {
                return true;
            }
        }

        // 3. Check outgoing transactions
        for (Transaction tx : context.getOutgoingTransactions()) {
            String to = tx.getTo().trim();
            if (isVaspAddress(to, vaspAddresses)) {
                return true;
            }
        }

        return false;
    }

    private boolean isVaspAddress(String address, Set<String> knownVasps) {
        if (address == null || address.isBlank()) {
            return false;
        }
        if (knownVasps != null && knownVasps.contains(address)) {
            return true;
        }
        // Hackathon demo heuristic for mock VASP addresses (e.g. 0xVASP8888...)
        return address.toUpperCase().startsWith("0XVASP");
    }

    @Override
    public RiskResult.TriggeredRule buildTriggeredRule(RiskContext context) {
        return new RiskResult.TriggeredRule(
                getRuleId(),
                "VASP Interaction",
                getScoreDelta(),
                "Transaction interaction detected with a known or signaled Virtual Asset Service Provider (VASP)."
        );
    }
}
