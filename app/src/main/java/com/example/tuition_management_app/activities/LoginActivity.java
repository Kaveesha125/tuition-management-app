package com.example.tuition_management_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.SupabaseClient;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Toggle password visibility on eye icon click
        etPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2; // Right drawable index
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    isPasswordVisible = !isPasswordVisible;
                    if (isPasswordVisible) {
                        etPassword.setTransformationMethod(null);
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0);
                    } else {
                        etPassword.setTransformationMethod(new PasswordTransformationMethod());
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_close, 0);
                    }
                    etPassword.setSelection(etPassword.getText().length());
                    return true;
                }
            }
            return false;
        });

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build filter map for email and password only
        Map<String, String> filters = new HashMap<>();
        filters.put("email", "eq." + email);
        filters.put("password", "eq." + password);

        SupabaseClient.select("user", filters, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "Login failed: " + response.message(), Toast.LENGTH_SHORT).show()
                    );
                    response.close(); // Close here if unsuccessful
                    return;
                }

                try (ResponseBody responseBody = response.body()) {
                    if (responseBody == null) {
                        runOnUiThread(() ->
                                Toast.makeText(LoginActivity.this, "Empty response from server", Toast.LENGTH_SHORT).show()
                        );
                        return;
                    }

                    String jsonResponse = responseBody.string();  // consumes and closes body

                    JSONArray jsonArray = new JSONArray(jsonResponse);
                    if (jsonArray.length() > 0) {
                        String role = jsonArray.getJSONObject(0).getString("role");
                        runOnUiThread(() -> {
                            switch (role.toLowerCase()) {
                                case "admin":
                                    Toast.makeText(LoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                                    finish();
                                    break;
                                case "student":
                                    Toast.makeText(LoginActivity.this, "Welcome Student", Toast.LENGTH_SHORT).show();
                                    // TODO: student activity
                                    break;
                                case "teacher":
                                    Toast.makeText(LoginActivity.this, "Welcome Teacher", Toast.LENGTH_SHORT).show();
                                    // TODO: teacher activity
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        });
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                        );
                    }
                } catch (JSONException e) {
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "Response parsing error", Toast.LENGTH_SHORT).show()
                    );
                }
            }

        });
    }
}
