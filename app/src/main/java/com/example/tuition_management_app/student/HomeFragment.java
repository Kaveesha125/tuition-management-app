package com.example.tuition_management_app.student;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.tuition_management_app.R;
import com.example.tuition_management_app.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SessionManager sessionManager = new SessionManager(requireContext());

        TextView welcomeTextView = view.findViewById(R.id.welcomeTextView);
        TextView dateTextView = view.findViewById(R.id.dateTextView);

        // Set the welcome message with the user's name
        String studentName = sessionManager.getName();
        if (studentName != null && !studentName.isEmpty()) {
            welcomeTextView.setText("Welcome " + studentName);
        }

        // Get and format the current date
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        dateTextView.setText(currentDate);

        // TODO: Add OnClickListener for the submitAssignmentButton and logic to fetch dashboard data

        return view;
    }
}