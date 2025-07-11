package com.example.tuition_management_app.student;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.utils.SessionManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class QrFragment extends Fragment {

    public QrFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Add FLAG_SECURE to prevent screenshots
        if (getActivity() != null) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        View view = inflater.inflate(R.layout.fragment_qr, container, false);

        ImageView qrCodeImageView = view.findViewById(R.id.qrCodeImageView);
        generateQrCode(qrCodeImageView);

        return view;
    }

    private void generateQrCode(ImageView imageView) {
        SessionManager sessionManager = new SessionManager(requireContext());
        long studentId = sessionManager.getUserId();
        String studentName = sessionManager.getName();
        String studentEmail = sessionManager.getEmail();

        if (studentId == -1 || studentName == null || studentEmail == null) {
            Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("student_id", String.valueOf(studentId));
            jsonObject.put("name", studentName);
            jsonObject.put("email", studentEmail);
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error creating QR data.", Toast.LENGTH_SHORT).show();
            return;
        }

        String qrData = jsonObject.toString();
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            imageView.setImageBitmap(bmp);

        } catch (WriterException e) {
            Toast.makeText(getContext(), "Error generating QR code.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear FLAG_SECURE when the fragment is destroyed
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
    }
}