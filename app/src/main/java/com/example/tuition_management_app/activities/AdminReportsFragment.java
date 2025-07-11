package com.example.tuition_management_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tuition_management_app.R;

public class AdminReportsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView btnViewStudentReports = view.findViewById(R.id.btnViewStudentReports);
        TextView btnViewTeacherReports = view.findViewById(R.id.btnViewTeacherReports);

        btnViewStudentReports.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), StudentReportActivity.class);
            startActivity(intent);
        });

        btnViewTeacherReports.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TeacherReportActivity.class);
            startActivity(intent);
        });
    }
}