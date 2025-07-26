package com.example.tuition_management_app.activities;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.model.User;
import com.example.tuition_management_app.network.UserService;
import com.example.tuition_management_app.SupabaseClient;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingUserAdapter extends RecyclerView.Adapter<PendingUserAdapter.UserViewHolder> {

    private final List<User> userList;
    private final Context context;
    private final UserService userService;

    public PendingUserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
        this.userService = SupabaseClient.getClient().create(UserService.class);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textEmail.setText(user.email);
        holder.textRole.setText("Role: " + user.role);

        holder.btnApprove.setOnClickListener(v -> approveUser(user, position));
        holder.btnReject.setOnClickListener(v -> rejectUser(user, position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void approveUser(User user, int position) {
        Map<String, Boolean> update = new HashMap<>();
        update.put("is_verified", true);

        userService.approveUser("eq." + user.email, update).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Approved: " + user.email, Toast.LENGTH_SHORT).show();

                    // ➕ Add to student or teacher table
                    if (user.role.equalsIgnoreCase("student")) {
                        insertStudent(user);
                    } else if (user.role.equalsIgnoreCase("teacher")) {
                        insertTeacher(user);
                    }

                    removeUserAt(position);
                } else {
                    Toast.makeText(context, "Approval failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void insertStudent(User user) {
        Map<String, Object> studentData = new HashMap<>();
        studentData.put("email", user.email);
        // Add additional fields if necessary

        userService.createStudent(studentData).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Student added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to insert into student table.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(context, "Student insert error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertTeacher(User user) {
        Map<String, Object> teacherData = new HashMap<>();
        teacherData.put("email", user.email);
        // Add additional fields if necessary

        userService.createTeacher(teacherData).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Teacher added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to insert into teacher table.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(context, "Teacher insert error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rejectUser(User user, int position) {
        userService.deleteUser("eq." + user.email).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Rejected: " + user.email, Toast.LENGTH_SHORT).show();
                    removeUserAt(position);
                } else {
                    Toast.makeText(context, "Rejection failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error on rejection: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void removeUserAt(int position) {
        userList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, userList.size());
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textEmail, textRole;
        Button btnApprove, btnReject;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textEmail = itemView.findViewById(R.id.text_email);
            textRole = itemView.findViewById(R.id.text_role);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }
}