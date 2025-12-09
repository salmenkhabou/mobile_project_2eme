package com.example.projet_android;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.projet_android.utils.PreferencesManager;
import java.util.Calendar;

/**
 * Activité de suivi et d'analyse du sommeil
 * 
 * Cette activité permet aux utilisateurs de :
 * - Définir leurs horaires de coucher et de réveil
 * - Calculer automatiquement la durée de sommeil
 * - Évaluer la qualité du sommeil selon les recommandations médicales
 * - Enregistrer leurs données de sommeil quotidiennes
 * - Recevoir des conseils personnalisés pour améliorer le sommeil
 * - Gagner des points wellness pour un sommeil régulier et de qualité
 * 
 * Fonctionnalités d'analyse :
 * - Calcul précis de la durée avec gestion overnight (coucher le soir, réveil le matin)
 * - Évaluation de la qualité basée sur les recommandations (7-9h optimales)
 * - Conseils adaptatifs selon les habitudes de sommeil détectées
 * - Visualisation progressive avec barre de progression vers l'objectif 8h
 * - Historique des patterns de sommeil (à implémenter)
 * 
 * Intégration wellness :
 * - Attribution de points bonus pour sommeil optimal (7-9h)
 * - Suivi des streaks de bon sommeil
 * - Alertes douces pour rappeler l'importance du sommeil
 * - Intégration avec le score de santé global
 * 
 * @author Équipe de développement Health Tracker
 * @version 2.1
 * @since 2.1
 */
public class SleepTrackerActivity extends AppCompatActivity {
    
    private TextView tvSleepDuration, tvBedTime, tvWakeTime, tvSleepQuality, tvSleepTips;
    private ProgressBar progressSleep;
    private Button btnSetBedTime, btnSetWakeTime, btnLogSleep;
    private CardView cardSleepStats, cardSleepTips;
    
