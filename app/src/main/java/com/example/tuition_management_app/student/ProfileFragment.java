package com.example.tuition_management_app.student;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.tuition_management_app.R;
import com.example.tuition_management_app.utils.SessionManager;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SessionManager sessionManager = new SessionManager(requireContext());

        TextView nameTextView = view.findViewById(R.id.textView5);
        TextView emailTextView = view.findViewById(R.id.textView6);
        TextView idTextView = view.findViewById(R.id.textView8);
        TextView qrTextView = view.findViewById(R.id.textView13);
        TextView logoutTextView = view.findViewById(R.id.textView12);

        // Set user data from SessionManager
        nameTextView.setText(sessionManager.getName());
        emailTextView.setText(sessionManager.getEmail());
        idTextView.setText(String.valueOf(sessionManager.getUserId()));

        qrTextView.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new QrFragment())
                    .addToBackStack(null)
                    .commit();
        });

        logoutTextView.setOnClickListener(v -> {
            SessionManager.logout(requireActivity());
        });

        return view;
    }
}