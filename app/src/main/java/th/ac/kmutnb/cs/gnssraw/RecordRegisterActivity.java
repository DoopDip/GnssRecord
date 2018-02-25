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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.apache.commons.validator.routines.EmailValidator;

public class RecordRegisterActivity extends AppCompatActivity {

    private static final String TAG = "RecordRegisterActivity";

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextRePassword;
    private EditText editTextName;
    private TextView textViewBtnRegister;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_register);

        editTextEmail = (EditText) findViewById(R.id.recordRegister_inputMail);
        editTextPassword = (EditText) findViewById(R.id.recordRegister_inputPassword);
        editTextRePassword = (EditText) findViewById(R.id.recordRegister_inputRePassword);
        editTextName = (EditText) findViewById(R.id.recordRegister_inputName);
        textViewBtnRegister = (TextView) findViewById(R.id.recordRegister_btnRegister);

        firebaseAuth = FirebaseAuth.getInstance();

        textViewBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    firebaseAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(),
                            editTextPassword.getText().toString())
                            .addOnCompleteListener(RecordRegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        addDisplayName(editTextName.getText().toString());
                                        Log.i(TAG, "createUserWithEmail:success");
                                        Intent intent = new Intent(RecordRegisterActivity.this, RecordStartActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.i(TAG, "createUserWithEmail:failure", task.getException());
                                        Snackbar.make(textViewBtnRegister, R.string.register_failed, Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
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
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }
}
