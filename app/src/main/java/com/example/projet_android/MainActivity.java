package com.example.projet_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    
    private TextView appTitleTextView;
    private TextView appDescriptionTextView;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        appTitleTextView = findViewById(R.id.tv_app_title);
        appDescriptionTextView = findViewById(R.id.tv_app_description);
        startButton = findViewById(R.id.btn_start_app);
    }
    
    private void setupClickListeners() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naviguer vers l'écran principal de l'application
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
                finish(); // Fermer l'écran de démarrage
            }
        });
    }
}