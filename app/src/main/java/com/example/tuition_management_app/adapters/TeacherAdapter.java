package com.example.tuition_management_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tuition_management_app.R;
import com.example.tuition_management_app.models.User;
import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder> {

    private List<User> teachers;

    public TeacherAdapter(List<User> teachers) {
        this.teachers = teachers;
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
    public TeacherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_item, parent, false);
        return new TeacherAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TeacherAdapter.ViewHolder holder, int position) {
        User teacher = teachers.get(position);
        holder.tvName.setText(teacher.getName());
        holder.tvEmail.setText(teacher.getEmail());
        holder.tvUserId.setText("ID: " + teacher.id);
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }
}