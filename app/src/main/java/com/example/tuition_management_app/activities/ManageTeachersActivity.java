package com.example.tuition_management_app.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.SupabaseClient;
import com.example.tuition_management_app.adapter.TeacherAdapter;
import com.example.tuition_management_app.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ManageTeachersActivity extends AppCompatActivity {

    RecyclerView recyclerTeachers;
    TeacherAdapter adapter;
    EditText etUserIdToDelete;
    Button btnDelete;
    List<User> teachers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_teachers);

        recyclerTeachers = findViewById(R.id.recyclerTeachers);
        etUserIdToDelete = findViewById(R.id.etUserIdToDelete);
        btnDelete = findViewById(R.id.btnDeleteTeacher);

        adapter = new TeacherAdapter(teachers);
        recyclerTeachers.setLayoutManager(new LinearLayoutManager(this));
        recyclerTeachers.setAdapter(adapter);

        loadTeachers();

        btnDelete.setOnClickListener(v -> {
            String inputId = etUserIdToDelete.getText().toString().trim();
            if (!inputId.isEmpty()) {
                deleteTeacherById(inputId);
            } else {
                Toast.makeText(this, "User ID field is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTeachers() {
        SupabaseClient.select("user",
                Map.of("role", "eq.Teacher", "is_verified", "eq.true"),
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() ->
                                Toast.makeText(ManageTeachersActivity.this, "Failed to load teachers", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        runOnUiThread(() -> {
                            try {
                                JSONArray jsonArray = new JSONArray(json);
                                teachers.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    User u = new User(
                                            obj.getString("name"),
                                            obj.getString("email"),
                                            "", // password ignored for safety
                                            obj.getString("role"),
                                            obj.getBoolean("is_verified")
                                    );
                                    u.id = String.valueOf(obj.getLong("id"));
                                    teachers.add(u);
                                }
                                adapter.notifyDataSetChanged();

                                if (teachers.isEmpty()) {
                                    Toast.makeText(ManageTeachersActivity.this, "No teachers found", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                Toast.makeText(ManageTeachersActivity.this, "JSON parse error", Toast.LENGTH_SHORT).show();
                                Log.e("LOAD_TEACHERS_ERR", e.toString());
                            }
                        });
                    }
                });
    }

    private void deleteTeacherById(String userId) {
        SupabaseClient.delete("user", "id=eq." + userId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(ManageTeachersActivity.this, "Delete failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(ManageTeachersActivity.this, "Teacher deleted", Toast.LENGTH_SHORT).show();
                        loadTeachers(); // refresh
                    } else {
                        Toast.makeText(ManageTeachersActivity.this, "Delete error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
