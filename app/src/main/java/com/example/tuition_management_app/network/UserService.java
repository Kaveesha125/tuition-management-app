package com.example.tuition_management_app.network;

import com.example.tuition_management_app.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface UserService {

    String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZkcnBoaWpsdnJlc3l1ZHhnZnJqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE4Njc3NzYsImV4cCI6MjA2NzQ0Mzc3Nn0.HYCsGy7U5la9oG68t7msPkDvFZPUpIevVgPlNNjZa5w";
    String AUTHORIZATION = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZkcnBoaWpsdnJlc3l1ZHhnZnJqIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1MTg2Nzc3NiwiZXhwIjoyMDY3NDQzNzc2fQ.iUlJwEbUFbWUZg7oG5aLhly8O9TNylQwDSC7_4VgvEw";

    String READ_HEADERS = "apikey: " + API_KEY + ", Authorization: " + AUTHORIZATION + ", Accept: application/json";
    String WRITE_HEADERS = "apikey: " + API_KEY + ", Authorization: " + AUTHORIZATION + ", Content-Type: application/json, Prefer: return=representation";

    // Admin login
    @Headers({
            "apikey: " + API_KEY,
            "Authorization: " + AUTHORIZATION,
            "Accept: application/json"
    })
    @GET("user")
    Call<List<User>> loginAdmin(
            @Query("email") String email,
            @Query("password") String password,
            @Query("role") String role
    );

    // Register user
    @Headers({
            "apikey: " + API_KEY,
            "Authorization: " + AUTHORIZATION,
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    @POST("user")
    Call<User> registerUser(@Body User user);

    // Get pending users
    @Headers({
            "apikey: " + API_KEY,
            "Authorization: " + AUTHORIZATION,
            "Accept: application/json"
    })
    @GET("user")
    Call<List<User>> getPendingUsers(@Query("is_verified") String isVerified);

    // Approve user
    @Headers({
            "apikey: " + API_KEY,
            "Authorization: " + AUTHORIZATION,
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    @PATCH("user")
    Call<List<User>> approveUser(
            @Query("email") String email,
            @Body Map<String, Boolean> body
    );

    // Delete user
    @Headers({
            "apikey: " + API_KEY,
            "Authorization: " + AUTHORIZATION,
            "Accept: application/json"
    })
    @DELETE("user")
    Call<Void> deleteUser(@Query("email") String email);

    // Add student
    @Headers({
            "apikey: " + API_KEY,
            "Authorization: " + AUTHORIZATION,
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    @POST("student")
    Call<Object> createStudent(@Body Map<String, Object> student);

    // Add teacher
    @Headers({
            "apikey: " + API_KEY,
            "Authorization: " + AUTHORIZATION,
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    @POST("teacher")
    Call<Object> createTeacher(@Body Map<String, Object> teacher);


    @GET("user")
    Call<List<User>> getVerifiedUsersByRole(
            @Query("role") String role,
            @Query("is_verified") boolean isVerified
    );

   // @DELETE("user")
    //Call<Void> deleteUserByEmail(@Query("email") String email);

    // 🔄 [UPDATED] Get all users for role filtering in client code
    @Headers({
            "apikey: " + API_KEY,
            "Authorization: " + AUTHORIZATION,
            "Accept: application/json"
    })
    @GET("user")
    Call<List<User>> getAllUsers();


    // ... your other definitions ...

    // 🔄 [UPDATED] Delete user using Supabase-style filter by user ID
    @Headers({
            "apikey: " + API_KEY,
            "Authorization: " + AUTHORIZATION,
            "Accept: application/json"
    })
    @DELETE("user")
    Call<Void> deleteUserById(@Query("id") Long id); // ✅ sends as number



}




