package th.ac.kmutnb.cs.gnssrecord.model;

public class RinexHeader {
    private String markName;
    private String observerName;
    private String observerAgencyName;
    private String receiverNumber;
    private String receiverType;
    private String receiverVersion;
    private String antennaNumber;
    private String antennaType;
    private double antennaEccentricityEast;
    private double antennaEccentricityNorth;
    private double antennaHeight;
    private String cartesianX;
    private String cartesianY;
    private String cartesianZ;
    private long gpsTime;

    public RinexHeader(String markName, String observerName, String observerAgencyName, String receiverNumber, String receiverType, String receiverVersion, String antennaNumber, String antennaType, double antennaEccentricityEast, double antennaEccentricityNorth, double antennaHeight, String cartesianX, String cartesianY, String cartesianZ, long gpsTime) {
        this.markName = markName;
        this.observerName = observerName;
        this.observerAgencyName = observerAgencyName;
        this.receiverNumber = receiverNumber;
        this.receiverType = receiverType;
        this.receiverVersion = receiverVersion;
        this.antennaNumber = antennaNumber;
        this.antennaType = antennaType;
        this.antennaEccentricityEast = antennaEccentricityEast;
        this.antennaEccentricityNorth = antennaEccentricityNorth;
        this.antennaHeight = antennaHeight;
        this.cartesianX = cartesianX;
        this.cartesianY = cartesianY;
        this.cartesianZ = cartesianZ;
        this.gpsTime = gpsTime;
    }

    public String getMarkName() {
        return markName;
    }

    public String getObserverName() {
        return observerName;
    }

    public String getObserverAgencyName() {
        return observerAgencyName;
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public String getReceiverType() {
        return receiverType;
    }

    public String getReceiverVersion() {
        return receiverVersion;
    }

    public String getAntennaNumber() {
        return antennaNumber;
    }

    public String getAntennaType() {
        return antennaType;
    }

    public double getAntennaEccentricityEast() {
        return antennaEccentricityEast;
    }

    public double getAntennaEccentricityNorth() {
        return antennaEccentricityNorth;
    }

    public double getAntennaHeight() {
        return antennaHeight;
    }

    public String getCartesianX() {
        return cartesianX;
    }

    public String getCartesianY() {
        return cartesianY;
    }

    public String getCartesianZ() {
        return cartesianZ;
    }

    public long getGpsTime() {
        return gpsTime;
    }
}
