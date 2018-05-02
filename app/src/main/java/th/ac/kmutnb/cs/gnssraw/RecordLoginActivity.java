package th.ac.kmutnb.cs.gnssraw;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.validator.routines.EmailValidator;

public class RecordLoginActivity extends AppCompatActivity {

    private static final String TAG = RecordLoginActivity.class.getSimpleName();

    private TextView textViewBtnLogin;
    private TextView textViewBtnRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_login);

        textViewBtnLogin = findViewById(R.id.recordLogin_btnLogin);
        textViewBtnRegister = findViewById(R.id.recordLogin_btnRegister);
        editTextEmail = findViewById(R.id.recordLogin_inputMail);
        editTextPassword = findViewById(R.id.recordLogin_inputPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        textViewBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> Login");
                if (validateForm()) {
                    firebaseAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(),
                            editTextPassword.getText().toString())
                            .addOnCompleteListener(RecordLoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "signInWithEmail:success");
                                        startActivity(new Intent(RecordLoginActivity.this, RecordStartActivity.class));
                                        finish();
                                    } else {
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Snackbar.make(textViewBtnRegister, R.string.login_failed, Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        textViewBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> Register");
                startActivity(new Intent(RecordLoginActivity.this, RecordRegisterActivity.class));
                finish();
            }
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
