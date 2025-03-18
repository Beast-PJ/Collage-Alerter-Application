package com.example.collage_alerter;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class FacilityBookingActivity extends AppCompatActivity {

    private Spinner spinnerFacility, spinnerTimeSlot;
    private Button btnSelectDate, btnCheckAvailability;
    private FloatingActionButton fabBookNow;
    private TextView textSelectedDate;
    private FirebaseFirestore db;
    private String selectedDate = "";
    private String selectedFacility, selectedTimeSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_booking);

        spinnerFacility = findViewById(R.id.spinnerFacility);
        spinnerTimeSlot = findViewById(R.id.spinnerTimeSlot);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnCheckAvailability = findViewById(R.id.btnCheckAvailability);
        fabBookNow = findViewById(R.id.fabBookNow);
        textSelectedDate = findViewById(R.id.textSelectedDate);
        db = FirebaseFirestore.getInstance();

        // Populate Facility List
        ArrayAdapter<String> facilityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Library Hall",
                        "Computer Lab",
                        "Auditorium",
                        "Sports Ground",
                        "Conference Room",
                        "Chemistry Lab",
                        "Physics Lab",
                        "Music Room",
                        "Art Studio",
                        "Cafeteria"});
        spinnerFacility.setAdapter(facilityAdapter);

        // Populate Time Slot List
        ArrayAdapter<String> timeSlotAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"08:00 AM - 09:00 AM",
                        "09:00 AM - 10:00 AM",
                        "10:00 AM - 11:00 AM",
                        "11:00 AM - 12:00 PM",
                        "12:00 PM - 01:00 PM",
                        "02:00 PM - 03:00 PM",
                        "03:00 PM - 04:00 PM",
                        "04:00 PM - 05:00 PM"});
        spinnerTimeSlot.setAdapter(timeSlotAdapter);

        btnSelectDate.setOnClickListener(view -> showDatePicker());
        btnCheckAvailability.setOnClickListener(view -> checkAvailability());
        fabBookNow.setOnClickListener(view -> bookFacility());
    }

    private void bookFacility() {
        String selectedFacility = spinnerFacility.getSelectedItem().toString();
        String selectedTimeSlot = spinnerTimeSlot.getSelectedItem().toString();
        String bookedBy = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get user ID

        if (TextUtils.isEmpty(selectedFacility) || TextUtils.isEmpty(selectedTimeSlot)) {
            Toast.makeText(this, "Please select a facility and time slot", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference bookingsRef = db.collection("facility_bookings");

        // Check if the selected time slot is already booked
        bookingsRef.whereEqualTo("facility", selectedFacility)
                .whereEqualTo("timeSlot", selectedTimeSlot)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "Time slot is already booked!", Toast.LENGTH_LONG).show();
                    } else {
                        // If available, proceed with booking
                        Map<String, Object> booking = new HashMap<>();
                        booking.put("facility", selectedFacility);
                        booking.put("timeSlot", selectedTimeSlot);
                        booking.put("bookedBy", bookedBy);
                        booking.put("timestamp", Timestamp.now());

                        bookingsRef.add(booking)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(this, "Booking successful!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Booking failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking availability: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDate = day + "/" + (month + 1) + "/" + year;
            textSelectedDate.setText("Selected Date: " + selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void checkAvailability() {
        selectedFacility = spinnerFacility.getSelectedItem().toString();
        selectedTimeSlot = spinnerTimeSlot.getSelectedItem().toString();

        db.collection("bookings")
                .whereEqualTo("facility", selectedFacility)
                .whereEqualTo("date", selectedDate)
                .whereEqualTo("timeSlot", selectedTimeSlot)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        fabBookNow.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Slot available!", Toast.LENGTH_SHORT).show();
                    } else {
                        fabBookNow.setVisibility(View.GONE);
                        Toast.makeText(this, "Slot already booked!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
