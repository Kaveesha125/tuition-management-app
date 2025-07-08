package com.example.tuition_management_app;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AttendanceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        Button btnSubmit = findViewById(R.id.btnSubmitAttendance);
        btnSubmit.setOnClickListener(v -> {
            // Handle attendance submission logic
            finish();
        });
    }
}