package com.example.tuition_management_app.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import android.os.Bundle;

import com.example.tuition_management_app.teachers.FragmentTeacherCourseAssessments;
import com.example.tuition_management_app.teachers.FragmentTeacherCourseMaterials;
import com.example.tuition_management_app.teachers.FragmentTeacherCourseResults;
import com.example.tuition_management_app.teachers.FragmentTeacherCourseAttendance;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final long courseId;

    public ViewPagerAdapter(@NonNull Fragment fragment, long courseId) {
        super(fragment);
        this.courseId = courseId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        Bundle args = new Bundle();
        args.putLong("course_id", courseId);

        switch (position) {
            case 0:
                fragment = new FragmentTeacherCourseAssessments();
                break;
            case 1:
                fragment = new FragmentTeacherCourseMaterials();
                break;
            case 2:
                fragment = new FragmentTeacherCourseResults();
                break;
            case 3:
                fragment = new FragmentTeacherCourseAttendance();
                break;
            default:
                fragment = new FragmentTeacherCourseAssessments();
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4; // Total tabs
    }
}
