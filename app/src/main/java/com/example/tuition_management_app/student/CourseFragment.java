package com.example.tuition_management_app.student;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CourseFragment extends Fragment {

    private TextView courseNameTextView, courseDescriptionTextView;
    private SessionManager sessionManager;

    public CourseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        courseNameTextView = view.findViewById(R.id.courseNameTextView);
        courseDescriptionTextView = view.findViewById(R.id.courseDescriptionTextView);

        CardView assignmentsCard = view.findViewById(R.id.assignmentsCardView);
        CardView materialsCard = view.findViewById(R.id.materialsCardView);

        assignmentsCard.setOnClickListener(v -> navigateToFragment(new AssignmentsFragment()));
        materialsCard.setOnClickListener(v -> navigateToFragment(new MaterialsFragment()));

        fetchStudentCourse();
    }

    private void navigateToFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void fetchStudentCourse() {
        long userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Step 1: Fetch the student's record from the 'students' table to get the course_id
        Map<String, String> studentFilters = new HashMap<>();
        studentFilters.put("user_id", "eq." + userId);
        studentFilters.put("select", "course_id");

        SupabaseClient.select("students", studentFilters, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error fetching student profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
                            if (studentObject.has("course_id") && !studentObject.isNull("course_id")) {
                                long courseId = studentObject.getLong("course_id");
                                // Step 2: Use course_id to fetch course details from the 'courses' table
                                fetchCourseDetails(courseId);
                            } else {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        courseNameTextView.setText("Course not assigned");
                                        courseDescriptionTextView.setText("Please contact administration.");
                                    });
                                }
                            }
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> courseNameTextView.setText("Student profile not found"));
                            }
                        }
                    } catch (JSONException e) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error parsing student data", Toast.LENGTH_SHORT).show());
                        }
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to fetch student profile", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    private void fetchCourseDetails(long courseId) {
        Map<String, String> courseFilters = new HashMap<>();
        courseFilters.put("course_id", "eq." + courseId);
        courseFilters.put("select", "course_name,description");

        SupabaseClient.select("courses", courseFilters, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error fetching course details: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    final String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        if (jsonArray.length() > 0) {
                            JSONObject courseObject = jsonArray.getJSONObject(0);
                            String courseName = courseObject.getString("course_name");
                            String courseDescription = courseObject.getString("description");
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    courseNameTextView.setText(courseName);
                                    courseDescriptionTextView.setText(courseDescription);
                                });
                            }
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> courseNameTextView.setText("Course not found"));
                            }
                        }
                    } catch (JSONException e) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error parsing course data", Toast.LENGTH_SHORT).show());
                        }
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to fetch course details", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }
}