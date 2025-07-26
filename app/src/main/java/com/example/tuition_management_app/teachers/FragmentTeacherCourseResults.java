package com.example.tuition_management_app.teachers;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.SupabaseClient;
import com.example.tuition_management_app.adapter.ResultAdapter;
import com.example.tuition_management_app.model.Result;
import com.example.tuition_management_app.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentTeacherCourseResults extends Fragment {

    private androidx.recyclerview.widget.RecyclerView rvResults;
    private ResultAdapter adapter;
    private final List<Result> resultList = new ArrayList<>();
    private long courseId, teacherId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup c, Bundle s) {
        return inflater.inflate(R.layout.fragment_teacher_course_results, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        rvResults = v.findViewById(R.id.recyclerResults);
        rvResults.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ResultAdapter(resultList, this::showDialog);
        rvResults.setAdapter(adapter);

        courseId = getArguments().getLong("course_id", -1);
        teacherId = new SessionManager(requireContext()).getUserId();

        loadStudentsWithResults();
    }

    private void loadStudentsWithResults() {
        SupabaseClient.select("students", Map.of("course_id", "eq." + courseId), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showToast("Failed to load students: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONArray studentArr = new JSONArray(response.body().string());
                    if (studentArr.length() == 0) {
                        showToast("No students found");
                        return;
                    }

                    List<Result> tempList = new ArrayList<>();
                    int total = studentArr.length();
                    int[] completed = {0};

                    for (int i = 0; i < total; i++) {
                        JSONObject studentObj = studentArr.getJSONObject(i);
                        long studentId = studentObj.getLong("student_id");
                        long userId = studentObj.getLong("user_id");
                        double resultScore = studentObj.optDouble("results", 0);

                        SupabaseClient.select("user", Map.of("id", "eq." + userId), new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                addToResultList(studentId, "Unknown", resultScore, tempList, completed, total);
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                String name = "Unknown";
                                try {
                                    JSONArray arr = new JSONArray(response.body().string());
                                    if (arr.length() > 0) {
                                        name = arr.getJSONObject(0).getString("name");
                                    }
                                } catch (Exception ignored) {}
                                addToResultList(studentId, name, resultScore, tempList, completed, total);
                            }
                        });
                    }
                } catch (JSONException e) {
                    showToast("JSON error: " + e.getMessage());
                }
            }
        });
    }

    private void addToResultList(long studentId, String name, double score,
                                 List<Result> tempList, int[] completed, int total) {
        synchronized (tempList) {
            Result r = new Result(studentId, courseId, teacherId, score, name);
            tempList.add(r);
        }

        synchronized (completed) {
            completed[0]++;
            if (completed[0] == total) {
                requireActivity().runOnUiThread(() -> {
                    resultList.clear();
                    resultList.addAll(tempList);
                    adapter.notifyDataSetChanged();
                });
            }
        }
    }

    private void showDialog(Result existing, long studentId) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_result, null);
        EditText scoreEt = dialogView.findViewById(R.id.inputScore);
        if (existing != null) {
            scoreEt.setText(String.valueOf(existing.getResult()));
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Update Result")
                .setView(dialogView)
                .setPositiveButton("Save", (d, w) -> {
                    try {
                        double score = Double.parseDouble(scoreEt.getText().toString());

                        JSONObject updateJson = new JSONObject();
                        updateJson.put("results", score);

                        SupabaseClient.update("students", "student_id=eq." + studentId, updateJson.toString(), new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                showToast("Failed to update result");
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) {
                                requireActivity().runOnUiThread(() -> {
                                    showToast("Result updated");
                                    loadStudentsWithResults();
                                });
                            }
                        });
                    } catch (Exception e) {
                        showToast("Invalid input");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showToast(String msg) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show());
    }
}
