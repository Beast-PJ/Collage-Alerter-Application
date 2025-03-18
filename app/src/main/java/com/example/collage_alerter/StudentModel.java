package com.example.collage_alerter;

public class StudentModel {
    private String name;
    private String prn;
    private String email;
    private String course;
    private String year;
    private String phone;
    private String password; // Added password field

    public StudentModel() {
        // Empty constructor required for Firebase
    }

    public StudentModel(String name, String prn, String email, String course, String year, String phone, String password) {
        this.name = name;
        this.prn = prn;
        this.email = email;
        this.course = course;
        this.year = year;
        this.phone = phone;
        this.password = password; // Initialize password
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrn() {
        return prn;
    }

    public void setPrn(String prn) {
        this.prn = prn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
