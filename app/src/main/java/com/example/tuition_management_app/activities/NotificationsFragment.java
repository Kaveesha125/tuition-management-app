package com.example.tuition_management_app.activities;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.adapter.PendingUserAdapter;
import com.example.tuition_management_app.model.User;
import com.example.tuition_management_app.network.UserService;
import com.example.tuition_management_app.SupabaseClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment implements PendingUserAdapter.OnUserActionListener {

    RecyclerView recyclerView;
    UserService userService;
    PendingUserAdapter adapter;
    List<User> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView = view.findViewById(R.id.recyclerPendingUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userService = SupabaseClient.getClient().create(UserService.class);

        fetchPendingUsers();
        return view;
    }

    private void fetchPendingUsers() {
        Map<String, String> filters = new HashMap<>();
        filters.put("is_verified", "eq.false");

        SupabaseClient.select("user", filters, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error fetching users: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();

                    // Convert JSON array to List<User>
                    List<User> users = new com.google.gson.Gson().fromJson(json, new com.google.gson.reflect.TypeToken<List<User>>() {}.getType());

                    requireActivity().runOnUiThread(() -> {
                        userList = users;
                        adapter = new PendingUserAdapter(userList, getContext(), NotificationsFragment.this);
                        recyclerView.setAdapter(adapter);
                    });
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to fetch pending users: " + response.message(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }


    @Override
    public void onApprove(User user, int position) {
        String filterQuery = "id=eq." + user.getId();


        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("is_verified", true);
        } catch (JSONException e) {
            Toast.makeText(getContext(), "JSON error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseClient.update("user", filterQuery, jsonBody.toString(), new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Approve failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "User approved!", Toast.LENGTH_SHORT).show();
                        adapter.removeAt(position);
                    } else {
                        Toast.makeText(getContext(), "Approve failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onReject(User user, int position) {
        String filterQuery = "email=eq." + user.getEmail(); // or "id=eq." + user.getId()

        SupabaseClient.delete("user", filterQuery, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Reject failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "User rejected and deleted!", Toast.LENGTH_SHORT).show();
                        adapter.removeAt(position);
                    } else {
                        Toast.makeText(getContext(), "Reject failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }







}
