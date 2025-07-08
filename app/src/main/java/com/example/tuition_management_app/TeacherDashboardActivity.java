package com.example.tuitionapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tuitionapp.R;

public class TeacherDashboardActivity extends AppCompatActivity {
    Button btnAttendance, btnAssignment, btnMaterial, btnResult, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        btnAttendance = findViewById(R.id.btnAttendance);
        btnAssignment = findViewById(R.id.btnAssignment);
        btnMaterial = findViewById(R.id.btnMaterial);
        btnResult = findViewById(R.id.btnResult);
        btnLogout = findViewById(R.id.btnLogout);

        btnAttendance.setOnClickListener(v -> startActivity(new Intent(this, TakeAttendanceActivity.class)));
        btnAssignment.setOnClickListener(v -> startActivity(new Intent(this, UploadAssignmentActivity.class)));
        btnMaterial.setOnClickListener(v -> startActivity(new Intent(this, UploadMaterialActivity.class)));
        btnResult.setOnClickListener(v -> startActivity(new Intent(this, UploadResultActivity.class)));
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
