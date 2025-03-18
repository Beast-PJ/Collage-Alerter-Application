package com.example.collage_alerter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collage_alerter.Student.StudentElectionActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class NonStaffDashboardActivity extends AppCompatActivity {

    private TextView welcomeText, staffName, staffDetails;
    private ImageButton logoutBtn;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private LinearLayout manageStud, Election, noticeBoard, manageEvents, assignCalendar, timetable, complaint, facility;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_staff_dashboard);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        // Initialize UI Components
        welcomeText = findViewById(R.id.welcomeText);
        staffName = findViewById(R.id.admin_name);
        logoutBtn = findViewById(R.id.logoutBtn);

        manageStud = findViewById(R.id.manage_staff);
        Election = findViewById(R.id.election);
        noticeBoard = findViewById(R.id.notice_board);
        manageEvents = findViewById(R.id.manage_events);
        timetable = findViewById(R.id.notice_board);
        complaint = findViewById(R.id.complaint);
        facility = findViewById(R.id.facility_booking);

        // Load staff details from Firebase
        loadStaffDetails();

        // Set Click Listeners for Dashboard Options
        manageStud.setOnClickListener(v -> openActivity(ManageEventsActivity.class));
        Election.setOnClickListener(v -> openActivity(StudentElectionActivity.class));
//        noticeBoard.setOnClickListener(v -> openActivity(NoticeBoardActivity.class));
//        manageEvents.setOnClickListener(v -> openActivity(ManageEventsActivity.class));
        facility.setOnClickListener(v -> openActivity(FacilityBookingActivity.class));
//        timetable.setOnClickListener(v -> openActivity(TimetableActivity.class));
        complaint.setOnClickListener(v -> openActivity(ComplaintsActivity.class));

        // Logout Button Click
        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(NonStaffDashboardActivity.this, Login_Activity.class));
            finish();
        });
    }

    @SuppressLint("SetTextI18n")
    private void loadStaffDetails() {
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_LONG).show();
            return;
        }

        String email = currentUser.getEmail();
        db.collection("staff").document(email).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        staffName.setText(documentSnapshot.getString("name"));
                        welcomeText.setText("Welcome, " + documentSnapshot.getString("name") + "!");
                        staffDetails.setText("Role: " + documentSnapshot.getString("role"));
                    } else {
                        Toast.makeText(this, "Staff data not found!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading data!", Toast.LENGTH_LONG).show());
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(NonStaffDashboardActivity.this, activityClass);
        startActivity(intent);
    }
}
