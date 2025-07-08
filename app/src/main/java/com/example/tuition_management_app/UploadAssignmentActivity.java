package com.example.tuition_management_app;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class UploadAssignmentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_assignment);

        Button btnSubmit = findViewById(R.id.btnSubmitAssignment);
        btnSubmit.setOnClickListener(v -> {
            // Handle assignment upload logic
            finish();
        });
    }
}