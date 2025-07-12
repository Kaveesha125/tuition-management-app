package com.example.tuition_management_app.teachers;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.tuition_management_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class TeacherDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);



        BottomNavigationView bottomNav = findViewById(R.id.teacher_bottom_navigation);

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.teacher_fragment_container, new TeacherHome())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_homeTeacher) {
                selectedFragment = new TeacherHome();
            } else if (itemId == R.id.nav_courseTeacher) {
                selectedFragment = new FragmentTeacherCourse();
            } else if (itemId == R.id.nav_profileTeacher) {
                selectedFragment = new FragmentTeacherProfile();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.teacher_fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
}