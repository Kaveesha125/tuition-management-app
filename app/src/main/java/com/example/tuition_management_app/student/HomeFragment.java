package com.example.tuition_management_app.student;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tuition_management_app.R;
import com.example.tuition_management_app.SupabaseClient;
import com.example.tuition_management_app.utils.SessionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private TextView welcomeTextView, dateTextView, resultTextView, assignmentsTextView;
    private Button submitAssignmentButton;
    private SessionManager sessionManager;
    private long studentCourseId = -1;
    private long studentRowId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        welcomeTextView = view.findViewById(R.id.welcomeTextView);
        dateTextView = view.findViewById(R.id.dateTextView);
        resultTextView = view.findViewById(R.id.resultTextView);
        assignmentsTextView = view.findViewById(R.id.assignmentsTextView);
        submitAssignmentButton = view.findViewById(R.id.submitAssignmentButton);

        // Set welcome message and date
        welcomeTextView.setText("Welcome " + sessionManager.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        dateTextView.setText(currentDate);

        submitAssignmentButton.setOnClickListener(v -> showSubmitAssignmentDialog());

        fetchStudentData();
    }

    private void fetchStudentData() {
        long userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> filters = new HashMap<>();
        filters.put("user_id", "eq." + userId);
        filters.put("select", "student_id,results,course_id");

        SupabaseClient.select("students", filters, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error fetching student data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    final String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        if (jsonArray.length() > 0) {
                            JSONObject studentObject = jsonArray.getJSONObject(0);
                            studentRowId = studentObject.getLong("student_id");
                            double results = studentObject.optDouble("results", 0.0);

                            if (studentObject.has("course_id") && !studentObject.isNull("course_id")) {
                                studentCourseId = studentObject.getLong("course_id");
                                fetchCourseName(studentCourseId, results);
                                fetchAssignmentsCount(studentCourseId);
                            } else {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        resultTextView.setText(String.format(Locale.US, "%.2f for Unassigned Course", results));
                                        assignmentsTextView.setText("Total: 0");
                                    });
                                }
                            }
                        }
                    } catch (JSONException e) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error parsing student data.", Toast.LENGTH_SHORT).show());
                        }
                    }
                }
            }
        });
    }

    private void showSubmitAssignmentDialog() {
        if (studentCourseId == -1) {
            Toast.makeText(getContext(), "You are not enrolled in any course.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> filters = new HashMap<>();
        filters.put("course_id", "eq." + studentCourseId);
        filters.put("select", "id,title");

        SupabaseClient.select("assignments", filters, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to fetch assignments.", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        final List<Assignment> assignments = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            assignments.add(new Assignment(obj.getLong("id"), obj.getString("title")));
                        }

                        getActivity().runOnUiThread(() -> {
                            if (assignments.isEmpty()) {
                                Toast.makeText(getContext(), "No assignments available for your course.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            displayAssignmentDialog(assignments);
                        });

                    } catch (JSONException e) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error parsing assignments.", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    private void displayAssignmentDialog(List<Assignment> assignments) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_submit_assignment, null);
        builder.setView(dialogView);

        final Spinner spinner = dialogView.findViewById(R.id.spinnerAssignments);
        final EditText urlEditText = dialogView.findViewById(R.id.editTextSubmissionUrl);

        ArrayAdapter<Assignment> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, assignments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        builder.setTitle("Upload Submission")
                .setPositiveButton("Submit", (dialog, id) -> {
                    Assignment selectedAssignment = (Assignment) spinner.getSelectedItem();
                    String submissionUrl = urlEditText.getText().toString().trim();
                    if (selectedAssignment != null && !TextUtils.isEmpty(submissionUrl)) {
                        submitAssignment(selectedAssignment.getId(), submissionUrl);
                    } else {
                        Toast.makeText(getContext(), "Please select an assignment and enter a URL.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void submitAssignment(long assignmentId, String submissionUrl) {
        if (studentRowId == -1) {
            Toast.makeText(getContext(), "Could not identify student.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject submissionJson = new JSONObject();
            submissionJson.put("assignment_id", assignmentId);
            submissionJson.put("student_id", studentRowId);
            submissionJson.put("submission_url", submissionUrl);
            submissionJson.put("submitted_at", new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()));

            SupabaseClient.insert("submissions", submissionJson.toString(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Submission failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (getActivity() == null) return;
                    if (response.isSuccessful()) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Submission successful!", Toast.LENGTH_SHORT).show());
                    } else {
                        final String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Submission failed: " + errorBody, Toast.LENGTH_LONG).show());
                    }
                }
            });

        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error creating submission data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAssignmentsCount(long courseId) {
        Map<String, String> filters = new HashMap<>();
        filters.put("course_id", "eq." + courseId);
        filters.put("select", "id"); // We only need a column to count the rows

        SupabaseClient.select("assignments", filters, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error fetching assignments count: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    final String responseBody = response.body().string();
                    getActivity().runOnUiThread(() -> {
                        try {
                            JSONArray jsonArray = new JSONArray(responseBody);
                            int assignmentCount = jsonArray.length();
                            assignmentsTextView.setText(String.format(Locale.US, "Total: %d", assignmentCount));
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Error parsing assignments count.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void fetchCourseName(long courseId, double results) {
        Map<String, String> filters = new HashMap<>();
        filters.put("course_id", "eq." + courseId);
        filters.put("select", "course_name");

        SupabaseClient.select("courses", filters, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error fetching course name: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    final String responseBody = response.body().string();
                    getActivity().runOnUiThread(() -> {
                        try {
                            JSONArray jsonArray = new JSONArray(responseBody);
                            String courseName = "Unknown Course";
                            if (jsonArray.length() > 0) {
                                JSONObject courseObject = jsonArray.getJSONObject(0);
                                courseName = courseObject.getString("course_name");
                            }
                            resultTextView.setText(String.format(Locale.US, "%.2f for \"%s\"", results, courseName));
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Error parsing course data.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    // Inner class to hold assignment data
    private static class Assignment {
        private final long id;
        private final String title;

        public Assignment(long id, String title) {
            this.id = id;
            this.title = title;
        }

        public long getId() {
            return id;
        }

        @NonNull
        @Override
        public String toString() {
            return title; // This is what will be displayed in the Spinner
        }
    }
}