package com.example.collage_alerter;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collage_alerter.Candidate;
import com.example.collage_alerter.CandidateAdapter;
import com.example.collage_alerter.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class StudentVoteActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView candidatesRecyclerView;
    private CandidateAdapter adapter;
    private String studentId = "student_123"; // Replace with logged-in student's ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_vote);

        candidatesRecyclerView = findViewById(R.id.candidateRecyclerView);
        candidatesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        checkIfVoted();
    }

    private void checkIfVoted() {
        db.collection("students").document(studentId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists() && Boolean.TRUE.equals(document.getBoolean("hasVoted"))) {
                        Toast.makeText(this, "You have already voted!", Toast.LENGTH_LONG).show();
                        finish(); // Prevent voting again
                    } else {
                        loadCandidates();
                    }
                });
    }

    private void loadCandidates() {
        db.collection("candidates").get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Candidate> candidateList = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Candidate candidate = doc.toObject(Candidate.class);
                        candidate.setId(doc.getId()); // Store document ID
                        candidateList.add(candidate);
                    }
                    adapter = new CandidateAdapter(candidateList);
                    candidatesRecyclerView.setAdapter(adapter);
                });
    }

    public void voteForCandidate(String candidateId) {
        db.collection("students").document(studentId)
                .update("hasVoted", true);

        db.collection("candidates").document(candidateId)
                .update("votes", FieldValue.increment(1))
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Vote Submitted!", Toast.LENGTH_SHORT).show();
                    finish(); // Close after voting
                });
    }
}
