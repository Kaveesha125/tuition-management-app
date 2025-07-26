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
import com.example.tuition_management_app.adapter.StudentAdapter;
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

public class ManageStudentsActivity extends AppCompatActivity {

    RecyclerView recyclerStudents;
    StudentAdapter adapter;
    EditText etUserIdToDelete;
    Button btnDelete;
    List<User> students = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        recyclerStudents = findViewById(R.id.recyclerStudents);
        etUserIdToDelete = findViewById(R.id.etEmailToDelete);
        btnDelete = findViewById(R.id.btnDeleteStudent);

        adapter = new StudentAdapter(students);
        recyclerStudents.setLayoutManager(new LinearLayoutManager(this));
        recyclerStudents.setAdapter(adapter);

        loadStudents();

        btnDelete.setOnClickListener(v -> {
            String inputId = etUserIdToDelete.getText().toString().trim();
            if (!inputId.isEmpty()) {
                deleteUserById(inputId);
            } else {
                Toast.makeText(this, "User ID field is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStudents() {
        SupabaseClient.select("user",
                Map.of("role", "eq.Student", "is_verified", "eq.true"),
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> Toast.makeText(ManageStudentsActivity.this, "Error loading students", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        runOnUiThread(() -> {
                            try {
                                JSONArray jsonArray = new JSONArray(json);
                                students.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    User u = new User(
                                            obj.getString("name"),
                                            obj.getString("email"),
                                            "",
                                            obj.getString("role"),
                                            obj.getBoolean("is_verified")
                                    );
                                    u.id = String.valueOf(obj.getLong("id"));
                                    students.add(u);
                                }
                                adapter.notifyDataSetChanged();

                                if (students.isEmpty()) {
                                    Toast.makeText(ManageStudentsActivity.this, "No students found", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                Toast.makeText(ManageStudentsActivity.this, "Parse error", Toast.LENGTH_SHORT).show();
                                Log.e("LOAD_ERR", e.toString());
                            }
                        });
                    }
                });
    }

    private void deleteUserById(String userId) {
        SupabaseClient.delete("user", "id=eq." + userId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ManageStudentsActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(ManageStudentsActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                        loadStudents();
                    } else {
                        Toast.makeText(ManageStudentsActivity.this, "Delete failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
