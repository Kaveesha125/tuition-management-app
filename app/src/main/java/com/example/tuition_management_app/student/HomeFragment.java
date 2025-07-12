package com.example.tuition_management_app.student;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private TextView welcomeTextView, dateTextView, resultTextView, assignmentsTextView;
    private SessionManager sessionManager;

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

        // Set welcome message and date
        welcomeTextView.setText("Welcome " + sessionManager.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        dateTextView.setText(currentDate);

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
        filters.put("select", "results,course_id");

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
                            double results = studentObject.optDouble("results", 0.0);

                            if (studentObject.has("course_id") && !studentObject.isNull("course_id")) {
                                long courseId = studentObject.getLong("course_id");
                                fetchCourseName(courseId, results);
                                fetchAssignmentsCount(courseId);
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
}