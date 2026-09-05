package com.cryptofraud.service.risk.rules;

import com.cryptofraud.model.RiskResult;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.service.risk.RiskContext;
import com.cryptofraud.service.risk.RiskRule;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rule 5: UNUSUAL AMOUNT (+10)
 * Detects unusually large amounts compared with the transaction set using a simple,
 * explainable statistical heuristic (amount > mean + 1.5 * stdDev and > 2.0 * mean).
 */
@Component
@Order(5)
public class UnusualAmountRule implements RiskRule {

    @Override
    public String getRuleId() {
        return "RULE_UNUSUAL_AMOUNT";
    }

    @Override
    public String getPatternName() {
        return "Unusual Amount";
    }

    @Override
    public int getScoreDelta() {
        return 10;
    }

    @Override
    public boolean evaluate(RiskContext context) {
        List<Transaction> txList = context.getAllValidTransactions();
        if (txList.isEmpty()) {
            return false;
        }

        List<Transaction> walletTxs = new ArrayList<>();
        walletTxs.addAll(context.getIncomingTransactions());
        walletTxs.addAll(context.getOutgoingTransactions());

        if (walletTxs.isEmpty()) {
            return false;
        }

        List<Double> amounts = new ArrayList<>();
        for (Transaction tx : txList) {
            amounts.add(tx.getAmount());
        }

        Collections.sort(amounts);
        int n = amounts.size();
        double median = (n % 2 == 1) ? amounts.get(n / 2) : (amounts.get(n / 2 - 1) + amounts.get(n / 2)) / 2.0;

        if (n >= 2 && median > 0.000001) {
            double threshold = 2.5 * median;
            for (Transaction tx : walletTxs) {
                if (tx.getAmount() >= threshold) {
                    return true;
                }
            }
        } else {
            // Absolute large amount threshold for single tx or near-zero median
            for (Transaction tx : walletTxs) {
                if (tx.getAmount() >= 20.0) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public RiskResult.TriggeredRule buildTriggeredRule(RiskContext context) {
        double maxWalletAmount = 0.0;
        for (Transaction tx : context.getIncomingTransactions()) {
            if (tx.getAmount() > maxWalletAmount) maxWalletAmount = tx.getAmount();
        }
        for (Transaction tx : context.getOutgoingTransactions()) {
            if (tx.getAmount() > maxWalletAmount) maxWalletAmount = tx.getAmount();
        }

        return new RiskResult.TriggeredRule(
                getRuleId(),
                "Unusual Transaction Amount",
                getScoreDelta(),
                String.format("Unusually large transaction amount (%.2f ETH) detected relative to the baseline transaction set.", maxWalletAmount)
        );
    }
}
