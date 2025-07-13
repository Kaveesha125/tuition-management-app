package com.example.tuition_management_app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.utils.SessionManager;
import com.example.tuition_management_app.SupabaseClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class AdminProfileFragment extends Fragment {

    private TextView adminName, logoutBtn, changePasswordBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);

        SessionManager session = new SessionManager(requireContext());

        // Display admin name
        adminName = view.findViewById(R.id.tvAdminName);
        adminName.setText(session.getName());

        // Logout
        logoutBtn = view.findViewById(R.id.btnLogout);
        logoutBtn.setOnClickListener(v -> SessionManager.logout(requireContext()));

        // Change password
        changePasswordBtn = view.findViewById(R.id.btnChangePassword);
        changePasswordBtn.setOnClickListener(v -> showChangePasswordDialog(session));

        return view;
    }

    private void showChangePasswordDialog(SessionManager session) {
        Context context = requireContext();

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        EditText inputOld = new EditText(context);
        inputOld.setHint("Old Password");
        inputOld.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputOld);

        EditText inputNew = new EditText(context);
        inputNew.setHint("New Password");
        inputNew.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputNew);

        new AlertDialog.Builder(context)
                .setTitle("Change Password")
                .setView(layout)
                .setPositiveButton("Update", (dialog, which) -> {
                    String oldPwd = inputOld.getText().toString().trim();
                    String newPwd = inputNew.getText().toString().trim();
                    if (oldPwd.isEmpty() || newPwd.isEmpty()) {
                        Toast.makeText(context, "Please enter both fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updatePassword(session.getEmail(), oldPwd, newPwd);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updatePassword(String email, String oldPassword, String newPassword) {
        Context context = requireContext();

        // Step 1: verify old password first (query)
        SupabaseClient.select("user", Map.of("email", "eq." + email, "password", "eq." + oldPassword), new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(context, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String res = response.body().string();
                if (response.isSuccessful() && res.contains(email)) {
                    // Step 2: update password
                    JSONObject json = new JSONObject();
                    try {
                        json.put("password", newPassword);
                    } catch (JSONException e) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(context, "JSON error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                        return;
                    }

                    SupabaseClient.update("user", "email=eq." + email, json.toString(), new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(context, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            requireActivity().runOnUiThread(() -> {
                                if (response.isSuccessful()) {
                                    Toast.makeText(context, "Password updated!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Failed to update password", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(context, "Old password is incorrect", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
