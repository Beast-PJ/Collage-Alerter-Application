package com.example.collage_alerter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.ViewHolder> {
    private final List<Candidate> candidateList;

    public CandidateAdapter(List<Candidate> candidateList) {
        this.candidateList = candidateList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_candidate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Candidate candidate = candidateList.get(position);
        holder.txtName.setText(candidate.getName());
        holder.txtPosition.setText(candidate.getPosition());
        holder.txtVotes.setText("Votes: " + candidate.getVotes());
    }

    @Override
    public int getItemCount() {
        return candidateList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPosition, txtVotes;

        ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPosition = itemView.findViewById(R.id.txtPosition);
            txtVotes = itemView.findViewById(R.id.txtVotes);
        }
    }
}
