package com.example.tuition_management_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tuition_management_app.R;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_admin_dashboard);

        findViewById(R.id.btnManageUsers).setOnClickListener(v ->
                Toast.makeText(this, "Manage Users clicked", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnManageCourses).setOnClickListener(v ->
                Toast.makeText(this, "Manage Courses clicked", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnViewReports).setOnClickListener(v ->
                Toast.makeText(this, "View Reports clicked", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
