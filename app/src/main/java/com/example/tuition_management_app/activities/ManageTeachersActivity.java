package com.example.tuition_management_app.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tuition_management_app.R;
import com.example.tuition_management_app.adapters.TeacherAdapter;
import com.example.tuition_management_app.models.User;
import com.example.tuition_management_app.network.SupabaseClient;
import com.example.tuition_management_app.network.UserService;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageTeachersActivity extends AppCompatActivity {

    RecyclerView recyclerTeachers;
    TeacherAdapter adapter;
    EditText etUserIdToDelete;
    Button btnDelete;
    List<User> teachers = new ArrayList<>();
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_teachers);

        recyclerTeachers = findViewById(R.id.recyclerTeachers);
        etUserIdToDelete = findViewById(R.id.etUserIdToDelete);
        btnDelete = findViewById(R.id.btnDeleteTeacher);

        userService = SupabaseClient.getClient().create(UserService.class);
        adapter = new TeacherAdapter(teachers);
        recyclerTeachers.setLayoutManager(new LinearLayoutManager(this));
        recyclerTeachers.setAdapter(adapter);

        loadTeachers();

        btnDelete.setOnClickListener(v -> {
            String inputId = etUserIdToDelete.getText().toString().trim();
            if (!inputId.isEmpty()) {
                try {
                    Long userId = Long.parseLong(inputId);
                    deleteTeacherById(userId);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid numeric ID", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User ID field is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTeachers() {
        userService.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    teachers.clear();
                    for (User user : response.body()) {
                        if ("Teacher".equals(user.role) && user.is_verified) {
                            teachers.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (teachers.isEmpty()) {
                        Toast.makeText(ManageTeachersActivity.this, "No teachers found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ManageTeachersActivity.this, "No data received", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(ManageTeachersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTeacherById(Long userId) {
        userService.deleteUserById(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageTeachersActivity.this, "Teacher deleted", Toast.LENGTH_SHORT).show();
                    loadTeachers(); // refresh list
                } else {
                    Toast.makeText(ManageTeachersActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ManageTeachersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}