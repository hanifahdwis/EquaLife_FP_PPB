package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DietActivity extends AppCompatActivity {
    Button btnOption1, btnOption2, btnOption3, btnBack;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);
        db = new DatabaseHelper(this); // Inisialisasi DB

        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnBack = findViewById(R.id.btnBack);

        View.OnClickListener goNext = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Ambil SEMUA data
                Intent previousIntent = getIntent();
                String email = previousIntent.getStringExtra("USER_EMAIL");
                String name = previousIntent.getStringExtra("USER_NAME");
                String ageRange = previousIntent.getStringExtra("USER_AGE_RANGE");
                String height = previousIntent.getStringExtra("USER_HEIGHT");
                String weight = previousIntent.getStringExtra("USER_WEIGHT");
                String dietPref = ((Button) v).getText().toString(); // <-- Data diet

                // 2. SIMPAN data ke DB
                boolean updated = db.updateOnboardingData(email, name, ageRange, height, weight, dietPref);
                if (updated) {
                    Toast.makeText(DietActivity.this, "Profile saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DietActivity.this, "Failed to save profile.", Toast.LENGTH_SHORT).show();
                }

                // 3. Pindah ke FinishActivity
                Intent intent = new Intent(DietActivity.this, FinishActivity.class);
                intent.putExtra("USER_EMAIL", email); // Oper email
                startActivity(intent);

                // 4. TUTUP activity ini
                finish();
            }
        };

        btnOption1.setOnClickListener(goNext);
        btnOption2.setOnClickListener(goNext);
        btnOption3.setOnClickListener(goNext);

        btnBack.setOnClickListener(v -> finish());
    }
}