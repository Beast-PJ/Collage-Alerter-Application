package com.example.collage_alerter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditStaffActivity extends AppCompatActivity {

    private EditText editName, editRole, editEmail;
    private Button btnUpdateStaff, btnDeleteStaff;
    private FirebaseFirestore db;
    private String staffId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_staff);

        // Initialize UI components
        editName = findViewById(R.id.editStaffName);
        editRole = findViewById(R.id.editStaffRole);
        editEmail = findViewById(R.id.editStaffEmail);
        btnUpdateStaff = findViewById(R.id.btnUpdateStaff);
        btnDeleteStaff = findViewById(R.id.btnDeleteStaff);

        // Firestore instance
        db = FirebaseFirestore.getInstance();

        // Progress dialog setup
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        // Get staff details from Intent
        staffId = getIntent().getStringExtra("staffId");
        String staffName = getIntent().getStringExtra("staffName");
        String staffRole = getIntent().getStringExtra("staffRole");
        String staffEmail = getIntent().getStringExtra("staffEmail");

        // Debugging logs
        Log.d("EditStaffActivity", "Staff ID: " + staffId);
        Log.d("EditStaffActivity", "Staff Name: " + staffName);
        Log.d("EditStaffActivity", "Staff Role: " + staffRole);
        Log.d("EditStaffActivity", "Staff Email: " + staffEmail);

        // Set existing values
        editName.setText(staffName);
        editRole.setText(staffRole);
        editEmail.setText(staffEmail);

        // Make email field non-editable
        editEmail.setFocusable(false);
        editEmail.setClickable(false);

        // Click listener for Update button
        btnUpdateStaff.setOnClickListener(v -> updateStaffData());

        // Click listener for Delete button
        btnDeleteStaff.setOnClickListener(v -> confirmDelete());
    }

    // Method to update staff details
    private void updateStaffData() {
        if (staffId == null || staffId.trim().isEmpty()) {
            Toast.makeText(this, "Error: Staff ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        String newName = editName.getText().toString().trim();
        String newRole = editRole.getText().toString().trim();

        if (newName.isEmpty() || newRole.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show(); // Show loading

        DocumentReference staffRef = db.collection("users").document(staffId);
        staffRef.update("name", newName, "role", newRole)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditStaffActivity.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e("EditStaffActivity", "Update Error: " + e.getMessage());
                    Toast.makeText(EditStaffActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    // Confirm before deleting staff
    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Staff")
                .setMessage("Are you sure you want to delete this staff member?")
                .setPositiveButton("Yes", (dialog, which) -> deleteStaff())
                .setNegativeButton("No", null)
                .show();
    }

    // Method to delete staff from Firestore
    private void deleteStaff() {
        if (staffId == null || staffId.trim().isEmpty()) {
            Toast.makeText(this, "Error: Staff ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show(); // Show loading

        DocumentReference staffRef = db.collection("users").document(staffId);
        staffRef.delete()
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditStaffActivity.this, "Staff deleted successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after deletion
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e("EditStaffActivity", "Delete Error: " + e.getMessage());
                    Toast.makeText(EditStaffActivity.this, "Delete failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

}
