package th.ac.kmutnb.cs.gnssraw;

import android.location.GnssMeasurement;

/**
 * Created by narit on 23/2/2018 AD.
 */

public class Gnss {

    private int svID;
    private int constellationType;
    private double accumulatedDeltaRangeMeters;
    private int accumulatedDeltaRangeState;
    private double accumulatedDeltaRangeUncertaintyMeters;
    private long carrierCycles;
    //private float carrierFrequencyHz;
    //private double carrierPhase;
    //private double carrierPhaseUncertainty;
    private double cn0DbHz;
    private int multipathIndicator;
    private double pseudorangeRateMetersPerSecond;
    private double pseudorangeRateUncertaintyMetersPerSecond;
    private long receivedSvTimeNanos;
    private long receivedSvTimeUncertaintyNanos;
    //private double snrInDb;
    private double timeOffsetNanos;

    public Gnss(GnssMeasurement measurement) {
        this.svID = measurement.getSvid();
        this.constellationType = measurement.getConstellationType();
        this.accumulatedDeltaRangeMeters = measurement.getAccumulatedDeltaRangeMeters();
        this.accumulatedDeltaRangeState = measurement.getAccumulatedDeltaRangeState();
        this.accumulatedDeltaRangeUncertaintyMeters = measurement.getAccumulatedDeltaRangeUncertaintyMeters();
        this.carrierCycles = measurement.getCarrierCycles();
//        this.carrierFrequencyHz = measurement.getCarrierFrequencyHz();
//        this.carrierPhase = measurement.getCarrierPhase();
//        this.carrierPhaseUncertainty = measurement.getCarrierPhaseUncertainty();
        this.cn0DbHz = measurement.getCn0DbHz();
        this.multipathIndicator = measurement.getMultipathIndicator();
        this.pseudorangeRateMetersPerSecond = measurement.getPseudorangeRateMetersPerSecond();
        this.pseudorangeRateUncertaintyMetersPerSecond = measurement.getPseudorangeRateUncertaintyMetersPerSecond();
        this.receivedSvTimeNanos = measurement.getReceivedSvTimeNanos();
        this.receivedSvTimeUncertaintyNanos = measurement.getReceivedSvTimeUncertaintyNanos();
//        this.snrInDb = measurement.getSnrInDb();
        this.timeOffsetNanos = measurement.getTimeOffsetNanos();
    }

    public int getSvID() {
        return svID;
    }

    public int getConstellationType() {
        return constellationType;
    }

    public double getAccumulatedDeltaRangeMeters() {
        return accumulatedDeltaRangeMeters;
    }

    public int getAccumulatedDeltaRangeState() {
        return accumulatedDeltaRangeState;
    }

    public double getAccumulatedDeltaRangeUncertaintyMeters() {
        return accumulatedDeltaRangeUncertaintyMeters;
    }

    public long getCarrierCycles() {
        return carrierCycles;
    }

    public double getCn0DbHz() {
        return cn0DbHz;
    }

    public int getMultipathIndicator() {
        return multipathIndicator;
    }

    public double getPseudorangeRateMetersPerSecond() {
        return pseudorangeRateMetersPerSecond;
    }

    public double getPseudorangeRateUncertaintyMetersPerSecond() {
        return pseudorangeRateUncertaintyMetersPerSecond;
    }

    public long getReceivedSvTimeNanos() {
        return receivedSvTimeNanos;
    }

    public long getReceivedSvTimeUncertaintyNanos() {
        return receivedSvTimeUncertaintyNanos;
    }

    public double getTimeOffsetNanos() {
        return timeOffsetNanos;
    }
}
