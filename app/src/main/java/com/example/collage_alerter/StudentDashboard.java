package com.example.collage_alerter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collage_alerter.Student.StudentElectionActivity;
import com.google.firebase.auth.FirebaseAuth;

public class StudentDashboard extends AppCompatActivity {

    private TextView welcomeText;
    ImageView logoutBtn;
    private LinearLayout electionBtn, complaintBtn, bookingBtn;
    private FirebaseAuth mAuth;
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        mAuth = FirebaseAuth.getInstance();

        welcomeText = findViewById(R.id.welcomeText);
        logoutBtn = findViewById(R.id.logoutBtn);
        electionBtn = findViewById(R.id.election);

        LinearLayout manageEvents = findViewById(R.id.manage_events);
        LinearLayout assignClubs = findViewById(R.id.club);
        LinearLayout studentElection = findViewById(R.id.election);
//        LinearLayout facilityBooking = findViewById(R.id.facility_booking);
        LinearLayout manageSchedule = findViewById(R.id.manage_schedule);
//        LinearLayout studentComplaints = findViewById(R.id.assign_class);
         LinearLayout studentComplaints = findViewById(R.id.raise_complaint);

        // Setting up click listeners
        manageEvents.setOnClickListener(view -> navigateTo(ManageEventsUserActivity.class));
        studentElection.setOnClickListener(view -> navigateTo(StudentElectionActivity.class));
//        facilityBooking.setOnClickListener(view -> navigateTo(FacilityBookingActivity.class));
//        manageSchedule.setOnClickListener(view -> navigateTo(ClassScheduleActivity.class));
        studentComplaints.setOnClickListener(view -> navigateTo(ComplaintsActivity.class));
        // Display the student's email as a welcome message
        if (mAuth.getCurrentUser() != null) {
            String email = mAuth.getCurrentUser().getEmail();
            welcomeText.setText("Welcome, " + email);
        }

        // Set up button click listeners
        logoutBtn.setOnClickListener(view -> logoutUser());
        electionBtn.setOnClickListener(view -> openElections());
//        complaintBtn.setOnClickListener(view -> openComplaints());
//        bookingBtn.setOnClickListener(view -> openBooking());
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(StudentDashboard.this, targetActivity);
        startActivity(intent);
    }

    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(StudentDashboard.this, Login_Activity.class));
        finish();
    }

    private void openElections() {
        Toast.makeText(this, "Opening Elections...", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, StudentElectionActivity.class)); // Ensure StudentElectionActivity exists
    }


}
