package com.example.collage_alerter;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddStaffActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail;
    private Spinner spinnerRole;
    private Button btnSaveStaff;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        editTextName = findViewById(R.id.editTextName);
        spinnerRole = findViewById(R.id.spinnerRole);
        editTextEmail = findViewById(R.id.editTextEmail);
        btnSaveStaff = findViewById(R.id.btnSaveStaff);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Load roles into Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.college_roles, android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        btnSaveStaff.setOnClickListener(v -> addNewStaff());
    }

    private void addNewStaff() {
        String name = editTextName.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();
        String email = editTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                        if (isNewUser) {
                            saveStaffToFirestore(name, role, email);
                        } else {
                            Toast.makeText(this, "Email already in use!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            saveStaffToFirestore(name, role, email);
                        } else {
                            Toast.makeText(this, "Error checking email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveStaffToFirestore(String name, String role, String email) {
        Map<String, Object> staff = new HashMap<>();
        staff.put("name", name);
        staff.put("role", role);
        staff.put("email", email);
        staff.put("status", "pending");

        db.collection("users").document(email).set(staff)
                .addOnSuccessListener(aVoid -> {
                    // Create the user in Firebase Authentication
                    mAuth.createUserWithEmailAndPassword(email, "DefaultPass123") // Temp password
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    sendPasswordResetEmail(email);
                                    Toast.makeText(this, "Staff added & email sent!", Toast.LENGTH_SHORT).show();
                                    clearFields();
                                } else {
                                    Toast.makeText(this, "Error creating user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error adding staff: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Reset email sent to " + email, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Failed to send email: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void clearFields() {
        editTextName.setText("");
        editTextEmail.setText("");
        spinnerRole.setSelection(0);
    }
}
