package com.example.collage_alerter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Login_Activity extends AppCompatActivity {

    private EditText emailedittxt, passedittxt;
    private Button login_btn;
    private CheckBox rememberMeCheckBox;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailedittxt = findViewById(R.id.emailedittxt);
        passedittxt = findViewById(R.id.passedittxt);
        login_btn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progressbar);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check if "Remember Me" is enabled and auto-login the user
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean rememberMe = prefs.getBoolean("rememberMe", false);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (rememberMe && currentUser != null && currentUser.isEmailVerified()) {
            checkUserRole(currentUser.getEmail());
        }

        login_btn.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = emailedittxt.getText().toString().trim();
        String password = passedittxt.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailedittxt.setError("Invalid Email");
            return;
        }

        if (password.length() < 6) {
            passedittxt.setError("Password must be at least 6 characters");
            return;
        }

        loginToFirebase(email, password);
    }

    private void loginToFirebase(String email, String pass) {
        showProgress(true);
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                // Save "Remember Me" preference
                                SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("rememberMe", rememberMeCheckBox.isChecked());
                                editor.apply();

                                checkUserRole(email);
                            } else {
                                Toast.makeText(this, "Verify your email first!", Toast.LENGTH_LONG).show();

                                // Send Password Reset Email
                                mAuth.sendPasswordResetEmail(email)
                                        .addOnSuccessListener(aVoid ->
                                                Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Failed to send reset email: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        }
                    } else {
                        handleLoginError(task.getException());
                    }
                });
    }

    private void handleLoginError(Exception e) {
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
        } else if (e instanceof FirebaseAuthInvalidUserException) {
            Toast.makeText(this, "No account found with this email!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Authentication failed! Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        login_btn.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void checkUserRole(String email) {
        db.collection("users").document(email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        DocumentSnapshot document = task.getResult();
                        String role = document.getString("role");

                        Map<String, Class<?>> roleMap = new HashMap<>();
                        roleMap.put("student", StudentDashboard.class);
                        roleMap.put("admin", AdminDashboardActivity.class);

                        // Staff roles
                        List<String> staffRoles = Arrays.asList("Director", "Principal", "HOD", "Professor", "Lecturer");
                        List<String> nonStaffRoles = Arrays.asList("Lab Assistant", "Clerk", "Accountant", "Librarian",
                                "Peon", "Security Guard", "Cleaner", "Driver", "Doctor");

                        if (staffRoles.contains(role)) {
                            roleMap.put(role, StaffDashboardActivity.class);
                        } else if (nonStaffRoles.contains(role)) {
                            roleMap.put(role, NonStaffDashboardActivity.class);
                        }

                        if (roleMap.containsKey(role)) {
                            startActivity(new Intent(this, roleMap.get(role)));
                            finish();
                        } else {
                            Toast.makeText(this, "Role not assigned or invalid!", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }

                    } else {
                        Toast.makeText(this, "Error fetching user role!", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                });
    }

    public void logoutUser() {
        FirebaseAuth.getInstance().signOut();

        // Clear "Remember Me" setting
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("rememberMe", false);
        editor.apply();

        startActivity(new Intent(this, Login_Activity.class));
        finish();
    }
}
