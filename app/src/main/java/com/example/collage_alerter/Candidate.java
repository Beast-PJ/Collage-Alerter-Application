package com.example.collage_alerter;

import com.google.firebase.firestore.Exclude;

public class Candidate {
    private String id;
    private String name;
    private String position;
    private int votes;

    public Candidate() {
        // Default constructor required for Firestore
    }

    public Candidate(String name, String position) {
        this.name = name;
        this.position = position;
        this.votes = 0;  // Default votes count
    }

    @Exclude  // Prevents Firestore from storing the ID field
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public String getPosition() { return position; }

    public int getVotes() { return votes; }

    public void setVotes(int votes) { this.votes = votes; }
}
