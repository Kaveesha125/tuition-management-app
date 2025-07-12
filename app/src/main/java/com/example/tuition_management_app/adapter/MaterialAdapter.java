package com.example.tuition_management_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.model.Material;

import java.util.List;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

    public interface OnItemActionListener {
        void onUpdate(Material material);
        void onDelete(Material material);
    }

    private final List<Material> materials;
    private final OnItemActionListener listener;

    public MaterialAdapter(List<Material> materials, OnItemActionListener listener) {
        this.materials = materials;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_material, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        Material material = materials.get(position);
        holder.titleTextView.setText(material.getTitle());
        holder.urlTextView.setText(material.getFileUrl());

        holder.editBtn.setOnClickListener(v -> listener.onUpdate(material));
        holder.deleteBtn.setOnClickListener(v -> listener.onDelete(material));
    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

    static class MaterialViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, urlTextView;
        ImageButton editBtn, deleteBtn;

        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.materialTitleTextView);
            urlTextView = itemView.findViewById(R.id.materialUrlTextView);
            editBtn = itemView.findViewById(R.id.editMaterialButton);
            deleteBtn = itemView.findViewById(R.id.deleteMaterialButton);
        }
    }
}
