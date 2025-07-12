package com.example.tuition_management_app.teachers;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.SupabaseClient;
import com.example.tuition_management_app.adapter.MaterialAdapter;
import com.example.tuition_management_app.model.Material;
import com.example.tuition_management_app.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentTeacherCourseMaterials extends Fragment {

    private RecyclerView materialsRecyclerView;
    private MaterialAdapter adapter;
    private List<Material> materialList = new ArrayList<>();

    private long courseId = -1;
    private long teacherId = -1;

    private static final String TAG = "FragmentTeacherMaterials";

    public FragmentTeacherCourseMaterials() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_course_materials, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        materialsRecyclerView = view.findViewById(R.id.materialsRecyclerView);
        materialsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MaterialAdapter(materialList, new MaterialAdapter.OnItemActionListener() {
            @Override
            public void onUpdate(Material material) {
                showEditDialog(material);
            }

            @Override
            public void onDelete(Material material) {
                deleteMaterial(material);
            }
        });

        materialsRecyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            courseId = getArguments().getLong("course_id", -1);
        }

        view.findViewById(R.id.addMaterialFab).setOnClickListener(v -> {
            if (teacherId == -1) {
                Toast.makeText(getContext(), "Teacher not loaded yet", Toast.LENGTH_SHORT).show();
            } else {
                showAddDialog();
            }
        });

        fetchTeacherIdThenLoadMaterials();
    }

    private void fetchTeacherIdThenLoadMaterials() {
        SessionManager session = new SessionManager(requireContext());
        long userId = session.getUserId();

        SupabaseClient.select("teachers", Map.of("user_id", "eq." + userId), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Error fetching teacher id", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                try {
                    JSONArray array = new JSONArray(body);
                    if (array.length() > 0) {
                        teacherId = array.getJSONObject(0).getLong("teacher_id");
                        requireActivity().runOnUiThread(() -> fetchMaterials());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing teacher id response", e);
                }
            }
        });
    }

    private void fetchMaterials() {
        SupabaseClient.select("materials", Map.of("course_id", "eq." + courseId), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Error fetching materials", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                try {
                    JSONArray array = new JSONArray(body);
                    materialList.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        materialList.add(new Material(
                                obj.getLong("material_id"),  // 🔧 corrected
                                obj.getLong("course_id"),
                                obj.getLong("teacher_id"),
                                obj.getString("title"),
                                obj.getString("file_url"),
                                obj.optString("uploaded_at", "")
                        ));
                    }
                    requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing materials", e);
                }
            }
        });
    }

    private void showAddDialog() {
        showMaterialDialog(null);
    }

    private void showEditDialog(Material existing) {
        showMaterialDialog(existing);
    }

    private void showMaterialDialog(@Nullable Material existing) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_material, null);
        EditText titleInput = dialogView.findViewById(R.id.editMaterialTitle);
        EditText fileUrlInput = dialogView.findViewById(R.id.editMaterialFileUrl);

        if (existing != null) {
            titleInput.setText(existing.getTitle());
            fileUrlInput.setText(existing.getFileUrl());
        }

        new AlertDialog.Builder(getContext())
                .setTitle(existing == null ? "Add Material" : "Edit Material")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("title", titleInput.getText().toString());
                        json.put("file_url", fileUrlInput.getText().toString());
                        json.put("course_id", courseId);
                        json.put("teacher_id", teacherId);

                        if (existing == null) {
                            SupabaseClient.insert("materials", json.toString(), new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Log.e(TAG, "Insert material error", e);
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    requireActivity().runOnUiThread(() -> fetchMaterials());
                                }
                            });
                        } else {
                            String filterQuery = "material_id=eq." + existing.getId();
                            SupabaseClient.update("materials", filterQuery, json.toString(), new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Log.e(TAG, "Update material error", e);
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    requireActivity().runOnUiThread(() -> fetchMaterials());
                                }
                            });
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Error saving material JSON", e);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteMaterial(Material material) {
        String filterQuery = "material_id=eq." + material.getId();
        SupabaseClient.delete("materials", filterQuery, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Delete material error", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Material deleted", Toast.LENGTH_SHORT).show();
                    fetchMaterials();
                });
            }
        });
    }
}
