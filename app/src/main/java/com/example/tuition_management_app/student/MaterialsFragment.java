package com.example.tuition_management_app.student;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tuition_management_app.R;
import com.example.tuition_management_app.SupabaseClient;
import com.example.tuition_management_app.utils.SessionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MaterialsFragment extends Fragment {

    private RecyclerView materialsRecyclerView;
    private MaterialAdapter adapter;
    private List<Material> materialList;
    private SessionManager sessionManager;
    private TextView textViewNoMaterials;
    private ProgressBar progressBar;

    static class Material {
        private final String title;
        private final String fileUrl;

        public Material(String title, String fileUrl) {
            this.title = title;
            this.fileUrl = fileUrl;
        }

        public String getTitle() { return title; }
        public String getFileUrl() { return fileUrl; }
    }

    public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

        private final List<Material> materialList;

        public MaterialAdapter(List<Material> materialList) {
            this.materialList = materialList;
        }

        @NonNull
        @Override
        public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_material, parent, false);
            return new MaterialViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
            Material material = materialList.get(position);
            holder.name.setText(material.getTitle());
            holder.description.setText(material.getFileUrl());
        }

        @Override
        public int getItemCount() {
            return materialList.size();
        }

        class MaterialViewHolder extends RecyclerView.ViewHolder {
            TextView name, description;

            public MaterialViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.textViewMaterialName);
                description = itemView.findViewById(R.id.textViewMaterialDescription);
            }
        }
    }

    public MaterialsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_materials, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        materialsRecyclerView = view.findViewById(R.id.materialsRecyclerView);
        textViewNoMaterials = view.findViewById(R.id.textViewNoMaterials);
        progressBar = view.findViewById(R.id.progressBar);

        materialsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        materialList = new ArrayList<>();
        adapter = new MaterialAdapter(materialList);
        materialsRecyclerView.setAdapter(adapter);

        fetchStudentCourseId();
    }

    private void fetchStudentCourseId() {
        progressBar.setVisibility(View.VISIBLE);
        long userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        Map<String, String> studentFilters = new HashMap<>();
        studentFilters.put("user_id", "eq." + userId);
        studentFilters.put("select", "course_id");

        SupabaseClient.select("students", studentFilters, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() == null) return;

                try (ResponseBody responseBody = response.body()) {
                    if (responseBody == null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Failed to fetch student profile: Empty response", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        });
                        return;
                    }

                    final String responseBodyString = responseBody.string();
                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            try {
                                JSONArray jsonArray = new JSONArray(responseBodyString);
                                if (jsonArray.length() > 0) {
                                    JSONObject studentObject = jsonArray.getJSONObject(0);
                                    if (studentObject.has("course_id") && !studentObject.isNull("course_id")) {
                                        long courseId = studentObject.getLong("course_id");
                                        fetchMaterials(courseId);
                                    } else {
                                        textViewNoMaterials.setText("Course not assigned.");
                                        textViewNoMaterials.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                } else {
                                    textViewNoMaterials.setText("Student profile not found.");
                                    textViewNoMaterials.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getContext(), "Error parsing student data", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch student profile: " + responseBodyString, Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void fetchMaterials(long courseId) {
        Map<String, String> filters = new HashMap<>();
        filters.put("course_id", "eq." + courseId);
        filters.put("select", "title,file_url");

        SupabaseClient.select("materials", filters, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error fetching materials: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() == null) return;
                try (ResponseBody responseBody = response.body()) {
                    if (responseBody == null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Failed to fetch materials: Empty response", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        });
                        return;
                    }
                    final String responseBodyString = responseBody.string();
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            try {
                                JSONArray jsonArray = new JSONArray(responseBodyString);
                                if (jsonArray.length() == 0) {
                                    textViewNoMaterials.setVisibility(View.VISIBLE);
                                } else {
                                    materialList.clear();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject obj = jsonArray.getJSONObject(i);
                                        String title = obj.getString("title");
                                        String fileUrl = obj.getString("file_url");
                                        materialList.add(new Material(title, fileUrl));
                                    }
                                    adapter.notifyDataSetChanged();
                                    textViewNoMaterials.setVisibility(View.GONE);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getContext(), "Error parsing materials", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch materials: " + responseBodyString, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}