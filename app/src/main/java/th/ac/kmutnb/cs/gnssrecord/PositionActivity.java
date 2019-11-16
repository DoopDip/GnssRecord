package th.ac.kmutnb.cs.gnssrecord;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import th.ac.kmutnb.cs.gnssrecord.config.Constants;

public class PositionActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    private static final String TAG = PositionActivity.class.getSimpleName();

    private GnssStatus.Callback gnssStatus;
    private LocationManager locationManager;

    private RelativeLayout relativeLayoutRadar;

    private SensorManager sensorManager;
    private float currentDegree = 0f;

    private TextView textViewTotalGps;
    private TextView textViewTotalSbas;
    private TextView textViewTotalGlonass;
    private TextView textViewTotalQzss;
    private TextView textViewTotalBeidou;
    private TextView textViewTotalGalileo;

    private TextView textViewTotalSatellite;
    private LinearLayout linearLayoutGroupSatelliteIcon;

    private int totalGps = 0;
    private int totalSbas = 0;
    private int totalGlonass = 0;
    private int totalQzss = 0;
    private int totalBeidou = 0;
    private int totalGalileo = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        textViewTotalGps = findViewById(R.id.position_totalGps);
        textViewTotalSbas = findViewById(R.id.position_totalSbas);
        textViewTotalGlonass = findViewById(R.id.position_totalGlonass);
        textViewTotalQzss = findViewById(R.id.position_totalQzss);
        textViewTotalBeidou = findViewById(R.id.position_totalBeidou);
        textViewTotalGalileo = findViewById(R.id.position_totalGalileo);

        textViewTotalSatellite = findViewById(R.id.position_totalSatellite);
        linearLayoutGroupSatelliteIcon = findViewById(R.id.position_groupSatelliteIcon);

        relativeLayoutRadar = findViewById(R.id.position_radar);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gnssStatus = new GnssStatus.Callback() {
            @Override
            public void onSatelliteStatusChanged(GnssStatus status) {
                super.onSatelliteStatusChanged(status);
                totalSatelliteSetZero();
                relativeLayoutRadar.removeAllViews();
                for (int i = 0; i < status.getSatelliteCount(); i++)
                    radarPosition(status.getAzimuthDegrees(i),
                            status.getElevationDegrees(i),
                            status.getConstellationType(i),
                            status.getCn0DbHz(i));

                totalSatelliteTextView();
                textViewTotalSatellite.setText(String.valueOf(status.getSatelliteCount()));
            }
        };

        linearLayoutGroupSatelliteIcon.setOnClickListener(v -> new MaterialDialog.Builder(v.getContext())
                .customView(R.layout.dialog_satellite, true)
                .positiveText(R.string.close)
                .show());
    }

    private void radarPosition(float azimuth, float elevation, int type, float cn0DbHz) {
        int radarWidth = relativeLayoutRadar.getLayoutParams().width;
        int radarHeight = relativeLayoutRadar.getLayoutParams().height;
        int sizeSatellite = 20;

        int centerWidth = radarWidth / 2;
        int centerHeight = radarHeight / 2;
        if (centerWidth > centerHeight) centerHeight -= sizeSatellite;
        else centerHeight = centerWidth - sizeSatellite;

        int n = Math.round(centerHeight / 15);

        double d1 = (90.0F - elevation) / 90.0F * centerHeight;
        double d2 = azimuth * Math.PI / 180.0D;
        sizeSatellite += centerHeight;
        int x = ((int) Math.round(Math.sin(d2) * d1) + sizeSatellite) - n;
        int y = (sizeSatellite - (int) Math.round(d1 * Math.cos(d2))) - n;

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(logoAndTotalSatellite(type));
        if (cn0DbHz > Constants.STATUS_SATELLITE_GREEN)
            imageView.setColorFilter(Color.parseColor("#99cc00"));
        else if (cn0DbHz > Constants.STATUS_SATELLITE_YELLOW)
            imageView.setColorFilter(Color.parseColor("#ffbb33"));
        else imageView.setColorFilter(Color.parseColor("#ff4444"));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(radarWidth / 12, radarHeight / 12);
        imageView.setLayoutParams(params);
        imageView.setRotation(currentDegree);
        imageView.setX(x);
        imageView.setY(y);
        relativeLayoutRadar.addView(imageView);
    }

    private int logoAndTotalSatellite(int constellationType) {
        int logoImageResource = R.drawable.logo_unknow;
        if (constellationType == GnssStatus.CONSTELLATION_GPS) {
            logoImageResource = R.drawable.ic_position_1;
            totalGps++;
        } else if (constellationType == GnssStatus.CONSTELLATION_SBAS) {
            logoImageResource = R.drawable.ic_position_2;
            totalSbas++;
        } else if (constellationType == GnssStatus.CONSTELLATION_GLONASS) {
            logoImageResource = R.drawable.ic_position_3;
            totalGlonass++;
        } else if (constellationType == GnssStatus.CONSTELLATION_QZSS) {
            logoImageResource = R.drawable.ic_position_4;
            totalQzss++;
        } else if (constellationType == GnssStatus.CONSTELLATION_BEIDOU) {
            logoImageResource = R.drawable.ic_position_5;
            totalBeidou++;
        } else if (constellationType == GnssStatus.CONSTELLATION_GALILEO) {
            logoImageResource = R.drawable.ic_position_6;
            totalGalileo++;
        }
        return logoImageResource;
    }

    private void totalSatelliteSetZero() {
        totalGps = 0;
        totalSbas = 0;
        totalGlonass = 0;
        totalQzss = 0;
        totalBeidou = 0;
        totalGalileo = 0;
    }

    private void totalSatelliteTextView() {
        textViewTotalGps.setText(String.valueOf(totalGps));
        textViewTotalSbas.setText(String.valueOf(totalSbas));
        textViewTotalGlonass.setText(String.valueOf(totalGlonass));
        textViewTotalQzss.setText(String.valueOf(totalQzss));
        textViewTotalBeidou.setText(String.valueOf(totalBeidou));
        textViewTotalGalileo.setText(String.valueOf(totalGalileo));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.registerGnssStatusCallback(gnssStatus);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Log.i(TAG, "registerGnssStatusCallback -> gnssStatus");
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
        locationManager.unregisterGnssStatusCallback(gnssStatus);
        Log.i(TAG, "unregisterListener -> sensorManager");
        Log.i(TAG, "unregisterGnssStatusCallback -> gnssStatus");
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        Log.i(TAG, "registerListener -> sensorManager");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        currentDegree = Math.round(event.values[0]);
        relativeLayoutRadar.setRotation(-currentDegree);
        for (int i = 0; i < relativeLayoutRadar.getChildCount(); i++) {
            relativeLayoutRadar.getChildAt(i).setRotation(currentDegree);
        }
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