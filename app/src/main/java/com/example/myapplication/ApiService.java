package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/chat/completions")
    Call<ApiResponse> getChatCompletion(
            @Body ApiRequest requestBody,
            @Header("Authorization") String authToken
    );
}