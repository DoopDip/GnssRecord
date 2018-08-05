package th.ac.kmutnb.cs.gnssrecord;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private TextView textViewBtnLogin;
    private TextView textViewBtnRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        textViewBtnLogin = findViewById(R.id.login_btnLogin);
        textViewBtnRegister = findViewById(R.id.login_btnRegister);
        editTextEmail = findViewById(R.id.login_inputMail);
        editTextPassword = findViewById(R.id.login_inputPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        textViewBtnLogin.setOnClickListener(v -> {
            Log.i(TAG, "Click -> Login");
            if (validateForm()) {
                firebaseAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(),
                        editTextPassword.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");
                                startActivity(new Intent(LoginActivity.this, RecordActivity.class));
                                finish();
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Snackbar.make(textViewBtnRegister, R.string.login_failed, Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        textViewBtnRegister.setOnClickListener(v -> {
            Log.i(TAG, "Click -> Register");
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private boolean validateForm() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Log.i(TAG, "Email empty");
            Snackbar.make(textViewBtnRegister, R.string.please_email, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Log.i(TAG, "Password empty");
            Snackbar.make(textViewBtnRegister, R.string.please_password, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            Log.i(TAG, "Email invalid");
            Snackbar.make(textViewBtnRegister, R.string.email_invalid, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Log.i(TAG, "Password <= 6");
            Snackbar.make(textViewBtnRegister, R.string.password_6, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
