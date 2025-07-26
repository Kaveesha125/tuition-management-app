package com.example.tuition_management_app.teachers;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import com.example.tuition_management_app.adapter.AssessmentAdapter;
import com.example.tuition_management_app.model.Assessment;
import com.example.tuition_management_app.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentTeacherCourseAssessments extends Fragment {

    private RecyclerView assessmentsRecyclerView;
    private AssessmentAdapter adapter;
    private List<Assessment> assessmentList = new ArrayList<>();

    private long courseId = -1;
    private long teacherId = -1;

    private static final String TAG = "FragmentTeacherCourseAssessments";

    public FragmentTeacherCourseAssessments() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_course_assessments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        assessmentsRecyclerView = view.findViewById(R.id.assessmentsRecyclerView);
        assessmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AssessmentAdapter(assessmentList, new AssessmentAdapter.OnItemActionListener() {
            @Override
            public void onUpdate(Assessment assessment) {
                showEditDialog(assessment);
            }

            @Override
            public void onDelete(Assessment assessment) {
                deleteAssessment(assessment);
            }
        });

        assessmentsRecyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            courseId = getArguments().getLong("course_id", -1);
        }

        view.findViewById(R.id.addAssessmentFab).setOnClickListener(v -> {
            if (teacherId == -1) {
                Toast.makeText(getContext(), "Teacher not loaded yet", Toast.LENGTH_SHORT).show();
            } else {
                showAddDialog();
            }
        });

        fetchTeacherIdThenLoadAssessments();
    }

    private void fetchTeacherIdThenLoadAssessments() {
        SessionManager session = new SessionManager(requireContext());
        long userId = session.getUserId();

        Log.d(TAG, "Fetching teacher id for userId: " + userId);

        SupabaseClient.select("teachers", Map.of("user_id", "eq." + userId), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Error fetching teacher id", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                Log.d(TAG, "Teacher fetch response: " + body);
                try {
                    JSONArray array = new JSONArray(body);
                    if (array.length() > 0) {
                        teacherId = array.getJSONObject(0).getLong("teacher_id");
                        Log.d(TAG, "Teacher id found: " + teacherId);
                        requireActivity().runOnUiThread(() -> fetchAssessments());
                    } else {
                        Log.w(TAG, "No teacher found for userId: " + userId);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing teacher id response", e);
                }
            }
        });
    }

    private void fetchAssessments() {
        Log.d(TAG, "Fetching assessments for courseId: " + courseId);

        SupabaseClient.select("assignments", Map.of("course_id", "eq." + courseId), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Error fetching assessments", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                Log.d(TAG, "Assessments fetch response: " + body);
                try {
                    JSONArray array = new JSONArray(body);
                    assessmentList.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        assessmentList.add(new Assessment(
                                obj.getLong("id"),        // use "id" not "assignments_id"
                                obj.getLong("course_id"),
                                obj.getString("title"),
                                obj.getString("description"),
                                obj.optString("due_date", ""),
                                obj.optString("upload_url", "")
                        ));
                    }
                    requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing assessments", e);
                }
            }
        });
    }

    private void showAddDialog() {
        showAssessmentDialog(null);
    }

    private void showEditDialog(Assessment assessment) {
        showAssessmentDialog(assessment);
    }

    private void showAssessmentDialog(@Nullable Assessment existing) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_assessment, null);
        EditText titleInput = dialogView.findViewById(R.id.editAssessmentTitle);
        EditText descriptionInput = dialogView.findViewById(R.id.editAssessmentDescription);
        EditText dueDateInput = dialogView.findViewById(R.id.editAssessmentDueDate);
        EditText submitLinkInput = dialogView.findViewById(R.id.editAssessmentSubmitLink);

        if (existing != null) {
            titleInput.setText(existing.getTitle());
            descriptionInput.setText(existing.getDescription());
            dueDateInput.setText(existing.getDueDate());
            submitLinkInput.setText(existing.getUploadUrl());
        }

        dueDateInput.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(getContext(), (view, year, month, day) -> {
                dueDateInput.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        new AlertDialog.Builder(getContext())
                .setTitle(existing == null ? "Add Assessment" : "Edit Assessment")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("title", titleInput.getText().toString());
                        json.put("description", descriptionInput.getText().toString());
                        json.put("due_date", dueDateInput.getText().toString());
                        json.put("upload_url", submitLinkInput.getText().toString());
                        json.put("course_id", courseId);
                        json.put("teacher_id", teacherId);

                        if (existing == null) {
                            SupabaseClient.insert("assignments", json.toString(), new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Log.e(TAG, "Insert assessment error", e);
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    Log.d(TAG, "Assessment inserted: " + response.body().string());
                                    requireActivity().runOnUiThread(() -> fetchAssessments());
                                }
                            });
                        } else {
                            String filterQuery = "id=eq." + existing.getId();
                            SupabaseClient.update("assignments", filterQuery, json.toString(), new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Log.e(TAG, "Update assessment error", e);
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    Log.d(TAG, "Assessment updated: " + response.body().string());
                                    requireActivity().runOnUiThread(() -> fetchAssessments());
                                }
                            });
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Error saving assessment JSON", e);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAssessment(Assessment assessment) {
        String filterQuery = "id=eq." + assessment.getId();
        SupabaseClient.delete("assignments", filterQuery, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Delete assessment error", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "Assessment deleted: " + response.body().string());
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Assessment deleted", Toast.LENGTH_SHORT).show();
                    fetchAssessments();
                });
            }
        });
    }
}
