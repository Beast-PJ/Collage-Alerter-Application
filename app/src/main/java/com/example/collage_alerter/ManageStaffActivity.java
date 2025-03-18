package com.example.collage_alerter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageStaffActivity extends AppCompatActivity {

    private RecyclerView recyclerViewStaff;
    private StaffAdapter staffAdapter;
    private List<StaffModel> staffList, filteredList;
    private EditText searchStaff;
    private FloatingActionButton fabAddStaff;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_staff);

        // Initialize UI components
        recyclerViewStaff = findViewById(R.id.recyclerViewStaff);
        searchStaff = findViewById(R.id.searchStaff);
        fabAddStaff = findViewById(R.id.fabAddStaff);

        // Initialize RecyclerView
        recyclerViewStaff.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize lists
        staffList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Initialize Adapter
        staffAdapter = new StaffAdapter(filteredList, this);
        recyclerViewStaff.setAdapter(staffAdapter);

        // Load staff data from Firestore
        loadStaffData();

        // Search functionality
        searchStaff.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStaff(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Floating button to add new staff
        fabAddStaff.setOnClickListener(v -> startActivity(new Intent(ManageStaffActivity.this, AddStaffActivity.class)));
    }

    // Load staff data from Firestore
    private void loadStaffData() {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                staffList.clear();
                filteredList.clear();
                QuerySnapshot snapshot = task.getResult();
                if (snapshot != null) {
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        StaffModel staff = document.toObject(StaffModel.class);
                        staffList.add(staff);
                    }
                }
                filteredList.addAll(staffList);
                staffAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ManageStaffActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to filter staff based on search query
    private void filterStaff(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(staffList);
        } else {
            for (StaffModel staff : staffList) {
                if (staff.getName().toLowerCase().contains(query.toLowerCase()) ||
                        staff.getRole().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(staff);
                }
            }
        }
        staffAdapter.notifyDataSetChanged();
    }
}
