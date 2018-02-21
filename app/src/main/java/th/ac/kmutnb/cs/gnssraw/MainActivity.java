package th.ac.kmutnb.cs.gnssraw;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView textViewMenuList;
    private TextView textViewMenuRecord;

    private LinearLayout linearLayoutLogo;
    private LinearLayout linearLayoutLogoRecord;
    private LinearLayout linearLayoutMenu;
    private ImageView imageViewSatellite;
    private TextView textViewCredit;

    private Animation animationLogo;
    private Animation animationRecord;
    private Animation animationMenu;
    private Animation animationStatellite;
    private Animation animationCredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewMenuList = findViewById(R.id.main_menuList);
        textViewMenuRecord = findViewById(R.id.main_menuRecord);

        linearLayoutLogo = findViewById(R.id.main_logo);
        linearLayoutLogoRecord = findViewById(R.id.main_logoRecord);
        linearLayoutMenu = findViewById(R.id.main_menu);
        imageViewSatellite = findViewById(R.id.main_imgSatellite);
        textViewCredit = findViewById(R.id.main_credit);

        animationLogo = AnimationUtils.loadAnimation(this, R.anim.main_logo);
        animationRecord = AnimationUtils.loadAnimation(this, R.anim.main_record);
        animationMenu = AnimationUtils.loadAnimation(this, R.anim.main_menu);
        animationStatellite = AnimationUtils.loadAnimation(this, R.anim.main_satellite);
        animationCredit = AnimationUtils.loadAnimation(this, R.anim.main_credit);

        linearLayoutLogo.setAnimation(animationLogo);
        linearLayoutLogoRecord.setAnimation(animationRecord);
        linearLayoutMenu.setAnimation(animationMenu);
        imageViewSatellite.setAnimation(animationStatellite);
        textViewCredit.setAnimation(animationCredit);


        textViewMenuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> MenuList");
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                        MainActivity.this,
                        new Pair<View, String>(textViewMenuList, "main_listTransition")
                );
                startActivity(intent, activityOptions.toBundle());
            }
        });

        textViewMenuRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> MenuRecord");
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                        MainActivity.this,
                        new Pair<View, String>(textViewMenuRecord, "main_recordTransition")
                );
                startActivity(intent, activityOptions.toBundle());
            }
        });
    }
}
