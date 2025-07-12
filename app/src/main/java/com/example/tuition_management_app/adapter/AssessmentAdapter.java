package com.example.tuition_management_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.model.Assessment;

import java.util.List;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentViewHolder> {

    public interface OnItemActionListener {
        void onUpdate(Assessment assessment);
        void onDelete(Assessment assessment);
    }

    private List<Assessment> assessments;
    private OnItemActionListener listener;

    public AssessmentAdapter(List<Assessment> assessments, OnItemActionListener listener) {
        this.assessments = assessments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assessment, parent, false);
        return new AssessmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentViewHolder holder, int position) {
        Assessment assessment = assessments.get(position);
        holder.titleTextView.setText(assessment.getTitle());
        holder.descTextView.setText(assessment.getDescription());

        holder.updateButton.setOnClickListener(v -> {
            if (listener != null) listener.onUpdate(assessment);
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(assessment);
        });
    }

    @Override
    public int getItemCount() {
        return assessments.size();
    }

    public static class AssessmentViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descTextView;
        ImageButton updateButton, deleteButton;

        public AssessmentViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.assessmentTitleTextView);
            descTextView = itemView.findViewById(R.id.assessmentDescriptionTextView);
            updateButton = itemView.findViewById(R.id.updateAssessmentButton);
            deleteButton = itemView.findViewById(R.id.deleteAssessmentButton);
        }
    }
}
