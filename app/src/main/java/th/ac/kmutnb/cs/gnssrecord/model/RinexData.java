package th.ac.kmutnb.cs.gnssrecord.model;

public class RinexData {
    private String satellite;
    private double pseudoRange;
    private double carrierPhase;
    private double doppler;
    private double signalStrength;

    public RinexData(String satellite, double pseudoRange, double carrierPhase, double doppler, double signalStrength) {
        this.satellite = satellite;
        this.pseudoRange = pseudoRange;
        this.carrierPhase = carrierPhase;
        this.doppler = doppler;
        this.signalStrength = signalStrength;
    }

    public String getSatellite() {
        return satellite;
    }

    public double getPseudoRange() {
        return pseudoRange;
    }

    public double getCarrierPhase() {
        return carrierPhase;
    }

    public double getDoppler() {
        return doppler;
    }

    public double getSignalStrength() {
        return signalStrength;
    }
}
