package com.example.collage_alerter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
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
import java.util.UUID;

public class AddStudentActivity extends Activity {

    private static final int PICK_EXCEL_FILE = 1;

    private Button btnSelectExcel, btnUploadStudents;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Uri excelFileUri;
    private List<StudentModel> studentsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_students);

        btnSelectExcel = findViewById(R.id.btnSelectExcel);
        btnUploadStudents = findViewById(R.id.btnUploadStudents);
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnSelectExcel.setOnClickListener(v -> openFileChooser());
        btnUploadStudents.setOnClickListener(v -> {
            if (excelFileUri != null) {
                studentsList = readExcelData(excelFileUri);
                if (studentsList != null && !studentsList.isEmpty()) {
                    addStudentsToFirebase();
                } else {
                    Toast.makeText(this, "No valid student data found!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please select an Excel file first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        startActivityForResult(intent, PICK_EXCEL_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_EXCEL_FILE && resultCode == RESULT_OK && data != null) {
            excelFileUri = data.getData();
            Toast.makeText(this, "Excel file selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private List<StudentModel> readExcelData(Uri fileUri) {
        List<StudentModel> studentList = new ArrayList<>();
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                String name = row.getCell(0).getStringCellValue();
                String prn = row.getCell(1).getStringCellValue();
                String email = row.getCell(2).getStringCellValue();
                String course = row.getCell(3).getStringCellValue();
                String year = row.getCell(4).getStringCellValue();
                String phone = row.getCell(5).getStringCellValue();
                String password = row.getCell(6).getStringCellValue(); // Read password from Excel

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    studentList.add(new StudentModel(name, prn, email, course, year, phone, password));
                }
            }

            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading Excel file!", Toast.LENGTH_SHORT).show();
        }

        return studentList;
    }

    private void addStudentsToFirebase() {
        progressBar.setVisibility(View.VISIBLE);

        for (StudentModel student : studentsList) {
            String studentId = UUID.randomUUID().toString();
            String email = student.getEmail();
            String password = student.getPassword(); // Get password from Excel

            Map<String, Object> studentData = new HashMap<>();
            studentData.put("id", studentId);
            studentData.put("name", student.getName());
            studentData.put("prn", student.getPrn());
            studentData.put("email", email);
            studentData.put("course", student.getCourse());
            studentData.put("year", student.getYear());
            studentData.put("phone", student.getPhone());
            studentData.put("role", "student");
            studentData.put("password", password); // Store password in Firestore (not secure!)

            db.collection("students").document(studentId).set(studentData)
                    .addOnSuccessListener(aVoid -> registerStudentInAuth(email, password))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error adding student: " + student.getName(), Toast.LENGTH_SHORT).show();
                    });
        }

        progressBar.setVisibility(View.GONE);
    }

    private void registerStudentInAuth(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sendResetEmail(email);
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            sendResetEmail(email);
                        } else {
                            Toast.makeText(this, "Failed to register: " + email, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Reset email sent: " + email, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send reset email: " + email, Toast.LENGTH_SHORT).show();
                });
    }
}
