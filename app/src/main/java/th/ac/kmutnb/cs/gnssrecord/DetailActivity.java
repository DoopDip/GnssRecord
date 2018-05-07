package th.ac.kmutnb.cs.gnssrecord;

import android.content.pm.ActivityInfo;
import android.location.GnssMeasurement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private GnssMeasurement measurement;

    private ImageView imageViewHeadLogo;
    private TextView textViewHeadSvid;
    private TextView textViewHeadSatelliteName;

    private TextView textViewSvID;
    private TextView textViewConstellationType;
    private TextView textViewAccumulatedDeltaRangeMeters;
    private TextView textViewAccumulatedDeltaRangeState;
    private TextView textViewAccumulatedDeltaRangeUncertaintyMeters;
    private TextView textViewCarrierCycles;
    private TextView textViewCarrierFrequencyHz;
    private TextView textViewCarrierPhase;
    private TextView textViewCarrierPhaseUncertainty;
    private TextView textViewCn0DbHz;
    private TextView textViewMultipathIndicator;
    private TextView textViewPseudorangeRateMetersPerSecond;
    private TextView textViewPseudorangeRateUncertaintyMetersPerSecond;
    private TextView textViewReceivedSvTimeNanos;
    private TextView textViewReceivedSvTimeUncertaintyNanos;
    private TextView textViewSnrInDb;
    private TextView textViewTimeOffsetNanos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        measurement = Objects.requireNonNull(getIntent().getExtras()).getParcelable("measurement");
        int logoSatellite = getIntent().getExtras().getInt("logoSatellite");
        String nameSatellite = getIntent().getExtras().getString("nameSatellite");

        imageViewHeadLogo = findViewById(R.id.detail_headLogo);
        textViewHeadSvid = findViewById(R.id.detail_headSvid);
        textViewHeadSatelliteName = findViewById(R.id.detail_headSatelliteName);

        textViewSvID = findViewById(R.id.detail_svID);
        textViewConstellationType = findViewById(R.id.detail_constellationType);
        textViewAccumulatedDeltaRangeMeters = findViewById(R.id.detail_accumulatedDeltaRangeMeters);
        textViewAccumulatedDeltaRangeState = findViewById(R.id.detail_accumulatedDeltaRangeState);
        textViewAccumulatedDeltaRangeUncertaintyMeters = findViewById(R.id.detail_accumulatedDeltaRangeUncertaintyMeters);
        textViewCarrierCycles = findViewById(R.id.detail_carrierCycles);
        textViewCarrierFrequencyHz = findViewById(R.id.detail_carrierFrequencyHz);
        textViewCarrierPhase = findViewById(R.id.detail_carrierPhase);
        textViewCarrierPhaseUncertainty = findViewById(R.id.detail_carrierPhaseUncertainty);
        textViewCn0DbHz = findViewById(R.id.detail_cn0DbHz);
        textViewMultipathIndicator = findViewById(R.id.detail_multipathIndicator);
        textViewPseudorangeRateMetersPerSecond = findViewById(R.id.detail_pseudorangeRateMetersPerSecond);
        textViewPseudorangeRateUncertaintyMetersPerSecond = findViewById(R.id.detail_pseudorangeRateUncertaintyMetersPerSecond);
        textViewReceivedSvTimeNanos = findViewById(R.id.detail_receivedSvTimeNanos);
        textViewReceivedSvTimeUncertaintyNanos = findViewById(R.id.detail_receivedSvTimeUncertaintyNanos);
        textViewSnrInDb = findViewById(R.id.detail_snrInDb);
        textViewTimeOffsetNanos = findViewById(R.id.detail_timeOffsetNanos);

        imageViewHeadLogo.setImageResource(logoSatellite);
        textViewHeadSvid.setText(String.valueOf(measurement.getSvid()));
        textViewHeadSatelliteName.setText(nameSatellite);

        textViewSvID.setText(String.valueOf(measurement.getSvid()));
        textViewConstellationType.setText(String.valueOf(measurement.getConstellationType()));
        textViewAccumulatedDeltaRangeMeters.setText(String.valueOf(measurement.getAccumulatedDeltaRangeMeters()));
        textViewAccumulatedDeltaRangeState.setText(String.valueOf(measurement.getAccumulatedDeltaRangeState()));
        textViewAccumulatedDeltaRangeUncertaintyMeters.setText(String.valueOf(measurement.getAccumulatedDeltaRangeUncertaintyMeters()));
        textViewCarrierCycles.setText(String.valueOf(measurement.getCarrierCycles()));
        textViewCarrierFrequencyHz.setText(String.valueOf(measurement.getCarrierFrequencyHz()));
        textViewCarrierPhase.setText(String.valueOf(measurement.getCarrierPhase()));
        textViewCarrierPhaseUncertainty.setText(String.valueOf(measurement.getCarrierPhaseUncertainty()));
        textViewCn0DbHz.setText(String.valueOf(measurement.getCn0DbHz()));
        textViewMultipathIndicator.setText(String.valueOf(measurement.getMultipathIndicator()));
        textViewPseudorangeRateMetersPerSecond.setText(String.valueOf(measurement.getPseudorangeRateMetersPerSecond()));
        textViewPseudorangeRateUncertaintyMetersPerSecond.setText(String.valueOf(measurement.getPseudorangeRateUncertaintyMetersPerSecond()));
        textViewReceivedSvTimeNanos.setText(String.valueOf(measurement.getReceivedSvTimeNanos()));
        textViewReceivedSvTimeUncertaintyNanos.setText(String.valueOf(measurement.getReceivedSvTimeUncertaintyNanos()));
        textViewSnrInDb.setText(String.valueOf(measurement.getSnrInDb()));
        textViewTimeOffsetNanos.setText(String.valueOf(measurement.getTimeOffsetNanos()));

    }
}
