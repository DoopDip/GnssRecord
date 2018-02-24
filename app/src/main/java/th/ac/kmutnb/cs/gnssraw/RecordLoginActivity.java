package th.ac.kmutnb.cs.gnssraw;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class RecordLoginActivity extends AppCompatActivity {

    private static final String TAG = "RecordLoginActivity";

    private TextView textViewBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_login);

        textViewBtnRegister = (TextView) findViewById(R.id.recordLogin_btnRegister);

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
