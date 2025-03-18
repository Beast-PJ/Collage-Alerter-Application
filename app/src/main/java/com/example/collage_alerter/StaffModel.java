package com.example.collage_alerter;

public class StaffModel {
    private String id;
    private String name;
    private String role;
    private String email;

    public StaffModel() { } // Required for Firebase

    public StaffModel(String id, String name, String role, String email) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.email = email;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
}

