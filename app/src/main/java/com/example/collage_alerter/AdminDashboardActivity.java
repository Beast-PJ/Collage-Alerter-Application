package com.example.collage_alerter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collage_alerter.Student.StudentElectionActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private ImageButton logoutBtn;
    private TextView adminName;
    private LinearLayout manageStaff, manageUsers, noticeBoard, manageEvents, manageSchedule, assignClass, complaint;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize UI components
        logoutBtn = findViewById(R.id.logoutBtn);
        adminName = findViewById(R.id.admin_name);
        manageStaff = findViewById(R.id.manage_staff);
        manageUsers = findViewById(R.id.manage_non_teach);
        noticeBoard = findViewById(R.id.notice_board);
        manageEvents = findViewById(R.id.manage_events);
//        manageSchedule = findViewById(R.id.manage_schedule);
        assignClass = findViewById(R.id.admin_election);
        complaint = findViewById(R.id.complaint);

        // Set Admin Name (Hardcoded for now, replace with dynamic data)
        adminName.setText("Prathamesh Jadhav");

        // Logout Button Click Listener
        logoutBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboardActivity.this, Login_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Manage Staff Click Listener
        manageStaff.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageStaffActivity.class);
            startActivity(intent);
        });


        manageUsers.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageNonTeachStaffActivity.class);
            startActivity(intent);
        });

        // Notice Board Click Listener
        noticeBoard.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageStudentsActivity.class);
            startActivity(intent);
        });

        // Manage Events Click Listener
        manageEvents.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageEventsActivity.class);
            startActivity(intent);
        });

        // Assign Class Click Listener
        assignClass.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboardActivity.this, StudentElectionActivity.class);
            startActivity(intent);
        });

        complaint.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ComplaintsActivity.class);
            startActivity(intent);
        });
    }
}