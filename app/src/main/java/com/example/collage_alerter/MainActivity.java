package com.example.collage_alerter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Uri data = getIntent().getData();
        if (data != null && data.getPath().equals("/setpassword")) {
            String email = data.getQueryParameter("email");

            mAuth.signInWithEmailLink(email, data.toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        checkUserRole(email);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Invalid login link!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void checkUserRole(String email) {
        db.collection("users").document(email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String role = task.getResult().getString("role");

                Intent intent;
                if ("student".equals(role)) {
                    intent = new Intent(MainActivity.this, StudentDashboard.class);
                } else if ("staff".equals(role)) {
                    intent = new Intent(MainActivity.this, StaffDashboardActivity.class);
                } else if ("admin".equals(role)) {
                    intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                } else {
                    Toast.makeText(MainActivity.this, "Role not assigned!", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MainActivity.this, "User role not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
