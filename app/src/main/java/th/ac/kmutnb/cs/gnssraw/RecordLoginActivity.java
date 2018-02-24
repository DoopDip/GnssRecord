package th.ac.kmutnb.cs.gnssraw;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class RecordLoginActivity extends AppCompatActivity {

    private static final String TAG = "RecordLoginActivity";

    private TextView textViewBtnLogin;
    private TextView textViewBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_login);

        textViewBtnLogin = (TextView) findViewById(R.id.recordLogin_btnLogin);
        textViewBtnRegister = (TextView) findViewById(R.id.recordLogin_btnRegister);

        textViewBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> Login");
                Intent intent = new Intent(RecordLoginActivity.this, RecordStartActivity.class);
                startActivity(intent);
            }
        });

        textViewBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> Register");
                Intent intent = new Intent(RecordLoginActivity.this, RecordRegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
