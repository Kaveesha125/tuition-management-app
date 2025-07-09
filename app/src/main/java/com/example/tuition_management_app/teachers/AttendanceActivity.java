package com.example.tuition_management_app.teachers;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuition_management_app.R;

public class AttendanceActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        Button btnStartScanning = findViewById(R.id.btnStartQR);
        Button btnSubmit = findViewById(R.id.btnSubmitAttendance);

        btnStartScanning.setOnClickListener(v -> {
            // Intent to open the camera
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmit.setOnClickListener(v -> {
            // Handle attendance submission logic
            finish();
        });
    }
}