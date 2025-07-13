package com.example.tuition_management_app.model;

public class User {
    public String id;

    public String name;
    public String email;
    public String password;
    public String role;
    public boolean is_verified;

    public User(String name, String email, String password, String role, boolean is_verified) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.is_verified = is_verified;
    }

    // Existing getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public boolean getisverified() {
        return is_verified;
    }


    public String getRole() {
        return role;
    }

    public boolean isVerified() {
        return is_verified;
    }
}
