package com.cryptofraud.model;

/**
 * Result model representing whether tracked transactions interacted with a mock reference VASP.
 */
public class VaspInteractionResult {
    private boolean vaspInteraction;
    private String vaspName;
    private String vaspType;
    private String country;
    private String address;
    private String source;

    public VaspInteractionResult() {
        this.vaspInteraction = false;
        this.source = "Mock reference dataset";
    }

    public VaspInteractionResult(boolean vaspInteraction, String vaspName, String vaspType,
                                 String country, String address) {
        this.vaspInteraction = vaspInteraction;
        this.vaspName = vaspName;
        this.vaspType = vaspType;
        this.country = country;
        this.address = address;
        this.source = "Mock reference dataset";
    }

    public boolean isVaspInteraction() {
        return vaspInteraction;
    }

    public void setVaspInteraction(boolean vaspInteraction) {
        this.vaspInteraction = vaspInteraction;
    }

    public String getVaspName() {
        return vaspName;
    }

    public void setVaspName(String vaspName) {
        this.vaspName = vaspName;
    }

    public String getVaspType() {
        return vaspType;
    }

    public void setVaspType(String vaspType) {
        this.vaspType = vaspType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
