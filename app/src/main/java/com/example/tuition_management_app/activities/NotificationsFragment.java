package com.example.tuition_management_app.activities;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.models.User;
import com.example.tuition_management_app.network.SupabaseClient;
import com.example.tuition_management_app.network.UserService;

import java.util.List;
import retrofit2.*;

public class NotificationsFragment extends Fragment {

    RecyclerView recyclerView;
    UserService userService;
    PendingUserAdapter adapter;

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
                    adapter = new PendingUserAdapter(response.body(), getContext()); // ✅ fixed here
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
