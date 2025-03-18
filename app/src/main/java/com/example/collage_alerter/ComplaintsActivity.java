package com.example.collage_alerter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplaintsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ComplaintAdapter complaintAdapter;
    private List<Complaint> complaintList;
    private FirebaseFirestore db;
    private FloatingActionButton fabAddComplaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_complaints);

        recyclerView = findViewById(R.id.recyclerViewComplaints);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAddComplaint = findViewById(R.id.fabAddComplaint);
        db = FirebaseFirestore.getInstance();
        complaintList = new ArrayList<>();
        complaintAdapter = new ComplaintAdapter(complaintList);
        recyclerView.setAdapter(complaintAdapter);

        loadComplaints();

        fabAddComplaint.setOnClickListener(view -> showAddComplaintDialog());
    }

    private void loadComplaints() {
        CollectionReference complaintsRef = db.collection("complaints");
        complaintsRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(this, "Error loading complaints", Toast.LENGTH_SHORT).show();
                return;
            }
            complaintList.clear();
            if (value != null) {
                for (QueryDocumentSnapshot document : value) {
                    Complaint complaint = document.toObject(Complaint.class);
                    complaint.setId(document.getId());
                    complaintList.add(complaint);
                }
                complaintAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showAddComplaintDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_complaint, null);
        builder.setView(dialogView);

        EditText editComplaint = dialogView.findViewById(R.id.editComplaint);
        Button submitComplaintBtn = dialogView.findViewById(R.id.submitComplaintBtn);

        AlertDialog dialog = builder.create();
        dialog.show();

        submitComplaintBtn.setOnClickListener(v -> {
            String complaintText = editComplaint.getText().toString().trim();
            if (TextUtils.isEmpty(complaintText)) {
                Toast.makeText(this, "Please enter your complaint!", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> complaint = new HashMap<>();
            complaint.put("complaint", complaintText);
            complaint.put("timestamp", Timestamp.now());

            db.collection("complaints").add(complaint)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Complaint submitted!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to submit complaint!", Toast.LENGTH_SHORT).show());
        });
    }
}
