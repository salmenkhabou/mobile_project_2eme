package com.example.projet_android;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.os.CountDownTimer;
import android.view.View;
import android.app.AlertDialog;
import android.content.Intent;
import java.util.Calendar;

import com.example.projet_android.utils.PreferencesManager;

/**
 * Activité de méditation guidée avec minuteur et conseils
 */
public class MeditationActivity extends AppCompatActivity {
    
    private TextView titleTextView;
    private TextView instructionsTextView;
    private TextView timerTextView;
    private Button startButton;
    private Button stopButton;
    // Note: Progress tracking removed - no ProgressBar in modernized layout
    
    private PreferencesManager preferencesManager;
    private CountDownTimer meditationTimer;
    private boolean isTimerRunning = false;
    private long currentDuration = 5 * 60 * 1000; // 5 minutes par défaut
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);
        
        preferencesManager = new PreferencesManager(this);
        
        initViews();
        setupClickListeners();
        updateUI();
    }
    
    private void initViews() {
        // Use existing layout IDs or create fallbacks
        titleTextView = findViewById(R.id.tvSessionInfo); // Using existing ID
        instructionsTextView = findViewById(R.id.tvMeditationStreak); // Using existing ID as fallback
        timerTextView = findViewById(R.id.tvTotalTime); // Using existing ID
        startButton = findViewById(R.id.cardBreathing); // Using card as button fallback
        stopButton = findViewById(R.id.cardMindfulness); // Using card as button fallback  
        // Note: ProgressBar functionality removed - using modernized layout
        
        // Configuration initiale with null checks
        if (titleTextView != null) titleTextView.setText("🧘 Méditation Guidée");
        updateInstructions();
        updateTimer(currentDuration);
    }
    
    private void setupClickListeners() {
        if (startButton != null) startButton.setOnClickListener(v -> startMeditation());
        if (stopButton != null) stopButton.setOnClickListener(v -> stopMeditation());
        
        // Boutons de durée using existing card IDs
        findViewById(R.id.card3Min).setOnClickListener(v -> setDuration(3 * 60 * 1000));
        findViewById(R.id.card5Min).setOnClickListener(v -> setDuration(5 * 60 * 1000));
        findViewById(R.id.card10Min).setOnClickListener(v -> setDuration(10 * 60 * 1000));
    }
    
    private void setDuration(long duration) {
        if (!isTimerRunning) {
            currentDuration = duration;
            updateTimer(duration);
            // Progress tracking removed for modernized layout
        }
    }
    
    private void startMeditation() {
        if (!isTimerRunning) {
            isTimerRunning = true;
            
            // Update UI
            if (startButton != null) {
                startButton.setVisibility(View.GONE);
            }
            if (stopButton != null) {
                stopButton.setVisibility(View.VISIBLE);
            }
            
            // Start meditation session
            startMeditationTimer(currentDuration);
            
            // Update instructions for active session
            updateInstructions();
            
            // Save session start
            preferencesManager.setLastMeditationStart(System.currentTimeMillis());
        }
    }
    
    private void startMeditationTimer(long duration) {
        meditationTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                currentDuration = millisUntilFinished;
                updateTimer(millisUntilFinished);
                
                // Provide guidance every 30 seconds
                if (millisUntilFinished % 30000 == 0) {
                    provideGuidedInstruction(millisUntilFinished);
                }
            }
            
            @Override
            public void onFinish() {
                completeMeditationSession();
            }
        };
        meditationTimer.start();
    }
    
    private void provideGuidedInstruction(long timeRemaining) {
        String[] instructions = {
            "Focus on your breathing... in and out slowly 🌬️",
            "Notice any thoughts, then gently return to your breath 💭",
            "Relax your shoulders and jaw... release any tension 😌",
            "Feel gratitude for this moment of peace 🙏",
            "Let your mind settle like leaves on still water 🍃",
            "You're doing great... keep breathing mindfully ✨"
        };
        
        int instructionIndex = (int) ((currentDuration / 30000) % instructions.length);
        
        runOnUiThread(() -> {
            if (instructionsTextView != null) {
                instructionsTextView.setText(instructions[instructionIndex]);
            }
        });
    }
    
    private void completeMeditationSession() {
        isTimerRunning = false;
        
        // Calculate session duration
        long sessionStart = preferencesManager.getLastMeditationStart();
        long sessionDuration = System.currentTimeMillis() - sessionStart;
        int sessionMinutes = (int) (sessionDuration / 60000);
        
        // Update statistics
        int totalMinutes = preferencesManager.getTotalMeditationMinutes() + sessionMinutes;
        int sessionsCount = preferencesManager.getMeditationSessionsCount() + 1;
        int currentStreak = preferencesManager.getMeditationStreak();
        
        preferencesManager.setTotalMeditationMinutes(totalMinutes);
        preferencesManager.setMeditationSessionsCount(sessionsCount);
        preferencesManager.setHasMeditatedToday(true);
        
        // Update streak if it's a new day
        if (!preferencesManager.hasMeditatedYesterday()) {
            preferencesManager.setMeditationStreak(currentStreak + 1);
        }
        
        // Award wellness points
        int pointsEarned = sessionMinutes * 2; // 2 points per minute
        preferencesManager.addWellnessPoints(pointsEarned);
        
        // Reset UI
        resetUI();
        
        // Show completion dialog
        showCompletionDialog(sessionMinutes, pointsEarned);
        
        // Update UI with new stats
        updateUI();
    }
    
    private void showCompletionDialog(int minutes, int points) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Meditation Complete! 🧘‍♀️");
        
        StringBuilder message = new StringBuilder();
        message.append("Excellent session! You meditated for ").append(minutes).append(" minutes.\n\n");
        message.append("🏆 Points earned: ").append(points).append("\n");
        message.append("📊 Total sessions: ").append(preferencesManager.getMeditationSessionsCount()).append("\n");
        message.append("⏰ Total minutes: ").append(preferencesManager.getTotalMeditationMinutes()).append("\n");
        message.append("🔥 Current streak: ").append(preferencesManager.getMeditationStreak()).append(" days");
        
        // Add achievement messages
        int totalSessions = preferencesManager.getMeditationSessionsCount();
        if (totalSessions == 1) {
            message.append("\n\n🌟 Achievement: First meditation session!");
        } else if (totalSessions == 10) {
            message.append("\n\n🎯 Achievement: 10 sessions completed!");
        } else if (totalSessions == 50) {
            message.append("\n\n🏅 Achievement: Meditation master - 50 sessions!");
        }
        
        builder.setMessage(message.toString());
        builder.setPositiveButton("Continue", null);
        builder.setNeutralButton("Share Progress", (dialog, which) -> shareProgress());
        builder.show();
    }
    
    private void shareProgress() {
        int totalMinutes = preferencesManager.getTotalMeditationMinutes();
        int streak = preferencesManager.getMeditationStreak();
        
        String shareText = "🧘‍♀️ I've been practicing mindfulness!\n\n" +
                          "Total meditation time: " + totalMinutes + " minutes\n" +
                          "Current streak: " + streak + " days\n\n" +
                          "Taking care of my mental health, one breath at a time 🌟\n\n" +
                          "#Mindfulness #Meditation #Wellness";
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share your meditation progress"));
    }
    
    private void stopMeditation() {
        if (isTimerRunning && meditationTimer != null) {
            meditationTimer.cancel();
            isTimerRunning = false;
            
            // Show partial session dialog
            showPartialSessionDialog();
            
            resetUI();
        }
    }
    
    private void showPartialSessionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Session Paused");
        builder.setMessage("You ended your session early. Even short meditation sessions are beneficial!\n\n" +
                          "Would you like to try a shorter session or take a break?");
        builder.setPositiveButton("Try Again", (dialog, which) -> {
            currentDuration = 2 * 60 * 1000; // 2 minutes
            updateTimer(currentDuration);
        });
        builder.setNegativeButton("Take Break", null);
        builder.show();
    }
    
    private void resetUI() {
        if (startButton != null) {
            startButton.setVisibility(View.VISIBLE);
        }
        if (stopButton != null) {
            stopButton.setVisibility(View.GONE);
        }
        
        currentDuration = 5 * 60 * 1000; // Reset to 5 minutes
        updateTimer(currentDuration);
        updateInstructions();
    }
    
    private void updateTimer(long milliseconds) {
        int minutes = (int) (milliseconds / 1000) / 60;
        int seconds = (int) (milliseconds / 1000) % 60;
        timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
    }
    
    private void updateInstructions() {
        if (instructionsTextView == null) return;
        
        if (isTimerRunning) {
            instructionsTextView.setText("Close your eyes, breathe deeply, and let your mind settle 🌸");
        } else {
            // Provide different instructions based on time of day
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            
            if (hour < 12) {
                instructionsTextView.setText("Start your day with mindful breathing. Set a positive intention 🌅");
            } else if (hour < 17) {
                instructionsTextView.setText("Take a midday pause. Release stress and refocus your energy ☀️");
            } else {
                instructionsTextView.setText("Evening reflection time. Let go of the day's tensions 🌙");
            }
        }
    }
    
    private void updateMeditationInstructions(long millisUntilFinished) {
        long elapsed = currentDuration - millisUntilFinished;
        long elapsedMinutes = elapsed / (60 * 1000);
        
        String instruction = "";
        if (elapsedMinutes < 1) {
            instruction = "🌬️ Respirez profondément... Inspirez... Expirez...";
        } else if (elapsedMinutes < 2) {
            instruction = "🧠 Laissez vos pensées passer comme des nuages dans le ciel...";
        } else if (elapsedMinutes < 3) {
            instruction = "💙 Concentrez-vous sur les sensations de votre corps...";
        } else {
            instruction = "✨ Vous êtes dans un état de calme profond... Continuez...";
        }
        
        instructionsTextView.setText(instruction);
    }
    
    private void updateUI() {
        stopButton.setEnabled(false);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (meditationTimer != null) {
            meditationTimer.cancel();
        }
    }
}
