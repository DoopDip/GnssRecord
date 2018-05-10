package th.ac.kmutnb.cs.gnssrecord;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.LocationManager;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import th.ac.kmutnb.cs.gnssrecord.model.RinexData;
import th.ac.kmutnb.cs.gnssrecord.model.RinexHeader;
import th.ac.kmutnb.cs.gnssrecord.rinex.Rinex;

public class RecordActivity extends AppCompatActivity {

    private static final String TAG = RecordActivity.class.getSimpleName();

    private TextView textViewWelcome;
    private TextView textViewName;
    private TextView textViewStart;
    private TextView textViewBtnScroll;
    private TextView textViewBtnLogOut;
    private ScrollView scrollViewLog;
    private TextView textViewLog;

    private Rinex rinex;

    private boolean statusRecord;
    private boolean statusScroll;

    private float tempYBtnStart;

    private FirebaseUser firebaseUser;

    private String log;
    private Handler handler;
    private LocationManager locationManager;
    private GnssMeasurementsEvent.Callback measurementsEvent =
            new GnssMeasurementsEvent.Callback() {
                @Override
                public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
                    super.onGnssMeasurementsReceived(eventArgs);
                    ArrayList<GnssMeasurement> measurementList = new ArrayList<>(eventArgs.getMeasurements());
                    writeRecordRinex(measurementList);
                    log(measurementList);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        handler = new Handler();
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        statusRecord = false;
        statusScroll = false;

        textViewWelcome = findViewById(R.id.record_welcome);
        textViewName = findViewById(R.id.record_name);
        textViewStart = findViewById(R.id.record_start);
        textViewBtnScroll = findViewById(R.id.record_btnScroll);
        textViewBtnLogOut = findViewById(R.id.recordLogin_btnLogOut);
        scrollViewLog = findViewById(R.id.record_logScroll);
        textViewLog = findViewById(R.id.record_log);

        if (firebaseUser != null) textViewName.setText(firebaseUser.getDisplayName());
        tempYBtnStart = textViewStart.getY();

        textViewStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!statusRecord) {
                    Log.i(TAG, "Click -> textViewStart = Start");
                    animationClickStart();
                    registerGnssMeasurements();
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    startRecordRinex();
                    statusRecord = true;
                } else {
                    Log.i(TAG, "Click -> textViewStart = Stop");
                    stopRecordRinex();
                    animationClickStop();
                    unregisterGnssMeasurements();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    statusRecord = false;
                }
            }
        });

        textViewBtnScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!statusScroll) {
                    Log.i(TAG, "Click -> textViewBtnScroll = UnAuto");
                    textViewBtnScroll.setText(R.string.auto_scrollbar);
                    Snackbar.make(textViewStart, R.string.un_auto_scrollbar, Snackbar.LENGTH_SHORT).show();
                    statusScroll = true;
                } else {
                    Log.i(TAG, "Click -> textViewBtnScroll = Auto");
                    textViewBtnScroll.setText(R.string.un_auto_scrollbar);
                    Snackbar.make(textViewStart, R.string.auto_scrollbar, Snackbar.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.unregisterGnssMeasurementsCallback(measurementsEvent);
        Log.i(TAG, "!! UnRegister callback -> measurementsEvent");
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
        Log.i(TAG, "!! UnRegister callback -> measurementsEvent");
    }

    private void animationClickStop() {
        textViewWelcome.setVisibility(View.VISIBLE);
        textViewName.setVisibility(View.VISIBLE);
        textViewStart.setBackgroundResource(R.drawable.bg_btn_green);
        textViewStart.setText(R.string.start);
        textViewBtnScroll.setVisibility(View.INVISIBLE);
        textViewBtnScroll.setAlpha(0f);
        textViewBtnLogOut.setVisibility(View.VISIBLE);
        //Animation
        ObjectAnimator.ofFloat(textViewStart, View.TRANSLATION_Y, tempYBtnStart)
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
    }

    private void animationClickStart() {
        textViewWelcome.setVisibility(View.INVISIBLE);
        textViewName.setVisibility(View.INVISIBLE);
        textViewStart.setBackgroundResource(R.drawable.bg_btn_red);
        textViewStart.setText(R.string.stop);
        scrollViewLog.setVisibility(View.VISIBLE);
        textViewBtnScroll.setVisibility(View.VISIBLE);
        textViewBtnLogOut.setVisibility(View.INVISIBLE);
        textViewBtnLogOut.setAlpha(0f);
        //Animation
        ObjectAnimator.ofFloat(textViewStart, View.TRANSLATION_Y, tempYBtnStart - 430)
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
        rinex = new Rinex(getApplicationContext());
        rinex.writeHeader(new RinexHeader(
                "GnssRecord",
                "Geodetic",
                "RINEX Logger user",
                "GnssRecord",
                "GSLDU17C29002159",
                "HUAWEI",
                "FRD-AL10",
                "GSLDU17C29002159",
                "FRD-AL10",
                0.0000,
                0.0000,
                0.0000,
                -1129349.1474,
                6091633.9329,
                1510495.6984
        ));
    }

    private void stopRecordRinex() {
        rinex.closeFile();
    }

    private void writeRecordRinex(ArrayList<GnssMeasurement> measurementList) {
        ArrayList<RinexData> rinexData = new ArrayList<>();
        for (GnssMeasurement measurement : measurementList)
            rinexData.add(new RinexData(measurement.getSvid() + "",
                    measurement.getAccumulatedDeltaRangeMeters(),
                    772467.6560,
                    -3009.854,
                    measurement.getCn0DbHz()));
        rinex.writeData(rinexData);
    }

    private void log(ArrayList<GnssMeasurement> measurementList) {
        if (log == null) log = "";
        log += "Satellite total : " + +measurementList.size();
        log += "\n";

        handler.post(new Runnable() {
            @Override
            public void run() {
                textViewLog.setText(log);
                if (!statusScroll) scrollViewLog.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        Log.i(TAG, "GnssMeasurementsEvent callback -> Satellite total : " + measurementList.size());
    }

}
