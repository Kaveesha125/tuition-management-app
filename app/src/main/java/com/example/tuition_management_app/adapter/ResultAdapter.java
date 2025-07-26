package com.example.tuition_management_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.model.Result;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    public interface OnActionListener {
        void onAddOrUpdate(Result result, long studentId);
    }

    private final List<Result> results;
    private final OnActionListener listener;

    public ResultAdapter(List<Result> results, OnActionListener listener) {
        this.results = results;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new ResultViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        Result r = results.get(position);
        holder.studentName.setText("Name: " + r.getStudentName());
        holder.result.setText("Result: " + r.getResult());
        holder.btnAdd.setOnClickListener(v -> listener.onAddOrUpdate(r, r.getStudentId()));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, result;
        ImageButton btnAdd;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.textStudentName);
            result = itemView.findViewById(R.id.textResult);
            btnAdd = itemView.findViewById(R.id.btnAddUpdate);
        }
    }
}
