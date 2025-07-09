package com.example.tuition_management_app.teachers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tuition_management_app.R;

public class UploadResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_result);

        Button btnUploadResult = findViewById(R.id.btnUploadResult);
        Button btnDone = findViewById(R.id.btnDone);

        btnUploadResult.setOnClickListener(v -> {
            // Handle result upload logic
        });

        btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(this, TeacherDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}