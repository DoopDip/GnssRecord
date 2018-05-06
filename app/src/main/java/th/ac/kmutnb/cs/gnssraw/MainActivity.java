package th.ac.kmutnb.cs.gnssraw;

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
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_ACCESS_FINE_LOCATION = 99;

    private RelativeLayout relativeLayoutPosition;
    private RelativeLayout relativeLayoutList;
    private RelativeLayout relativeLayoutRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        relativeLayoutPosition = findViewById(R.id.main_position);
        relativeLayoutList = findViewById(R.id.main_list);
        relativeLayoutRecord = findViewById(R.id.main_record);

        startAnimation();
        checkPermissionLocation();

        relativeLayoutPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> MenuPosition");
                startActivity(new Intent(MainActivity.this, PositionActivity.class));
            }
        });

        relativeLayoutList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> MenuList");
                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        });

        relativeLayoutRecord.setOnClickListener(new View.OnClickListener() {
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
                    Snackbar.make(relativeLayoutRecord, R.string.please_internet, Snackbar.LENGTH_INDEFINITE)
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

    private void checkPermissionLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);
        }
    }

    private void startAnimation() {
        Log.i(TAG, "Play Animation");
        relativeLayoutPosition.setAlpha(0f);
        relativeLayoutList.setAlpha(0f);
        relativeLayoutRecord.setAlpha(0f);
        ObjectAnimator animatorPosition = ObjectAnimator.ofFloat(relativeLayoutPosition, View.ALPHA, 1f);
        ObjectAnimator animatorList = ObjectAnimator.ofFloat(relativeLayoutList, View.ALPHA, 1f);
        ObjectAnimator animatorRecord = ObjectAnimator.ofFloat(relativeLayoutRecord, View.ALPHA, 1f);
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
