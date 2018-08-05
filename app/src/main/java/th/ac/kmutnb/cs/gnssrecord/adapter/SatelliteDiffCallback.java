package th.ac.kmutnb.cs.gnssrecord.adapter;

import android.location.GnssMeasurement;
import android.support.v7.util.DiffUtil;

import java.util.List;

import th.ac.kmutnb.cs.gnssrecord.config.Constants;

public class SatelliteDiffCallback extends DiffUtil.Callback {

    private List<GnssMeasurement> measurementListOld;
    private List<GnssMeasurement> measurementListNew;

    SatelliteDiffCallback(List<GnssMeasurement> gnssMeasurementsOld, List<GnssMeasurement> gnssMeasurementsNew) {
        this.measurementListOld = gnssMeasurementsOld;
        this.measurementListNew = gnssMeasurementsNew;
    }

    @Override
    public int getOldListSize() {
        return measurementListOld != null ? measurementListOld.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return measurementListNew != null ? measurementListNew.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        GnssMeasurement measurementOld = measurementListOld.get(oldItemPosition);
        GnssMeasurement measurementNew = measurementListNew.get(newItemPosition);
        return measurementOld.getSvid() == measurementNew.getSvid() &&
                measurementOld.getConstellationType() == measurementNew.getConstellationType();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        GnssMeasurement measurementOld = measurementListOld.get(oldItemPosition);
        GnssMeasurement measurementNew = measurementListNew.get(newItemPosition);
        double cn0DbHzNew = measurementNew.getCn0DbHz();
        double cn0DbHzOld = measurementOld.getCn0DbHz();
        return cn0DbHzOld > Constants.STATUS_SATELLITE_GREEN && cn0DbHzNew > Constants.STATUS_SATELLITE_GREEN ||
                cn0DbHzOld > Constants.STATUS_SATELLITE_YELLOW && cn0DbHzNew > Constants.STATUS_SATELLITE_YELLOW ||
                cn0DbHzOld < Constants.STATUS_SATELLITE_YELLOW && cn0DbHzNew < Constants.STATUS_SATELLITE_YELLOW;
    }
}
