package com.example.projet_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.projet_android.utils.PreferencesManager;

public class SplashActivity extends AppCompatActivity {
    
    private static final int SPLASH_DURATION = 3000; // 3 secondes
      private TextView appNameTextView;
    private TextView loadingTextView;
    
    private PreferencesManager preferencesManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        preferencesManager = new PreferencesManager(this);
        
        initViews();
        startAnimations();
        navigateAfterDelay();
    }
      private void initViews() {
        appNameTextView = findViewById(R.id.tv_splash_app_name);
        loadingTextView = findViewById(R.id.tv_splash_loading);
    }
      private void startAnimations() {
        // Animation slide up pour le titre
        Animation slideUpTitle = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        if (appNameTextView != null) {
            appNameTextView.startAnimation(slideUpTitle);
        }
        
        // Animation fade in avec délai pour le texte de chargement
        Animation fadeInLoading = AnimationUtils.loadAnimation(this, R.anim.fade_in_delayed);
        loadingTextView.startAnimation(fadeInLoading);
    }
    
    private void navigateAfterDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            
            // Vérifier si l'utilisateur est connecté
            if (preferencesManager.isUserLoggedIn()) {
                // Aller directement au dashboard
                intent = new Intent(SplashActivity.this, MainActivity2.class);
            } else {
                // Aller à l'écran de login
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            
            startActivity(intent);
            finish();
            
            // Animation de transition
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }, SPLASH_DURATION);
    }
}
