package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    EditText etTaskName;
    Button btnSelectDate, btnSelectStartTime, btnSelectEndTime, btnSaveTask;
    DatabaseHelper db; // (BARU)
    String userEmail;  // (BARU)

    private int selectedYear, selectedMonth, selectedDay, selectedStartHour, selectedStartMinute, selectedEndHour, selectedEndMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        db = new DatabaseHelper(this); // (BARU)
        userEmail = getIntent().getStringExtra("USER_EMAIL"); // (BARU) Ambil email

        etTaskName = findViewById(R.id.etTaskName);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectStartTime = findViewById(R.id.btnSelectStartTime);
        btnSelectEndTime = findViewById(R.id.btnSelectEndTime);
        btnSaveTask = findViewById(R.id.btnSaveTask);

        // Ambil waktu saat ini sebagai default
        Calendar c = Calendar.getInstance();
        selectedYear = c.get(Calendar.YEAR);
        selectedMonth = c.get(Calendar.MONTH);
        selectedDay = c.get(Calendar.DAY_OF_MONTH);
        selectedStartHour = c.get(Calendar.HOUR_OF_DAY);
        selectedStartMinute = c.get(Calendar.MINUTE);
        selectedEndHour = selectedStartHour + 1;
        selectedEndMinute = selectedStartMinute;

        // Set teks tombol default
        btnSelectDate.setText(String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay));
        btnSelectStartTime.setText(String.format("%02d:%02d", selectedStartHour, selectedStartMinute));
        btnSelectEndTime.setText(String.format("%02d:%02d", selectedEndHour, selectedEndMinute));

        // --- (Listener untuk Tombol Tanggal, Jam Mulai, Jam Selesai tetap sama) ---
        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        selectedYear = year;
                        selectedMonth = month;
                        selectedDay = dayOfMonth;
                        btnSelectDate.setText(String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay));
                    }, selectedYear, selectedMonth, selectedDay);
            datePickerDialog.show();
        });
        btnSelectStartTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        selectedStartHour = hourOfDay;
                        selectedStartMinute = minute;
                        btnSelectStartTime.setText(String.format("%02d:%02d", selectedStartHour, selectedStartMinute));
                    }, selectedStartHour, selectedStartMinute, true);
            timePickerDialog.show();
        });
        btnSelectEndTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        selectedEndHour = hourOfDay;
                        selectedEndMinute = minute;
                        btnSelectEndTime.setText(String.format("%02d:%02d", selectedEndHour, selectedEndMinute));
                    }, selectedEndHour, selectedEndMinute, true);
            timePickerDialog.show();
        });

        // --- Listener untuk Tombol Simpan (DIMODIFIKASI) ---
        btnSaveTask.setOnClickListener(v -> {
            String taskName = etTaskName.getText().toString().trim();
            if (taskName.isEmpty()) {
                Toast.makeText(this, "Please enter a task name", Toast.LENGTH_SHORT).show();
                return;
            }

            if(userEmail == null || userEmail.isEmpty()){
                Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Format data untuk disimpan
            String dateStr = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
            String startTimeStr = String.format("%02d:%02d", selectedStartHour, selectedStartMinute);
            String endTimeStr = String.format("%02d:%02d", selectedEndHour, selectedEndMinute);

            // Simpan ke database
            boolean inserted = db.insertTask(userEmail, taskName, dateStr, startTimeStr, endTimeStr);

            if (inserted) {
                Toast.makeText(this, "Task Saved!", Toast.LENGTH_SHORT).show();
                finish(); // Tutup AddTaskActivity dan kembali ke HomeActivity
            } else {
                Toast.makeText(this, "Failed to save task", Toast.LENGTH_SHORT).show();
            }
        });
    }
}