    private PreferencesManager preferencesManager;
    private int bedTimeHour = 22, bedTimeMinute = 0;
    private int wakeTimeHour = 7, wakeTimeMinute = 0;
    private float sleepDuration = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracker);
        
        preferencesManager = new PreferencesManager(this);
        
        initializeViews();
        loadData();
        updateUI();
        setupClickListeners();
        
        // Set up toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sleep Tracker");
        }
    }
    
    private void initializeViews() {
        tvSleepDuration = findViewById(R.id.tvSleepDuration);
        tvBedTime = findViewById(R.id.tvBedTime);
        tvWakeTime = findViewById(R.id.tvWakeTime);
        tvSleepQuality = findViewById(R.id.tvSleepQuality);
        tvSleepTips = findViewById(R.id.tvSleepTips);
        progressSleep = findViewById(R.id.progressSleep);
        btnSetBedTime = findViewById(R.id.btnSetBedTime);
        btnSetWakeTime = findViewById(R.id.btnSetWakeTime);
        btnLogSleep = findViewById(R.id.btnLogSleep);
        cardSleepStats = findViewById(R.id.cardSleepStats);
        cardSleepTips = findViewById(R.id.cardSleepTips);
    }
    
    private void loadData() {
        // Load saved sleep times
        bedTimeHour = preferencesManager.getSharedPreferences().getInt("bed_time_hour", 22);
        bedTimeMinute = preferencesManager.getSharedPreferences().getInt("bed_time_minute", 0);
        wakeTimeHour = preferencesManager.getSharedPreferences().getInt("wake_time_hour", 7);
        wakeTimeMinute = preferencesManager.getSharedPreferences().getInt("wake_time_minute", 0);
        
        calculateSleepDuration();
    }
    
    private void calculateSleepDuration() {
        // Calculate sleep duration
        int bedTimeInMinutes = bedTimeHour * 60 + bedTimeMinute;
        int wakeTimeInMinutes = wakeTimeHour * 60 + wakeTimeMinute;
        
        // Handle overnight sleep (wake time is next day)
        if (wakeTimeInMinutes <= bedTimeInMinutes) {
            wakeTimeInMinutes += 24 * 60; // Add 24 hours
        }
        
        int sleepMinutes = wakeTimeInMinutes - bedTimeInMinutes;
        sleepDuration = sleepMinutes / 60.0f;
    }
    
    private void updateUI() {
        // Update sleep times
        tvBedTime.setText(String.format("Bedtime: %02d:%02d", bedTimeHour, bedTimeMinute));
        tvWakeTime.setText(String.format("Wake time: %02d:%02d", wakeTimeHour, wakeTimeMinute));
        
        // Update sleep duration
        int hours = (int) sleepDuration;
        int minutes = (int) ((sleepDuration - hours) * 60);
        tvSleepDuration.setText(String.format("Duration: %dh %02dm", hours, minutes));
        
        // Update progress (goal: 8 hours)
        int progress = (int) (sleepDuration / 8.0 * 100);
        progressSleep.setProgress(Math.min(progress, 100));
        
        // Update sleep quality assessment
        updateSleepQuality();
        
        // Update tips
        updateSleepTips();
    }
    
    private void updateSleepQuality() {
        String quality;
        String emoji;
        
        if (sleepDuration >= 7 && sleepDuration <= 9) {
            quality = "Excellent";
            emoji = "😴✨";
        } else if (sleepDuration >= 6 && sleepDuration < 7) {
            quality = "Good";
            emoji = "😊";
        } else if (sleepDuration >= 5 && sleepDuration < 6) {
            quality = "Fair";
            emoji = "😐";
        } else {
            quality = "Poor";
            emoji = "😴";
        }
        
        tvSleepQuality.setText(String.format("Quality: %s %s", quality, emoji));
    }
    
    private void updateSleepTips() {
        String tips = "🌙 Sleep Better Tonight!\n\n";
        
        if (sleepDuration < 6) {
            tips += "• You need more sleep for better health\n";
            tips += "• Try going to bed 1 hour earlier\n";
            tips += "• Avoid screens before bedtime\n";
            tips += "• Create a relaxing bedtime routine";
        } else if (sleepDuration >= 6 && sleepDuration < 7) {
            tips += "• Close to optimal! Try for 7-8 hours\n";
            tips += "• Keep consistent sleep schedule\n";
            tips += "• Avoid caffeine after 2 PM\n";
            tips += "• Make your bedroom cool and dark";
        } else if (sleepDuration >= 7 && sleepDuration <= 9) {
            tips += "• Perfect sleep duration! ✅\n";
            tips += "• Maintain this healthy schedule\n";
            tips += "• Regular exercise helps sleep quality\n";
            tips += "• You're doing great!";
        } else {
            tips += "• That's quite a lot of sleep!\n";
            tips += "• Quality matters more than quantity\n";
            tips += "• Consider if you're getting restful sleep\n";
            tips += "• Consistent timing is important";
        }
        
        tvSleepTips.setText(tips);
    }
    
    private void setupClickListeners() {
        btnSetBedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(true);
            }
        });
        
        btnSetWakeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(false);
            }
        });
        
        btnLogSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logSleep();
            }
        });
    }
    
    private void showTimePickerDialog(boolean isBedTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = isBedTime ? bedTimeHour : wakeTimeHour;
        int minute = isBedTime ? bedTimeMinute : wakeTimeMinute;
        
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
            (view, selectedHour, selectedMinute) -> {
                if (isBedTime) {
                    bedTimeHour = selectedHour;
                    bedTimeMinute = selectedMinute;
                    saveBedTime();
                } else {
                    wakeTimeHour = selectedHour;
                    wakeTimeMinute = selectedMinute;
                    saveWakeTime();
                }
                calculateSleepDuration();
                updateUI();
            }, hour, minute, true);
        
        timePickerDialog.setTitle(isBedTime ? "Set Bedtime" : "Set Wake Time");
        timePickerDialog.show();
    }
      private void logSleep() {
        // Save today's sleep data
        preferencesManager.setLastNightSleep((int) sleepDuration);
        
        // Award wellness points for good sleep
        if (sleepDuration >= 7 && sleepDuration <= 9) {
            preferencesManager.addWellnessPoints(15);
            Toast.makeText(this, "🏆 Great sleep logged! +15 wellness points", Toast.LENGTH_LONG).show();
        } else {
            preferencesManager.addWellnessPoints(5);
            Toast.makeText(this, "Sleep logged! +5 wellness points", Toast.LENGTH_SHORT).show();
        }
        
        // Show summary
        showSleepSummary();
    }
    
    private void showSleepSummary() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Sleep Summary 😴");
        
        int hours = (int) sleepDuration;
        int minutes = (int) ((sleepDuration - hours) * 60);
        
        String message = String.format("Sleep Duration: %dh %02dm\n", hours, minutes);
        message += String.format("Bedtime: %02d:%02d\n", bedTimeHour, bedTimeMinute);
        message += String.format("Wake time: %02d:%02d\n", wakeTimeHour, wakeTimeMinute);
        
        if (sleepDuration >= 7 && sleepDuration <= 9) {
            message += "\n✅ Optimal sleep duration!";
        } else if (sleepDuration < 7) {
            message += "\n⏰ Try to get more sleep tonight";
        } else {
            message += "\n💤 That's a lot of sleep!";
        }
        
        builder.setMessage(message);
        builder.setPositiveButton("Thanks!", null);
        builder.show();
    }
    
    private void saveBedTime() {
        preferencesManager.getEditor().putInt("bed_time_hour", bedTimeHour);
        preferencesManager.getEditor().putInt("bed_time_minute", bedTimeMinute);
        preferencesManager.getEditor().apply();
    }
    
    private void saveWakeTime() {
        preferencesManager.getEditor().putInt("wake_time_hour", wakeTimeHour);
        preferencesManager.getEditor().putInt("wake_time_minute", wakeTimeMinute);
        preferencesManager.getEditor().apply();
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
