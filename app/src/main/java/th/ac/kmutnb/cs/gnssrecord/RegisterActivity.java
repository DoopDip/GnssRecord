package th.ac.kmutnb.cs.gnssrecord;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextRePassword;
    private EditText editTextName;
    private TextView textViewBtnRegister;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        editTextEmail = findViewById(R.id.register_inputMail);
        editTextPassword = findViewById(R.id.register_inputPassword);
        editTextRePassword = findViewById(R.id.register_inputRePassword);
        editTextName = findViewById(R.id.register_inputName);
        textViewBtnRegister = findViewById(R.id.register_btnRegister);

        firebaseAuth = FirebaseAuth.getInstance();

        textViewBtnRegister.setOnClickListener(v -> {
            if (validateForm()) {
                firebaseAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(),
                        editTextPassword.getText().toString())
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                addDisplayName(editTextName.getText().toString());
                                Log.i(TAG, "Register -> Successfully");
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Log.i(TAG, "Register ->" + Objects.requireNonNull(task.getException()).getLocalizedMessage());
                                Snackbar.make(textViewBtnRegister, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
            }
        });

    }

    private boolean validateForm() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String rePassword = editTextRePassword.getText().toString();
        String name = editTextName.getText().toString();

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
        if (TextUtils.isEmpty(rePassword)) {
            Log.i(TAG, "rePassword empty");
            Snackbar.make(textViewBtnRegister, R.string.please_re_password, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            Log.i(TAG, "name empty");
            Snackbar.make(textViewBtnRegister, R.string.please_name, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            Log.i(TAG, "Email invalid");
            Snackbar.make(textViewBtnRegister, R.string.email_invalid, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6 || rePassword.length() < 6) {
            Log.i(TAG, "Password <= 6");
            Snackbar.make(textViewBtnRegister, R.string.password_6, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(rePassword)) {
            Log.i(TAG, "Password not equals");
            Snackbar.make(textViewBtnRegister, R.string.password_match, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void addDisplayName(String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        assert user != null;
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                    }
                });
    }
}
