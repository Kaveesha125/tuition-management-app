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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.SupabaseClient;
import com.example.tuition_management_app.adapter.ReportAdapter;
import com.example.tuition_management_app.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TeacherReportActivity extends AppCompatActivity {

    RecyclerView recyclerReport;
    TextView tvTotalCount;
    ReportAdapter adapter;
    List<User> reportData = new ArrayList<>();
    Button btnDownloadPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_report);

        recyclerReport = findViewById(R.id.recyclerReport);
        tvTotalCount = findViewById(R.id.tvTotalCount);
        btnDownloadPdf = findViewById(R.id.btnDownloadPdf);

        adapter = new ReportAdapter(reportData);
        recyclerReport.setLayoutManager(new LinearLayoutManager(this));
        recyclerReport.setAdapter(adapter);

        checkStoragePermission();
        loadTeachers();

        btnDownloadPdf.setOnClickListener(v -> generatePdfReport());
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void loadTeachers() {
        SupabaseClient.select("user",
                Map.of("role", "eq.teacher", "is_verified", "eq.true"),
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> tvTotalCount.setText("Failed to load report"));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        runOnUiThread(() -> {
                            try {
                                JSONArray array = new JSONArray(body);
                                reportData.clear();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    User u = new User(
                                            obj.getString("name"),
                                            obj.getString("email"),
                                            "",
                                            obj.getString("role"),
                                            obj.getBoolean("is_verified")
                                    );
                                    u.id = obj.getString("id");
                                    reportData.add(u);
                                }
                                adapter.notifyDataSetChanged();
                                tvTotalCount.setText("Total Teachers: " + reportData.size());
                            } catch (JSONException e) {
                                tvTotalCount.setText("JSON Parse Error");
                            }
                        });
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
        canvas.drawText("Teacher Report", 200, y, paint);
        y += 30;

        paint.setTextSize(14);
        paint.setFakeBoldText(false);
        canvas.drawText("Total Teachers: " + reportData.size(), 50, y, paint);
        y += 40;

        for (User teacher : reportData) {
            canvas.drawText("ID: " + teacher.id, 50, y, paint);
            y += 20;
            canvas.drawText("Name: " + teacher.getName(), 50, y, paint);
            y += 20;
            canvas.drawText("Email: " + teacher.getEmail(), 50, y, paint);
            y += 30;
        }

        pdfDocument.finishPage(page);

        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, "teacher_report.pdf");
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF saved in Downloads", Toast.LENGTH_LONG).show();

            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    this.getPackageName() + ".provider", // Add this authority in manifest
                    file
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);

        } catch (IOException e) {
            Toast.makeText(this, "Error creating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "No app found to view PDF", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }
}
