package com.example.collage_alerter.Student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.collage_alerter.Candidate;
import com.example.collage_alerter.CandidateAdapter;
import com.example.collage_alerter.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class StudentElectionActivity extends AppCompatActivity {

    private RecyclerView candidateRecyclerView;
    private Button btnAddCandidate, btnViewResults;
    private FirebaseFirestore db;
    private List<Candidate> candidateList;
    private CandidateAdapter candidateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_election);

        // Initialize UI components
        candidateRecyclerView = findViewById(R.id.candidateRecyclerView);
        btnAddCandidate = findViewById(R.id.btnAddCandidate);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Setup RecyclerView
        candidateList = new ArrayList<>();
        candidateAdapter = new CandidateAdapter(candidateList);
        candidateRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        candidateRecyclerView.setAdapter(candidateAdapter);

        loadCandidates();

        btnAddCandidate.setOnClickListener(v -> {
            Intent intent = new Intent(StudentElectionActivity.this, AddCandidateActivity.class);
            startActivity(intent);
        });

//        btnViewResults.setOnClickListener(v -> startActivity(new Intent(StudentElectionActivity.this, VoteResultsActivity.class)));
    }

    private void loadCandidates() {
        db.collection("Candidates").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                candidateList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    Candidate candidate = document.toObject(Candidate.class);
                    if (candidate != null) {
                        candidateList.add(candidate);
                    }
                }
                candidateAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to load candidates!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
