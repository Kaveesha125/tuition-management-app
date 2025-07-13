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
        userService.getPendingUsers("eq.false").enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList = response.body();
                    adapter = new PendingUserAdapter(userList, getContext(), NotificationsFragment.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch pending users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onApprove(User user, int position) {
        Map<String, Boolean> body = new HashMap<>();
        body.put("is_verified", true);

        userService.approveUser(user.getEmail(), body).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "User approved!", Toast.LENGTH_SHORT).show();
                    adapter.removeAt(position);
                } else {
                    Toast.makeText(getContext(), "Failed to approve user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onReject(User user, int position) {
        userService.deleteUser(user.getEmail()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "User rejected and deleted!", Toast.LENGTH_SHORT).show();
                    adapter.removeAt(position);
                } else {
                    Toast.makeText(getContext(), "Failed to delete user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
