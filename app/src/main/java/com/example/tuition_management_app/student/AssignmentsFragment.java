package com.example.tuition_management_app.student;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AssignmentsFragment extends Fragment {

    private RecyclerView assignmentsRecyclerView;
    private AssignmentAdapter adapter;
    private List<Assignment> assignmentList;
    private SessionManager sessionManager;
    private TextView textViewNoAssignments;
    private ProgressBar progressBar;

    // 1. Model as a static inner class
    static class Assignment {
        private final String title;
        private final String description;
        private final String dueDate;

        public Assignment(String title, String description, String dueDate) {
            this.title = title;
            this.description = description;
            this.dueDate = dueDate;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getDueDate() { return dueDate; }
    }

    // 2. Adapter as an inner class
    public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

        private final List<Assignment> assignmentList;

        public AssignmentAdapter(List<Assignment> assignmentList) {
            this.assignmentList = assignmentList;
        }

        @NonNull
        @Override
        public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_assignment, parent, false);
            return new AssignmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
            Assignment assignment = assignmentList.get(position);
            holder.title.setText(assignment.getTitle());
            holder.description.setText(assignment.getDescription());
            holder.dueDate.setText("Due: " + assignment.getDueDate());
        }

        @Override
        public int getItemCount() {
            return assignmentList.size();
        }

        class AssignmentViewHolder extends RecyclerView.ViewHolder {
            TextView title, description, dueDate;

            public AssignmentViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.textViewAssignmentTitle);
                description = itemView.findViewById(R.id.textViewAssignmentDescription);
                dueDate = itemView.findViewById(R.id.textViewAssignmentDueDate);
            }
        }
    }

    public AssignmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assignments_s, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        assignmentsRecyclerView = view.findViewById(R.id.assignmentsRecyclerView);
        textViewNoAssignments = view.findViewById(R.id.textViewNoAssignments);
        progressBar = view.findViewById(R.id.progressBar);

        assignmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        assignmentList = new ArrayList<>();
        adapter = new AssignmentAdapter(assignmentList);
        assignmentsRecyclerView.setAdapter(adapter);

        fetchStudentCourseId();
    }

    private void fetchStudentCourseId() {
        progressBar.setVisibility(View.VISIBLE);
        long userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        Map<String, String> studentFilters = new HashMap<>();
        studentFilters.put("user_id", "eq." + userId);
        studentFilters.put("select", "course_id");

        SupabaseClient.select("students", studentFilters, new Callback() {
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
                if (response.isSuccessful() && response.body() != null) {
                    final String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        if (jsonArray.length() > 0) {
                            JSONObject studentObject = jsonArray.getJSONObject(0);
                            if (studentObject.has("course_id") && !studentObject.isNull("course_id")) {
                                long courseId = studentObject.getLong("course_id");
                                fetchAssignments(courseId);
                            } else {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        textViewNoAssignments.setVisibility(View.VISIBLE);
                                        textViewNoAssignments.setText("Course not assigned.");
                                        progressBar.setVisibility(View.GONE);
                                    });
                                }
                            }
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    textViewNoAssignments.setVisibility(View.VISIBLE);
                                    textViewNoAssignments.setText("Student profile not found.");
                                    progressBar.setVisibility(View.GONE);
                                });
                            }
                        }
                    } catch (JSONException e) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Error parsing student data", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            });
                        }
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Failed to fetch student profile", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        });
                    }
                }
            }
        });
    }

    private void fetchAssignments(long courseId) {
        Map<String, String> filters = new HashMap<>();
        filters.put("course_id", "eq." + courseId);
        filters.put("select", "title,description,due_date");

        SupabaseClient.select("assignments", filters, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error fetching assignments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() == null) return;
                final String responseBody = response.body().string();
                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            JSONArray jsonArray = new JSONArray(responseBody);
                            if (jsonArray.length() == 0) {
                                textViewNoAssignments.setVisibility(View.VISIBLE);
                            } else {
                                assignmentList.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    String title = obj.getString("title");
                                    String description = obj.getString("description");
                                    String dueDate = obj.getString("due_date");
                                    assignmentList.add(new Assignment(title, description, dueDate));
                                }
                                adapter.notifyDataSetChanged();
                                textViewNoAssignments.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Error parsing assignments", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch assignments", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}