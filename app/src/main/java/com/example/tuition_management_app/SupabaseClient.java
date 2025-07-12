package com.example.tuition_management_app;


import java.util.Map;

import okhttp3.*;

public class SupabaseClient {
    // Replace these with your actual project credentials
    private static final String SUPABASE_URL = "https://vdrphijlvresyudxgfrj.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZkcnBoaWpsdnJlc3l1ZHhnZnJqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE4Njc3NzYsImV4cCI6MjA2NzQ0Mzc3Nn0.HYCsGy7U5la9oG68t7msPkDvFZPUpIevVgPlNNjZa5w";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient();

    // === SELECT ===
    public static void select(String table, Map<String, String> filters, Callback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SUPABASE_URL + "/rest/v1/" + table).newBuilder();

        // Handle the 'select' parameter separately to allow overriding the default '*'
        if (filters != null && filters.containsKey("select")) {
            urlBuilder.addQueryParameter("select", filters.get("select"));
        } else {
            urlBuilder.addQueryParameter("select", "*");
        }

        if (filters != null && !filters.isEmpty()) {
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                // Avoid adding 'select' again as it's already handled
                if (!entry.getKey().equals("select")) {
                    urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
        }

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    // === INSERT ===
    public static void insert(String table, String jsonBody, Callback callback) {
        RequestBody body = RequestBody.create(JSON, jsonBody);

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/" + table)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    // === UPDATE ===
    public static void update(String table, String filterQuery, String jsonBody, Callback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SUPABASE_URL + "/rest/v1/" + table).newBuilder();

        // Add filters like id=eq.5
        if (filterQuery != null && !filterQuery.isEmpty()) {
            String[] filters = filterQuery.split("&");
            for (String f : filters) {
                String[] kv = f.split("=");
                urlBuilder.addQueryParameter(kv[0], kv[1]);
            }
        }

        RequestBody body = RequestBody.create(JSON, jsonBody);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .patch(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    // === DELETE ===
    public static void delete(String table, String filterQuery, Callback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SUPABASE_URL + "/rest/v1/" + table).newBuilder();

        if (filterQuery != null && !filterQuery.isEmpty()) {
            String[] filters = filterQuery.split("&");
            for (String f : filters) {
                String[] kv = f.split("=");
                urlBuilder.addQueryParameter(kv[0], kv[1]);
            }
        }

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .delete()
                .build();

        client.newCall(request).enqueue(callback);
    }
}