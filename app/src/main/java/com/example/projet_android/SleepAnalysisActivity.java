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
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.example.projet_android.utils.PreferencesManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SleepAnalysisActivity extends AppCompatActivity {
    private TextView tvSleepHours, tvSleepQuality, tvBedtime, tvWakeTime;
    private TextView tvSleepScore, tvLastNight, tvWeeklyAverage, tvSleepGoal;
    private ProgressBar progressSleepScore, progressWeeklyGoal;
    private ImageView imgSleepStatus, btnSetBedtime, btnSetWakeTime;
    private Button btnLogSleep, btnSleepTips, btnAnalytics;
    private CardView cardSleepSummary, cardSleepSchedule, cardSleepTips, cardSleepStats;
    
    private PreferencesManager preferencesManager;
    private double lastNightSleep = 0.0;
    private double weeklyAverage = 0.0;
    private int sleepScore = 0;
    private int sleepGoal = 8; // Default 8 hours

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_analysis);
        
        preferencesManager = new PreferencesManager(this);
        
        initializeViews();
        setupClickListeners();
        loadData();
        updateUI();
        
        // Set up toolbar/action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sleep Analysis");
        }
    }
    
    private void initializeViews() {
        tvSleepHours = findViewById(R.id.tvSleepHours);
        tvSleepQuality = findViewById(R.id.tvSleepQuality);
        tvBedtime = findViewById(R.id.tvBedtime);
        tvWakeTime = findViewById(R.id.tvWakeTime);
        tvSleepScore = findViewById(R.id.tvSleepScore);
        tvLastNight = findViewById(R.id.tvLastNight);
        tvWeeklyAverage = findViewById(R.id.tvWeeklyAverage);
        tvSleepGoal = findViewById(R.id.tvSleepGoal);
        progressSleepScore = findViewById(R.id.progressSleepScore);
        progressWeeklyGoal = findViewById(R.id.progressWeeklyGoal);
        imgSleepStatus = findViewById(R.id.imgSleepStatus);
        btnSetBedtime = findViewById(R.id.btnSetBedtime);
        btnSetWakeTime = findViewById(R.id.btnSetWakeTime);
        btnLogSleep = findViewById(R.id.btnLogSleep);
        btnSleepTips = findViewById(R.id.btnSleepTips);
        btnAnalytics = findViewById(R.id.btnAnalytics);
        cardSleepSummary = findViewById(R.id.cardSleepSummary);
        cardSleepSchedule = findViewById(R.id.cardSleepSchedule);
        cardSleepTips = findViewById(R.id.cardSleepTips);
        cardSleepStats = findViewById(R.id.cardSleepStats);
    }
    
    private void setupClickListeners() {
        btnSetBedtime.setOnClickListener(v -> showTimePickerDialog(true));
        btnSetWakeTime.setOnClickListener(v -> showTimePickerDialog(false));
        btnLogSleep.setOnClickListener(v -> showSleepLogDialog());
        btnSleepTips.setOnClickListener(v -> showSleepTips());
        btnAnalytics.setOnClickListener(v -> showSleepAnalytics());
        cardSleepTips.setOnClickListener(v -> showSleepTips());
        cardSleepStats.setOnClickListener(v -> showSleepAnalytics());
    }
    
    private void loadData() {
        lastNightSleep = preferencesManager.getSleepHours();
        sleepGoal = preferencesManager.getSleepGoal();
        weeklyAverage = preferencesManager.getWeeklySleepAverage();
        
        // Calculate sleep score based on various factors
        calculateSleepScore();
        
        // Set default goal if not set
        if (sleepGoal == 0) {
            sleepGoal = 8;
            preferencesManager.setSleepGoal(sleepGoal);
        }
    }
    
    private void calculateSleepScore() {
        // Sleep score calculation based on:
        // - Hours of sleep (0-40 points)
        // - Consistency (0-30 points) 
        // - Sleep schedule regularity (0-30 points)
        
        int hoursScore = 0;
        if (lastNightSleep >= 7 && lastNightSleep <= 9) {
            hoursScore = 40; // Optimal range
        } else if (lastNightSleep >= 6 && lastNightSleep <= 10) {
            hoursScore = 30; // Good range
        } else if (lastNightSleep >= 5 && lastNightSleep <= 11) {
            hoursScore = 20; // Acceptable range
        } else {
            hoursScore = 10; // Poor sleep
        }
        
        // Consistency score (simplified - in real app would track over time)
        int consistencyScore = weeklyAverage > 0 ? 25 : 15;
        
        // Schedule score (simplified)
        int scheduleScore = 25;
        
        sleepScore = hoursScore + consistencyScore + scheduleScore;
        sleepScore = Math.min(100, sleepScore); // Cap at 100
    }
    
    private void updateUI() {
        // Update sleep hours display
        if (lastNightSleep > 0) {
            tvSleepHours.setText(String.format("%.1fh", lastNightSleep));
            tvLastNight.setText(String.format("Last night: %.1f hours", lastNightSleep));
        } else {
            tvSleepHours.setText("--");
            tvLastNight.setText("No sleep data");
        }
        
        // Update sleep quality and score
        tvSleepScore.setText(sleepScore + "/100");
        animateProgress(progressSleepScore, sleepScore);
        
        // Update sleep quality text and icon
        updateSleepQualityDisplay();
        
        // Update weekly average
        if (weeklyAverage > 0) {
            tvWeeklyAverage.setText(String.format("Weekly avg: %.1fh", weeklyAverage));
            int weeklyGoalProgress = (int)((weeklyAverage / sleepGoal) * 100);
            animateProgress(progressWeeklyGoal, Math.min(100, weeklyGoalProgress));
        } else {
            tvWeeklyAverage.setText("Weekly avg: --");
            progressWeeklyGoal.setProgress(0);
        }
        
        // Update sleep goal
        tvSleepGoal.setText("Goal: " + sleepGoal + " hours");
        
        // Update bedtime and wake time from preferences
        updateScheduleDisplay();
    }
    
    private void updateSleepQualityDisplay() {
        String quality;
        int colorRes;
        int iconRes;
        
        if (sleepScore >= 80) {
            quality = "Excellent";
            colorRes = R.color.sleep_excellent;
            iconRes = R.drawable.ic_sleep_excellent;
        } else if (sleepScore >= 60) {
            quality = "Good";
            colorRes = R.color.sleep_good;
            iconRes = R.drawable.ic_sleep_good;
        } else if (sleepScore >= 40) {
            quality = "Fair";
            colorRes = R.color.sleep_fair;
            iconRes = R.drawable.ic_sleep_fair;
        } else {
            quality = "Poor";
            colorRes = R.color.sleep_poor;
            iconRes = R.drawable.ic_sleep_poor;
        }
        
        tvSleepQuality.setText(quality);
        tvSleepQuality.setTextColor(ContextCompat.getColor(this, colorRes));
        imgSleepStatus.setImageResource(iconRes);
        imgSleepStatus.setColorFilter(ContextCompat.getColor(this, colorRes));
    }
    
    private void updateScheduleDisplay() {
        long bedtime = preferencesManager.getBedtime();
        long wakeTime = preferencesManager.getWakeTime();
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        
        if (bedtime > 0) {
            tvBedtime.setText(timeFormat.format(new Date(bedtime)));
        } else {
            tvBedtime.setText("Not set");
        }
        
        if (wakeTime > 0) {
            tvWakeTime.setText(timeFormat.format(new Date(wakeTime)));
        } else {
            tvWakeTime.setText("Not set");
        }
    }
    
    private void animateProgress(ProgressBar progressBar, int targetProgress) {
        ValueAnimator animator = ValueAnimator.ofInt(progressBar.getProgress(), targetProgress);
        animator.setDuration(1500);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int animatedValue = (Integer) animation.getAnimatedValue();
            progressBar.setProgress(animatedValue);
        });
        animator.start();
    }
    
    private void showTimePickerDialog(boolean isBedtime) {
        Calendar calendar = Calendar.getInstance();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isBedtime ? "Set Bedtime" : "Set Wake Time");
        
        TimePicker timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);
        
        // Set current time or saved time
        long savedTime = isBedtime ? preferencesManager.getBedtime() : preferencesManager.getWakeTime();
        if (savedTime > 0) {
            calendar.setTimeInMillis(savedTime);
            timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(calendar.get(Calendar.MINUTE));
        }
        
        builder.setView(timePicker);
        builder.setPositiveButton("Set", (dialog, which) -> {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
            
            if (isBedtime) {
                preferencesManager.setBedtime(calendar.getTimeInMillis());
                Toast.makeText(this, "Bedtime set for " + 
                    String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute()), 
                    Toast.LENGTH_SHORT).show();
            } else {
                preferencesManager.setWakeTime(calendar.getTimeInMillis());
                Toast.makeText(this, "Wake time set for " + 
                    String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute()), 
                    Toast.LENGTH_SHORT).show();
            }
            
            updateScheduleDisplay();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showSleepLogDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log Sleep Hours");
        
        // Create simple number input for sleep hours
        final String[] sleepOptions = {"4", "5", "5.5", "6", "6.5", "7", "7.5", "8", "8.5", "9", "9.5", "10", "11", "12"};
        
        builder.setItems(sleepOptions, (dialog, which) -> {
            double sleepHours = Double.parseDouble(sleepOptions[which]);
            preferencesManager.setSleepHours(sleepHours);
            
            // Update weekly average (simplified calculation)
            double newWeeklyAvg = (weeklyAverage * 6 + sleepHours) / 7;
            preferencesManager.setWeeklySleepAverage(newWeeklyAvg);
            
            // Add wellness points
            int points = (int)(sleepHours * 5); // 5 points per hour
            preferencesManager.addWellnessPoints(points);
            
            Toast.makeText(this, String.format("Logged %.1f hours of sleep! +%d points", 
                sleepHours, points), Toast.LENGTH_SHORT).show();
            
            loadData();
            updateUI();
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showSleepTips() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sleep Tips 😴");
        builder.setMessage(
            "🌙 Maintain a consistent sleep schedule\n\n" +
            "📱 Avoid screens 1 hour before bed\n\n" +
            "🌡️ Keep your bedroom cool (60-67°F)\n\n" +
            "☕ Avoid caffeine after 2 PM\n\n" +
            "🧘 Practice relaxation techniques\n\n" +
            "💡 Use dim lighting in the evening\n\n" +
            "🛏️ Create a comfortable sleep environment"
        );
        builder.setPositiveButton("Got it!", null);
        builder.show();
    }
    
    private void showSleepAnalytics() {
        Toast.makeText(this, "Detailed sleep analytics coming soon!", Toast.LENGTH_SHORT).show();
        // TODO: Implement detailed analytics activity
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
}
