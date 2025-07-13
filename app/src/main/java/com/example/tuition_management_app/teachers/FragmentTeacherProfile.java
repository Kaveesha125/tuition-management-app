package com.example.tuition_management_app.teachers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.utils.SessionManager;

public class FragmentTeacherProfile extends Fragment {

    private TextView nameTextView, emailTextView, logoutTextView, changePasswordTextView;

    public FragmentTeacherProfile() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        nameTextView = view.findViewById(R.id.textView5);
        emailTextView = view.findViewById(R.id.textView6);
        logoutTextView = view.findViewById(R.id.textView12);
        changePasswordTextView = view.findViewById(R.id.textView11);


        SessionManager session = new SessionManager(requireContext());
        nameTextView.setText(session.getName());
        emailTextView.setText(session.getEmail());

        long userId = session.getUserId();


        changePasswordTextView.setOnClickListener(v -> {
            ChangePasswordDialogFragment dialog = ChangePasswordDialogFragment.newInstance(userId);
            dialog.show(getParentFragmentManager(), "ChangePasswordDialog");
        });


        logoutTextView.setOnClickListener(v -> SessionManager.logout(requireContext()));
    }
}
