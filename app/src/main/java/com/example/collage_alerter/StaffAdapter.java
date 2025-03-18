package com.example.collage_alerter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {
    private List<StaffModel> staffList;
    private Context context;

    public StaffAdapter(List<StaffModel> staffList, Context context) {
        this.staffList = staffList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_staff, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StaffModel staff = staffList.get(position);
        holder.name.setText(staff.getName());
        holder.role.setText(staff.getRole());
        holder.email.setText(staff.getEmail()); // Now setting email

        // Handle edit button click
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditStaffActivity.class);
            intent.putExtra("staffId", staff.getId());  // Make sure this is not null
            intent.putExtra("staffName", staff.getName());
            intent.putExtra("staffRole", staff.getRole());
            intent.putExtra("staffEmail", staff.getEmail());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, role, email;
        ImageView editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.staffName);
            role = itemView.findViewById(R.id.staffRole);
            email = itemView.findViewById(R.id.staffEmail); // Bind email field
            editButton = itemView.findViewById(R.id.editStaff);
        }
    }
}
