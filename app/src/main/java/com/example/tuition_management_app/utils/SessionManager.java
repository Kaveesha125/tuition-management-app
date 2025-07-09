package com.example.tuition_management_app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager (Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(long userId, String name, String email, String role) {
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    // for testing purposes change to private
    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }
    public String getName() {
        return prefs.getString(KEY_NAME, null);
    }
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }
    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }
    public void clearSession() {
        editor.clear();
        editor.apply();
    }


}
