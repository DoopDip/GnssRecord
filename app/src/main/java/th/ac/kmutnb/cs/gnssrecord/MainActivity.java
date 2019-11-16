package th.ac.kmutnb.cs.gnssrecord;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import th.ac.kmutnb.cs.gnssrecord.config.Constants;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_LANGUAGE = "language";
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView textViewBtnPosition;
    private TextView textViewBtnList;
    private TextView textViewBtnRecord;
    private Spinner spinnerLanguage;

    private SharedPreferences sharedPreferences;
    private String[][] language = {{"en", "th"}, {"EN", "TH"}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sharedPreferences = getSharedPreferences(Constants.FILE_SETTING, 0);
        setDisplayLanguage(language[0][sharedPreferences.getInt("language", 0)]);

        textViewBtnPosition = findViewById(R.id.main_btnPosition);
        textViewBtnList = findViewById(R.id.main_btnList);
        textViewBtnRecord = findViewById(R.id.main_btnRecord);
        spinnerLanguage = findViewById(R.id.main_language);

        startAnimation();
        checkPermission();

        textViewBtnPosition.setOnClickListener(v -> {
            Log.i(TAG, "Click -> MenuPosition");
            startActivity(new Intent(MainActivity.this, PositionActivity.class));
        });

        textViewBtnList.setOnClickListener(v -> {
            Log.i(TAG, "Click -> MenuList");
            startActivity(new Intent(MainActivity.this, ListActivity.class));
        });

        textViewBtnRecord.setOnClickListener(v -> {
            Log.i(TAG, "Click -> MenuRecord");
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null)
                startActivity(new Intent(MainActivity.this, RecordActivity.class));
            else
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        spinnerLanguage.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        new String[]{language[1][0], language[1][1]}
                )
        );
        spinnerLanguage.setSelection(sharedPreferences.getInt("language", 0), true);
        TextView textViewSpinnerLanguage = (TextView) spinnerLanguage.getSelectedView();
        textViewSpinnerLanguage.setTextColor(getColor(R.color.colorWhite));
        textViewSpinnerLanguage.setTypeface(ResourcesCompat.getFont(this, R.font.thaisansneue_semibold));
        textViewSpinnerLanguage.setTextSize(20f);
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Set language = " + language[0][position]);
                TextView textViewSpinnerLanguage = (TextView) spinnerLanguage.getSelectedView();
                textViewSpinnerLanguage.setTextColor(getColor(R.color.colorWhite));
                textViewSpinnerLanguage.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.thaisansneue_semibold));
                textViewSpinnerLanguage.setTextSize(20f);
                setDisplayLanguage(language[0][position]);
                sharedPreferences.edit().putInt(KEY_LANGUAGE, position).apply();

                new Handler().post(() -> {
                    textViewBtnPosition.setText(R.string.position);
                    textViewBtnList.setText(R.string.list);
                    textViewBtnRecord.setText(R.string.record);
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 101);
        }
    }

    private void startAnimation() {
        Log.i(TAG, "Play Animation");
        textViewBtnPosition.setAlpha(0f);
        textViewBtnList.setAlpha(0f);
        textViewBtnRecord.setAlpha(0f);
        spinnerLanguage.setAlpha(0f);
        ObjectAnimator animatorPosition = ObjectAnimator.ofFloat(textViewBtnPosition, View.ALPHA, 1f);
        ObjectAnimator animatorList = ObjectAnimator.ofFloat(textViewBtnList, View.ALPHA, 1f);
        ObjectAnimator animatorRecord = ObjectAnimator.ofFloat(textViewBtnRecord, View.ALPHA, 1f);
        ObjectAnimator animatorLanguage = ObjectAnimator.ofFloat(spinnerLanguage, View.ALPHA, 0.5f);
        animatorPosition.setStartDelay(200);
        animatorPosition.setDuration(1200).start();
        animatorList.setStartDelay(400);
        animatorList.setDuration(1200).start();
        animatorRecord.setStartDelay(600);
        animatorRecord.setDuration(1200).start();
        animatorLanguage.setStartDelay(800);
        animatorLanguage.setDuration(4000).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnimation();
    }

    private void setDisplayLanguage(String code) {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }
}
