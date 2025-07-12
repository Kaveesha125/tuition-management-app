package com.example.tuition_management_app.student;

import android.os.Bundle;
import androidx.annotation.NonNull;
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

public class ProfileFragment extends Fragment {

    private SessionManager sessionManager;
    private TextView courseNameTextView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(requireContext());

        TextView nameTextView = view.findViewById(R.id.textView5);
        TextView emailTextView = view.findViewById(R.id.textView6);
        TextView idTextView = view.findViewById(R.id.textView8);
        courseNameTextView = view.findViewById(R.id.textView9);
        TextView qrTextView = view.findViewById(R.id.textView13);
        TextView logoutTextView = view.findViewById(R.id.textView12);

        // Set user data from SessionManager
        nameTextView.setText(sessionManager.getName());
        emailTextView.setText(sessionManager.getEmail());
        idTextView.setText(String.valueOf(sessionManager.getUserId()));

        qrTextView.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new QrFragment())
                    .addToBackStack(null)
                    .commit();
        });

        logoutTextView.setOnClickListener(v -> {
            SessionManager.logout(requireActivity());
        });

        fetchStudentCourse();

        return view;
    }

    private void fetchStudentCourse() {
        long userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

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
                                fetchCourseDetails(courseId);
                            } else {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> courseNameTextView.setText("Not Enrolled"));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error parsing student data", Toast.LENGTH_SHORT).show());
                        }
                    }
                }
            }
        });
    }

    private void fetchCourseDetails(long courseId) {
        Map<String, String> courseFilters = new HashMap<>();
        courseFilters.put("course_id", "eq." + courseId);
        courseFilters.put("select", "course_name");

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
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> courseNameTextView.setText(courseName));
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
                }
            }
        });
    }
}