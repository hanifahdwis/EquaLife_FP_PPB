package com.example.myapplication;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // 1. GANTI DENGAN API KEY ANDA
    // (Harus diawali "Bearer ")
    public static final String API_KEY = "Bearer YOUR_API_KEY_HERE";

    // 2. Base URL dari API Senopati
    private static final String BASE_URL = "https://chat.ragita.net/";

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}