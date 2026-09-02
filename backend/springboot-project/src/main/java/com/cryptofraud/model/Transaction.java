package com.cryptofraud.model;

/**
 * Common data model representing a single cryptocurrency transaction.
 * All backend services and team members MUST use this exact model.
 */
public class Transaction {
    private String hash;
    private String from;
    private String to;
    private double amount;
    private String asset;
    private String timestamp;

    public Transaction() {
    }

    public Transaction(String hash, String from, String to, double amount, String asset, String timestamp) {
        this.hash = hash;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.asset = asset;
        this.timestamp = timestamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "hash='" + hash + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", amount=" + amount +
                ", asset='" + asset + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
