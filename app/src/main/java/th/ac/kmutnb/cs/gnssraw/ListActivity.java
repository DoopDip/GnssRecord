package th.ac.kmutnb.cs.gnssraw;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;

import java.util.ArrayList;
import java.util.List;

import th.ac.kmutnb.cs.gnssraw.adapter.ListAdapter;
import th.ac.kmutnb.cs.gnssraw.adapter.SatelliteDiffCallback;

public class ListActivity extends AppCompatActivity {

    private static final String TAG = ListActivity.class.getSimpleName();
    private static final int PERMISSIONS_ACCESS_FINE_LOCATION = 99;

    private RecyclerView recyclerView;
    private ListAdapter listAdapter;

    private TextView textViewTotalSatellite;

    private Handler handler;
    private List<GnssMeasurement> measurementListNew;
    private List<GnssMeasurement> measurementListOld;
    private LocationManager locationManager;
    private GnssMeasurementsEvent.Callback measurementsEvent = new GnssMeasurementsEvent.Callback() {
        @Override
        public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
            super.onGnssMeasurementsReceived(eventArgs);
            measurementListOld.clear();
            measurementListOld.addAll(measurementListNew);
            measurementListNew.clear();
            measurementListNew.addAll(eventArgs.getMeasurements());
            Log.i(TAG, "GnssMeasurementsEvent callback -> Satellite total : " + measurementListNew.size());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    textViewTotalSatellite.setText(String.valueOf(measurementListNew.size()));
                    listAdapter.updateGnssMeasurementList(measurementListOld, measurementListNew);
                }
            });
        }

        @Override
        public void onStatusChanged(int status) {
            super.onStatusChanged(status);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Log.i(TAG, "onCreate");

        measurementListNew = new ArrayList<>();
        measurementListOld = new ArrayList<>();
        handler = new Handler();

        listAdapter = new ListAdapter(measurementListNew);

        textViewTotalSatellite = findViewById(R.id.list_totalSatellite);
        textViewTotalSatellite.setText("0");

        recyclerView = findViewById(R.id.list_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(listAdapter);
        //new GravitySnapHelper(Gravity.START).attachToRecyclerView(recyclerView);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check Permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);
        }
        locationManager.registerGnssMeasurementsCallback(measurementsEvent);
        Log.i(TAG, "Register callback -> measurementsEvent");
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.unregisterGnssMeasurementsCallback(measurementsEvent);
        Log.i(TAG, "!! UnRegister callback -> measurementsEvent");
    }
}
