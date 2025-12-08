package com.example.projet_android;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.example.projet_android.utils.PreferencesManager;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BodyAnalysisActivity extends AppCompatActivity {
    private TextView tvBMI, tvBMICategory, tvWeight, tvHeight, tvBodyFat, tvMuscleMass;
    private TextView tvLastUpdate, tvWeightGoal, tvProgressToGoal, tvHealthScore;
    private ProgressBar progressWeightGoal, progressBMI;
    private ImageView imgBodyStatus;
    private com.google.android.material.button.MaterialButton btnEditWeight, btnEditHeight, btnEditBodyFat;
    private com.google.android.material.button.MaterialButton btnUpdateMeasurements, btnGoals, btnHistory, btnTips;
    private com.google.android.material.card.MaterialCardView cardHealthDashboard, cardMeasurements, cardGoals;
    
    private PreferencesManager preferencesManager;
    private double currentWeight = 0.0;
    private double currentHeight = 0.0;
    private double currentBodyFat = 0.0;
    private double currentMuscleMass = 0.0;
    private double weightGoal = 0.0;
    private double bmi = 0.0;
    private int healthScore = 0;
    
    private DecimalFormat df = new DecimalFormat("#.#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_analysis);
        
        preferencesManager = new PreferencesManager(this);
        
        initializeViews();
        setupClickListeners();
        loadData();
        updateUI();
        
        // Set up toolbar/action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Body Analysis");
        }
    }
      private void initializeViews() {
        tvBMI = findViewById(R.id.tvBMI);
        tvBMICategory = findViewById(R.id.tvBMICategory);
        tvWeight = findViewById(R.id.tvWeight);
        tvHeight = findViewById(R.id.tvHeight);
        tvBodyFat = findViewById(R.id.tvBodyFat);
        tvMuscleMass = findViewById(R.id.tvMuscleMass);
        tvLastUpdate = findViewById(R.id.tvLastUpdate);
        tvWeightGoal = findViewById(R.id.tvWeightGoal);
        tvProgressToGoal = findViewById(R.id.tvProgressToGoal);
        tvHealthScore = findViewById(R.id.tvHealthScore);
        progressWeightGoal = findViewById(R.id.progressWeightGoal);
        progressBMI = findViewById(R.id.progressBMI);
        imgBodyStatus = findViewById(R.id.imgBodyStatus);
        btnEditWeight = findViewById(R.id.btnEditWeight);
        btnEditHeight = findViewById(R.id.btnEditHeight);
        btnEditBodyFat = findViewById(R.id.btnEditBodyFat);
        btnUpdateMeasurements = findViewById(R.id.btnUpdateMeasurements);
        btnGoals = findViewById(R.id.btnGoals);
        btnHistory = findViewById(R.id.btnHistory);
        btnTips = findViewById(R.id.btnTips);
        cardHealthDashboard = findViewById(R.id.cardHealthDashboard);
        cardMeasurements = findViewById(R.id.cardMeasurements);
        cardGoals = findViewById(R.id.cardGoals);
    }
      private void setupClickListeners() {
        btnEditWeight.setOnClickListener(v -> showEditDialog("Weight", "kg", currentWeight, this::updateWeight));
        btnEditHeight.setOnClickListener(v -> showEditDialog("Height", "cm", currentHeight, this::updateHeight));
        btnEditBodyFat.setOnClickListener(v -> showEditDialog("Body Fat", "%", currentBodyFat, this::updateBodyFat));
        btnUpdateMeasurements.setOnClickListener(v -> showUpdateMeasurementsDialog());
        btnGoals.setOnClickListener(v -> showGoalsDialog());
        btnHistory.setOnClickListener(v -> showHistory());
        btnTips.setOnClickListener(v -> showHealthTips());
        cardHealthDashboard.setOnClickListener(v -> showHealthDetails());
    }
    
    private void loadData() {
        currentWeight = preferencesManager.getCurrentWeight();
        currentHeight = preferencesManager.getCurrentHeight();
        currentBodyFat = preferencesManager.getBodyFatPercentage();
        currentMuscleMass = preferencesManager.getMuscleMass();
        weightGoal = preferencesManager.getWeightGoal();
        
        // Calculate BMI if we have height and weight
        if (currentWeight > 0 && currentHeight > 0) {
            double heightInMeters = currentHeight / 100.0;
            bmi = currentWeight / (heightInMeters * heightInMeters);
        }
        
        // Calculate muscle mass if we have weight and body fat
        if (currentWeight > 0 && currentBodyFat > 0) {
            currentMuscleMass = currentWeight * (1 - currentBodyFat / 100) * 0.5; // Simplified calculation
            preferencesManager.setMuscleMass(currentMuscleMass);
        }
        
        // Calculate health score
        calculateHealthScore();
    }
    
    private void calculateHealthScore() {
        healthScore = 0;
        
        // BMI Score (0-30 points)
        if (bmi > 0) {
            if (bmi >= 18.5 && bmi <= 24.9) {
                healthScore += 30; // Optimal BMI
            } else if (bmi >= 17.5 && bmi <= 27.0) {
                healthScore += 20; // Good BMI
            } else if (bmi >= 16.0 && bmi <= 30.0) {
                healthScore += 10; // Acceptable BMI
            }
        }
        
        // Body Fat Score (0-25 points)
        if (currentBodyFat > 0) {
            if (currentBodyFat >= 10 && currentBodyFat <= 20) {
                healthScore += 25; // Optimal body fat
            } else if (currentBodyFat >= 8 && currentBodyFat <= 25) {
                healthScore += 15; // Good body fat
            } else if (currentBodyFat >= 5 && currentBodyFat <= 30) {
                healthScore += 10; // Acceptable body fat
            }
        }
        
        // Weight Goal Progress Score (0-25 points)
        if (weightGoal > 0 && currentWeight > 0) {
            double progressPercent = Math.abs(currentWeight - weightGoal) / weightGoal;
            if (progressPercent <= 0.05) { // Within 5%
                healthScore += 25;
            } else if (progressPercent <= 0.10) { // Within 10%
                healthScore += 15;
            } else if (progressPercent <= 0.20) { // Within 20%
                healthScore += 10;
            }
        }
        
        // Consistency Score (0-20 points)
        long lastUpdate = preferencesManager.getLastBodyAnalysisUpdate();
        if (lastUpdate > 0) {
            long daysSinceUpdate = (System.currentTimeMillis() - lastUpdate) / (24 * 60 * 60 * 1000);
            if (daysSinceUpdate <= 7) {
                healthScore += 20;
            } else if (daysSinceUpdate <= 14) {
                healthScore += 10;
            } else if (daysSinceUpdate <= 30) {
                healthScore += 5;
            }
        }
        
        healthScore = Math.min(100, healthScore); // Cap at 100
    }
    
    private void updateUI() {
        // Update BMI
        if (bmi > 0) {
            tvBMI.setText(df.format(bmi));
            tvBMICategory.setText(getBMICategory(bmi));
            tvBMICategory.setTextColor(ContextCompat.getColor(this, getBMIColor(bmi)));
            
            // Update BMI progress bar (scale BMI to 0-100 for display)
            int bmiProgress = (int) Math.min(100, (bmi / 40.0) * 100); // Scale based on max BMI of 40
            animateProgress(progressBMI, bmiProgress);
        } else {
            tvBMI.setText("--");
            tvBMICategory.setText("Enter height and weight");
            progressBMI.setProgress(0);
        }
        
        // Update measurements
        tvWeight.setText(currentWeight > 0 ? df.format(currentWeight) + " kg" : "Not set");
        tvHeight.setText(currentHeight > 0 ? df.format(currentHeight) + " cm" : "Not set");
        tvBodyFat.setText(currentBodyFat > 0 ? df.format(currentBodyFat) + "%" : "Not set");
        tvMuscleMass.setText(currentMuscleMass > 0 ? df.format(currentMuscleMass) + " kg" : "Not set");
        
        // Update goals
        if (weightGoal > 0) {
            tvWeightGoal.setText("Goal: " + df.format(weightGoal) + " kg");
            if (currentWeight > 0) {
                double diff = currentWeight - weightGoal;
                String progressText;
                int progressPercent;
                
                if (Math.abs(diff) < 0.5) {
                    progressText = "Goal reached! 🎉";
                    progressPercent = 100;
                } else if (diff > 0) {
                    progressText = df.format(diff) + " kg to lose";
                    progressPercent = Math.max(0, (int) (100 - (diff / weightGoal * 100)));
                } else {
                    progressText = df.format(Math.abs(diff)) + " kg to gain";
                    progressPercent = Math.max(0, (int) (100 - (Math.abs(diff) / weightGoal * 100)));
                }
                
                tvProgressToGoal.setText(progressText);
                animateProgress(progressWeightGoal, progressPercent);
            } else {
                tvProgressToGoal.setText("Enter current weight");
                progressWeightGoal.setProgress(0);
            }
        } else {
            tvWeightGoal.setText("No goal set");
            tvProgressToGoal.setText("Set a weight goal");
            progressWeightGoal.setProgress(0);
        }
          // Update health score
        tvHealthScore.setText(healthScore + "/100");
        
        // Update body status icon
        updateBodyStatusIcon();
        
        // Update last update time
        long lastUpdate = preferencesManager.getLastBodyAnalysisUpdate();
        if (lastUpdate > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
            tvLastUpdate.setText("Updated: " + sdf.format(new Date(lastUpdate)));
        } else {
            tvLastUpdate.setText("No data recorded");
        }
    }
    
    private void updateBodyStatusIcon() {
        int iconRes;
        int colorRes;
        
        if (healthScore >= 80) {
            iconRes = R.drawable.ic_body_excellent;
            colorRes = R.color.health_excellent;
        } else if (healthScore >= 60) {
            iconRes = R.drawable.ic_body_good;
            colorRes = R.color.health_good;
        } else if (healthScore >= 40) {
            iconRes = R.drawable.ic_body_fair;
            colorRes = R.color.health_fair;
        } else {
            iconRes = R.drawable.ic_body_poor;
            colorRes = R.color.health_poor;
        }
        
        imgBodyStatus.setImageResource(iconRes);
        imgBodyStatus.setColorFilter(ContextCompat.getColor(this, colorRes));
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
    
    private String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25) return "Normal weight";
        else if (bmi < 30) return "Overweight";
        else return "Obese";
    }
    
    private int getBMIColor(double bmi) {
        if (bmi < 18.5 || bmi >= 30) return R.color.health_poor;
        else if (bmi < 20 || bmi >= 27) return R.color.health_fair;
        else if (bmi < 22.5) return R.color.health_excellent;
        else return R.color.health_good;
    }
    
    private void showEditDialog(String title, String unit, double currentValue, ValueUpdateCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update " + title);
        
        EditText editText = new EditText(this);
        editText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (currentValue > 0) {
            editText.setText(String.valueOf(currentValue));
        }
        editText.setHint("Enter " + title.toLowerCase() + " in " + unit);
        
        builder.setView(editText);
        builder.setPositiveButton("Update", (dialog, which) -> {
            try {
                double value = Double.parseDouble(editText.getText().toString());
                if (value > 0) {
                    callback.onValueUpdate(value);
                    preferencesManager.setLastBodyAnalysisUpdate(System.currentTimeMillis());
                    
                    // Add wellness points
                    preferencesManager.addWellnessPoints(5);
                    
                    loadData();
                    updateUI();
                    
                    Toast.makeText(this, title + " updated! +5 points", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please enter a valid value", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void updateWeight(double weight) {
        preferencesManager.setCurrentWeight(weight);
        currentWeight = weight;
    }
    
    private void updateHeight(double height) {
        preferencesManager.setCurrentHeight(height);
        currentHeight = height;
    }
    
    private void updateBodyFat(double bodyFat) {
        preferencesManager.setBodyFatPercentage(bodyFat);
        currentBodyFat = bodyFat;
    }
    
    private void showUpdateMeasurementsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quick Update");
        builder.setMessage("Update all your measurements at once for the most accurate analysis.");
        
        // Create a simple form layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_measurements_update, null);
        EditText etWeight = dialogView.findViewById(R.id.etWeight);
        EditText etBodyFat = dialogView.findViewById(R.id.etBodyFat);
        
        if (currentWeight > 0) etWeight.setText(String.valueOf(currentWeight));
        if (currentBodyFat > 0) etBodyFat.setText(String.valueOf(currentBodyFat));
        
        builder.setView(dialogView);
        builder.setPositiveButton("Update All", (dialog, which) -> {
            try {
                if (!etWeight.getText().toString().isEmpty()) {
                    updateWeight(Double.parseDouble(etWeight.getText().toString()));
                }
                if (!etBodyFat.getText().toString().isEmpty()) {
                    updateBodyFat(Double.parseDouble(etBodyFat.getText().toString()));
                }
                
                preferencesManager.setLastBodyAnalysisUpdate(System.currentTimeMillis());
                preferencesManager.addWellnessPoints(15); // Bonus for complete update
                
                loadData();
                updateUI();
                
                Toast.makeText(this, "All measurements updated! +15 points", Toast.LENGTH_SHORT).show();
                
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showGoalsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Weight Goal");
        
        EditText editText = new EditText(this);
        editText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (weightGoal > 0) {
            editText.setText(String.valueOf(weightGoal));
        }
        editText.setHint("Target weight in kg");
        
        builder.setView(editText);
        builder.setPositiveButton("Set Goal", (dialog, which) -> {
            try {
                double goal = Double.parseDouble(editText.getText().toString());
                if (goal > 0) {
                    preferencesManager.setWeightGoal(goal);
                    weightGoal = goal;
                    preferencesManager.addWellnessPoints(10);
                    
                    updateUI();
                    Toast.makeText(this, "Weight goal set! +10 points", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please enter a valid weight", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showHistory() {
        Toast.makeText(this, "Body measurement history coming soon!", Toast.LENGTH_SHORT).show();
        // TODO: Implement history activity with charts and trends
    }
    
    private void showHealthTips() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Health & Fitness Tips 💪");
        
        String tips = generatePersonalizedTips();
        builder.setMessage(tips);
        builder.setPositiveButton("Got it!", null);
        builder.show();
    }
    
    private void showHealthDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Health Score Breakdown 📊");
        
        StringBuilder details = new StringBuilder();
        details.append("Your health score is based on:\n\n");
        details.append("🎯 BMI Analysis: ").append(getBMIScoreContribution()).append("\n");
        details.append("📏 Body Composition: ").append(getBodyFatScoreContribution()).append("\n");
        details.append("⚖️ Goal Progress: ").append(getGoalScoreContribution()).append("\n");
        details.append("📅 Data Consistency: ").append(getConsistencyScoreContribution()).append("\n\n");
        details.append("Keep tracking regularly for better insights!");
        
        builder.setMessage(details.toString());
        builder.setPositiveButton("Understood", null);
        builder.show();
    }
    
    private void showNutritionTracking() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Daily Nutrition Summary 🍎");
        
        // Get today's nutrition data
        String nutritionSummary = generateNutritionSummary();
        
        builder.setMessage(nutritionSummary);
        builder.setPositiveButton("Track Food", (dialog, which) -> {
            Intent intent = new Intent(this, NutritionActivity.class);
            startActivity(intent);
        });
        builder.setNegativeButton("Close", null);
        builder.show();
    }
    
    private String generateNutritionSummary() {
        // Get daily nutrition targets based on user profile
        int targetCalories = calculateTargetCalories();
        int targetProtein = (int) (currentWeight * 1.6); // 1.6g per kg body weight
        int targetCarbs = (int) (targetCalories * 0.45 / 4); // 45% of calories
        int targetFat = (int) (targetCalories * 0.30 / 9); // 30% of calories
        
        // This would typically come from database
        int consumedCalories = preferencesManager.getDailyCalories();
        int consumedProtein = preferencesManager.getDailyProtein();
        int consumedCarbs = preferencesManager.getDailyCarbs();
        int consumedFat = preferencesManager.getDailyFat();
        
        StringBuilder summary = new StringBuilder();
        summary.append("📊 Today's Nutrition:\n\n");
        summary.append(String.format("Calories: %d / %d kcal (%.0f%%)\n", 
            consumedCalories, targetCalories, (consumedCalories / (float) targetCalories) * 100));
        summary.append(String.format("Protein: %dg / %dg (%.0f%%)\n", 
            consumedProtein, targetProtein, (consumedProtein / (float) targetProtein) * 100));
        summary.append(String.format("Carbs: %dg / %dg (%.0f%%)\n", 
            consumedCarbs, targetCarbs, (consumedCarbs / (float) targetCarbs) * 100));
        summary.append(String.format("Fat: %dg / %dg (%.0f%%)\n\n", 
            consumedFat, targetFat, (consumedFat / (float) targetFat) * 100));
        
        // Add recommendations
        summary.append("💡 Recommendations:\n");
        if (consumedCalories < targetCalories * 0.8) {
            summary.append("• You may need more calories for your goals\n");
        }
        if (consumedProtein < targetProtein * 0.8) {
            summary.append("• Consider adding more protein sources\n");
        }
        if (consumedCalories > targetCalories * 1.2) {
            summary.append("• You've exceeded your calorie target\n");
        }
        
        return summary.toString();
    }
    
    private int calculateTargetCalories() {
        if (currentWeight == 0 || currentHeight == 0) return 2000; // Default
        
        // Harris-Benedict Equation (simplified)
        // This is a basic calculation - could be enhanced with activity level, age, gender
        double bmr = 88.362 + (13.397 * currentWeight) + (4.799 * currentHeight) - (5.677 * 25); // Assuming age 25
        return (int) (bmr * 1.5); // Moderate activity level
    }
    
    private String generatePersonalizedTips() {
        StringBuilder tips = new StringBuilder();
        
        if (bmi < 18.5) {
            tips.append("💡 Focus on healthy weight gain with protein-rich foods\n\n");
        } else if (bmi > 25) {
            tips.append("💡 Consider a balanced diet and regular cardio exercise\n\n");
        } else {
            tips.append("💡 Great BMI! Maintain with balanced nutrition and exercise\n\n");
        }
        
        if (currentBodyFat > 25) {
            tips.append("🏋️ Strength training can help reduce body fat percentage\n\n");
        } else if (currentBodyFat < 10) {
            tips.append("🍎 Make sure you're eating enough healthy fats\n\n");
        }
        
        tips.append("📏 Take measurements weekly for best tracking\n");
        tips.append("💧 Stay hydrated - it affects body composition\n");
        tips.append("😴 Quality sleep is crucial for body recovery\n");
        tips.append("🥗 Focus on whole foods over processed options");
        
        return tips.toString();
    }
    
    private String getBMIScoreContribution() {
        if (bmi >= 18.5 && bmi <= 24.9) return "Excellent (30/30)";
        else if (bmi >= 17.5 && bmi <= 27.0) return "Good (20/30)";
        else if (bmi >= 16.0 && bmi <= 30.0) return "Fair (10/30)";
        else return "Needs improvement (0/30)";
    }
    
    private String getBodyFatScoreContribution() {
        if (currentBodyFat >= 10 && currentBodyFat <= 20) return "Excellent (25/25)";
        else if (currentBodyFat >= 8 && currentBodyFat <= 25) return "Good (15/25)";
        else if (currentBodyFat >= 5 && currentBodyFat <= 30) return "Fair (10/25)";
        else if (currentBodyFat > 0) return "Needs improvement (0/25)";
        else return "No data (0/25)";
    }
    
    private String getGoalScoreContribution() {
        if (weightGoal > 0 && currentWeight > 0) {
            double progressPercent = Math.abs(currentWeight - weightGoal) / weightGoal;
            if (progressPercent <= 0.05) return "Excellent (25/25)";
            else if (progressPercent <= 0.10) return "Good (15/25)";
            else if (progressPercent <= 0.20) return "Fair (10/25)";
            else return "Needs work (0/25)";
        }
        return "No goal set (0/25)";
    }
    
    private String getConsistencyScoreContribution() {
        long lastUpdate = preferencesManager.getLastBodyAnalysisUpdate();
        if (lastUpdate > 0) {
            long daysSinceUpdate = (System.currentTimeMillis() - lastUpdate) / (24 * 60 * 60 * 1000);
            if (daysSinceUpdate <= 7) return "Excellent (20/20)";
            else if (daysSinceUpdate <= 14) return "Good (10/20)";
            else if (daysSinceUpdate <= 30) return "Fair (5/20)";
            else return "Poor (0/20)";
        }
        return "No data (0/20)";
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
    
    interface ValueUpdateCallback {
        void onValueUpdate(double value);
    }
}
