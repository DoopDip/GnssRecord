package th.ac.kmutnb.cs.gnssraw;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import th.ac.kmutnb.cs.gnssraw.model.Record;

public class RecordActivity extends AppCompatActivity {

    private static final String TAG = RecordActivity.class.getSimpleName();
    private static final int PERMISSIONS_ACCESS_FINE_LOCATION = 99;

    private TextView textViewWelcome;
    private TextView textViewName;
    private TextView textViewStart;
    private TextView textViewBtnScroll;
    private TextView textViewBtnLogOut;
    private ScrollView scrollViewLog;
    private TextView textViewLog;

    private boolean statusRecord;
    private boolean statusScroll;

    private float tempYBtnStart;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String log;
    private Handler handler;
    private List<GnssMeasurement> measurementList;
    private LocationManager locationManager;
    private GnssMeasurementsEvent.Callback measurementsEvent =
            new GnssMeasurementsEvent.Callback() {
                @Override
                public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
                    super.onGnssMeasurementsReceived(eventArgs);
                    measurementList.clear();
                    measurementList.addAll(eventArgs.getMeasurements());
                    log += "Satellite total : " + +measurementList.size();
                    log += "\n";
                    DatabaseReference referenceLog = FirebaseDatabase.getInstance()
                            .getReference("users/" + firebaseUser.getUid() + "/log").push();
                    databaseReference.child("log/" + referenceLog.getKey() + "/dateTime")
                            .setValue(Calendar.getInstance().getTime());
                    for (GnssMeasurement measurement : measurementList)
                        databaseReference.child("log/" + referenceLog.getKey()).push().setValue(new Record(measurement));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            textViewLog.setText(log);
                            if (!statusScroll) scrollViewLog.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                    Log.i(TAG, "GnssMeasurementsEvent callback -> Satellite total : " + measurementList.size());
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users/" + firebaseUser.getUid());
        log = "";
        measurementList = new ArrayList<>();
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
                    //Check Permission
                    if (ActivityCompat.checkSelfPermission(RecordActivity.this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RecordActivity.this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSIONS_ACCESS_FINE_LOCATION);
                    }
                    locationManager.registerGnssMeasurementsCallback(measurementsEvent);
                    Log.i(TAG, "Register callback -> measurementsEvent");

                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    databaseReference.child("status").setValue(true);
                    statusRecord = true;
                } else {
                    Log.i(TAG, "Click -> textViewStart = Stop");
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

                    locationManager.unregisterGnssMeasurementsCallback(measurementsEvent);
                    Log.i(TAG, "!! UnRegister callback -> measurementsEvent");

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    databaseReference.child("status").setValue(false);
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
        databaseReference.child("status").setValue(false);
    }
}
