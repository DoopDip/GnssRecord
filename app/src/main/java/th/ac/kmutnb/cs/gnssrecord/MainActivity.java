package th.ac.kmutnb.cs.gnssrecord;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView textViewBtnPosition;
    private TextView textViewBtnList;
    private TextView textViewBtnRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        textViewBtnPosition = findViewById(R.id.main_btnPosition);
        textViewBtnList = findViewById(R.id.main_btnList);
        textViewBtnRecord = findViewById(R.id.main_btnRecord);

        startAnimation();
        checkPermission();

        textViewBtnPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> MenuPosition");
                startActivity(new Intent(MainActivity.this, PositionActivity.class));
            }
        });

        textViewBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> MenuList");
                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        });

        textViewBtnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> MenuRecord");
                if (isConnectedToInternet(getApplicationContext())) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (firebaseUser != null)
                        startActivity(new Intent(MainActivity.this, RecordActivity.class));
                    else
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    Snackbar.make(textViewBtnRecord, R.string.please_internet, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.i(TAG, "Click -> " + R.string.please_internet);
                                }
                            }).show();
                }
            }
        });
    }

    private boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
        ObjectAnimator animatorPosition = ObjectAnimator.ofFloat(textViewBtnPosition, View.ALPHA, 1f);
        ObjectAnimator animatorList = ObjectAnimator.ofFloat(textViewBtnList, View.ALPHA, 1f);
        ObjectAnimator animatorRecord = ObjectAnimator.ofFloat(textViewBtnRecord, View.ALPHA, 1f);
        animatorPosition.setStartDelay(200);
        animatorPosition.setDuration(1200).start();
        animatorList.setStartDelay(400);
        animatorList.setDuration(1200).start();
        animatorRecord.setStartDelay(600);
        animatorRecord.setDuration(1200).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnimation();
    }
}
