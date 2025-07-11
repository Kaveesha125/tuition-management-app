package com.example.tuition_management_app.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.adapters.ReportAdapter;
import com.example.tuition_management_app.models.User;
import com.example.tuition_management_app.network.SupabaseClient;
import com.example.tuition_management_app.network.UserService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentReportActivity extends AppCompatActivity {

    RecyclerView recyclerReport;
    TextView tvTotalCount;
    ReportAdapter adapter;
    List<User> reportData = new ArrayList<>();
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_report);

        recyclerReport = findViewById(R.id.recyclerReport);
        tvTotalCount = findViewById(R.id.tvTotalCount);
        Button btnDownloadPdf = findViewById(R.id.btnDownloadPdf);

        userService = SupabaseClient.getClient().create(UserService.class);

        adapter = new ReportAdapter(reportData);
        recyclerReport.setLayoutManager(new LinearLayoutManager(this));
        recyclerReport.setAdapter(adapter);

        loadReport("Student");

        checkStoragePermission();

        btnDownloadPdf.setOnClickListener(v -> generatePdfReport());
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void loadReport(String role) {
        userService.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reportData.clear();
                    for (User user : response.body()) {
                        if ("Student".equals(user.role) && user.is_verified) {
                            reportData.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    tvTotalCount.setText("Total Students: " + reportData.size());
                } else {
                    tvTotalCount.setText("No data received");
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                tvTotalCount.setText("Error: " + t.getMessage());
            }
        });
    }

    private void generatePdfReport() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int y = 50;
        paint.setTextSize(18);
        paint.setFakeBoldText(true);
        canvas.drawText("Student Report", 200, y, paint);
        y += 30;

        paint.setTextSize(14);
        paint.setFakeBoldText(false);
        canvas.drawText("Total Students: " + reportData.size(), 50, y, paint);
        y += 40;

        for (User student : reportData) {
            canvas.drawText("ID: " + student.id, 50, y, paint);
            y += 20;
            canvas.drawText("Name: " + student.getName(), 50, y, paint);
            y += 20;
            canvas.drawText("Email: " + student.getEmail(), 50, y, paint);
            y += 30;
        }

        pdfDocument.finishPage(page);

        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            File file = new File(downloadsDir, "student_report.pdf");
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF saved in Downloads", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No app found to view PDF", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Toast.makeText(this, "Error creating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();


    }
}