package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.util.Log;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

// Import untuk RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

// Import untuk Dialog Konfirmasi
import android.app.AlertDialog;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    TextView tvWelcome;
    Button btnAdjustDiet, btnAdjustSleep, btnAdjustHydration;
    FloatingActionButton fabAddTask;
    DatabaseHelper db;
    String userEmail;

    // AI Helper
    AIAnalyticsHelper aiHelper;

    // RecyclerView Components
    RecyclerView rvCalendarAgenda;
    TaskAdapter taskAdapter;
    List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "=== onCreate started ===");

        try {
            setContentView(R.layout.activity_home);
            Log.d(TAG, "Layout set successfully");

            // Inisialisasi Database
            db = new DatabaseHelper(this);
            Log.d(TAG, "Database initialized");

            // Ambil email dari intent
            userEmail = getIntent().getStringExtra("USER_EMAIL");
            Log.d(TAG, "User email received: " + userEmail);

            // Validasi email
            if (userEmail == null || userEmail.isEmpty()) {
                Log.e(TAG, "ERROR: Email is null or empty!");
                Toast.makeText(this, "Error: Email tidak ditemukan. Silakan login kembali.", Toast.LENGTH_LONG).show();
                // Redirect ke Login
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            // Inisialisasi Views
            try {
                tvWelcome = findViewById(R.id.tvWelcome);
                btnAdjustDiet = findViewById(R.id.btnAdjustDiet);
                btnAdjustSleep = findViewById(R.id.btnAdjustSleep);
                btnAdjustHydration = findViewById(R.id.btnAdjustHydration);
                fabAddTask = findViewById(R.id.fabAddTask);
                rvCalendarAgenda = findViewById(R.id.rvCalendarAgenda);
                Log.d(TAG, "All views found successfully");

                // SET WELCOME TEXT DENGAN NAMA USER
                if (tvWelcome != null && userEmail != null) {
                    try {
                        UserProfile profile = db.getUserProfile(userEmail);
                        if (profile != null && profile.getName() != null && !profile.getName().isEmpty()) {
                            tvWelcome.setText("Welcome, " + profile.getName() + "!");
                            Log.d(TAG, "Welcome text set with user name: " + profile.getName());
                        } else {
                            tvWelcome.setText("Welcome, " + userEmail + "!");
                            Log.d(TAG, "Welcome text set with email");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error getting user profile: " + e.getMessage());
                        tvWelcome.setText("Welcome, User!");
                    }
                }

                // Cek apakah ada view yang null
                if (btnAdjustDiet == null || btnAdjustSleep == null || btnAdjustHydration == null || fabAddTask == null || rvCalendarAgenda == null) {
                    Log.e(TAG, "ERROR: One or more views are null!");
                    Toast.makeText(this, "Error: Layout tidak lengkap", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error finding views: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "Error loading layout", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Setup RecyclerView
            try {
                taskList = new ArrayList<>();
                taskAdapter = new TaskAdapter(taskList);
                rvCalendarAgenda.setLayoutManager(new LinearLayoutManager(this));
                rvCalendarAgenda.setAdapter(taskAdapter);
                Log.d(TAG, "RecyclerView setup complete");
            } catch (Exception e) {
                Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage());
                e.printStackTrace();
                // Lanjutkan tanpa RecyclerView jika error
            }

            // Inisialisasi AI Helper (optional - skip jika error)
            try {
                aiHelper = new AIAnalyticsHelper(this, db, userEmail);
                Log.d(TAG, "AI Helper initialized successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error initializing AI Helper: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "AI features unavailable", Toast.LENGTH_SHORT).show();
                // Lanjutkan tanpa AI Helper
            }

            // Setup Button Listeners
            setupButtonListeners();

            // Load tasks pertama kali
            loadTasks();

            Log.d(TAG, "onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "FATAL ERROR in onCreate: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Fatal error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupButtonListeners() {
        // --- Listener Tombol AI ---

        // 1. Tombol Diet
        btnAdjustDiet.setOnClickListener(v -> {
            if (aiHelper == null) {
                Toast.makeText(this, "AI Helper tidak tersedia", Toast.LENGTH_SHORT).show();
                return;
            }

            showConfirmationDialog("Adjust Diet", () -> {
                Toast.makeText(this, "AI sedang menganalisis...", Toast.LENGTH_SHORT).show();

                // Panggil fungsi AI dengan callback
                aiHelper.adjustDiet(new AIResponseListener() {
                    @Override
                    public void onSuccess() {
                        loadTasks(); // Refresh list
                        Toast.makeText(HomeActivity.this, "Jadwal diet telah ditambahkan!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(HomeActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    }
                });
            });
        });

        // 2. Tombol Sleep
        btnAdjustSleep.setOnClickListener(v -> {
            if (aiHelper == null) {
                Toast.makeText(this, "AI Helper tidak tersedia", Toast.LENGTH_SHORT).show();
                return;
            }

            showConfirmationDialog("Adjust Sleep", () -> {
                Toast.makeText(this, "AI sedang menganalisis...", Toast.LENGTH_SHORT).show();

                // Panggil fungsi AI dengan callback
                aiHelper.adjustSleep(new AIResponseListener() {
                    @Override
                    public void onSuccess() {
                        loadTasks();
                        Toast.makeText(HomeActivity.this, "Jadwal tidur telah ditambahkan!", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onError(String message) {
                        Toast.makeText(HomeActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    }
                });
            });
        });

        // 3. Tombol Hydration
        btnAdjustHydration.setOnClickListener(v -> {
            if (aiHelper == null) {
                Toast.makeText(this, "AI Helper tidak tersedia", Toast.LENGTH_SHORT).show();
                return;
            }

            showConfirmationDialog("Adjust Hydration", () -> {
                Toast.makeText(this, "AI sedang menganalisis...", Toast.LENGTH_SHORT).show();

                // Panggil fungsi AI dengan callback
                aiHelper.adjustHydration(new AIResponseListener() {
                    @Override
                    public void onSuccess() {
                        loadTasks();
                        Toast.makeText(HomeActivity.this, "Pengingat hidrasi telah ditambahkan!", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onError(String message) {
                        Toast.makeText(HomeActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    }
                });
            });
        });

        // --- Listener Tombol Tambah Tugas (+) ---
        fabAddTask.setOnClickListener(v -> {
            Log.d(TAG, "FAB Add Task clicked");
            Intent intent = new Intent(HomeActivity.this, AddTaskActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });
    }

    // Fungsi untuk memuat tugas dari DB
    private void loadTasks() {
        try {
            if (userEmail != null && !userEmail.isEmpty()) {
                Log.d(TAG, "Loading tasks for user: " + userEmail);
                List<Task> tasks = db.getTasksForUser(userEmail);
                Log.d(TAG, "Tasks loaded: " + tasks.size());

                if (taskAdapter != null) {
                    taskAdapter.updateData(tasks);
                } else {
                    Log.e(TAG, "TaskAdapter is null, cannot update data");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading tasks: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error loading tasks", Toast.LENGTH_SHORT).show();
        }
    }

    // Dipanggil setiap kali Anda kembali ke HomeActivity
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        loadTasks(); // Muat ulang tugas setiap kali layar aktif
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }

    // Fungsi konfirmasi agar AI tidak langsung jalan
    private void showConfirmationDialog(String title, Runnable onConfirm) {
        try {
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage("Apakah Anda yakin ingin AI menganalisis dan menambahkan jadwal baru?")
                    .setPositiveButton("Ya, Jalankan", (dialog, which) -> onConfirm.run())
                    .setNegativeButton("Batal", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
}