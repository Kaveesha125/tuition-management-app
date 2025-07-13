package com.example.tuition_management_app.teachers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.SupabaseClient;
import com.example.tuition_management_app.utils.SessionManager;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentTeacherCourseAttendance extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private CompoundBarcodeView barcodeView;
    private long courseId;
    private long teacherId;
    private static final String TAG = "FragCourseAttendance";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_course_attendance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        courseId = getArguments() != null ? getArguments().getLong("course_id", -1) : -1;
        teacherId = new SessionManager(requireContext()).getUserId();
        barcodeView = view.findViewById(R.id.barcode_scanner);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startScanner();
        }
    }

    private void startScanner() {
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                String scannedData = result.getText();
                if (scannedData != null) {
                    barcodeView.pause();
                    Log.d(TAG, "Scanned QR Data: " + scannedData);
                    handleScannedData(scannedData);
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
        });
    }

    private void handleScannedData(String scannedJson) {
        try {
            JSONObject jsonObject = new JSONObject(scannedJson);
            long userId = jsonObject.getLong("id"); // Extract from QR code

            // Step 1: Fetch student by user_id
            SupabaseClient.select("students",
                    Map.of("user_id", "eq." + userId, "course_id", "eq." + courseId),
                    new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            requireActivity().runOnUiThread(() -> {
                                showToast("Failed to verify student");
                                barcodeView.resume();
                            });
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            try {
                                String body = response.body().string();
                                if (body == null || body.isEmpty()) {
                                    throw new IOException("Empty response body");
                                }

                                org.json.JSONArray array = new org.json.JSONArray(body);
                                if (array.length() == 0) {
                                    requireActivity().runOnUiThread(() -> {
                                        showToast("Student not enrolled in this course");
                                        barcodeView.resume();
                                    });
                                    return;
                                }

                                JSONObject studentRecord = array.getJSONObject(0);
                                long studentId = studentRecord.getLong("student_id");

                                JSONObject result = new JSONObject();
                                result.put("student_id", studentId);
                                result.put("teacher_id", teacherId);
                                result.put("course_id", courseId);
                                result.put("status", "present");

                                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                result.put("date", currentDate);

                                // Step 2: Insert attendance
                                SupabaseClient.insert("attendance", result.toString(), new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        requireActivity().runOnUiThread(() -> {
                                            showToast("Failed to record attendance");
                                            barcodeView.resume();
                                        });
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                                        requireActivity().runOnUiThread(() -> {
                                            showToast("Attendance recorded");
                                            barcodeView.resume();
                                        });
                                    }
                                });

                            } catch (JSONException e) {
                                requireActivity().runOnUiThread(() -> {
                                    showToast("Error parsing student info");
                                    barcodeView.resume();
                                });
                            }
                        }
                    });

        } catch (JSONException e) {
            showToast("Invalid QR Code Format");
            barcodeView.resume(); // already on main thread here
        }
    }


    private void showToast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanner();
            } else {
                Toast.makeText(getContext(), "Camera permission is required for attendance scanning", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeView != null) barcodeView.resume();
    }

    @Override
    public void onPause() {
        if (barcodeView != null) barcodeView.pause();
        super.onPause();
    }
}
