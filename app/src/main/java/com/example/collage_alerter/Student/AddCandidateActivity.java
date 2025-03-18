package com.example.collage_alerter.Student;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.collage_alerter.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddCandidateActivity extends AppCompatActivity {

    private EditText editName, editPosition;
    private Button btnAddCandidate;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_candidate);

        editName = findViewById(R.id.editName);
        editPosition = findViewById(R.id.editPosition);
        btnAddCandidate = findViewById(R.id.btnAddCandidate);
        db = FirebaseFirestore.getInstance();

        btnAddCandidate.setOnClickListener(v -> addCandidateToFirestore());
    }

    private void addCandidateToFirestore() {
        String name = editName.getText().toString().trim();
        String position = editPosition.getText().toString().trim();

        if (name.isEmpty() || position.isEmpty()) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> candidate = new HashMap<>();
        candidate.put("name", name);
        candidate.put("position", position);
        candidate.put("votes", 0);

        db.collection("Candidates").add(candidate)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Candidate Added Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add candidate", Toast.LENGTH_SHORT).show());
    }
}
