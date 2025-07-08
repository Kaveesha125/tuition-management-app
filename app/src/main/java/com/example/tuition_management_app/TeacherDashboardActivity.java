package com.example.tuition_management_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        Button btnAttendance = findViewById(R.id.btnAttendance);
        Button btnAssignment = findViewById(R.id.btnAssignment);
        Button btnMaterial = findViewById(R.id.btnMaterial);
        Button btnResult = findViewById(R.id.btnResult);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnAttendance.setOnClickListener(v ->
                startActivity(new Intent(TeacherDashboardActivity.this, AttendanceActivity.class))
        );
        btnAssignment.setOnClickListener(v ->
                startActivity(new Intent(TeacherDashboardActivity.this, UploadAssignmentActivity.class))
        );
        btnMaterial.setOnClickListener(v ->
                startActivity(new Intent(TeacherDashboardActivity.this, UploadMaterialActivity.class))
        );
        btnResult.setOnClickListener(v ->
                startActivity(new Intent(TeacherDashboardActivity.this, UploadResultActivity.class))
        );
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}