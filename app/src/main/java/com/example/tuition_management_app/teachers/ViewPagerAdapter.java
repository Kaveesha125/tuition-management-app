package com.example.tuition_management_app.teachers;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;



public class ViewPagerAdapter extends FragmentStateAdapter {

    private final long courseId;

    public ViewPagerAdapter(@NonNull Fragment fragment, long courseId) {
        super(fragment);
        this.courseId = courseId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle args = new Bundle();
        args.putLong("course_id", courseId);

        switch (position) {
            case 0:
                FragmentTeacherCourseAssessments assessmentsFragment = new FragmentTeacherCourseAssessments();
                assessmentsFragment.setArguments(args);
                return assessmentsFragment;

            case 1:
                FragmentTeacherCourseMaterials materialsFragment = new FragmentTeacherCourseMaterials();
                materialsFragment.setArguments(args);
                return materialsFragment;

            case 2:
                FragmentTeacherCourseResults resultsFragment = new FragmentTeacherCourseResults();
                resultsFragment.setArguments(args);
                return resultsFragment;

            case 3:
                FragmentTeacherCourseAttendance attendanceFragment = new FragmentTeacherCourseAttendance();
                attendanceFragment.setArguments(args);
                return attendanceFragment;

            default:
                return new FragmentTeacherCourseAssessments(); // fallback
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
