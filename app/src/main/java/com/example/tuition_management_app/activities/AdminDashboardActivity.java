package com.example.tuition_management_app.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tuition_management_app.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        MaterialToolbar toolbar = findViewById(R.id.adminToolbar);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // Set default fragment and title
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new AdminHomeFragment())
                    .commit();
            toolbar.setTitle("Home");
            bottomNav.setSelectedItemId(R.id.nav_home);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            String title = "Home";
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                selected = new AdminHomeFragment();
                title = "Home";
            } else if (id == R.id.nav_reports) {
                selected = new AdminReportsFragment();
                title = "Reports";
            } else if (id == R.id.nav_profile) {
                selected = new AdminProfileFragment();
                title = "Profile";
            }
            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, selected)
                        .commit();
                toolbar.setTitle(title);
            }
            return true;
        });
    }
}