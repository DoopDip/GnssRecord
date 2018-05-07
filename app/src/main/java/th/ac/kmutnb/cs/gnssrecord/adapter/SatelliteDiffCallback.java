package th.ac.kmutnb.cs.gnssrecord.adapter;

import android.location.GnssMeasurement;
import android.support.v7.util.DiffUtil;

import java.util.List;

public class SatelliteDiffCallback extends DiffUtil.Callback {
    private static final String TAG = SatelliteDiffCallback.class.getSimpleName();

    private List<GnssMeasurement> measurementListOld;
    private List<GnssMeasurement> measurementListNew;

    public SatelliteDiffCallback(List<GnssMeasurement> gnssMeasurementsOld, List<GnssMeasurement> gnssMeasurementsNew) {
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
        return measurementOld.getSvid() == measurementNew.getSvid() && measurementOld.getConstellationType() == measurementNew.getConstellationType();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        GnssMeasurement measurementOld = measurementListOld.get(oldItemPosition);
        GnssMeasurement measurementNew = measurementListNew.get(newItemPosition);
        double cn0DbHzNew = measurementNew.getCn0DbHz();
        double cn0DbHzOld = measurementOld.getCn0DbHz();
        if (cn0DbHzOld > ListAdapter.STATUS_SATELLITE_GREEN && cn0DbHzNew > ListAdapter.STATUS_SATELLITE_GREEN)
            return true;
        else if (cn0DbHzOld > ListAdapter.STATUS_SATELLITE_YELLOW && cn0DbHzNew > ListAdapter.STATUS_SATELLITE_YELLOW)
            return true;
        else if (cn0DbHzOld < ListAdapter.STATUS_SATELLITE_YELLOW && cn0DbHzNew < ListAdapter.STATUS_SATELLITE_YELLOW)
            return true;
        else
            return false;
    }
}