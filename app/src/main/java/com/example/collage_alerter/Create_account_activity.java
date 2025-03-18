
package com.example.collage_alerter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Create_account_activity extends AppCompatActivity {

    EditText emailedittxt, passedittxt, passcontxt;
    Button createacc;
    ProgressBar progressBar;
    RadioGroup roleGroup;
    TextView loginbtntxt;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailedittxt = findViewById(R.id.email_edit_text);
        passedittxt = findViewById(R.id.pass_edit_text);
        passcontxt = findViewById(R.id.pass_conform_edit_text);
        createacc = findViewById(R.id.create_acc_btn);
        progressBar = findViewById(R.id.progressbar);
        loginbtntxt = findViewById(R.id.login_txtview_btn);
        roleGroup = findViewById(R.id.roleGroup);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        findViewById(R.id.cardView21).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        findViewById(R.id.roleGroup).startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in));
        findViewById(R.id.toplinearLayout1).startAnimation(AnimationUtils.loadAnimation(this, R.anim.drop_down));

        createacc.setOnClickListener(view -> CreateAcc());
        loginbtntxt.setOnClickListener(view -> finish());
    }

    void CreateAcc() {
        String email = emailedittxt.getText().toString().trim();
        String pass = passedittxt.getText().toString().trim();
        String conpass = passcontxt.getText().toString().trim();

        if (!validateData(email, pass, conpass)) {
            return;
        }

        int selectedRoleId = roleGroup.getCheckedRadioButtonId();
        RadioButton selectedRoleButton = findViewById(selectedRoleId);
        String role = selectedRoleButton.getText().toString().toLowerCase();

        createacc_fire(email, pass, role);
    }

    boolean validateData(String email, String pass, String conpass) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailedittxt.setError("Invalid Email");
            return false;
        }
        if (pass.length() < 6) {
            passedittxt.setError("Password must be at least 6 characters");
            return false;
        }
        if (!pass.equals(conpass)) {
            passcontxt.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    void createacc_fire(String email, String pass, String role) {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    user.sendEmailVerification().addOnCompleteListener(verifyTask -> {
                        if (verifyTask.isSuccessful()) {
                            storeUserRole(email, role);
                        } else {
                            Toast.makeText(Create_account_activity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            } else {
                Toast.makeText(Create_account_activity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    void storeUserRole(String email, String role) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("role", role);

        db.collection("users").document(email).set(user).addOnCompleteListener(task -> {
            Toast.makeText(Create_account_activity.this, "Account Created. Verify Email!", Toast.LENGTH_SHORT).show();
            Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification();
            firebaseAuth.signOut();
            finish();
        });
    }
}
