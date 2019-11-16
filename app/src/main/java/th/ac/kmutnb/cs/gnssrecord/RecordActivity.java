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
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import th.ac.kmutnb.cs.gnssrecord.config.Constants;
import th.ac.kmutnb.cs.gnssrecord.model.RinexData;
import th.ac.kmutnb.cs.gnssrecord.model.RinexHeader;
import th.ac.kmutnb.cs.gnssrecord.rinex.Rinex;

public class RecordActivity extends AppCompatActivity {

    private static final String TAG = RecordActivity.class.getSimpleName();

    private static final double SPEED_OF_LIGHT = 299792458.0; // ความเร็วแสง เมตร:วิ
    private static final double GPS_L1_FREQ = 154.0 * 10.23e6;
    private static final double GPS_L1_WAVELENGTH = SPEED_OF_LIGHT / GPS_L1_FREQ;
    private static final double GPS_WEEK_SECS = 604800; // วินาทีในหนึ่งสัปดาห์
    private static final double NS_TO_S = 1.0e-9;
    private static final long GPS_START_TIME = 315939600000L; // เวลาเริ่มต้นของ GPS = 1980-1-6 หน่วนมิลลิวิ;

    private TextView textViewWelcome;
    private TextView textViewName;
    private TextView textViewBtnStartStop;
    private TextView textViewBtnScroll;
    private TextView textViewBtnLogOut;
    private TextView textViewLog;
    private TextView textViewBtnFile;
    private TextView textViewBtnSetting;
    private TextView textViewTime;
    private TextView textViewDate;
    private TextView textViewBtnTimer;
    private TextView textViewTimer;
    private TextView textViewTotalGps;
    private TextView textViewTotalGalileo;
    private TextView textViewTotalGlonass;
    private TextView textViewTotalSbas;
    private TextView textViewTotalQzss;
    private TextView textViewTotalBeidou;
    private ScrollView scrollViewLog;
    private LinearLayout linearLayoutGroupMenu;
    private LinearLayout linearLayoutGroupTimeScroll;

    private int totalGps;
    private int totalGalileo;
    private int totalGlonass;
    private int totalSbas;
    private int totalQzss;
    private int totalBeidou;

    private Rinex rinex;
    private SharedPreferences sharedPreferences;

    private boolean statusRecord;
    private boolean statusScroll;
    private boolean statusTimer;
    private boolean statusGnss;

    private float tempYBtnStart;

    private long gpsTime;
    private long timer;

