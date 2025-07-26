package com.example.tuition_management_app.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuition_management_app.R;
import com.example.tuition_management_app.model.User;

import java.util.List;

public class PendingUserAdapter extends RecyclerView.Adapter<PendingUserAdapter.ViewHolder> {

    private List<User> userList;
    private Context context;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onApprove(User user, int position);
        void onReject(User user, int position);
    }

    public PendingUserAdapter(List<User> userList, Context context, OnUserActionListener listener) {
        this.userList = userList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PendingUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingUserAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textEmail.setText(user.getEmail());
        holder.textRole.setText(user.getRole());

        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApprove(user, holder.getAdapterPosition());
            }
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReject(user, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void removeAt(int position) {
        userList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, userList.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textEmail, textRole;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textEmail = itemView.findViewById(R.id.text_email);
            textRole = itemView.findViewById(R.id.text_role);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }
}
