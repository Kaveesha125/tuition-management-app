package com.example.tuitionapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tuitionapp.R;
import com.example.tuitionapp.database.DBHelper;

public class UploadAssignmentActivity extends AppCompatActivity {

    EditText inputTitle, inputDescription;
    Button btnSubmitAssignment;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_assignment);

        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        btnSubmitAssignment = findViewById(R.id.btnSubmitAssignment);

        db = new DBHelper(this);

        btnSubmitAssignment.setOnClickListener(v -> {
            String title = inputTitle.getText().toString();
            String desc = inputDescription.getText().toString();

            boolean inserted = db.insertAssignment(title, desc);
            if (inserted) {
                Toast.makeText(this, "Assignment Uploaded", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error Uploading", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
