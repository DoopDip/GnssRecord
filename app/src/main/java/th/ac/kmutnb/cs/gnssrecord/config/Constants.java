package th.ac.kmutnb.cs.gnssrecord.config;

import android.os.Build;

final public class Constants {

    public static final int VER_2_11 = 0;
    public static final int VER_3_03 = 1;

    public static final int STATUS_SATELLITE_GREEN = 30;
    public static final int STATUS_SATELLITE_YELLOW = 14;
    //public static final int STATUS_SATELLITE_RED = 2;

    public static final String KEY_RINEX_VER = "rinexVer"; // value 0=2.11, 1=3.03
    public static final String KEY_MARK_NAME = "markName";
    public static final String KEY_MARK_TYPE = "markType";
    public static final String KEY_OBSERVER_NAME = "observerName";
    public static final String KEY_OBSERVER_AGENCY_NAME = "observerAgencyName";
    public static final String KEY_RECEIVER_NUMBER = "receiverNumber";
    public static final String KEY_RECEIVER_TYPE = "receiverType";
    public static final String KEY_RECEIVER_VERSION = "receiverVersion";
    public static final String KEY_ANTENNA_NUMBER = "antennaNumber";
    public static final String KEY_ANTENNA_TYPE = "antennaType";
    public static final String KEY_ANTENNA_ECCENTRICITY_EAST = "antennaEccentricityEast";
    public static final String KEY_ANTENNA_ECCENTRICITY_NORTH = "antennaEccentricityNorth";
    public static final String KEY_ANTENNA_HEIGHT = "antennaHeight";

    public static final int DEF_RINEX_VER = Constants.VER_2_11;
    public static final String DEF_MARK_NAME = "GnssRecord";
    public static final String DEF_MARK_TYPE = "Geodetic";
    public static final String DEF_OBSERVER_NAME = "RINEX Logger user";
    public static final String DEF_OBSERVER_AGENCY_NAME = "GnssRecord";
    public static final String DEF_RECEIVER_NUMBER = Build.SERIAL;
    public static final String DEF_RECEIVER_TYPE = Build.MANUFACTURER;
    public static final String DEF_RECEIVER_VERSION = Build.PRODUCT;
    public static final String DEF_ANTENNA_NUMBER = Build.SERIAL;
    public static final String DEF_ANTENNA_TYPE = Build.PRODUCT;
    public static final String DEF_ANTENNA_ECCENTRICITY_EAST = "0.0000";
    public static final String DEF_ANTENNA_ECCENTRICITY_NORTH = "0.0000";
    public static final String DEF_ANTENNA_HEIGHT = "0.0000";

    public static final String FILE_SETTING = "gnss_record_setting";
}
