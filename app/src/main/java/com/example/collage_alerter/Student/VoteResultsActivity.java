package com.example.collage_alerter.Student;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
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

public class VoteResultsActivity extends AppCompatActivity {

    private RecyclerView resultsRecyclerView;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private List<Candidate> candidateList;
    private CandidateAdapter candidateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_results);

        // Initialize UI components
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Setup RecyclerView
        candidateList = new ArrayList<>();
        candidateAdapter = new CandidateAdapter(candidateList);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultsRecyclerView.setAdapter(candidateAdapter);

        loadResults();
    }

    private void loadResults() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("Candidates").orderBy("votes").get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
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
                Toast.makeText(this, "Failed to load results!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
