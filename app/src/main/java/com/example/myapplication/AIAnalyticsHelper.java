package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AIAnalyticsHelper {

    private DatabaseHelper db;
    private String userEmail;
    private ApiService apiService;
    private Gson gson;

    public AIAnalyticsHelper(Context context, DatabaseHelper db, String userEmail) {
        this.db = db;
        this.userEmail = userEmail;
        this.apiService = RetrofitClient.getApiService();
        this.gson = new Gson();
    }

    // --- FUNGSI 1: ADJUST DIET ---
    public void adjustDiet(AIResponseListener listener) {
        UserProfile profile = db.getUserProfile(userEmail);
        List<Task> busySchedule = db.getTasksForUser(userEmail);

        if (profile == null) {
            listener.onError("User profile not found.");
            return;
        }

        String profileJson = gson.toJson(profile);
        String scheduleJson = gson.toJson(busySchedule);
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String systemPrompt = "Anda adalah asisten kesehatan. Tugas Anda adalah membuat jadwal makan " +
                "berdasarkan profil dan jadwal sibuk pengguna. " +
                "Anda HARUS membalas HANYA dengan JSON Array yang berisi objek Task. " +
                "Setiap objek Task harus memiliki key: 'taskName', 'date', 'startTime', dan 'endTime'. " +
                "Contoh: [{'taskName': 'Sarapan', 'date': '2025-11-02', 'startTime': '07:00', 'endTime': '07:30'}]";

        String userPrompt = "Tolong buatkan jadwal 'Sarapan', 'Makan Siang', dan 'Makan Malam' untuk saya hari ini, " +
                "tanggal " + todayDate + ". " +
                "Ini adalah profil saya: " + profileJson + ". " +
                "Ini adalah jadwal sibuk saya (JANGAN menimpa jadwal ini): " + scheduleJson + ". " +
                "Pastikan jadwal makan tidak bentrok dengan jadwal sibuk saya.";

        // Panggil API
        callApi(systemPrompt, userPrompt, "Diet", listener);
    }

    // --- FUNGSI 2: ADJUST SLEEP (BARU) ---
    public void adjustSleep(AIResponseListener listener) {
        UserProfile profile = db.getUserProfile(userEmail);
        List<Task> busySchedule = db.getTasksForUser(userEmail);

        if (profile == null) {
            listener.onError("User profile not found.");
            return;
        }

        String profileJson = gson.toJson(profile);
        String scheduleJson = gson.toJson(busySchedule);
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String systemPrompt = "Anda adalah asisten kesehatan. Tugas Anda adalah membuat SATU jadwal tidur ideal " +
                "berdasarkan profil usia dan jadwal sibuk pengguna. " +
                "Anda HARUS membalas HANYA dengan JSON Array yang berisi SATU objek Task. " +
                "Objek Task harus memiliki key: 'taskName', 'date', 'startTime', dan 'endTime'. " +
                "Contoh: [{'taskName': 'Waktu Tidur', 'date': '2025-11-02', 'startTime': '22:00', 'endTime': '06:00'}]";

        String userPrompt = "Tolong buatkan jadwal tidur 8 jam untuk saya malam ini, " +
                "tanggal " + todayDate + ". " +
                "Cari slot waktu 8 jam yang paling ideal antara jam 21:00 dan 07:00 keesokan harinya. " +
                "Ini adalah profil saya: " + profileJson + ". " +
                "Ini adalah jadwal sibuk saya (JANGAN menimpa jadwal ini): " + scheduleJson + ". " +
                "Pastikan jadwal tidur tidak bentrok.";

        // Panggil API
        callApi(systemPrompt, userPrompt, "Sleep", listener);
    }

    // --- FUNGSI 3: ADJUST HYDRATION (BARU) ---
    public void adjustHydration(AIResponseListener listener) {
        UserProfile profile = db.getUserProfile(userEmail);
        List<Task> busySchedule = db.getTasksForUser(userEmail);

        if (profile == null) {
            listener.onError("User profile not found.");
            return;
        }

        String profileJson = gson.toJson(profile);
        String scheduleJson = gson.toJson(busySchedule);
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String systemPrompt = "Anda adalah asisten kesehatan. Tugas Anda adalah membuat BEBERAPA jadwal pengingat minum " +
                "sepanjang hari. " +
                "Anda HARUS membalas HANYA dengan JSON Array yang berisi objek-objek Task. " +
                "Setiap objek Task harus memiliki key: 'taskName', 'date', 'startTime', dan 'endTime'. " +
                "Contoh: [{'taskName': 'Minum Air', 'date': '2025-11-02', 'startTime': '08:00', 'endTime': '08:05'}]";

        String userPrompt = "Tolong buatkan 5 jadwal pengingat minum (durasi 5 menit) untuk saya hari ini, " +
                "tanggal " + todayDate + ", yang tersebar antara jam 08:00 dan 17:00. " +
                "Ini adalah profil saya: " + profileJson + ". " +
                "Ini adalah jadwal sibuk saya (JANGAN menimpa jadwal ini): " + scheduleJson + ". " +
                "Pastikan pengingat minum tidak bentrok.";

        // Panggil API
        callApi(systemPrompt, userPrompt, "Hydration", listener);
    }


    // --- FUNGSI UTAMA PEMANGGIL API (REUSABLE) ---
    private void callApi(String systemPrompt, String userPrompt, String logTag, AIResponseListener listener) {

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", systemPrompt));
        messages.add(new Message("user", userPrompt));

        ApiRequest request = new ApiRequest("qwen2.5:14b", messages);

        apiService.getChatCompletion(request, RetrofitClient.API_KEY).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().choices.isEmpty()) {

                    String aiResponseContent = response.body().choices.get(0).message.content;

                    try {
                        // AI membalas JSON Array (List<Task>), kita parse
                        Type taskListType = new TypeToken<ArrayList<Task>>(){}.getType();
                        List<Task> aiTasks = gson.fromJson(aiResponseContent, taskListType);

                        if (aiTasks == null || aiTasks.isEmpty()) {
                            listener.onError("AI tidak memberikan jadwal.");
                            return;
                        }

                        // Simpan setiap tugas dari AI ke database
                        for (Task task : aiTasks) {
                            db.insertTask(userEmail,
                                    "AI: " + task.getTaskName(), // Tambah prefix "AI:"
                                    task.getDate(),
                                    task.getStartTime(),
                                    task.getEndTime());
                        }
                        listener.onSuccess(); // Berhasil!

                    } catch (Exception e) {
                        Log.e("AIAnalyticsHelper", "Error parsing AI JSON (" + logTag + "): " + aiResponseContent, e);
                        listener.onError("AI memberikan balasan, tapi formatnya salah.");
                    }
                } else {
                    Log.e("AIAnalyticsHelper", "API Response Error (" + logTag + "): " + response.message());
                    listener.onError("Gagal mendapat balasan dari server AI.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("AIAnalyticsHelper", "Network Error (" + logTag + ")", t);
                listener.onError("Error Jaringan: " + t.getMessage());
            }
        });
    }
}