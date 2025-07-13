package com.example.tuition_management_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.tuition_management_app.R;

public class AdminProfileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);

        // Set admin name from users table (replace with your actual logic)
        TextView adminName = view.findViewById(R.id.tvAdminName);
        String name = getAdminNameFromDatabase(); // Implement this method as needed
        adminName.setText(name);

        // Fix: Use the correct ID for logout
        TextView logout = view.findViewById(R.id.btnLogout);
        logout.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });

        return view;
    }

    // Placeholder for fetching admin name from your users table
    private String getAdminNameFromDatabase() {
        // TODO: Replace with actual database query
        return "Admin User";
    }
}