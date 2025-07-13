package com.example.tuition_management_app.activities;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tuition_management_app.R;
import com.example.tuition_management_app.SupabaseClient;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AssignCourseActivity extends AppCompatActivity {

    Spinner roleSpinner;
    EditText etUserId, etCourseId;
    Button btnAssignCourse;

    String selectedRole = "Student"; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_course);

        roleSpinner = findViewById(R.id.roleSpinner);
        etUserId = findViewById(R.id.etUserId);
        etCourseId = findViewById(R.id.etCourseId);
        btnAssignCourse = findViewById(R.id.btnAssignCourse);

        setupRoleSpinner();

        btnAssignCourse.setOnClickListener(v -> {
            String userId = etUserId.getText().toString().trim();
            String courseId = etCourseId.getText().toString().trim();

            if (userId.isEmpty() || courseId.isEmpty() || selectedRole.equals("Select a role")) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Long.parseLong(userId);
                Long.parseLong(courseId);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "User ID and Course ID must be numeric", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedRole.equals("Student")) {
                assignCourseToStudent(userId, courseId);
            } else if (selectedRole.equals("Teacher")) {
                assignCourseToTeacher(userId, courseId);
            }
        });
    }

    private void setupRoleSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRole = "Select a role";
            }
        });
    }

    private void assignCourseToStudent(String userId, String courseId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("course_id", Long.parseLong(courseId));

        String filter = "user_id=eq." + userId;
        String jsonBody = new JSONObject(updates).toString();

        SupabaseClient.update("students", filter, jsonBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(AssignCourseActivity.this, "Error assigning course to student: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(AssignCourseActivity.this, "Course assigned to student!", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String err = response.body() != null ? response.body().string() : "Unknown error";
                            Toast.makeText(AssignCourseActivity.this, "Failed to assign course to student: " + err, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(AssignCourseActivity.this, "Failed to assign course to student and error reading response", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void assignCourseToTeacher(String userId, String courseId) {
        // Step 1: Get teacher_id from teachers table by user_id
        Map<String, String> filters = new HashMap<>();
        filters.put("user_id", "eq." + userId);
        filters.put("select", "teacher_id");

        SupabaseClient.select("teachers", filters, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(AssignCourseActivity.this, "Failed to fetch teacher: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        try {
                            String err = response.body() != null ? response.body().string() : "Unknown error";
                            Toast.makeText(AssignCourseActivity.this, "Failed to fetch teacher: " + err, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(AssignCourseActivity.this, "Error reading response when fetching teacher", Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }

                String responseBody = response.body().string();
                try {
                    org.json.JSONArray jsonArray = new org.json.JSONArray(responseBody);
                    if (jsonArray.length() > 0) {
                        long teacherId = jsonArray.getJSONObject(0).getLong("teacher_id");
                        insertTeacherCourse(teacherId, Long.parseLong(courseId));
                    } else {
                        // Teacher does not exist, you may want to handle inserting teacher here or show error
                        runOnUiThread(() ->
                                Toast.makeText(AssignCourseActivity.this, "Teacher not found for given user ID", Toast.LENGTH_LONG).show()
                        );
                    }
                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(AssignCourseActivity.this, "Failed to parse teacher response: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
                }
            }
        });
    }

    private void insertTeacherCourse(long teacherId, long courseId) {
        JSONObject body = new JSONObject();
        try {
            body.put("teacher_id", teacherId);
            body.put("course_id", courseId);
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(this, "Failed to prepare teacher_courses data", Toast.LENGTH_SHORT).show());
            return;
        }

        SupabaseClient.insert("teacher_courses", body.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(AssignCourseActivity.this, "Failed to insert teacher_courses: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(AssignCourseActivity.this, "Teacher assigned to course successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String err = response.body() != null ? response.body().string() : "Unknown error";
                            Toast.makeText(AssignCourseActivity.this, "Failed to assign teacher to course: " + err, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(AssignCourseActivity.this, "Error reading response from insert teacher_courses", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
