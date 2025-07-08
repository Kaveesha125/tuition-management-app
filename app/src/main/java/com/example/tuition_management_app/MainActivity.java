package com.example.tuition_management_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static int registrationId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize all views
        EditText idEditText = findViewById(R.id.editTextNumber);
        EditText nameEditText = findViewById(R.id.editTextText2);
        EditText emailEditText = findViewById(R.id.editTextTextEmailAddress);
        EditText passwordEditText = findViewById(R.id.editTextTextPassword);
        Spinner roleSpinner = findViewById(R.id.spinner);
        Button registerButton = findViewById(R.id.button2);

        // id auto increment & can't edit
        idEditText.setText(String.valueOf(registrationId));
        idEditText.setEnabled(false);

        // The caution msg & popup
        TextView errorText = new TextView(this);
        errorText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        errorText.setText("Fill All The Fields...");
        ((androidx.constraintlayout.widget.ConstraintLayout) findViewById(R.id.main)).addView(errorText);
        //layout of popup text
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params = new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.topToBottom = R.id.button2;
        params.startToStart = R.id.main;
        params.endToEnd = R.id.main;
        params.topMargin = 100;
        errorText.setLayoutParams(params);

        // Register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input values
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String role = roleSpinner.getSelectedItem().toString();

                // check all the fields filled
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
                    errorText.setText("Make Sure All The Fields are Filled...");
                    return;
                }
                // check email have @
                if (!email.contains("@")) {
                    errorText.setText("Enter A vaild Email....");
                    return;
                }
                //increase id &refresh
                registrationId++;
                idEditText.setText(String.valueOf(registrationId));
                nameEditText.setText("");
                emailEditText.setText("");
                passwordEditText.setText("");
                roleSpinner.setSelection(0);
            }
        });
    }
}