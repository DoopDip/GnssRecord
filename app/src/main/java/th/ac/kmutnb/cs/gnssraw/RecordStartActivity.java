package th.ac.kmutnb.cs.gnssraw;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RecordStartActivity extends AppCompatActivity {

    private static final String TAG = "RecordStartActivity";

    private TextView textViewWelcome;
    private TextView textViewName;
    private TextView textViewStart;
    private TextView textViewBtnScroll;
    private TextView textViewBtnLogOut;
    private ScrollView scrollViewLog;

    private boolean statusRecord;
    private boolean statusScroll;

    private float tempYBtnStart;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_start);

        statusRecord = false;
        statusScroll = false;

        textViewWelcome = (TextView) findViewById(R.id.recordStart_welcome);
        textViewName = (TextView) findViewById(R.id.recordStart_name);
        textViewStart = (TextView) findViewById(R.id.recordStart_start);
        textViewBtnScroll = (TextView) findViewById(R.id.recordStart_btnScroll);
        textViewBtnLogOut = (TextView) findViewById(R.id.recordLogin_btnLogOut);
        scrollViewLog = (ScrollView) findViewById(R.id.recordStart_logScroll);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            textViewName.setText(firebaseUser.getDisplayName());
        }

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
                Intent intent = new Intent(RecordStartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
