package com.cryptofraud.model;

/**
 * Model representing the identification of a Virtual Asset Service Provider (VASP)
 * and whether the address marks the "LAST TRACEABLE POINT" on-chain.
 */
public class VaspCheckResult {
    private String address;
    private boolean isVasp;
    private String vaspName;
    private String category;
    private boolean isLastTraceablePoint;
    private String boundaryNotice;
    private String jurisdictionNote;

    public VaspCheckResult() {
    }

    public VaspCheckResult(String address, boolean isVasp, String vaspName, String category, boolean isLastTraceablePoint, String boundaryNotice, String jurisdictionNote) {
        this.address = address;
        this.isVasp = isVasp;
        this.vaspName = vaspName;
        this.category = category;
        this.isLastTraceablePoint = isLastTraceablePoint;
        this.boundaryNotice = boundaryNotice;
        this.jurisdictionNote = jurisdictionNote;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isVasp() {
        return isVasp;
    }

    public void setVasp(boolean vasp) {
        isVasp = vasp;
    }

    public String getVaspName() {
        return vaspName;
    }

    public void setVasp(String vaspName) {
        this.vaspName = vaspName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isLastTraceablePoint() {
        return isLastTraceablePoint;
    }

    public void setLastTraceablePoint(boolean lastTraceablePoint) {
        isLastTraceablePoint = lastTraceablePoint;
    }

    public String getBoundaryNotice() {
        return boundaryNotice;
    }

    public void setBoundaryNotice(String boundaryNotice) {
        this.boundaryNotice = boundaryNotice;
    }

    public String getJurisdictionNote() {
        return jurisdictionNote;
    }

    public void setJurisdictionNote(String jurisdictionNote) {
        this.jurisdictionNote = jurisdictionNote;
    }
}
