package com.example.tuition_management_app.teachers;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.SupabaseClient;
import com.example.tuition_management_app.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class ChangePasswordDialogFragment extends DialogFragment {

    private EditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button changePasswordButton;
    private final OkHttpClient client = new OkHttpClient();

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // Add newInstance method to fix the error
    public static ChangePasswordDialogFragment newInstance(long userId) {
        ChangePasswordDialogFragment fragment = new ChangePasswordDialogFragment();
        Bundle args = new Bundle();
        args.putLong("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_change_password, container, false);

        oldPasswordEditText = view.findViewById(R.id.editTextOldPassword);
        newPasswordEditText = view.findViewById(R.id.editTextNewPassword);
        confirmPasswordEditText = view.findViewById(R.id.editTextConfirmPassword);
        changePasswordButton = view.findViewById(R.id.buttonChangePassword);

        changePasswordButton.setOnClickListener(v -> attemptChangePassword());
        return view;
    }

    private void attemptChangePassword() {
        String oldPass = oldPasswordEditText.getText().toString().trim();
        String newPass = newPasswordEditText.getText().toString().trim();
        String confirmPass = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(oldPass)) {
            oldPasswordEditText.setError("Old password required");
            return;
        }
        if (TextUtils.isEmpty(newPass)) {
            newPasswordEditText.setError("New password required");
            return;
        }
        if (!newPass.equals(confirmPass)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        // Use userId from arguments if available, otherwise from SessionManager
        long userId = getArguments() != null ? getArguments().getLong("userId") :
                new SessionManager(requireContext()).getUserId();

        // Check old password
        checkOldPasswordAndUpdate(userId, oldPass, newPass);
    }

    private void checkOldPasswordAndUpdate(long userId, String oldPass, String newPass) {
        SupabaseClient.select("user", Map.of("id", "eq." + userId, "select", "password"), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Failed to verify old password", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                try {
                    org.json.JSONArray jsonArray = new org.json.JSONArray(responseBody);
                    if (jsonArray.length() == 0) {
                        showToast("User not found");
                        return;
                    }
                    JSONObject user = jsonArray.getJSONObject(0);
                    String currentPassword = user.getString("password");

                    if (!oldPass.equals(currentPassword)) {
                        showToast("Old password is incorrect");
                        return;
                    }

                    updatePassword(userId, newPass);

                } catch (JSONException e) {
                    showToast("Error parsing response");
                }
            }
        });
    }

    private void updatePassword(long userId, String newPass) {
        JSONObject json = new JSONObject();
        try {
            json.put("password", newPass);
        } catch (JSONException e) {
            showToast("Error preparing request");
            return;
        }

        SupabaseClient.update("user", "id=eq." + userId, json.toString(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(requireContext(), "Password update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showToast(String msg) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}