package com.example.projet_android;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.example.projet_android.utils.PreferencesManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HydrationTrackerActivity extends AppCompatActivity {
    private TextView tvWaterIntake, tvGoalProgress, tvLastDrank, tvTodayIntake;
    private ProgressBar progressHydration;
    private ImageView btnAddWater, btnHistory, imgWaterBottle;
    private Button btnQuickAdd250, btnQuickAdd500, btnQuickAdd1000;
    private CardView cardHydrationLevel, cardTips, cardStats;
    
    private PreferencesManager preferencesManager;
    private int currentIntake = 0;
    private int dailyGoal = 2500; // Default 2.5 liters in ml
    
    // Quick add amounts in ml
    private final int QUICK_ADD_250 = 250;
    private final int QUICK_ADD_500 = 500;
    private final int QUICK_ADD_1000 = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hydration_tracker);
        
        preferencesManager = new PreferencesManager(this);
        
        initializeViews();
        setupClickListeners();
        loadData();
        updateUI();
        
        // Set up toolbar/action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Hydration Tracker");
        }
    }
    
    private void initializeViews() {
        tvWaterIntake = findViewById(R.id.tvWaterIntake);
        tvGoalProgress = findViewById(R.id.tvGoalProgress);
        tvLastDrank = findViewById(R.id.tvLastDrank);
        tvTodayIntake = findViewById(R.id.tvTodayIntake);
        progressHydration = findViewById(R.id.progressHydration);
        btnAddWater = findViewById(R.id.btnAddWater);
        btnHistory = findViewById(R.id.btnHistory);
        imgWaterBottle = findViewById(R.id.imgWaterBottle);
        btnQuickAdd250 = findViewById(R.id.btnQuickAdd250);
        btnQuickAdd500 = findViewById(R.id.btnQuickAdd500);
        btnQuickAdd1000 = findViewById(R.id.btnQuickAdd1000);
        cardHydrationLevel = findViewById(R.id.cardHydrationLevel);
        cardTips = findViewById(R.id.cardTips);
        cardStats = findViewById(R.id.cardStats);
    }
    
    private void setupClickListeners() {
        btnAddWater.setOnClickListener(v -> showCustomWaterDialog());
        
        btnQuickAdd250.setOnClickListener(v -> addWaterIntake(QUICK_ADD_250));
        btnQuickAdd500.setOnClickListener(v -> addWaterIntake(QUICK_ADD_500));
        btnQuickAdd1000.setOnClickListener(v -> addWaterIntake(QUICK_ADD_1000));
        
        btnHistory.setOnClickListener(v -> showHydrationHistory());
        
        cardTips.setOnClickListener(v -> showHydrationTips());
    }
    
    private void loadData() {
        currentIntake = preferencesManager.getWaterIntake();
        dailyGoal = preferencesManager.getDailyWaterGoal();
        
        // Get daily goal from preferences if set, otherwise use default
        if (dailyGoal == 0) {
            dailyGoal = 2500;
            preferencesManager.setDailyWaterGoal(dailyGoal);
        }
    }
    
    private void updateUI() {
        // Update water intake display
        tvWaterIntake.setText(String.format("%.1f L", currentIntake / 1000.0));
        
        // Calculate progress
        int progressPercent = Math.min(100, (currentIntake * 100) / dailyGoal);
        tvGoalProgress.setText(progressPercent + "%");
        
        // Update progress bar with animation
        animateProgress(progressPercent);
        
        // Update today's intake
        tvTodayIntake.setText(String.format("Today: %d/%d ml", currentIntake, dailyGoal));
        
        // Update last drink time
        long lastDrinkTime = preferencesManager.getLastWaterIntakeTime();
        if (lastDrinkTime > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            tvLastDrank.setText("Last: " + sdf.format(new Date(lastDrinkTime)));
        } else {
            tvLastDrank.setText("No drinks today");
        }
        
        // Update bottle color based on hydration level
        updateWaterBottleColor(progressPercent);
        
        // Update hydration level card
        updateHydrationLevelCard(progressPercent);
    }
    
    private void animateProgress(int targetProgress) {
        ValueAnimator animator = ValueAnimator.ofInt(progressHydration.getProgress(), targetProgress);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int animatedValue = (Integer) animation.getAnimatedValue();
            progressHydration.setProgress(animatedValue);
        });
        animator.start();
    }
    
    private void updateWaterBottleColor(int progressPercent) {
        if (progressPercent >= 100) {
            imgWaterBottle.setColorFilter(ContextCompat.getColor(this, R.color.hydration_excellent));
        } else if (progressPercent >= 75) {
            imgWaterBottle.setColorFilter(ContextCompat.getColor(this, R.color.hydration_good));
        } else if (progressPercent >= 50) {
            imgWaterBottle.setColorFilter(ContextCompat.getColor(this, R.color.hydration_okay));
        } else {
            imgWaterBottle.setColorFilter(ContextCompat.getColor(this, R.color.hydration_low));
        }
    }
    
    private void updateHydrationLevelCard(int progressPercent) {
        TextView tvHydrationStatus = cardHydrationLevel.findViewById(R.id.tvHydrationStatus);
        TextView tvHydrationDescription = cardHydrationLevel.findViewById(R.id.tvHydrationDescription);
        
        if (progressPercent >= 100) {
            tvHydrationStatus.setText("Excellent!");
            tvHydrationDescription.setText("You've reached your daily hydration goal! Keep it up!");
            cardHydrationLevel.setCardBackgroundColor(ContextCompat.getColor(this, R.color.hydration_excellent_bg));
        } else if (progressPercent >= 75) {
            tvHydrationStatus.setText("Good");
            tvHydrationDescription.setText("You're doing well! Just a bit more to reach your goal.");
            cardHydrationLevel.setCardBackgroundColor(ContextCompat.getColor(this, R.color.hydration_good_bg));
        } else if (progressPercent >= 50) {
            tvHydrationStatus.setText("Moderate");
            tvHydrationDescription.setText("Halfway there! Keep drinking water regularly.");
            cardHydrationLevel.setCardBackgroundColor(ContextCompat.getColor(this, R.color.hydration_okay_bg));
        } else {
            tvHydrationStatus.setText("Low");
            tvHydrationDescription.setText("You need to drink more water. Stay hydrated!");
            cardHydrationLevel.setCardBackgroundColor(ContextCompat.getColor(this, R.color.hydration_low_bg));
        }
    }
    
    private void addWaterIntake(int amount) {
        currentIntake += amount;
        preferencesManager.setWaterIntake(currentIntake);
        preferencesManager.setLastWaterIntakeTime(System.currentTimeMillis());
        
        // Add points for wellness system
        int points = amount / 50; // 1 point per 50ml
        preferencesManager.addWellnessPoints(points);
        
        updateUI();
        
        // Show toast with encouragement
        String message = String.format("Added %d ml! +%d points", amount, points);
        if (currentIntake >= dailyGoal) {
            message += " 🎉 Goal reached!";
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        
        // Animate the add button
        animateButton(findViewById(android.R.id.content));
    }
    
    private void animateButton(View view) {
        view.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction(() -> {
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100);
            });
    }
    
    private void showCustomWaterDialog() {
        // For now, just add a default amount
        // In a full implementation, show a dialog with custom amount input
        addWaterIntake(350); // Default glass of water
    }
    
    private void showHydrationHistory() {
        Toast.makeText(this, "Hydration history coming soon!", Toast.LENGTH_SHORT).show();
        // TODO: Implement history activity
    }
    
    private void showHydrationTips() {
        Intent intent = new Intent(this, HydrationTipsActivity.class);
        startActivity(intent);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        updateUI();
    }
    
    // Inner class for hydration tips activity
    public static class HydrationTipsActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Simple tips display - in a full app this would be a proper activity
            setTitle("Hydration Tips");
            Toast.makeText(this, "💧 Drink water before you feel thirsty\n" +
                    "🌅 Start your day with a glass of water\n" +
                    "🍎 Eat water-rich foods\n" +
                    "⏰ Set regular reminders", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
