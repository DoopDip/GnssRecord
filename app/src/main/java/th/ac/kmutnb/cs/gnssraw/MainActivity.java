package th.ac.kmutnb.cs.gnssraw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

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
    }
}
