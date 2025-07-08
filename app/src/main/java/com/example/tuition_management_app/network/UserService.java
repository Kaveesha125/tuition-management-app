package com.example.tuition_management_app.network;

import com.example.tuition_management_app.models.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface UserService {

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZkcnBoaWpsdnJlc3l1ZHhnZnJqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE4Njc3NzYsImV4cCI6MjA2NzQ0Mzc3Nn0.HYCsGy7U5la9oG68t7msPkDvFZPUpIevVgPlNNjZa5w",
            "Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZkcnBoaWpsdnJlc3l1ZHhnZnJqIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1MTg2Nzc3NiwiZXhwIjoyMDY3NDQzNzc2fQ.iUlJwEbUFbWUZg7oG5aLhly8O9TNylQwDSC7_4VgvEw",
            "Accept: application/json"
    })
    @GET("user")  // ✅ This must match your Supabase table name exactly
    Call<List<User>> loginAdmin(
            @Query("email") String emailFilter,
            @Query("password") String passwordFilter,
            @Query("role") String roleFilter
    );
}
