package com.cryptofraud.model;

/**
 * Model representing the final traceable on-chain node determined from transaction relationship tracing.
 */
public class LastTraceablePoint {
    private String address;
    private String type; // e.g., "VASP-associated address" or "Unidentified Wallet"
    private String reason;
    private boolean offChainRequired;
    private String message;

    public LastTraceablePoint() {
    }

    public LastTraceablePoint(String address, String type, String reason, boolean offChainRequired, String message) {
        this.address = address;
        this.type = type;
        this.reason = reason;
        this.offChainRequired = offChainRequired;
        this.message = message;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isOffChainRequired() {
        return offChainRequired;
    }

    public void setOffChainRequired(boolean offChainRequired) {
        this.offChainRequired = offChainRequired;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
