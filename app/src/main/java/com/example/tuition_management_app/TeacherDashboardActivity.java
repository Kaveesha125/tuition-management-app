package com.example.tuitionapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tuitionapp.R;

public class TeacherDashboardActivity extends AppCompatActivity {

    Button btnAttendance, btnAssignment, btnMaterial, btnResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        btnAttendance = findViewById(R.id.btnAttendance);
        btnAssignment = findViewById(R.id.btnAssignment);
        btnMaterial = findViewById(R.id.btnMaterial);
        btnResult = findViewById(R.id.btnResult);

        btnAttendance.setOnClickListener(v -> startActivity(new Intent(this, TakeAttendanceActivity.class)));
        btnAssignment.setOnClickListener(v -> startActivity(new Intent(this, UploadAssignmentActivity.class)));
        btnMaterial.setOnClickListener(v -> startActivity(new Intent(this, UploadMaterialActivity.class)));
        btnResult.setOnClickListener(v -> startActivity(new Intent(this, UploadResultActivity.class)));
    }
}
