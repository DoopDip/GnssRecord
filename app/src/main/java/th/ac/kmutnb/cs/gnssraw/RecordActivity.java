package th.ac.kmutnb.cs.gnssraw;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecordActivity extends AppCompatActivity {

    private static final String TAG = "RecordActivity";

    private TextView textViewStartStop;
    private TextView textViewLog;
    private TextView textViewTotalSatellite;
    private ScrollView scrollViewLog;

    private boolean btnStatus = false;
    private boolean gnssStatus = false;

    private Handler handler;
    private List<GnssMeasurement> measurementList;
    private LocationManager locationManager;
    private String log;
    private GnssMeasurementsEvent.Callback measurementsEvent = new GnssMeasurementsEvent.Callback() {
        @Override
        public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
            super.onGnssMeasurementsReceived(eventArgs);
            measurementList.clear();
            measurementList.addAll(eventArgs.getMeasurements());
            log += logMeasurement(measurementList);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    textViewTotalSatellite.setText(String.valueOf(measurementList.size()));
                    textViewLog.setText(log);
                    scrollViewLog.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
            Log.i(TAG, "GnssMeasurementsEvent callback -> Satellite total : " + measurementList.size());
        }

        @Override
        public void onStatusChanged(int status) {
            super.onStatusChanged(status);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        measurementList = new ArrayList<>();
        handler = new Handler();
        log = new String();

        textViewStartStop = (TextView) findViewById(R.id.record_startStop);
        textViewLog = (TextView) findViewById(R.id.record_log);
        textViewTotalSatellite = (TextView) findViewById(R.id.record_totalSatellite);
        scrollViewLog = (ScrollView) findViewById(R.id.record_logScroll);

        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

        textViewStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!btnStatus) {
                    //Check Permission
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) return;
                    gnssStatus = locationManager.registerGnssMeasurementsCallback(measurementsEvent);
                    textViewStartStop.setText("Stop");
                    textViewStartStop.setBackgroundResource(R.drawable.record_bg_btn_stop);
                    textViewTotalSatellite.setVisibility(View.VISIBLE);
                    btnStatus = true;
                    Log.i(TAG, "Register callback -> measurementsEvent");
                } else {
                    locationManager.unregisterGnssMeasurementsCallback(measurementsEvent);
                    textViewStartStop.setText("Start");
                    textViewStartStop.setBackgroundResource(R.drawable.record_bg_btn_start);
                    textViewTotalSatellite.setVisibility(View.INVISIBLE);
                    btnStatus = false;
                    Log.i(TAG, "!! UnRegister callback -> measurementsEvent");
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gnssStatus) {
            locationManager.unregisterGnssMeasurementsCallback(measurementsEvent);
            Log.i(TAG, "!! UnRegister callback -> measurementsEvent");
        }
    }

    private String logMeasurement(List<GnssMeasurement> measurementList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String log = dateFormat.format(Calendar.getInstance().getTime())+ " > Svid : ";
        for (GnssMeasurement measurement: measurementList) {
            log += measurement.getSvid()+",";
        }
        log = log.substring(0, log.length()-1);
        log +="\n";
        return log;
    }
}
