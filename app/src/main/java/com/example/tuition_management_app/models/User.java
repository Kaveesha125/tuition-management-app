package com.example.tuition_management_app.models;

public class User {
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
}
