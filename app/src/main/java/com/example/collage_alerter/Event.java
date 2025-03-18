package com.example.collage_alerter;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class Event {
    @Exclude  // Prevents Firestore from saving this field
    private String eventId;
    private String eventName;
    private String eventDate;
    private String eventDescription;

    // Empty Constructor (Required for Firebase)
    public Event() {}

    // Constructor with all fields
    public Event(String eventId, String eventName, String eventDate, String eventDescription) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventDescription = eventDescription;
    }

    // Getters and Setters
    @Exclude
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @NonNull
    public String getEventName() {
        return eventName != null ? eventName : "Untitled Event";
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @NonNull
    public String getEventDate() {
        return eventDate != null ? eventDate : "Date not set";
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    @NonNull
    public String getEventDescription() {
        return eventDescription != null ? eventDescription : "No description available";
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", eventDescription='" + eventDescription + '\'' +
                '}';
    }
}
