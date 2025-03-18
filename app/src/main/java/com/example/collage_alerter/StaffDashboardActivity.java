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

public class StaffDashboardActivity extends AppCompatActivity {

    private TextView welcomeText, staffName, staffDetails;
    private ImageButton logoutBtn;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private LinearLayout manageStud, Election, manageEvents, assignCalendar, timetable;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        // Initialize UI Components
        welcomeText = findViewById(R.id.welcomeText);
        staffName = findViewById(R.id.admin_name);
        logoutBtn = findViewById(R.id.logoutBtn);

        manageEvents = findViewById(R.id.manage_events);
        Election = findViewById(R.id.election);
//        noticeBoard = findViewById(R.id.notice_board);
        assignCalendar = findViewById(R.id.assign_class);
        timetable = findViewById(R.id.notice_board);

        // Load staff details from Firebase
        loadStaffDetails();

        // Set Click Listeners for Dashboard Options

        Election.setOnClickListener(v -> openActivity(StudentElectionActivity.class));
//        noticeBoard.setOnClickListener(v -> openActivity(NoticeBoardActivity.class));
        manageEvents.setOnClickListener(v -> openActivity(ManageEventsActivity.class));
//        assignCalendar.setOnClickListener(v -> openActivity(AssignToCalendarActivity.class));
//        timetable.setOnClickListener(v -> openActivity(TimetableActivity.class));

        // Logout Button Click
        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(StaffDashboardActivity.this, Login_Activity.class));
            finish();
        });
    }

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
        Intent intent = new Intent(StaffDashboardActivity.this, activityClass);
        startActivity(intent);
    }
}
