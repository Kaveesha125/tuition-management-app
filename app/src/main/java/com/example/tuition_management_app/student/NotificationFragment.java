package com.example.tuition_management_app.student;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tuition_management_app.R;
import com.example.tuition_management_app.SupabaseClient;
import com.example.tuition_management_app.utils.SessionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NotificationFragment extends Fragment {

    private RecyclerView notificationRecyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private SessionManager sessionManager;
    private TextView textViewNoNotifications;
    private ProgressBar progressBar;

    static class Notification {
        private final long id;
        private final String message;
        private final String sentAt;
        private final String type;
        private String senderName;
        private final Long senderId;
        private boolean isRead;


        public Notification(long id, String message, String sentAt, String type, Long senderId, boolean isRead) {
            this.id = id;
            this.message = message;
            this.sentAt = sentAt;
            this.type = type;
            this.senderId = senderId;
            this.isRead = isRead;
            this.senderName = "Loading..."; // Default name
        }

        public long getId() { return id; }
        public String getMessage() { return message; }
        public String getSentAt() { return sentAt; }
        public String getSenderName() { return senderName; }
        public Long getSenderId() { return senderId; }
        public boolean isRead() { return isRead; }
        public void setSenderName(String senderName) { this.senderName = senderName; }
        public void setRead(boolean read) { isRead = read; }
    }

    public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

        private final List<Notification> notificationList;
        private final DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        private final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");


        public NotificationAdapter(List<Notification> notificationList) {
            this.notificationList = notificationList;
        }

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification, parent, false);
            return new NotificationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
            Notification notification = notificationList.get(position);
            holder.senderName.setText(notification.getSenderName());
            holder.message.setText(notification.getMessage());

            try {
                OffsetDateTime odt = OffsetDateTime.parse(notification.getSentAt(), inputFormatter);
                holder.timestamp.setText(odt.format(outputFormatter));
            } catch (Exception e) {
                holder.timestamp.setText(notification.getSentAt());
            }

            if (notification.isRead()) {
                holder.readStatusIndicator.setVisibility(View.INVISIBLE);
                holder.senderName.setTypeface(null, Typeface.NORMAL);
                holder.message.setTypeface(null, Typeface.NORMAL);
            } else {
                holder.readStatusIndicator.setVisibility(View.VISIBLE);
                holder.senderName.setTypeface(null, Typeface.BOLD);
                holder.message.setTypeface(null, Typeface.BOLD);
            }

            holder.itemView.setOnClickListener(v -> {
                if (!notification.isRead()) {
                    markNotificationAsRead(notification, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return notificationList.size();
        }

        class NotificationViewHolder extends RecyclerView.ViewHolder {
            TextView message, timestamp, senderName;
            View readStatusIndicator;

            public NotificationViewHolder(@NonNull View itemView) {
                super(itemView);
                senderName = itemView.findViewById(R.id.textViewSenderName);
                message = itemView.findViewById(R.id.textViewNotificationMessage);
                timestamp = itemView.findViewById(R.id.textViewNotificationTimestamp);
                readStatusIndicator = itemView.findViewById(R.id.readStatusIndicator);
            }
        }
    }

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        notificationRecyclerView = view.findViewById(R.id.notificationRecyclerView);
        textViewNoNotifications = view.findViewById(R.id.textViewNoNotifications);
        progressBar = view.findViewById(R.id.progressBar);

        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        notificationRecyclerView.setAdapter(adapter);

        fetchNotifications();
    }

    private void fetchNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        long userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        Map<String, String> filters = new HashMap<>();
        filters.put("receiver_id", "eq." + userId);
        filters.put("order", "sent_at.desc");

        SupabaseClient.select("notifications", filters, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() == null) return;
                try (ResponseBody responseBody = response.body()) {
                    if (responseBody == null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Failed to fetch notifications: Empty response", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        });
                        return;
                    }
                    final String responseBodyString = responseBody.string();
                    Log.d("NotificationFragment", "Response: " + responseBodyString);
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            try {
                                JSONArray jsonArray = new JSONArray(responseBodyString);
                                if (jsonArray.length() == 0) {
                                    textViewNoNotifications.setVisibility(View.VISIBLE);
                                } else {
                                    notificationList.clear();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject obj = jsonArray.getJSONObject(i);
                                        long id = obj.getLong("notification_id");
                                        String message = obj.getString("message");
                                        String sentAt = obj.getString("sent_at");
                                        String type = obj.optString("type", "General");
                                        boolean isRead = obj.getBoolean("is_read");
                                        Long senderId = obj.has("sender_id") && !obj.isNull("sender_id") ? obj.getLong("sender_id") : null;

                                        Notification notification = new Notification(id, message, sentAt, type, senderId, isRead);
                                        notificationList.add(notification);
                                    }
                                    adapter.notifyDataSetChanged(); // Initial load with "Loading..."

                                    // Now fetch names and update items individually
                                    for (int i = 0; i < notificationList.size(); i++) {
                                        Notification notification = notificationList.get(i);
                                        if (notification.getSenderId() != null) {
                                            fetchSenderName(notification, i);
                                        } else {
                                            notification.setSenderName("System Notification");
                                            adapter.notifyItemChanged(i);
                                        }
                                    }
                                    textViewNoNotifications.setVisibility(View.GONE);
                                }
                            } catch (JSONException e) {
                                Log.e("NotificationFragment", "JSON Parsing error", e);
                                Toast.makeText(getContext(), "Error parsing notifications", Toast.LENGTH_SHORT).show();
                                textViewNoNotifications.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch notifications: " + responseBodyString, Toast.LENGTH_SHORT).show();
                            textViewNoNotifications.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    private void fetchSenderName(Notification notification, final int position) {
        Map<String, String> filters = new HashMap<>();
        filters.put("id", "eq." + notification.getSenderId());

        SupabaseClient.select("user", filters, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        notification.setSenderName("Unknown");
                        adapter.notifyItemChanged(position);
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() == null) return;
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String responseString = responseBody.string();
                        try {
                            JSONArray jsonArray = new JSONArray(responseString);
                            if (jsonArray.length() > 0) {
                                JSONObject userObject = jsonArray.getJSONObject(0);
                                String name = userObject.optString("name", "Unknown Sender");
                                notification.setSenderName(name);
                            } else {
                                notification.setSenderName("Unknown Sender");
                            }
                        } catch (JSONException e) {
                            Log.e("NotificationFragment", "Error parsing sender name", e);
                            notification.setSenderName("Error");
                        }
                    } else {
                        notification.setSenderName("Unknown");
                    }
                    getActivity().runOnUiThread(() -> adapter.notifyItemChanged(position));
                }
            }
        });
    }

    private void markNotificationAsRead(Notification notification, int position) {
        String filterQuery = "notification_id=eq." + notification.getId();
        String jsonBody = "{\"is_read\": true}";

        SupabaseClient.update("notifications", filterQuery, jsonBody, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to update notification", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            notification.setRead(true);
                            adapter.notifyItemChanged(position);
                        } else {
                            Toast.makeText(getContext(), "Failed to update notification status", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}