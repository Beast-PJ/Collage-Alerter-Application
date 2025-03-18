package com.example.collage_alerter;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdminElectionActivity extends AppCompatActivity {
    private EditText editName, editPosition;
    private Button btnAddCandidate;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_election);

        editName = findViewById(R.id.editName);
        editPosition = findViewById(R.id.editPosition);
        btnAddCandidate = findViewById(R.id.btnAddCandidate);
        db = FirebaseFirestore.getInstance();

        btnAddCandidate.setOnClickListener(v -> addCandidate());
    }

    private void addCandidate() {
        String name = editName.getText().toString().trim();
        String position = editPosition.getText().toString().trim();

        if (name.isEmpty() || position.isEmpty()) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        String candidateId = UUID.randomUUID().toString();
        Map<String, Object> candidate = new HashMap<>();
        candidate.put("id", candidateId);
        candidate.put("name", name);
        candidate.put("position", position);
        candidate.put("votes", 0);

        db.collection("Candidates").document(candidateId).set(candidate)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Candidate Added Successfully!", Toast.LENGTH_SHORT).show();
                    editName.setText("");
                    editPosition.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add candidate", Toast.LENGTH_SHORT).show());
    }
}
