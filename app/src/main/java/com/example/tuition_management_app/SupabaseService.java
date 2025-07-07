package com.example.tuitionapp.utils;

import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class SupabaseAuthService {
    private final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "https://vdrphijlvresyudxgfrj.supabase.co";
    private static final String API_KEY = "YOUR_SUPABASE_API_KEY";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public void login(String email, String password, Callback callback) {
        try {
            JSONObject bodyJson = new JSONObject();
            bodyJson.put("email", email);
            bodyJson.put("password", password);

            RequestBody body = RequestBody.create(bodyJson.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/auth/v1/token?grant_type=password")
                    .addHeader("apikey", API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