    private StringBuilder log;
    private Handler handler;
    private LocationManager locationManager;
    private GnssMeasurementsEvent.Callback measurementsEvent =
            new GnssMeasurementsEvent.Callback() {
                @Override
                public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
                    super.onGnssMeasurementsReceived(eventArgs);
                    Log.i(TAG, "GnssMeasurements [available]");
                    GnssClock clock = eventArgs.getClock();
                    double gpsWeek = Math.floor(-clock.getFullBiasNanos() * NS_TO_S / GPS_WEEK_SECS);
                    double local_est_GPS_time = clock.getTimeNanos() - (clock.getFullBiasNanos() + clock.getBiasNanos());
                    double gpsSow = local_est_GPS_time * NS_TO_S - gpsWeek * GPS_WEEK_SECS;
                    gpsTime = (long) ((GPS_WEEK_SECS * gpsWeek + gpsSow) * 1000) + GPS_START_TIME;

                    if (statusRecord) {
                        ArrayList<GnssMeasurement> measurementList = new ArrayList<>(eventArgs.getMeasurements());
                        writeRecordRinex(measurementList, eventArgs.getClock());
                        if (statusTimer) {
                            timer--;
                            if (timer <= 0) {
                                statusTimer = false;
                                statusRecord = false;
                                stopRecordRinex();
                                runOnUiThread(() -> {
                                    animationClickStop();
                                    textViewTimer.setTextColor(getColor(R.color.colorWhite));
                                });
                                RingtoneManager.getRingtone(
                                        getApplicationContext(),
                                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                                ).play();
                            } else if (timer < 10) {
                                runOnUiThread(() -> textViewTimer.setTextColor(getColor(R.color.colorDanger)));
                            }
                        } else {
                            timer++;
                        }
                        long hour = timer / 3600;
                        long min = (timer % 3600) / 60;
                        long sec = timer % 60;
                        DecimalFormat df = new DecimalFormat("00");
                        StringBuffer timeText = new StringBuffer();
                        timeText.append(df.format(hour))
                                .append(":")
                                .append(df.format(min))
                                .append(":")
                                .append(df.format(sec));
                        handler.post(() -> textViewTimer.setText(timeText));
                    } else {
                        statusGnss = true;

                        resetTotalSatellite();
                        eventArgs.getMeasurements().forEach(e -> addTotalSatellite(e.getConstellationType()));

                        Date date = new Date(gpsTime);
                        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss", Locale.US);
                        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/YY", Locale.US);
                        handler.post(() -> {
                            textViewTotalGps.setText(String.valueOf(totalGps));
                            textViewTotalGalileo.setText(String.valueOf(totalGalileo));
                            textViewTotalGlonass.setText(String.valueOf(totalGlonass));
                            textViewTotalSbas.setText(String.valueOf(totalSbas));
                            textViewTotalQzss.setText(String.valueOf(totalQzss));
                            textViewTotalBeidou.setText(String.valueOf(totalBeidou));

                            textViewTime.setText(formatTime.format(date));
                            textViewDate.setText(formatDate.format(date));
                            if (!statusRecord)
                                textViewBtnStartStop.setBackgroundResource(R.drawable.bg_btn_green);
                        });
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        sharedPreferences = getSharedPreferences(Constants.FILE_SETTING, 0);
        log = new StringBuilder();
        handler = new Handler();
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        statusRecord = false;
        statusScroll = false;
        statusGnss = false;
        statusTimer = false;
        timer = 0;

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
        textViewTime = findViewById(R.id.record_time);
        textViewDate = findViewById(R.id.record_date);
        linearLayoutGroupTimeScroll = findViewById(R.id.record_groupTimeScroll);
        textViewBtnTimer = findViewById(R.id.record_btnTimer);
        textViewTimer = findViewById(R.id.record_timer);
        textViewTotalGps = findViewById(R.id.record_totalGps);
        textViewTotalGalileo = findViewById(R.id.record_totalGalileo);
        textViewTotalGlonass = findViewById(R.id.record_totalGlonass);
        textViewTotalSbas = findViewById(R.id.record_totalSbas);
        textViewTotalQzss = findViewById(R.id.record_totalQzss);
        textViewTotalBeidou = findViewById(R.id.record_totalBeidou);

        if (firebaseUser != null) textViewName.setText(firebaseUser.getDisplayName());
        tempYBtnStart = textViewBtnStartStop.getY();

        btnEvent();
        resetTotalSatellite();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusGnss = false;
        textViewBtnStartStop.setBackgroundResource(R.drawable.bg_btn_gray);
        registerGnssMeasurements();
    }

    private void btnEvent() {
        textViewBtnStartStop.setOnClickListener(v -> {
            if (!statusRecord) {
                if (statusGnss) {
                    Log.i(TAG, "Click -> textViewStart = Start");
                    logClear();
                    handler.post(() -> textViewTimer.setText(getString(R.string.zero_time)));
                    animationClickStart();
                    registerGnssMeasurements();
                    startRecordRinex();
                    statusRecord = true;
                } else
                    Snackbar.make(textViewBtnScroll, R.string.wait_location, Snackbar.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "Click -> textViewStart = Stop");
                statusRecord = false;
                stopRecordRinex();
                animationClickStop();
            }
        });

        textViewBtnScroll.setOnClickListener(v -> {
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
        });

        textViewBtnLogOut.setOnClickListener(v -> {
            Log.i(TAG, "Click -> LogOut");
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(RecordActivity.this, MainActivity.class));
            finish();
        });

        textViewBtnFile.setOnClickListener(v -> {
            Log.i(TAG, "Click -> File");
            startActivity(new Intent(RecordActivity.this, FileActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext()).toBundle()
            );
        });

        textViewBtnSetting.setOnClickListener(v -> {
            Log.i(TAG, "Click -> Setting (RinexHeader)");
            startActivity(
                    new Intent(RecordActivity.this, SettingActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext()).toBundle()
            );
        });

        textViewBtnTimer.setOnClickListener(v -> new MaterialDialog.Builder(v.getContext())
                .customView(R.layout.dialog_timer, true)
                .positiveText(R.string.cancel)
                .negativeText(R.string.set)
                .onPositive((dialog, which) -> {
                    Log.i(TAG, "Timer dialog Click -> Cancel");
                    statusTimer = false;
                    timer = 0;
                    textViewBtnTimer.setTextColor(getColor(R.color.colorWhite));
                    textViewBtnTimer.setBackground(getDrawable(R.drawable.bg_btn_menu));
                })
                .onNegative((dialog, which) -> {
                    Log.i(TAG, "Timer dialog Click -> Set");
                    assert dialog.getCustomView() != null;
                    NumberPicker hour = dialog.getCustomView().findViewById(R.id.dialogTimer_hour);
                    NumberPicker min = dialog.getCustomView().findViewById(R.id.dialogTimer_min);
                    NumberPicker sec = dialog.getCustomView().findViewById(R.id.dialogTimer_sec);
                    Log.i(TAG, "Set time = " + hour.getValue() + ":" + min.getValue() + ":" + sec.getValue());
                    timer = sec.getValue() + (min.getValue() * 60) + (hour.getValue() * 60 * 60);
                    Log.e(TAG, "Timer = " + timer);
                    statusTimer = true;
                    textViewBtnTimer.setTextColor(getColor(R.color.colorGreen));
                    textViewBtnTimer.setBackground(getDrawable(R.drawable.bg_btn_menu_green));
                })
                .show());
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
    }

