package com.example.tuition_management_app;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class UploadMaterialActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_material);

        Button btnSubmit = findViewById(R.id.btnSubmitMaterial);
        btnSubmit.setOnClickListener(v -> {
            // Handle material upload logic
            finish();
        });
    }
}