package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NameActivity extends AppCompatActivity {
    EditText etName;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        etName = findViewById(R.id.etName);
        btnNext = findViewById(R.id.btnNext);
        String email = getIntent().getStringExtra("USER_EMAIL");

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                Intent intent = new Intent(NameActivity.this, AgeActivity.class);
                intent.putExtra("USER_NAME", name);
                intent.putExtra("USER_EMAIL", email);
                startActivity(intent);
            }
        });
    }
}
