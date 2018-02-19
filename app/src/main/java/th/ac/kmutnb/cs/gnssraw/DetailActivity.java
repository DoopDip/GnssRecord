package th.ac.kmutnb.cs.gnssraw;

import android.location.GnssMeasurement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

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

        measurement = getIntent().getExtras().getParcelable("measurement");
        int logoSatellite = getIntent().getExtras().getInt("logoSatellite");
        String nameSatellite = getIntent().getExtras().getString("nameSatellite");

        imageViewHeadLogo = (ImageView) findViewById(R.id.detail_headLogo);
        textViewHeadSvid = (TextView) findViewById(R.id.detail_headSvid);
        textViewHeadSatelliteName = (TextView) findViewById(R.id.detail_headSatelliteName);
        
        textViewSvID = (TextView) findViewById(R.id.detail_svID);
        textViewConstellationType = (TextView) findViewById(R.id.detail_constellationType);
        textViewAccumulatedDeltaRangeMeters = (TextView) findViewById(R.id.detail_accumulatedDeltaRangeMeters);
        textViewAccumulatedDeltaRangeState = (TextView) findViewById(R.id.detail_accumulatedDeltaRangeState);
        textViewAccumulatedDeltaRangeUncertaintyMeters = (TextView) findViewById(R.id.detail_accumulatedDeltaRangeUncertaintyMeters);
        textViewCarrierCycles = (TextView) findViewById(R.id.detail_carrierCycles);
        textViewCarrierFrequencyHz = (TextView) findViewById(R.id.detail_carrierFrequencyHz);
        textViewCarrierPhase = (TextView) findViewById(R.id.detail_carrierPhase);
        textViewCarrierPhaseUncertainty = (TextView) findViewById(R.id.detail_carrierPhaseUncertainty);
        textViewCn0DbHz = (TextView) findViewById(R.id.detail_cn0DbHz);
        textViewMultipathIndicator = (TextView) findViewById(R.id.detail_multipathIndicator);
        textViewPseudorangeRateMetersPerSecond = (TextView) findViewById(R.id.detail_pseudorangeRateMetersPerSecond);
        textViewPseudorangeRateUncertaintyMetersPerSecond = (TextView) findViewById(R.id.detail_pseudorangeRateUncertaintyMetersPerSecond);
        textViewReceivedSvTimeNanos = (TextView) findViewById(R.id.detail_receivedSvTimeNanos);
        textViewReceivedSvTimeUncertaintyNanos = (TextView) findViewById(R.id.detail_receivedSvTimeUncertaintyNanos);
        textViewSnrInDb = (TextView) findViewById(R.id.detail_snrInDb);
        textViewTimeOffsetNanos = (TextView) findViewById(R.id.detail_timeOffsetNanos);


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
