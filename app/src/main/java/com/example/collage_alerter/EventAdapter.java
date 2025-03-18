package com.example.collage_alerter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> eventList;
    private Context context;

    public EventAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.textViewTitle.setText(event.getEventName());
        holder.textViewDate.setText("Date: " + event.getEventDate());

        // **Handle Delete Button Click**
        holder.btnDelete.setOnClickListener(v -> {
            if (event.getEventId() != null) {
                deleteEvent(event.getEventId(), position, holder.itemView);
            } else {
                Toast.makeText(context, "Error: Event ID is null", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDate;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewEventTitle);
            textViewDate = itemView.findViewById(R.id.textViewEventDate);
            btnDelete = itemView.findViewById(R.id.btnDeleteEvent);
        }
    }

    // **Method to Delete an Event from Firestore**
    private void deleteEvent(String eventId, int position, View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Event removedEvent = eventList.get(position);
                    eventList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, eventList.size()); // Update positions

                    // 🎨 **Snackbar with Undo Option**
                    Snackbar.make(view, "Event deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", v -> {
                                // **Restore the deleted event**
                                db.collection("events").document(eventId)
                                        .set(removedEvent)
                                        .addOnSuccessListener(a -> {
                                            eventList.add(position, removedEvent);
                                            notifyItemInserted(position);
                                            notifyItemRangeChanged(position, eventList.size());
                                            Toast.makeText(view.getContext(), "Undo successful", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(view.getContext(), "Undo failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                        );
                            })
                            .show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(view.getContext(), "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
