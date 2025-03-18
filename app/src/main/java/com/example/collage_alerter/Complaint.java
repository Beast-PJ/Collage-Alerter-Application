package com.example.collage_alerter;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Complaint {
    private String id;
    private String complaint;

    @ServerTimestamp
    private Date timestamp;

    public Complaint() {} // Needed for Firebase

    public Complaint(String complaint) {
        this.complaint = complaint;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
