package com.example.tuitionapp.activities;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tuitionapp.R;
import com.example.tuitionapp.utils.SupabaseService;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.*;

public class UploadAssignmentActivity extends AppCompatActivity {
    EditText inputTitle, inputDescription;
    Button btnSubmit;
    SupabaseService supabase;
    String accessToken = "<replace_with_token_from_login>"; // Should be passed securely
    String teacherId = "<replace_with_teacher_id>";         // Should be dynamic

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_assignment);

        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        btnSubmit = findViewById(R.id.btnSubmitAssignment);

        supabase = new SupabaseService();

        btnSubmit.setOnClickListener(v -> {
            try {
                JSONObject obj = new JSONObject();
                obj.put("title", inputTitle.getText().toString());
                obj.put("description", inputDescription.getText().toString());
                obj.put("teacher_id", teacherId);

                supabase.postData("assignments", obj, accessToken, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Uploaded!", Toast.LENGTH_SHORT).show());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
