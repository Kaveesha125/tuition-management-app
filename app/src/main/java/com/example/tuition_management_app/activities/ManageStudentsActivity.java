package com.example.tuition_management_app.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuition_management_app.adapters.StudentAdapter;
import com.example.tuition_management_app.models.User;
import com.example.tuition_management_app.network.SupabaseClient;
import com.example.tuition_management_app.network.UserService;
import com.example.tuition_management_app.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageStudentsActivity extends AppCompatActivity {

    RecyclerView recyclerStudents;
    StudentAdapter adapter;
    EditText etUserIdToDelete;
    Button btnDelete;
    List<User> students = new ArrayList<>();
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        recyclerStudents = findViewById(R.id.recyclerStudents);
        etUserIdToDelete = findViewById(R.id.etEmailToDelete);
        btnDelete = findViewById(R.id.btnDeleteStudent);

        userService = SupabaseClient.getClient().create(UserService.class);
        adapter = new StudentAdapter(students);
        recyclerStudents.setLayoutManager(new LinearLayoutManager(this));
        recyclerStudents.setAdapter(adapter);

        loadStudents();

        btnDelete.setOnClickListener(v -> {
            String inputId = etUserIdToDelete.getText().toString().trim();
            if (!inputId.isEmpty()) {
                try {
                    Long userId = Long.parseLong(inputId);
                    deleteUserById(userId);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid numeric user ID", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User ID field is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStudents() {
        userService.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    students.clear();
                    for (User user : response.body()) {
                        if ("Student".equals(user.role) && user.is_verified) {
                            students.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (students.isEmpty()) {
                        Toast.makeText(ManageStudentsActivity.this, "No students found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ManageStudentsActivity.this, "No data received", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(ManageStudentsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUserById(Long userId) {
        userService.deleteUserById(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageStudentsActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                    loadStudents();
                } else {
                    Toast.makeText(ManageStudentsActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ManageStudentsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}