package com.example.tuition_management_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.models.User;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<User> students;

    public StudentAdapter(List<User> students) {
        this.students = students;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvUserId;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvEmail = view.findViewById(R.id.tvEmail);
            tvUserId = view.findViewById(R.id.tvUserId);
        }
    }

    @Override
    public StudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_item, parent, false);
        return new StudentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentAdapter.ViewHolder holder, int position) {
        User student = students.get(position);
        holder.tvName.setText(student.getName());
        holder.tvEmail.setText(student.getEmail());
        holder.tvUserId.setText("ID: " + student.id); // directly using public field
    }

    @Override
    public int getItemCount() {
        return students.size();
    }
}