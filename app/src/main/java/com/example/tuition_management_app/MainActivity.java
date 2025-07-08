// File: app/src/main/java/com/example/tuition_management_app/MainActivity.java
package com.example.tuition_management_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tuition_management_app.teachers.TeacherDashboardActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText inputEmail = findViewById(R.id.inputEmail);
        EditText inputPassword = findViewById(R.id.inputPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString();

            if (email.isEmpty()) {
                inputEmail.setError("Email is required");
                inputEmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                inputEmail.setError("Enter a valid email");
                inputEmail.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                inputPassword.setError("Password is required");
                inputPassword.requestFocus();
                return;
            }
            if (password.length() < 6) {
                inputPassword.setError("Password must be at least 6 characters");
                inputPassword.requestFocus();
                return;
            }

            // If validation passes, proceed to dashboard
            Intent intent = new Intent(this, TeacherDashboardActivity.class);
            startActivity(intent);
        });
    }
}