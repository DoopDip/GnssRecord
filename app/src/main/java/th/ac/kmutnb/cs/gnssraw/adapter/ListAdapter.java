package th.ac.kmutnb.cs.gnssraw.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.location.GnssMeasurement;
import android.location.GnssStatus;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.util.ArrayList;
import java.util.List;

import th.ac.kmutnb.cs.gnssraw.DetailActivity;
import th.ac.kmutnb.cs.gnssraw.R;

/**
 * Created by narit on 18/2/2018 AD.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListHolder> {

    private static final String TAG = ListAdapter.class.getSimpleName();

    private List<GnssMeasurement> measurementList;

    public ListAdapter(List<GnssMeasurement> measurementList) {
        this.measurementList = measurementList;
        Log.i(TAG, "Set List -> GnssMeasurement");
    }

    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ListHolder holder, int position) {
        final GnssMeasurement measurement = measurementList.get(position);
        final int logoSatellite = logoSatelliteImageResource(measurement.getConstellationType());
        final String nameSatellite = satelliteName(measurement.getConstellationType());

        holder.imageViewLogo.setImageResource(logoSatellite);
        holder.textViewSvId.setText(String.valueOf(measurement.getSvid()));
        holder.textViewSatelliteName.setText(nameSatellite);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(holder.imageViewLogo, "list_logoTransition");
                pairs[1] = new Pair<View, String>(holder.textViewSvId, "list_SvidTransition");
                pairs[2] = new Pair<View, String>(holder.textViewSatelliteName, "list_SatelliteNameTransition");

                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                        (Activity) v.getContext(),
                        pairs
                );

                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra("measurement", measurement);
                intent.putExtra("logoSatellite", logoSatellite);
                intent.putExtra("nameSatellite", nameSatellite);
                v.getContext().startActivity(intent, activityOptions.toBundle());
                Log.i(TAG, "Click -> DetailActivity by Svid = " + measurement.getSvid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return measurementList.size();
    }

    static class ListHolder extends RecyclerView.ViewHolder {

        public LinearLayout linearLayout;
        public ImageView imageViewLogo;
        public TextView textViewSvId;
        public TextView textViewSatelliteName;

        public ListHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.adapter_list);
            imageViewLogo = itemView.findViewById(R.id.adapter_list_logo);
            textViewSvId = itemView.findViewById(R.id.adapter_list_svId);
            textViewSatelliteName = itemView.findViewById(R.id.adapter_list_satelliteName);
        }
    }

    public void updateGnssMeasurementList(List<GnssMeasurement> measurementListOld, List<GnssMeasurement> measurementListNew) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SatelliteDiffCallback(measurementListOld, measurementListNew));
        diffResult.dispatchUpdatesTo(this);
    }

    private String satelliteName(int svId) {
        if (svId == GnssStatus.CONSTELLATION_GPS)
            return "GPS";
        else if (svId == GnssStatus.CONSTELLATION_SBAS)
            return "SBAS";
        else if (svId == GnssStatus.CONSTELLATION_GLONASS)
            return "GLONASS";
        else if (svId == GnssStatus.CONSTELLATION_QZSS)
            return "QZSS";
        else if (svId == GnssStatus.CONSTELLATION_BEIDOU)
            return "BEIDOU";
        else if (svId == GnssStatus.CONSTELLATION_GALILEO)
            return "GALILEO";
        else
            return "UNKNOW";
    }

    private int logoSatelliteImageResource(int constellationType) {
        int logoImageResource = R.drawable.logo_unknow;
        if (constellationType == GnssStatus.CONSTELLATION_GPS)
            logoImageResource = R.drawable.logo_gps;
        else if (constellationType == GnssStatus.CONSTELLATION_SBAS)
            logoImageResource = R.drawable.logo_sbas;
        else if (constellationType == GnssStatus.CONSTELLATION_GLONASS)
            logoImageResource = R.drawable.logo_glonass;
        else if (constellationType == GnssStatus.CONSTELLATION_QZSS)
            logoImageResource = R.drawable.logo_qzss;
        else if (constellationType == GnssStatus.CONSTELLATION_BEIDOU)
            logoImageResource = R.drawable.logo_beidou;
        else if (constellationType == GnssStatus.CONSTELLATION_GALILEO)
            logoImageResource = R.drawable.logo_galileo;
        return logoImageResource;
    }
}
