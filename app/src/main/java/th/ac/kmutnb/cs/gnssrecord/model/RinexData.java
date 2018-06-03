package th.ac.kmutnb.cs.gnssrecord.model;

public class RinexData {
    private String satellite;
    private String pseudoRange;
    private String carrierPhase;
    private String signalStrength;
    private String doppler;

    public RinexData(String satellite, String pseudoRange, String carrierPhase, String signalStrength, String doppler) {
        this.satellite = satellite;
        this.pseudoRange = pseudoRange;
        this.carrierPhase = carrierPhase;
        this.signalStrength = signalStrength;
        this.doppler = doppler;
    }

    public String getSatellite() {
        return satellite;
    }

    public String getPseudoRange() {
        return pseudoRange;
    }

    public String getCarrierPhase() {
        return carrierPhase;
    }

    public String getSignalStrength() {
        return signalStrength;
    }

    public String getDoppler() {
        return doppler;
    }
}
