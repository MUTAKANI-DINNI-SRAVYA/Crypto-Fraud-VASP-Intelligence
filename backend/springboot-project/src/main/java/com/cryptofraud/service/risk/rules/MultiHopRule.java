package com.cryptofraud.service.risk.rules;

import com.cryptofraud.model.RiskResult;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.service.risk.RiskContext;
import com.cryptofraud.service.risk.RiskRule;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Rule 3: MULTIPLE HOPS / LAYERING (+15)
 * Detects funds moving through multiple wallet addresses (chains of length >= 2 edges)
 * connected to or passing through the target wallet.
 */
@Component
@Order(3)
public class MultiHopRule implements RiskRule {

    @Override
    public String getRuleId() {
        return "RULE_MULTI_HOP";
    }

    @Override
    public String getPatternName() {
        return "Multiple Hops";
    }

    @Override
    public int getScoreDelta() {
        return 15;
    }

    @Override
    public boolean evaluate(RiskContext context) {
        String target = context.getTargetWallet().toLowerCase();
        Map<String, List<Transaction>> outgoing = context.getOutgoingByFrom();
        Map<String, List<Transaction>> incoming = context.getIncomingByTo();

        // Check 1: Transit hop (A -> Target -> B)
        List<Transaction> targetIncoming = incoming.get(target);
        List<Transaction> targetOutgoing = outgoing.get(target);

        if (targetIncoming != null && !targetIncoming.isEmpty() && targetOutgoing != null && !targetOutgoing.isEmpty()) {
            for (Transaction in : targetIncoming) {
                String inFrom = in.getFrom().trim().toLowerCase();
                for (Transaction out : targetOutgoing) {
                    String outTo = out.getTo().trim().toLowerCase();
                    if (!inFrom.equals(target) && !outTo.equals(target) && !inFrom.equals(outTo)) {
                        return true;
                    }
                }
            }
        }

        // Check 2: Forward chain (Target -> A -> B)
        if (targetOutgoing != null) {
            for (Transaction out1 : targetOutgoing) {
                String intermediate = out1.getTo().trim().toLowerCase();
                if (!intermediate.equals(target)) {
                    List<Transaction> nextTxs = outgoing.get(intermediate);
                    if (nextTxs != null && !nextTxs.isEmpty()) {
                        for (Transaction out2 : nextTxs) {
                            String nextTarget = out2.getTo().trim().toLowerCase();
                            if (!nextTarget.equals(intermediate) && !nextTarget.equals(target)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        // Check 3: Inward chain (A -> B -> Target)
        if (targetIncoming != null) {
            for (Transaction in1 : targetIncoming) {
                String intermediate = in1.getFrom().trim().toLowerCase();
                if (!intermediate.equals(target)) {
                    List<Transaction> prevTxs = incoming.get(intermediate);
                    if (prevTxs != null && !prevTxs.isEmpty()) {
                        for (Transaction in2 : prevTxs) {
                            String prevSource = in2.getFrom().trim().toLowerCase();
                            if (!prevSource.equals(intermediate) && !prevSource.equals(target)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public RiskResult.TriggeredRule buildTriggeredRule(RiskContext context) {
        return new RiskResult.TriggeredRule(
                getRuleId(),
                "Multi-Hop Layering",
                getScoreDelta(),
                "Transfers are part of a multi-hop layering sequence spanning multiple intermediary wallet addresses."
        );
    }
}
