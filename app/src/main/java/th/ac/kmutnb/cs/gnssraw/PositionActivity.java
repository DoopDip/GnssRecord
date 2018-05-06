package th.ac.kmutnb.cs.gnssraw;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class PositionActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    private static final String TAG = PositionActivity.class.getSimpleName();

    private GnssStatus.Callback gnssStatus;
    private LocationManager locationManager;

    private RelativeLayout relativeLayoutRadar;

    private SensorManager sensorManager;
    private float currentDegree = 0f;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        relativeLayoutRadar = findViewById(R.id.position_radar);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gnssStatus = new GnssStatus.Callback() {
            @Override
            public void onSatelliteStatusChanged(GnssStatus status) {
                super.onSatelliteStatusChanged(status);
                relativeLayoutRadar.removeAllViews();
                for (int i = 0; i < status.getSatelliteCount(); i++) {
                    Log.i(TAG, i + " = " + status.getAzimuthDegrees(i) + ", " + status.getElevationDegrees(i));
                    radarPosition(status.getAzimuthDegrees(i), status.getElevationDegrees(i), status.getConstellationType(i));
                }
            }
        };


    }

    private void radarPosition(float azimuth, float elevation, int type) {
        float margin = 20;
        float cX = relativeLayoutRadar.getLayoutParams().width / 2 - margin;
        float cY = relativeLayoutRadar.getLayoutParams().height / 2 - margin;
        float average = ((cX / 90) + (cY / 90)) / 2;

        float x = cX + Math.round(Math.cos(Math.toRadians(azimuth)) * (elevation * average));
        float y = cY + Math.round(Math.sin(Math.toRadians(azimuth)) * (elevation * average));

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(logoSatelliteImageResource(type));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(relativeLayoutRadar.getLayoutParams().width / 12, relativeLayoutRadar.getLayoutParams().height / 12);
        imageView.setLayoutParams(params);
        imageView.setX(x);
        imageView.setY(y);
        relativeLayoutRadar.addView(imageView);

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

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.registerGnssStatusCallback(gnssStatus);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
        locationManager.unregisterGnssStatusCallback(gnssStatus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0] + 90);
        RotateAnimation ra = new RotateAnimation(
                currentDegree, -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);
        relativeLayoutRadar.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

}