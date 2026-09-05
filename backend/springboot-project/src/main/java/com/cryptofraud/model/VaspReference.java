package com.cryptofraud.model;

/**
 * Model representing a reference Virtual Asset Service Provider (VASP) entity in the mock registry.
 */
public class VaspReference {
    private String address;
    private String vaspName;
    private String category;
    private String custodialType;
    private boolean isLastTraceablePoint;
    private String boundaryNotice;
    private String fictionalJurisdiction;
    private String complianceNotice;
    private String source;

    public VaspReference() {
        this.source = "Mock reference dataset";
    }

    public VaspReference(String address, String vaspName, String category, String custodialType,
                         boolean isLastTraceablePoint, String boundaryNotice,
                         String fictionalJurisdiction, String complianceNotice) {
        this.address = address;
        this.vaspName = vaspName;
        this.category = category;
        this.custodialType = custodialType;
        this.isLastTraceablePoint = isLastTraceablePoint;
        this.boundaryNotice = boundaryNotice;
        this.fictionalJurisdiction = fictionalJurisdiction;
        this.complianceNotice = complianceNotice;
        this.source = "Mock reference dataset";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVaspName() {
        return vaspName;
    }

    public void setVaspName(String vaspName) {
        this.vaspName = vaspName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCustodialType() {
        return custodialType;
    }

    public void setCustodialType(String custodialType) {
        this.custodialType = custodialType;
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

    public String getFictionalJurisdiction() {
        return fictionalJurisdiction;
    }

    public void setFictionalJurisdiction(String fictionalJurisdiction) {
        this.fictionalJurisdiction = fictionalJurisdiction;
    }

    public String getComplianceNotice() {
        return complianceNotice;
    }

    public void setComplianceNotice(String complianceNotice) {
        this.complianceNotice = complianceNotice;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
