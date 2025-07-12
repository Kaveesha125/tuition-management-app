package com.example.tuition_management_app.teachers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.adapter.CourseAdapter;
import com.example.tuition_management_app.model.Course;
import com.example.tuition_management_app.utils.SessionManager;
import com.example.tuition_management_app.SupabaseClient;

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

public class FragmentTeacherCourse extends Fragment {

    private RecyclerView recyclerView;
    private List<Course> courseList;
    private CourseAdapter adapter;
    private static final String TAG = "FragmentTeacherCourse";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        courseList = new ArrayList<>();
        adapter = new CourseAdapter(courseList, course -> {
            FragmentTeacherCourseDetail detailFragment = new FragmentTeacherCourseDetail();
            Bundle args = new Bundle();
            args.putLong("course_id", course.getId());
            detailFragment.setArguments(args);

            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.teacher_fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchTeacherIdAndCourses();
    }

    // Step 1: get teacher_id using user_id
    private void fetchTeacherIdAndCourses() {
        SessionManager session = new SessionManager(requireContext());
        long userId = session.getUserId();

        if (userId == -1) {
            Log.e(TAG, "User not logged in.");
            return;
        }

        Map<String, String> teacherFilter = new HashMap<>();
        teacherFilter.put("user_id", "eq." + userId);

        SupabaseClient.select("teachers", teacherFilter, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Failed to fetch teacher_id", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    try {
                        JSONArray array = new JSONArray(responseBody);
                        if (array.length() > 0) {
                            JSONObject teacherObj = array.getJSONObject(0);
                            long teacherId = teacherObj.getLong("teacher_id");
                            fetchCoursesFromTeacherCourses(teacherId);
                        } else {
                            Log.e(TAG, "No teacher found for this user");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing teacher_id", e);
                    }
                } else {
                    Log.e(TAG, "Teacher fetch failed: " + responseBody);
                }
            }
        });
    }

    // Step 2: get courses using teacher_id via teacher_courses table
    private void fetchCoursesFromTeacherCourses(long teacherId) {
        Map<String, String> filters = new HashMap<>();
        filters.put("teacher_id", "eq." + teacherId);
        filters.put("select", "course_id,courses(*)");

        SupabaseClient.select("teacher_courses", filters, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Failed to fetch courses from teacher_courses", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    try {
                        JSONArray array = new JSONArray(responseBody);
                        List<Course> newCourses = new ArrayList<>();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            JSONObject courseObj = obj.getJSONObject("courses");

                            int courseId = courseObj.optInt("course_id");
                            String courseName = courseObj.optString("course_name");
                            String description = courseObj.optString("description");

                            Course course = new Course(courseId, courseName, description);
                            newCourses.add(course);
                        }

                        requireActivity().runOnUiThread(() -> {
                            courseList.clear();
                            courseList.addAll(newCourses);
                            adapter.notifyDataSetChanged();
                        });
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing courses", e);
                    }
                } else {
                    Log.e(TAG, "Failed course fetch: " + responseBody);
                }
            }
        });
    }
}
