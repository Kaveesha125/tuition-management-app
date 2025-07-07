package com.example.teacherdashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnAssignments, btnAttendance, btnSubmissions, btnResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAssignments = findViewById(R.id.btnAssignments);
        btnAttendance = findViewById(R.id.btnAttendance);
        btnSubmissions = findViewById(R.id.btnSubmissions);
        btnResults = findViewById(R.id.btnResults);

        btnAssignments.setOnClickListener(v -> startActivity(new Intent(this, AssignmentsActivity.class)));
        btnAttendance.setOnClickListener(v -> startActivity(new Intent(this, AttendanceActivity.class)));
        btnSubmissions.setOnClickListener(v -> startActivity(new Intent(this, SubmissionsActivity.class)));
        btnResults.setOnClickListener(v -> startActivity(new Intent(this, ResultsActivity.class)));
    }
}
