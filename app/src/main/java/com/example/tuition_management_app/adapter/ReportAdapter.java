package com.example.tuition_management_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.model.User;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private List<User> users;

    public ReportAdapter(List<User> users) {
        this.users = users;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserId, tvName, tvEmail;

        public ViewHolder(View view) {
            super(view);
            tvUserId = view.findViewById(R.id.tvUserId);
            tvName = view.findViewById(R.id.tvName);
            tvEmail = view.findViewById(R.id.tvEmail);
        }
    }

    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_item, parent, false);
        return new ReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReportAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvUserId.setText("ID: " + user.id);
        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}