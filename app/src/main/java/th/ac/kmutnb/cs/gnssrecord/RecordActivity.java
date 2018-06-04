package th.ac.kmutnb.cs.gnssrecord;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import th.ac.kmutnb.cs.gnssrecord.model.RinexData;
import th.ac.kmutnb.cs.gnssrecord.model.RinexHeader;
import th.ac.kmutnb.cs.gnssrecord.rinex.Rinex;

public class RecordActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = RecordActivity.class.getSimpleName();

    private static final double SPEED_OF_LIGHT = 299792458.0; // [m/s]
    private static final double GPS_L1_FREQ = 154.0 * 10.23e6;
    private static final double GPS_L1_WAVELENGTH = SPEED_OF_LIGHT / GPS_L1_FREQ;
    private static final double GPS_WEEK_SECS = 604800; // Number of seconds in a week
    private static final double NS_TO_S = 1.0e-9;

    private TextView textViewWelcome;
    private TextView textViewName;
    private TextView textViewBtnStartStop;
    private TextView textViewBtnScroll;
    private TextView textViewBtnLogOut;
    private ScrollView scrollViewLog;
    private TextView textViewLog;
    private TextView textViewBtnFile;
    private TextView textViewBtnSetting;
    private LinearLayout linearLayoutGroupMenu;

    private Rinex rinex;

    private SharedPreferences sharedPreferences;

    private boolean statusRecord;
    private boolean statusScroll;

    private float tempYBtnStart;

    private Location location;
    private Boolean locationStatus;
    private long gpsTime;

    private StringBuilder log;
    private Handler handler;
    private LocationManager locationManager;
    private GnssMeasurementsEvent.Callback measurementsEvent =
            new GnssMeasurementsEvent.Callback() {
                @Override
                public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
                    super.onGnssMeasurementsReceived(eventArgs);
                    if (statusRecord) { //หากมีการบันทึกค่าก็จะทำการดึงรายการดาวเทียมและนำไปเขียนลง file
                        ArrayList<GnssMeasurement> measurementList = new ArrayList<>(eventArgs.getMeasurements());
                        writeRecordRinex(measurementList, eventArgs.getClock());
                    }

                    gpsTime += 1000; //บวกเวลา Gps ให้เพิ่มขึ้น 1 วินาที ทุกครั้งเมื่อ GnssMeasurementsEvent.Callback ทำงาน
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = new Date(gpsTime);
                    Log.i(TAG, "GnssMeasurements [available], GPS time (UTC): " + simpleDateFormat.format(date));
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        sharedPreferences = getSharedPreferences(SettingActivity.FILE_SETTING, 0);

        log = new StringBuilder();
        handler = new Handler();
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        statusRecord = false;
        statusScroll = false;

        locationStatus = false;

        textViewWelcome = findViewById(R.id.record_welcome);
        textViewName = findViewById(R.id.record_name);
        textViewBtnStartStop = findViewById(R.id.record_btnStartStop);
        textViewBtnScroll = findViewById(R.id.record_btnScroll);
        textViewBtnLogOut = findViewById(R.id.record_btnLogOut);
        scrollViewLog = findViewById(R.id.record_logScroll);
        textViewLog = findViewById(R.id.record_log);
        textViewBtnFile = findViewById(R.id.record_btnFile);
        textViewBtnSetting = findViewById(R.id.record_btnSetting);
        linearLayoutGroupMenu = findViewById(R.id.record_groupMenu);

        if (firebaseUser != null) textViewName.setText(firebaseUser.getDisplayName());
        tempYBtnStart = textViewBtnStartStop.getY();

        btnEvent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (statusRecord) {
            stopRecordRinex();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            statusRecord = false;
        }
        unregisterGnssMeasurements();
        locationStatus = false;
        textViewBtnStartStop.setBackgroundResource(R.drawable.bg_btn_gray);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerGnssMeasurements();
    }

    private void btnEvent() {
        textViewBtnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!statusRecord) {
                    if (locationStatus) {
                        Log.i(TAG, "Click -> textViewStart = Start");
                        logClear();
                        animationClickStart();
                        registerGnssMeasurements();
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        startRecordRinex();
                        statusRecord = true;
                    } else
                        Snackbar.make(textViewBtnScroll, R.string.wait_location, Snackbar.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Click -> textViewStart = Stop");
                    statusRecord = false;
                    stopRecordRinex();
                    animationClickStop();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }
        });

        textViewBtnScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!statusScroll) {
                    Log.i(TAG, "Click -> textViewBtnScroll = UnAuto");
                    textViewBtnScroll.setText(R.string.auto_scrollbar);
                    Snackbar.make(textViewBtnScroll, R.string.un_auto_scrollbar, Snackbar.LENGTH_SHORT).show();
                    statusScroll = true;
                } else {
                    Log.i(TAG, "Click -> textViewBtnScroll = Auto");
                    textViewBtnScroll.setText(R.string.un_auto_scrollbar);
                    Snackbar.make(textViewBtnScroll, R.string.auto_scrollbar, Snackbar.LENGTH_SHORT).show();
                    statusScroll = false;
                }
            }
        });

        textViewBtnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> LogOut");
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(RecordActivity.this, MainActivity.class));
                finish();
            }
        });

        textViewBtnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> File");
                startActivity(
                        new Intent(RecordActivity.this, FileActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext()).toBundle()
                );
            }
        });

        textViewBtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> Setting (RinexHeader)");
                startActivity(
                        new Intent(RecordActivity.this, SettingActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext()).toBundle()
                );
            }
        });
    }

    private void registerGnssMeasurements() {
        //Check Permission
        if (ActivityCompat.checkSelfPermission(RecordActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RecordActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    101);
        }
        locationManager.registerGnssMeasurementsCallback(measurementsEvent);
        Log.i(TAG, "Register callback -> measurementsEvent");

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        Log.i(TAG, "requestLocationUpdates -> GPS");
    }

    private void unregisterGnssMeasurements() {
        locationManager.unregisterGnssMeasurementsCallback(measurementsEvent);
        Log.i(TAG, "!! UnRegister callback -> measurementsEvent");

        locationStatus = false;
        Log.i(TAG, "!! locationStatus -> false");
    }

    private void animationClickStop() {
        textViewWelcome.setVisibility(View.VISIBLE);
        textViewName.setVisibility(View.VISIBLE);
        textViewBtnStartStop.setBackgroundResource(R.drawable.bg_btn_green);
        textViewBtnStartStop.setText(R.string.start);
        textViewBtnScroll.setVisibility(View.INVISIBLE);
        textViewBtnScroll.setAlpha(0f);
        textViewBtnLogOut.setVisibility(View.VISIBLE);
        linearLayoutGroupMenu.setVisibility(View.VISIBLE);
        //Animation
        ObjectAnimator.ofFloat(textViewBtnStartStop, View.TRANSLATION_Y, tempYBtnStart)
                .setDuration(500).start();
        ObjectAnimator animatorLog = ObjectAnimator.ofFloat(scrollViewLog, View.ALPHA, 0f);
        animatorLog.setDuration(200);
        animatorLog.start();
        animatorLog.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                scrollViewLog.setVisibility(View.INVISIBLE);
            }
        });
        ObjectAnimator.ofFloat(textViewBtnLogOut, View.ALPHA, 1f)
                .setDuration(1000).start();
        ObjectAnimator.ofFloat(linearLayoutGroupMenu, View.ALPHA, 1f)
                .setDuration(1000).start();
    }

    private void animationClickStart() {
        textViewWelcome.setVisibility(View.INVISIBLE);
        textViewName.setVisibility(View.INVISIBLE);
        textViewBtnStartStop.setBackgroundResource(R.drawable.bg_btn_red);
        textViewBtnStartStop.setText(R.string.stop);
        scrollViewLog.setVisibility(View.VISIBLE);
        textViewBtnScroll.setVisibility(View.VISIBLE);
        textViewBtnLogOut.setVisibility(View.INVISIBLE);
        textViewBtnLogOut.setAlpha(0f);
        linearLayoutGroupMenu.setVisibility(View.INVISIBLE);
        linearLayoutGroupMenu.setAlpha(0f);
        //Animation
        ObjectAnimator.ofFloat(textViewBtnStartStop, View.TRANSLATION_Y, tempYBtnStart - 380)
                .setDuration(500).start();
        ObjectAnimator animatorLog = ObjectAnimator.ofFloat(scrollViewLog, View.ALPHA, 1f);
        animatorLog.setDuration(1000);
        animatorLog.setStartDelay(200);
        animatorLog.start();
        ObjectAnimator animatorBtnScroll = ObjectAnimator.ofFloat(textViewBtnScroll, View.ALPHA, 1f);
        animatorBtnScroll.setDuration(800);
        animatorBtnScroll.setStartDelay(700);
        animatorBtnScroll.start();
    }

    private void startRecordRinex() {

        double lng = location.getLongitude(); //ดึงค่า Longitude
        double lat = location.getLatitude(); //ดึงค่า Latitude
        double r = 6371000 + location.getAltitude(); //หาค่า R โดยนำ
        double cartesianX = r * Math.cos(lat) * Math.cos(lng);
        double cartesianY = r * Math.cos(lat) * Math.sin(lng);
        double cartesianZ = r * Math.sin(lat);
        Log.i(TAG, "Lat: " + lat + ", Lng: " + lng + ", Alt: " + location.getAltitude());
        Log.i(TAG, "Cartesian X: " + cartesianX + ", Y:" + cartesianY + ", Z:" + cartesianZ);
        DecimalFormat decimalFormat = new DecimalFormat("#.####");

        rinex = new Rinex(getApplicationContext());
        rinex.writeHeader(new RinexHeader(
                sharedPreferences.getString(SettingActivity.KEY_MARK_NAME, SettingActivity.DEF_MARK_NAME),
                sharedPreferences.getString(SettingActivity.KEY_OBSERVER_NAME, SettingActivity.DEF_OBSERVER_NAME),
                sharedPreferences.getString(SettingActivity.KEY_OBSERVER_AGENCY_NAME, SettingActivity.DEF_OBSERVER_AGENCY_NAME),
                sharedPreferences.getString(SettingActivity.KEY_RECEIVER_NUMBER, SettingActivity.DEF_RECEIVER_NUMBER),
                sharedPreferences.getString(SettingActivity.KEY_RECEIVER_TYPE, SettingActivity.DEF_RECEIVER_TYPE),
                sharedPreferences.getString(SettingActivity.KEY_RECEIVER_VERSION, SettingActivity.DEF_RECEIVER_VERSION),
                sharedPreferences.getString(SettingActivity.KEY_ANTENNA_NUMBER, SettingActivity.DEF_ANTENNA_NUMBER),
                sharedPreferences.getString(SettingActivity.KEY_ANTENNA_TYPE, SettingActivity.DEF_ANTENNA_TYPE),
                Double.parseDouble(sharedPreferences.getString(SettingActivity.KEY_ANTENNA_ECCENTRICITY_EAST, SettingActivity.DEF_ANTENNA_ECCENTRICITY_EAST)),
                Double.parseDouble(sharedPreferences.getString(SettingActivity.KEY_ANTENNA_ECCENTRICITY_NORTH, SettingActivity.DEF_ANTENNA_ECCENTRICITY_NORTH)),
                Double.parseDouble(sharedPreferences.getString(SettingActivity.KEY_ANTENNA_HEIGHT, SettingActivity.DEF_ANTENNA_HEIGHT)),
                decimalFormat.format(cartesianX),
                decimalFormat.format(cartesianY),
                decimalFormat.format(cartesianZ),
                gpsTime
        ));
    }

    private void stopRecordRinex() {
        rinex.closeFile();
    }

    private void writeRecordRinex(ArrayList<GnssMeasurement> measurementList, GnssClock clock) {
        ArrayList<RinexData> rinexData = new ArrayList<>();
        for (GnssMeasurement measurement : measurementList) {
            int constellationType = measurement.getConstellationType();
            if (constellationType == GnssStatus.CONSTELLATION_GPS || constellationType == GnssStatus.CONSTELLATION_GLONASS || constellationType == GnssStatus.CONSTELLATION_GALILEO) {

                double fullBiasNanos = clock.getFullBiasNanos();
                double gpsWeek = Math.floor(-fullBiasNanos * NS_TO_S / GPS_WEEK_SECS);
                double local_est_GPS_time = clock.getTimeNanos() - (fullBiasNanos + clock.getBiasNanos());
                double gpsSow = local_est_GPS_time * NS_TO_S - gpsWeek * GPS_WEEK_SECS;

                double tRxSeconds = gpsSow - measurement.getTimeOffsetNanos() * NS_TO_S;
                double tTxSeconds = measurement.getReceivedSvTimeNanos() * NS_TO_S;

                double tau = tRxSeconds - tTxSeconds;

                if (tau < 0) tau += GPS_WEEK_SECS;

                double c1 = tau * SPEED_OF_LIGHT;
                double l1 = measurement.getAccumulatedDeltaRangeMeters() / GPS_L1_WAVELENGTH;
                double d1 = -measurement.getPseudorangeRateMetersPerSecond() / GPS_L1_WAVELENGTH;
                DecimalFormat df = new DecimalFormat("0.000");

                if (c1 > 30e6 || c1 < 10e6) {
                    Log.e(TAG, "State=" + measurement.getState() + ", C1: " + df.format(c1) + ", L1: " + df.format(l1) + ", D1: " + df.format(d1) + ", S1: " + df.format(measurement.getCn0DbHz()));
                    continue;
                } else
                    Log.i(TAG, "State=" + measurement.getState() + ", C1: " + df.format(c1) + ", L1: " + df.format(l1) + ", D1: " + df.format(d1) + ", S1: " + df.format(measurement.getCn0DbHz()));

                rinexData.add(new RinexData(
                        numberSatellite(measurement.getConstellationType(), measurement.getSvid()),
                        df.format(c1),
                        df.format(l1),
                        df.format(measurement.getCn0DbHz()),
                        df.format(d1)
                ));
            }
        }
        rinex.writeData(rinexData, gpsTime);
        log(rinexData);
    }

    private String numberSatellite(int constellationType, int svid) {
        String sSvid = "" + svid;
        if (svid < 10)
            sSvid = "0" + svid;
        if (constellationType == GnssStatus.CONSTELLATION_GPS)
            return "G" + sSvid;
        else if (constellationType == GnssStatus.CONSTELLATION_SBAS)
            return "S" + sSvid;
        else if (constellationType == GnssStatus.CONSTELLATION_GLONASS)
            return "R" + sSvid;
        else if (constellationType == GnssStatus.CONSTELLATION_QZSS)
            return "J" + sSvid;
        else if (constellationType == GnssStatus.CONSTELLATION_BEIDOU)
            return "C" + sSvid;
        else if (constellationType == GnssStatus.CONSTELLATION_GALILEO)
            return "E" + sSvid;
        return "SNN";
    }

    private void log(ArrayList<RinexData> rinexData) {
        log.append(new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date()))
                .append(" > [")
                .append(rinexData.size())
                .append("]=");
        for (int i = 0; i < rinexData.size(); i++) {
            log.append(rinexData.get(i).getSatellite());
            if (i != rinexData.size() - 1) log.append(",");
        }
        log.append("\n");
        handler.post(new Runnable() {
            @Override
            public void run() {
                textViewLog.setText(log);
                if (!statusScroll) scrollViewLog.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        Log.i(TAG, "GnssMeasurementsEvent callback -> Satellite total : " + rinexData.size());
    }

    private void logClear() {
        log.setLength(0);
        log.append(this.getString(R.string.searching)).append("\n");
        handler.post(new Runnable() {
            @Override
            public void run() {
                textViewLog.setText(log);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (!locationStatus) {
            locationStatus = true;
            gpsTime = location.getTime();
            textViewBtnStartStop.setBackgroundResource(R.drawable.bg_btn_green);
        }
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
