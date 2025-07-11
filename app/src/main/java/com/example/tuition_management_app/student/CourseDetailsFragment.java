package com.example.tuition_management_app.student;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.tuition_management_app.R;

public class CourseDetailsFragment extends Fragment {

    public CourseDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_details, container, false);

        CardView materialsCardView = view.findViewById(R.id.materialsCardView);
        CardView assignmentsCardView = view.findViewById(R.id.assignmentsCardView);
        CardView submissionsCardView = view.findViewById(R.id.submissionsCardView);

        materialsCardView.setOnClickListener(v -> {
            // TODO: Navigate to Materials Fragment
            Toast.makeText(getContext(), "Opening Materials...", Toast.LENGTH_SHORT).show();
        });

        assignmentsCardView.setOnClickListener(v -> {
            // TODO: Navigate to Assignments Fragment
            Toast.makeText(getContext(), "Opening Assignments...", Toast.LENGTH_SHORT).show();
        });

        submissionsCardView.setOnClickListener(v -> {
            // TODO: Check submission status
            Toast.makeText(getContext(), "Checking Submissions...", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}