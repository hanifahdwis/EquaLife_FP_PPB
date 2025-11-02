package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Ambil layout utama
        View rootLayout = findViewById(android.R.id.content);

        String email = getIntent().getStringExtra("USER_EMAIL");

        // Kalau layar di-tap di mana saja → pindah ke NameActivity
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, NameActivity.class);
                intent.putExtra("USER_EMAIL", email);
                startActivity(intent);
            }
        });
    }
}