    private void unregisterGnssMeasurements() {
        locationManager.unregisterGnssMeasurementsCallback(measurementsEvent);
        statusGnss = false;
        Log.i(TAG, "!! UnRegister callback -> measurementsEvent");
    }

    private void animationClickStop() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        textViewBtnTimer.setTextColor(getColor(R.color.colorWhite));
        textViewBtnTimer.setBackground(getDrawable(R.drawable.bg_btn_menu));
        textViewWelcome.setVisibility(View.VISIBLE);
        textViewName.setVisibility(View.VISIBLE);
        textViewBtnStartStop.setBackgroundResource(R.drawable.bg_btn_green);
        textViewBtnStartStop.setText(R.string.start);
        linearLayoutGroupTimeScroll.setVisibility(View.INVISIBLE);
        linearLayoutGroupTimeScroll.setAlpha(0f);
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        textViewWelcome.setVisibility(View.INVISIBLE);
        textViewName.setVisibility(View.INVISIBLE);
        textViewBtnStartStop.setBackgroundResource(R.drawable.bg_btn_red);
        textViewBtnStartStop.setText(R.string.stop);
        scrollViewLog.setVisibility(View.VISIBLE);
        linearLayoutGroupTimeScroll.setVisibility(View.VISIBLE);
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
        ObjectAnimator animatorBtnScroll = ObjectAnimator.ofFloat(linearLayoutGroupTimeScroll, View.ALPHA, 1f);
        animatorBtnScroll.setDuration(800);
        animatorBtnScroll.setStartDelay(700);
        animatorBtnScroll.start();
    }

    private void startRecordRinex() {
        rinex = new Rinex(getApplicationContext(), sharedPreferences.getInt(Constants.KEY_RINEX_VER, Constants.DEF_RINEX_VER));
        rinex.writeHeader(new RinexHeader(
                sharedPreferences.getString(Constants.KEY_MARK_NAME, Constants.DEF_MARK_NAME),
                sharedPreferences.getString(Constants.KEY_MARK_TYPE, Constants.DEF_MARK_TYPE),
                sharedPreferences.getString(Constants.KEY_OBSERVER_NAME, Constants.DEF_OBSERVER_NAME),
                sharedPreferences.getString(Constants.KEY_OBSERVER_AGENCY_NAME, Constants.DEF_OBSERVER_AGENCY_NAME),
                sharedPreferences.getString(Constants.KEY_RECEIVER_NUMBER, Constants.DEF_RECEIVER_NUMBER),
                sharedPreferences.getString(Constants.KEY_RECEIVER_TYPE, Constants.DEF_RECEIVER_TYPE),
                sharedPreferences.getString(Constants.KEY_RECEIVER_VERSION, Constants.DEF_RECEIVER_VERSION),
                sharedPreferences.getString(Constants.KEY_ANTENNA_NUMBER, Constants.DEF_ANTENNA_NUMBER),
                sharedPreferences.getString(Constants.KEY_ANTENNA_TYPE, Constants.DEF_ANTENNA_TYPE),
                Double.parseDouble(sharedPreferences.getString(Constants.KEY_ANTENNA_ECCENTRICITY_EAST, Constants.DEF_ANTENNA_ECCENTRICITY_EAST)),
                Double.parseDouble(sharedPreferences.getString(Constants.KEY_ANTENNA_ECCENTRICITY_NORTH, Constants.DEF_ANTENNA_ECCENTRICITY_NORTH)),
                Double.parseDouble(sharedPreferences.getString(Constants.KEY_ANTENNA_HEIGHT, Constants.DEF_ANTENNA_HEIGHT)),
                "0.0000",
                "0.0000",
                "0.0000",
                gpsTime
        ));
    }

    private void stopRecordRinex() {
        rinex.closeFile();
        timer = 0;
    }

    private void writeRecordRinex(ArrayList<GnssMeasurement> measurementList, GnssClock clock) {
        ArrayList<RinexData> rinexData = new ArrayList<>();
        int rinexVer = sharedPreferences.getInt(Constants.KEY_RINEX_VER, Constants.DEF_RINEX_VER);
        for (GnssMeasurement measurement : measurementList) {
            int constellationType = measurement.getConstellationType();
            if (rinexVer == Constants.VER_2_11 && (constellationType == GnssStatus.CONSTELLATION_SBAS || constellationType == GnssStatus.CONSTELLATION_QZSS || constellationType == GnssStatus.CONSTELLATION_BEIDOU)) {
                continue;
            }
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
        handler.post(() -> {
            textViewLog.setText(log);
            if (!statusScroll) scrollViewLog.fullScroll(ScrollView.FOCUS_DOWN);
        });
        Log.i(TAG, "GnssMeasurementsEvent callback -> Satellite total : " + rinexData.size());
    }

    private void logClear() {
        log.setLength(0);
        log.append(this.getString(R.string.searching)).append("\n");
        handler.post(() -> textViewLog.setText(log));
    }

    private void resetTotalSatellite() {
        totalGps = 0;
        totalGalileo = 0;
        totalGlonass = 0;
        totalSbas = 0;
        totalQzss = 0;
        totalBeidou = 0;
    }

    private void addTotalSatellite(int constellationType) {
        if (constellationType == GnssStatus.CONSTELLATION_GPS)
            totalGps++;
        else if (constellationType == GnssStatus.CONSTELLATION_SBAS)
            totalSbas++;
        else if (constellationType == GnssStatus.CONSTELLATION_GLONASS)
            totalGlonass++;
        else if (constellationType == GnssStatus.CONSTELLATION_QZSS)
            totalQzss++;
        else if (constellationType == GnssStatus.CONSTELLATION_BEIDOU)
            totalBeidou++;
        else if (constellationType == GnssStatus.CONSTELLATION_GALILEO)
            totalGalileo++;
    }
}
