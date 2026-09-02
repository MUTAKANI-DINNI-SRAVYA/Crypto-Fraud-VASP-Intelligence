package com.cryptofraud.model;

/**
 * Summary statistics for a single analyzed cryptocurrency wallet.
 */
public class WalletSummary {
    private String address;
    private double balance;
    private String asset;
    private double totalReceived;
    private double totalSent;
    private int transactionCount;
    private String firstActive;
    private String lastActive;

    public WalletSummary() {
    }

    public WalletSummary(String address, double balance, String asset, double totalReceived, double totalSent, int transactionCount, String firstActive, String lastActive) {
        this.address = address;
        this.balance = balance;
        this.asset = asset;
        this.totalReceived = totalReceived;
        this.totalSent = totalSent;
        this.transactionCount = transactionCount;
        this.firstActive = firstActive;
        this.lastActive = lastActive;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public double getTotalReceived() {
        return totalReceived;
    }

    public void setTotalReceived(double totalReceived) {
        this.totalReceived = totalReceived;
    }

    public double getTotalSent() {
        return totalSent;
    }

    public void setTotalSent(double totalSent) {
        this.totalSent = totalSent;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public String getFirstActive() {
        return firstActive;
    }

    public void setFirstActive(String firstActive) {
        this.firstActive = firstActive;
    }

    public String getLastActive() {
        return lastActive;
    }

    public void setLastActive(String lastActive) {
        this.lastActive = lastActive;
    }
}
