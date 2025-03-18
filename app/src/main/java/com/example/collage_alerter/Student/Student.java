package com.example.collage_alerter.Student;

public class Student {
    private String id;
    private String name;
    private String prn;
    private String email;
    private String course;
    private String year;
    private String phone;
    private String role;

    // **Empty Constructor for Firebase**
    public Student() {}

    // **Parameterized Constructor**
    public Student(String id, String name, String prn, String email, String course, String year, String phone, String role) {
        this.id = id;
        this.name = name;
        this.prn = prn;
        this.email = email;
        this.course = course;
        this.year = year;
        this.phone = phone;
        this.role = role;
    }

    // **Getters**
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPrn() { return prn; }
    public String getEmail() { return email; }
    public String getCourse() { return course; }
    public String getYear() { return year; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }

    // **Setters**
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrn(String prn) { this.prn = prn; }
    public void setEmail(String email) { this.email = email; }
    public void setCourse(String course) { this.course = course; }
    public void setYear(String year) { this.year = year; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(String role) { this.role = role; }
}
