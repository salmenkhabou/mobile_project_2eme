package com.example.projet_android;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.example.projet_android.utils.PreferencesManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MoodTrackerActivity extends AppCompatActivity {    private TextView tvCurrentMood, tvMoodStreak, tvLastEntry, tvWeeklyMoodSummary;
    private ImageView imgCurrentMood;
    private LinearLayout layoutMoodOptions, layoutMoodTags;
    private Button btnLogMood, btnMoodHistory, btnMoodInsights;
    private CardView cardCurrentMood, cardMoodStreak, cardMoodTags, cardMoodInsights;
    
    private PreferencesManager preferencesManager;
    private int currentMoodLevel = 0; // 0 = not set, 1-5 = very sad to very happy
    private List<String> selectedTags = new ArrayList<>();
    
    // Mood levels and their corresponding data
    private final String[] moodLabels = {"", "Very Sad", "Sad", "Neutral", "Happy", "Very Happy"};
    private final String[] moodEmojis = {"", "😢", "😔", "😐", "😊", "😄"};
    private final int[] moodColors = {
        R.color.transparent,
        R.color.mood_very_sad,
        R.color.mood_sad,
        R.color.mood_neutral,
        R.color.mood_happy,
        R.color.mood_very_happy
    };
    
    // Mood tags for additional context
    private final String[] moodTagOptions = {
        "Stressed", "Relaxed", "Energetic", "Tired", "Motivated", 
        "Anxious", "Grateful", "Frustrated", "Peaceful", "Excited",
        "Lonely", "Social", "Confident", "Worried", "Content"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_tracker);
        
        preferencesManager = new PreferencesManager(this);
        
        initializeViews();
        setupClickListeners();
        loadData();
        updateUI();
        
        // Set up toolbar/action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mood Tracker");
        }
    }
    
    private void initializeViews() {
        tvCurrentMood = findViewById(R.id.tvCurrentMood);
        tvMoodStreak = findViewById(R.id.tvMoodStreak);        tvLastEntry = findViewById(R.id.tvLastEntry);
        tvWeeklyMoodSummary = findViewById(R.id.tvWeeklyMoodSummary);
        imgCurrentMood = findViewById(R.id.imgCurrentMood);
        layoutMoodOptions = findViewById(R.id.layoutMoodOptions);
        layoutMoodTags = findViewById(R.id.layoutMoodTags);
        btnLogMood = findViewById(R.id.btnLogMood);
        btnMoodHistory = findViewById(R.id.btnMoodHistory);
        btnMoodInsights = findViewById(R.id.btnMoodInsights);
        cardCurrentMood = findViewById(R.id.cardCurrentMood);
        cardMoodStreak = findViewById(R.id.cardMoodStreak);
        cardMoodTags = findViewById(R.id.cardMoodTags);
        cardMoodInsights = findViewById(R.id.cardMoodInsights);
        
        setupMoodOptions();
        setupMoodTags();
    }
    
    private void setupMoodOptions() {
        layoutMoodOptions.removeAllViews();
        
        for (int i = 1; i <= 5; i++) {
            final int moodLevel = i;
            
            LinearLayout moodOption = new LinearLayout(this);
            moodOption.setOrientation(LinearLayout.VERTICAL);
            moodOption.setGravity(android.view.Gravity.CENTER);
            moodOption.setPadding(16, 16, 16, 16);
            moodOption.setClickable(true);
            moodOption.setFocusable(true);
            moodOption.setBackground(ContextCompat.getDrawable(this, R.drawable.mood_option_background));
            
            // Mood emoji
            TextView emojiView = new TextView(this);
            emojiView.setText(moodEmojis[i]);
            emojiView.setTextSize(32);
            emojiView.setGravity(android.view.Gravity.CENTER);
            
            // Mood label
            TextView labelView = new TextView(this);
            labelView.setText(moodLabels[i]);
            labelView.setTextSize(12);
            labelView.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            labelView.setGravity(android.view.Gravity.CENTER);
            labelView.setPadding(0, 8, 0, 0);
            
            moodOption.addView(emojiView);
            moodOption.addView(labelView);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            params.setMargins(8, 0, 8, 0);
            moodOption.setLayoutParams(params);
            
            moodOption.setOnClickListener(v -> {
                currentMoodLevel = moodLevel;
                updateMoodSelection();
                animateMoodSelection(moodOption);
            });
            
            layoutMoodOptions.addView(moodOption);
        }
    }
    
    private void setupMoodTags() {
        layoutMoodTags.removeAllViews();
        
        for (String tag : moodTagOptions) {
            TextView tagView = new TextView(this);
            tagView.setText(tag);
            tagView.setPadding(24, 12, 24, 12);
            tagView.setTextSize(14);
            tagView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tagView.setBackground(ContextCompat.getDrawable(this, R.drawable.tag_background_unselected));
            tagView.setClickable(true);
            tagView.setFocusable(true);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8);
            tagView.setLayoutParams(params);
            
            tagView.setOnClickListener(v -> {
                toggleTag(tag, tagView);
            });
            
            layoutMoodTags.addView(tagView);
        }
    }
    
    private void setupClickListeners() {
        btnLogMood.setOnClickListener(v -> logCurrentMood());
        btnMoodHistory.setOnClickListener(v -> showMoodHistory());
        btnMoodInsights.setOnClickListener(v -> showMoodInsights());
        cardMoodInsights.setOnClickListener(v -> showMoodInsights());
    }
    
    private void loadData() {
        // Load today's mood if already logged
        int savedMood = preferencesManager.getTodayMood();
        if (savedMood > 0) {
            currentMoodLevel = savedMood;
        }
        
        // Load mood streak
        int streak = preferencesManager.getMoodStreak();
        tvMoodStreak.setText(streak + " day" + (streak != 1 ? "s" : ""));
        
        // Load last entry time
        long lastEntry = preferencesManager.getLastMoodEntryTime();
        if (lastEntry > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
            tvLastEntry.setText("Last entry: " + sdf.format(new Date(lastEntry)));
        } else {
            tvLastEntry.setText("No entries yet");
        }
        
        // Load weekly summary
        updateWeeklySummary();
    }
    
    private void updateUI() {
        if (currentMoodLevel > 0) {
            tvCurrentMood.setText(moodLabels[currentMoodLevel]);
            imgCurrentMood.setImageResource(getMoodDrawableRes(currentMoodLevel));
            imgCurrentMood.setColorFilter(ContextCompat.getColor(this, moodColors[currentMoodLevel]));
            
            cardCurrentMood.setCardBackgroundColor(ContextCompat.getColor(this, 
                getMoodBackgroundColor(currentMoodLevel)));
            
            btnLogMood.setText("Update Mood");
        } else {
            tvCurrentMood.setText("How are you feeling?");
            imgCurrentMood.setImageResource(R.drawable.ic_mood_neutral);
            imgCurrentMood.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary));
            btnLogMood.setText("Log Mood");
        }
        
        updateMoodSelection();
    }
    
    private void updateMoodSelection() {
        for (int i = 0; i < layoutMoodOptions.getChildCount(); i++) {
            View child = layoutMoodOptions.getChildAt(i);
            if (i + 1 == currentMoodLevel) {
                child.setBackground(ContextCompat.getDrawable(this, R.drawable.mood_option_selected));
                child.setElevation(8);
            } else {
                child.setBackground(ContextCompat.getDrawable(this, R.drawable.mood_option_background));
                child.setElevation(2);
            }
        }
    }
    
    private void toggleTag(String tag, TextView tagView) {
        if (selectedTags.contains(tag)) {
            selectedTags.remove(tag);
            tagView.setBackground(ContextCompat.getDrawable(this, R.drawable.tag_background_unselected));
            tagView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        } else {
            if (selectedTags.size() < 5) { // Limit to 5 tags
                selectedTags.add(tag);
                tagView.setBackground(ContextCompat.getDrawable(this, R.drawable.tag_background_selected));
                tagView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            } else {
                Toast.makeText(this, "You can select up to 5 tags", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void animateMoodSelection(View selectedView) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(selectedView, "scaleX", 1.0f, 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(selectedView, "scaleY", 1.0f, 1.2f, 1.0f);
        scaleX.setDuration(300);
        scaleY.setDuration(300);
        scaleX.start();
        scaleY.start();
    }
    
    private void logCurrentMood() {
        if (currentMoodLevel == 0) {
            Toast.makeText(this, "Please select a mood level first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Save mood data
        saveMoodData();
        
        // Update UI
        updateMoodDisplay();
        
        // Show success message with insights
        showMoodLoggedDialog();
        
        // Award wellness points
        preferencesManager.addWellnessPoints(5);
        
        // Reset for next entry
        resetMoodSelection();
    }
    
    private void saveMoodData() {
        // Save current mood
        preferencesManager.setDailyMood(moodLabels[currentMoodLevel]);
        preferencesManager.setHasLoggedMoodToday(true);
        
        // Save mood with timestamp for history
        long timestamp = System.currentTimeMillis();
        String moodEntry = timestamp + ":" + currentMoodLevel + ":" + String.join(",", selectedTags);
        
        // Get existing mood history and append new entry
        String existingHistory = preferencesManager.getSharedPreferences()
            .getString("mood_history", "");
        String newHistory = existingHistory.isEmpty() ? moodEntry : existingHistory + ";" + moodEntry;
        
        preferencesManager.getEditor().putString("mood_history", newHistory);
        preferencesManager.getEditor().apply();
        
        // Update mood streak
        updateMoodStreak();
    }
    
    private void updateMoodStreak() {
        int currentStreak = preferencesManager.getSharedPreferences()
            .getInt("mood_streak", 0);
        
        // Check if logged mood yesterday
        if (hasLoggedMoodYesterday()) {
            preferencesManager.getEditor().putInt("mood_streak", currentStreak + 1);
        } else {
            preferencesManager.getEditor().putInt("mood_streak", 1); // Reset streak
        }
        preferencesManager.getEditor().apply();
    }
    
    private boolean hasLoggedMoodYesterday() {
        // Simple check - could be enhanced with proper date calculation
        String history = preferencesManager.getSharedPreferences()
            .getString("mood_history", "");
        
        if (history.isEmpty()) return false;
        
        long yesterday = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        long dayBefore = yesterday - (24 * 60 * 60 * 1000);
        
        // Check if there's an entry from yesterday
        String[] entries = history.split(";");
        for (String entry : entries) {
            try {
                long timestamp = Long.parseLong(entry.split(":")[0]);
                if (timestamp > dayBefore && timestamp < (System.currentTimeMillis() - (12 * 60 * 60 * 1000))) {
                    return true;
                }
            } catch (Exception e) {
                // Skip malformed entries
            }
        }
        return false;
    }
    
    private void showMoodLoggedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mood Logged! " + moodEmojis[currentMoodLevel]);
        
        StringBuilder message = new StringBuilder();
        message.append("Thank you for tracking your mood!\n\n");
        message.append("Mood: ").append(moodLabels[currentMoodLevel]).append("\n");
        
        if (!selectedTags.isEmpty()) {
            message.append("Tags: ").append(String.join(", ", selectedTags)).append("\n");
        }
        
        message.append("\n🏆 +5 wellness points earned!\n");
        
        int streak = preferencesManager.getSharedPreferences().getInt("mood_streak", 0);
        message.append("📊 Current streak: ").append(streak).append(" days\n\n");
        
        // Add personalized insights based on mood
        message.append(getMoodInsight());
        
        builder.setMessage(message.toString());
        builder.setPositiveButton("Thanks!", null);
        builder.setNeutralButton("View Trends", (dialog, which) -> showMoodTrends());
        builder.show();
    }
    
    private String getMoodInsight() {
        String insight = "💡 Insight: ";
        
        switch (currentMoodLevel) {
            case 1:
            case 2:
                insight += "It's okay to have difficult days. Consider some self-care activities like meditation, talking to a friend, or gentle exercise.";
                break;
            case 3:
                insight += "Neutral moods are perfectly normal. Sometimes taking a moment to appreciate small things can help boost your spirits.";
                break;
            case 4:
            case 5:
                insight += "Great to see you feeling positive! This is a wonderful time to tackle challenges or help others feel good too.";
                break;
            default:
                insight += "Regular mood tracking helps you understand patterns and triggers in your emotional well-being.";
        }
        
        return insight;
    }
    
    private void showMoodTrends() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mood Trends 📊");
        
        String analysis = analyzeMoodHistory();
        builder.setMessage(analysis);
        builder.setPositiveButton("Interesting!", null);
        builder.setNeutralButton("Get Recommendations", (dialog, which) -> showMoodRecommendations());
        builder.show();
    }
    
    private String analyzeMoodHistory() {
        String history = preferencesManager.getSharedPreferences()
            .getString("mood_history", "");
        
        if (history.isEmpty()) {
            return "Start logging your mood regularly to see trends and insights!";
        }
        
        String[] entries = history.split(";");
        
        // Analyze last 7 days
        long weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);
        List<Integer> recentMoods = new ArrayList<>();
        
        for (String entry : entries) {
            try {
                String[] parts = entry.split(":");
                long timestamp = Long.parseLong(parts[0]);
                int moodLevel = Integer.parseInt(parts[1]);
                
                if (timestamp > weekAgo) {
                    recentMoods.add(moodLevel);
                }
            } catch (Exception e) {
                // Skip malformed entries
            }
        }
        
        if (recentMoods.isEmpty()) {
            return "No mood data from the past week. Keep logging to see trends!";
        }
        
        // Calculate average and trends
        double avgMood = recentMoods.stream().mapToInt(Integer::intValue).average().orElse(3.0);
        int streak = preferencesManager.getSharedPreferences().getInt("mood_streak", 0);
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("📈 Weekly Summary:\n\n");
        analysis.append("Entries logged: ").append(recentMoods.size()).append("\n");
        analysis.append("Average mood: ").append(String.format("%.1f", avgMood)).append("/5\n");
        analysis.append("Current streak: ").append(streak).append(" days\n\n");
        
        // Mood distribution
        analysis.append("Mood breakdown:\n");
        for (int i = 1; i <= 5; i++) {
            int count = 0;
            for (int mood : recentMoods) {
                if (mood == i) count++;
            }
            if (count > 0) {
                analysis.append(moodEmojis[i]).append(" ").append(moodLabels[i]).append(": ")
                    .append(count).append(" days\n");
            }
        }
        
        // Trend analysis
        analysis.append("\n📊 Insights:\n");
        if (avgMood >= 4.0) {
            analysis.append("• You've been feeling quite positive lately! 🌟\n");
        } else if (avgMood <= 2.0) {
            analysis.append("• You've had some challenging days. Consider self-care activities 💙\n");
        } else {
            analysis.append("• Your mood has been fairly balanced this week 📊\n");
        }
        
        if (streak >= 7) {
            analysis.append("• Amazing streak! Consistency in mood tracking is key 🏆\n");
        }
        
        return analysis.toString();
    }
    
    private void showMoodRecommendations() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Personalized Recommendations 💡");
        
        String recommendations = generateRecommendations();
        builder.setMessage(recommendations);
        builder.setPositiveButton("Try These!", null);
        builder.show();
    }
    
    private String generateRecommendations() {
        String history = preferencesManager.getSharedPreferences()
            .getString("mood_history", "");
        
        StringBuilder recs = new StringBuilder();
        recs.append("Based on your mood patterns:\n\n");
        
        // Analyze recent mood levels
        int recentLowMoods = 0;
        int recentHighMoods = 0;
        
        if (!history.isEmpty()) {
            String[] entries = history.split(";");
            long weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);
            
            for (String entry : entries) {
                try {
                    String[] parts = entry.split(":");
                    long timestamp = Long.parseLong(parts[0]);
                    int moodLevel = Integer.parseInt(parts[1]);
                    
                    if (timestamp > weekAgo) {
                        if (moodLevel <= 2) recentLowMoods++;
                        if (moodLevel >= 4) recentHighMoods++;
                    }
                } catch (Exception e) {
                    // Skip malformed entries
                }
            }
        }
        
        // Generate personalized recommendations
        if (recentLowMoods > recentHighMoods) {
            recs.append("🧘 Try meditation or mindfulness exercises\n");
            recs.append("🚶 Take a short walk outdoors\n");
            recs.append("📱 Connect with a friend or family member\n");
            recs.append("📖 Write in a gratitude journal\n");
            recs.append("🎵 Listen to uplifting music\n");
        } else if (recentHighMoods > recentLowMoods) {
            recs.append("🎯 Use this positive energy for goal-setting\n");
            recs.append("💪 Try a new physical activity\n");
            recs.append("🤝 Help someone else or volunteer\n");
            recs.append("📚 Learn something new\n");
            recs.append("🎨 Express yourself creatively\n");
        } else {
            recs.append("⚖️ Maintain your balanced approach:\n");
            recs.append("🏃 Regular exercise routine\n");
            recs.append("😴 Consistent sleep schedule\n");
            recs.append("🥗 Healthy nutrition habits\n");
            recs.append("🧘 Daily mindfulness practice\n");
            recs.append("👥 Social connections\n");
        }
        
        recs.append("\n💡 Remember: It's normal to have ups and downs. The key is being aware of your patterns!");
        
        return recs.toString();
    }
    
    private void showMoodHistory() {
        Intent intent = new Intent(this, MoodHistoryActivity.class);
        startActivity(intent);
    }
    
    private void showMoodInsights() {
        showMoodTrends();
    }
    
    private void updateWeeklySummary() {
        // Simplified weekly summary - in a full app this would analyze actual data
        int averageMood = preferencesManager.getWeeklyAverageMood();
        if (averageMood > 0) {
            tvWeeklyMoodSummary.setText("Weekly average: " + moodEmojis[averageMood] + " " + moodLabels[averageMood]);
        } else {
            tvWeeklyMoodSummary.setText("Not enough data for weekly summary");
        }
    }
    
    private int getMoodDrawableRes(int moodLevel) {
        switch (moodLevel) {
            case 1: return R.drawable.ic_mood_very_sad;
            case 2: return R.drawable.ic_mood_sad;
            case 3: return R.drawable.ic_mood_neutral;
            case 4: return R.drawable.ic_mood_happy;
            case 5: return R.drawable.ic_mood_very_happy;
            default: return R.drawable.ic_mood_neutral;
        }
    }
    
    private int getMoodBackgroundColor(int moodLevel) {
        switch (moodLevel) {
            case 1: return R.color.mood_very_sad_bg;
            case 2: return R.color.mood_sad_bg;
            case 3: return R.color.mood_neutral_bg;
            case 4: return R.color.mood_happy_bg;
            case 5: return R.color.mood_very_happy_bg;
            default: return R.color.background_light;
        }
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
    
    private void updateMoodDisplay() {
        // Update current mood display
        if (currentMoodLevel > 0) {
            tvCurrentMood.setText(moodLabels[currentMoodLevel]);
            imgCurrentMood.setImageResource(getMoodIcon(currentMoodLevel));
            
            // Update mood color
            cardCurrentMood.setCardBackgroundColor(ContextCompat.getColor(this, moodColors[currentMoodLevel]));
        }
        
        // Update streak display
        int streak = preferencesManager.getSharedPreferences().getInt("mood_streak", 0);
        tvMoodStreak.setText(streak + " days");
        
        // Update last entry
        if (preferencesManager.hasLoggedMoodToday()) {
            String mood = preferencesManager.getDailyMood();
            tvLastEntry.setText("Today: " + mood);
        } else {
            tvLastEntry.setText("No entry today");
        }
    }
    
    private void resetMoodSelection() {
        currentMoodLevel = 0;
        selectedTags.clear();
        
        // Reset UI
        updateMoodOptionsUI();
        updateMoodTagsUI();
        
        // Reset current mood display
        tvCurrentMood.setText("Select your mood");
        imgCurrentMood.setImageResource(R.drawable.ic_mood_neutral);
        cardCurrentMood.setCardBackgroundColor(ContextCompat.getColor(this, R.color.surface));
    }
    
    private void updateMoodOptionsUI() {
        // Update mood option buttons visual state
        for (int i = 0; i < layoutMoodOptions.getChildCount(); i++) {
            View child = layoutMoodOptions.getChildAt(i);
            if (i + 1 == currentMoodLevel) {
                child.setBackgroundColor(ContextCompat.getColor(this, moodColors[currentMoodLevel]));
            } else {
                child.setBackgroundColor(ContextCompat.getColor(this, R.color.surface_variant));
            }
        }
    }
    
    private void updateMoodTagsUI() {
        // Update mood tags visual state
        for (int i = 0; i < layoutMoodTags.getChildCount(); i++) {
            View child = layoutMoodTags.getChildAt(i);
            if (child instanceof TextView) {
                TextView tagView = (TextView) child;
                String tagText = tagView.getText().toString();
                
                if (selectedTags.contains(tagText)) {
                    tagView.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
                    tagView.setTextColor(ContextCompat.getColor(this, R.color.on_primary));
                } else {
                    tagView.setBackgroundColor(ContextCompat.getColor(this, R.color.surface_variant));
                    tagView.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
                }
            }
        }
    }
    
    private int getMoodIcon(int moodLevel) {
        switch (moodLevel) {
            case 1: return R.drawable.ic_mood_very_sad;
            case 2: return R.drawable.ic_mood_sad;
            case 3: return R.drawable.ic_mood_neutral;
            case 4: return R.drawable.ic_mood_happy;
            case 5: return R.drawable.ic_mood_very_happy;
            default: return R.drawable.ic_mood_neutral;
        }
    }
}
