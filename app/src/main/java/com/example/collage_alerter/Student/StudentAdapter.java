package com.example.collage_alerter.Student;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collage_alerter.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private final List<Student> studentList;
    private final FirebaseFirestore db;
    private final Context context;

    public StudentAdapter(List<Student> studentList, FirebaseFirestore db, Context context) {
        this.studentList = studentList;
        this.db = db;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.studentName.setText(student.getName());
        holder.studentPrn.setText("PRN: " + student.getPrn());
        holder.studentCourseYear.setText(student.getCourse() + ", " + student.getYear());
        holder.studentEmail.setText("Email: " + student.getEmail());

        // Edit Button
        holder.btnEdit.setOnClickListener(v -> showEditDialog(student, position));

        // Delete Button
        holder.btnDelete.setOnClickListener(v -> showDeleteDialog(student, position));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, studentPrn, studentCourseYear, studentEmail, studentPhone;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.student_name);
            studentPrn = itemView.findViewById(R.id.student_prn);
            studentCourseYear = itemView.findViewById(R.id.student_course_year);
            studentEmail = itemView.findViewById(R.id.student_email);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // Edit Student
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    private void showEditDialog(Student student, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Student");

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_student, null);
        EditText editName = view.findViewById(R.id.editStudentName);
        EditText editPrn = view.findViewById(R.id.editStudentPrn);
        EditText editCourse = view.findViewById(R.id.editStudentCourse);
        EditText editYear = view.findViewById(R.id.editStudentYear);
        EditText editPhone = view.findViewById(R.id.editStudentPhone);

        editName.setText(student.getName());
        editPrn.setText(student.getPrn());
        editCourse.setText(student.getCourse());
        editYear.setText(student.getYear());
        editPhone.setText(student.getPhone());

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            student.setName(editName.getText().toString().trim());
            student.setPrn(editPrn.getText().toString().trim());
            student.setCourse(editCourse.getText().toString().trim());
            student.setYear(editYear.getText().toString().trim());
            student.setPhone(editPhone.getText().toString().trim());

            db.collection("students").document(student.getId()).set(student);
            notifyItemChanged(position);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    // **Show Delete Confirmation Dialog**
    private void showDeleteDialog(Student student, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Student")
                .setMessage("Are you sure you want to delete " + student.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // **Delete from Firestore**
                    db.collection("students").document(student.getId()).delete()
                            .addOnSuccessListener(aVoid -> {
                                studentList.remove(position); // Remove from UI List
                                notifyItemRemoved(position); // Update RecyclerView
                                Toast.makeText(context, "Student deleted successfully!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Failed to delete student!", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

}
