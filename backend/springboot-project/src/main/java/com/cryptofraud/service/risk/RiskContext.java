package com.cryptofraud.service.risk;

import com.cryptofraud.model.Transaction;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Encapsulates the context of a risk evaluation session.
 * Pre-processes, sanitizes, and indexes transactions to enable explainable rule execution.
 */
public class RiskContext {

    private final String targetWallet;
    private final List<Transaction> allValidTransactions;
    private final List<Transaction> incomingTransactions;
    private final List<Transaction> outgoingTransactions;
    private final Map<Transaction, Instant> parsedTimestamps;
    private final Map<String, List<Transaction>> outgoingByFrom;
    private final Map<String, List<Transaction>> incomingByTo;
    private final Set<String> flaggedAddresses;
    private final Set<String> vaspAddresses;
    private final boolean explicitVaspSignal;

    public RiskContext(String targetWallet,
                       List<Transaction> transactions,
                       Set<String> configuredFlaggedAddresses,
                       Set<String> configuredVaspAddresses,
                       Boolean requestVaspSignal,
                       List<String> requestVaspAddresses,
                       List<String> requestFlaggedAddresses) {
        this.targetWallet = targetWallet != null ? targetWallet.trim() : "";
        this.allValidTransactions = new ArrayList<>();
        this.incomingTransactions = new ArrayList<>();
        this.outgoingTransactions = new ArrayList<>();
        this.parsedTimestamps = new HashMap<>();
        this.outgoingByFrom = new HashMap<>();
        this.incomingByTo = new HashMap<>();

        // Aggregate flagged addresses
        this.flaggedAddresses = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (configuredFlaggedAddresses != null) {
            for (String addr : configuredFlaggedAddresses) {
                if (addr != null && !addr.isBlank()) {
                    this.flaggedAddresses.add(addr.trim());
                }
            }
        }
        if (requestFlaggedAddresses != null) {
            for (String addr : requestFlaggedAddresses) {
                if (addr != null && !addr.isBlank()) {
                    this.flaggedAddresses.add(addr.trim());
                }
            }
        }

        // Aggregate VASP addresses
        this.vaspAddresses = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (configuredVaspAddresses != null) {
            for (String addr : configuredVaspAddresses) {
                if (addr != null && !addr.isBlank()) {
                    this.vaspAddresses.add(addr.trim());
                }
            }
        }
        if (requestVaspAddresses != null) {
            for (String addr : requestVaspAddresses) {
                if (addr != null && !addr.isBlank()) {
                    this.vaspAddresses.add(addr.trim());
                }
            }
        }

        this.explicitVaspSignal = Boolean.TRUE.equals(requestVaspSignal);

        // Sanitize and index transactions
        if (transactions != null) {
            for (Transaction tx : transactions) {
                if (isValidTransaction(tx)) {
                    allValidTransactions.add(tx);

                    Instant ts = parseTimestamp(tx.getTimestamp());
                    if (ts != null) {
                        parsedTimestamps.put(tx, ts);
                    }

                    String fromNorm = tx.getFrom().trim().toLowerCase();
                    String toNorm = tx.getTo().trim().toLowerCase();
                    String targetNorm = this.targetWallet.toLowerCase();

                    outgoingByFrom.computeIfAbsent(fromNorm, k -> new ArrayList<>()).add(tx);
                    incomingByTo.computeIfAbsent(toNorm, k -> new ArrayList<>()).add(tx);

                    if (fromNorm.equalsIgnoreCase(targetNorm)) {
                        outgoingTransactions.add(tx);
                    }
                    if (toNorm.equalsIgnoreCase(targetNorm)) {
                        incomingTransactions.add(tx);
                    }
                }
            }
        }

        // Sort incoming and outgoing transactions by timestamp when available
        Comparator<Transaction> timeComparator = Comparator.comparing(
                tx -> parsedTimestamps.getOrDefault(tx, Instant.EPOCH)
        );
        incomingTransactions.sort(timeComparator);
        outgoingTransactions.sort(timeComparator);
    }

    private boolean isValidTransaction(Transaction tx) {
        if (tx == null) {
            return false;
        }
        if (tx.getFrom() == null || tx.getFrom().trim().isEmpty()) {
            return false;
        }
        if (tx.getTo() == null || tx.getTo().trim().isEmpty()) {
            return false;
        }
        if (tx.getAmount() < 0) {
            return false;
        }
        return true;
    }

    private Instant parseTimestamp(String rawTimestamp) {
        if (rawTimestamp == null || rawTimestamp.isBlank()) {
            return null;
        }
        String trimmed = rawTimestamp.trim();
        try {
            return Instant.parse(trimmed);
        } catch (Exception ignored) {
        }

        try {
            // Handle without Z or with timezone offset
            return DateTimeFormatter.ISO_DATE_TIME.parse(trimmed, Instant::from);
        } catch (Exception ignored) {
        }

        try {
            // Check numeric epoch timestamp (seconds or millis)
            long val = Long.parseLong(trimmed);
            if (val > 1_000_000_000_000L) {
                return Instant.ofEpochMilli(val);
            } else {
                return Instant.ofEpochSecond(val);
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public String getTargetWallet() {
        return targetWallet;
    }

    public List<Transaction> getAllValidTransactions() {
        return allValidTransactions;
    }

    public List<Transaction> getIncomingTransactions() {
        return incomingTransactions;
    }

    public List<Transaction> getOutgoingTransactions() {
        return outgoingTransactions;
    }

    public Instant getTimestamp(Transaction tx) {
        return parsedTimestamps.get(tx);
    }

    public Map<String, List<Transaction>> getOutgoingByFrom() {
        return outgoingByFrom;
    }

    public Map<String, List<Transaction>> getIncomingByTo() {
        return incomingByTo;
    }

    public Set<String> getFlaggedAddresses() {
        return flaggedAddresses;
    }

    public Set<String> getVaspAddresses() {
        return vaspAddresses;
    }

    public boolean isExplicitVaspSignal() {
        return explicitVaspSignal;
    }
}
