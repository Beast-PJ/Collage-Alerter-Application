package com.example.collage_alerter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collage_alerter.Student.Student;
import com.example.collage_alerter.Student.StudentAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageStudentsActivity extends AppCompatActivity {

    private RecyclerView studentRecyclerView;
    private Button btnAddStudent, btnUploadExcel;
    private FirebaseFirestore db;
    private List<Student> studentList;
    private StudentAdapter studentAdapter;
    private static final int PICK_EXCEL_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        studentRecyclerView = findViewById(R.id.studentRecyclerView);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnUploadExcel = findViewById(R.id.btnUploadExcel);

        db = FirebaseFirestore.getInstance();
        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentList, db,this);

        studentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentRecyclerView.setAdapter(studentAdapter);

        loadStudents();

        btnAddStudent.setOnClickListener(v -> startActivity(new Intent(this, AddStudentActivity.class)));

        btnUploadExcel.setOnClickListener(v -> openFilePicker());
    }

    private void loadStudents() {
        db.collection("students").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                studentList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    Student student = document.toObject(Student.class);
                    studentList.add(student);
                }
                studentAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to load students!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        startActivityForResult(intent, PICK_EXCEL_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_EXCEL_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            processExcelFile(fileUri);
        }
    }

    private void processExcelFile(Uri fileUri) {
        try {
            if (fileUri == null) {
                Toast.makeText(this, "Invalid file URI!", Toast.LENGTH_SHORT).show();
                return;
            }

            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                Toast.makeText(this, "Unable to open file!", Toast.LENGTH_SHORT).show();
                return;
            }

            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                // SAFER way to read data
                String name = row.getCell(0) != null ? row.getCell(0).toString().trim() : "";
                String prn = row.getCell(1) != null ? row.getCell(1).toString().trim() : "";
                String email = row.getCell(2) != null ? row.getCell(2).toString().trim() : "";
                String dept = row.getCell(3) != null ? row.getCell(3).toString().trim() : "";
                String year = row.getCell(4) != null ? row.getCell(4).toString().trim() : "";

                if (name.isEmpty() || prn.isEmpty() || email.isEmpty()) {
                    Toast.makeText(this, "Skipping row " + row.getRowNum() + ": Missing data!", Toast.LENGTH_SHORT).show();
                    continue;
                }

                Map<String, Object> studentData = new HashMap<>();
                studentData.put("name", name);
                studentData.put("prn", prn);
                studentData.put("email", email);
                studentData.put("dept", dept);
                studentData.put("year", year);

                db.collection("students").document(prn)
                        .set(studentData)
                        .addOnSuccessListener(aVoid -> Log.d("ExcelUpload", "Student added: " + name))
                        .addOnFailureListener(e -> Log.e("ExcelUpload", "Error adding student: " + e.getMessage()));
            }

            workbook.close();
            inputStream.close();
            Toast.makeText(this, "Excel data uploaded successfully!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Failed to process file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ExcelProcessing", "Error: ", e);
        }
    }

}
