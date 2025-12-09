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

/**
 * Activité d'analyse corporelle - permet aux utilisateurs de suivre et analyser
 * leurs mesures corporelles (poids, taille, IMC, masse grasse, etc.)
 * 
 * Fonctionnalités principales :
 * - Calcul automatique de l'IMC (Indice de Masse Corporelle)
 * - Suivi des objectifs de poids
 * - Analyse de la composition corporelle
 * - Score de santé personnalisé
 * - Conseils santé personnalisés
 * - Intégration avec le système de points wellness
 * 
 * @author Équipe de développement Health Tracker
 * @version 2.1
 * @since 1.0
 */
public class BodyAnalysisActivity extends AppCompatActivity {
    
    // === COMPOSANTS DE L'INTERFACE UTILISATEUR ===
    
    /** Éléments d'affichage des données corporelles */
    private TextView tvBMI, tvBMICategory, tvWeight, tvHeight, tvBodyFat, tvMuscleMass;
    
    /** Éléments d'affichage du suivi et des objectifs */
    private TextView tvLastUpdate, tvWeightGoal, tvProgressToGoal, tvHealthScore;
    
    /** Barres de progression pour visualiser les objectifs */
    private ProgressBar progressWeightGoal, progressBMI;
    
    /** Icône représentant le statut corporel actuel */
    private ImageView imgBodyStatus;
    
    /** Boutons d'édition des mesures individuelles */
    private com.google.android.material.button.MaterialButton btnEditWeight, btnEditHeight, btnEditBodyFat;
    
    /** Boutons d'actions principales */
    private com.google.android.material.button.MaterialButton btnUpdateMeasurements, btnGoals, btnHistory, btnTips;
    
    /** Cartes contenant les différentes sections de l'interface */
    private com.google.android.material.card.MaterialCardView cardHealthDashboard, cardMeasurements, cardGoals;
    
    // === GESTIONNAIRES ET DONNÉES ===
    
    /** Gestionnaire des préférences utilisateur */
    private PreferencesManager preferencesManager;
    
    /** Variables stockant les mesures corporelles actuelles */
    private double currentWeight = 0.0;      // Poids actuel en kg
    private double currentHeight = 0.0;      // Taille actuelle en cm
    private double currentBodyFat = 0.0;     // Pourcentage de masse grasse
    private double currentMuscleMass = 0.0;  // Masse musculaire estimée en kg
    private double weightGoal = 0.0;         // Objectif de poids en kg
    private double bmi = 0.0;                // Indice de Masse Corporelle calculé
    private int healthScore = 0;             // Score de santé sur 100
    
    /** Formateur pour l'affichage des nombres décimaux */
    private DecimalFormat df = new DecimalFormat("#.#");    /**
     * Méthode appelée lors de la création de l'activité
     * Initialise l'interface, charge les données et configure les interactions
     * 
     * @param savedInstanceState État sauvegardé de l'activité (peut être null)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_analysis);
        
        // Initialisation du gestionnaire de préférences
        preferencesManager = new PreferencesManager(this);
        
        // Séquence d'initialisation de l'activité
        initializeViews();        // Liaison des composants UI
        setupClickListeners();    // Configuration des interactions
        loadData();              // Chargement des données utilisateur
        updateUI();              // Mise à jour de l'affichage
        
        // Configuration de la barre d'outils avec bouton de retour
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Analyse Corporelle");
        }
    }    /**
     * Initialise tous les composants de l'interface utilisateur
     * Lie les variables Java aux éléments XML du layout
     */
    private void initializeViews() {
        // Éléments d'affichage des données IMC et catégorie
        tvBMI = findViewById(R.id.tvBMI);
        tvBMICategory = findViewById(R.id.tvBMICategory);
        
        // Éléments d'affichage des mesures corporelles
        tvWeight = findViewById(R.id.tvWeight);
        tvHeight = findViewById(R.id.tvHeight);
        tvBodyFat = findViewById(R.id.tvBodyFat);
        tvMuscleMass = findViewById(R.id.tvMuscleMass);
        
        // Éléments d'affichage du suivi et des objectifs
        tvLastUpdate = findViewById(R.id.tvLastUpdate);
        tvWeightGoal = findViewById(R.id.tvWeightGoal);
        tvProgressToGoal = findViewById(R.id.tvProgressToGoal);
        tvHealthScore = findViewById(R.id.tvHealthScore);
        
        // Barres de progression
        progressWeightGoal = findViewById(R.id.progressWeightGoal);
        progressBMI = findViewById(R.id.progressBMI);
        
        // Icône de statut
        imgBodyStatus = findViewById(R.id.imgBodyStatus);
        
        // Boutons d'édition des mesures
        btnEditWeight = findViewById(R.id.btnEditWeight);
        btnEditHeight = findViewById(R.id.btnEditHeight);
        btnEditBodyFat = findViewById(R.id.btnEditBodyFat);
        
        // Boutons d'actions principales
        btnUpdateMeasurements = findViewById(R.id.btnUpdateMeasurements);
        btnGoals = findViewById(R.id.btnGoals);
        btnHistory = findViewById(R.id.btnHistory);
        btnTips = findViewById(R.id.btnTips);
        
        // Cartes de sections
        cardHealthDashboard = findViewById(R.id.cardHealthDashboard);
        cardMeasurements = findViewById(R.id.cardMeasurements);
        cardGoals = findViewById(R.id.cardGoals);
    }    /**
     * Configure les écouteurs d'événements pour tous les boutons et cartes interactives
     * Définit les actions à exécuter lors des clics utilisateur
     */
    private void setupClickListeners() {
        // Boutons d'édition des mesures individuelles
        btnEditWeight.setOnClickListener(v -> showEditDialog("Poids", "kg", currentWeight, this::updateWeight));
        btnEditHeight.setOnClickListener(v -> showEditDialog("Taille", "cm", currentHeight, this::updateHeight));
        btnEditBodyFat.setOnClickListener(v -> showEditDialog("Masse Grasse", "%", currentBodyFat, this::updateBodyFat));
        
        // Boutons d'actions principales
        btnUpdateMeasurements.setOnClickListener(v -> showUpdateMeasurementsDialog());
        btnGoals.setOnClickListener(v -> showGoalsDialog());
        btnHistory.setOnClickListener(v -> showHistory());
        btnTips.setOnClickListener(v -> showHealthTips());
        
        // Interaction avec le tableau de bord santé
        cardHealthDashboard.setOnClickListener(v -> showHealthDetails());
    }
      /**
     * Charge toutes les données utilisateur depuis les préférences
     * Effectue les calculs automatiques (IMC, masse musculaire, score de santé)
     */
    private void loadData() {
        // Chargement des données corporelles depuis les préférences
        currentWeight = preferencesManager.getCurrentWeight();
        currentHeight = preferencesManager.getCurrentHeight();
        currentBodyFat = preferencesManager.getBodyFatPercentage();
        currentMuscleMass = preferencesManager.getMuscleMass();
        weightGoal = preferencesManager.getWeightGoal();
        
        // Calcul de l'IMC si on a le poids et la taille
        if (currentWeight > 0 && currentHeight > 0) {
            double heightInMeters = currentHeight / 100.0; // Conversion cm -> m
            bmi = currentWeight / (heightInMeters * heightInMeters); // Formule IMC = poids/taille²
        }
        
        // Calcul estimé de la masse musculaire si on a le poids et le % de graisse
        if (currentWeight > 0 && currentBodyFat > 0) {
            // Calcul simplifié : masse maigre * facteur muscle (≈50% de la masse maigre)
            currentMuscleMass = currentWeight * (1 - currentBodyFat / 100) * 0.5;
            preferencesManager.setMuscleMass(currentMuscleMass);
        }
        
        // Calcul du score de santé global
        calculateHealthScore();
    }
      /**
     * Calcule le score de santé global basé sur plusieurs critères
     * Score sur 100 points répartis comme suit :
     * - IMC : 30 points max
     * - Composition corporelle : 25 points max  
     * - Progression vers l'objectif : 25 points max
     * - Régularité du suivi : 20 points max
     */
    private void calculateHealthScore() {
        healthScore = 0;
        
        // === SCORE IMC (0-30 points) ===
        if (bmi > 0) {
            if (bmi >= 18.5 && bmi <= 24.9) {
                healthScore += 30; // IMC optimal (normal)
            } else if (bmi >= 17.5 && bmi <= 27.0) {
                healthScore += 20; // IMC correct (légèrement en dehors de la normale)
            } else if (bmi >= 16.0 && bmi <= 30.0) {
                healthScore += 10; // IMC acceptable (modérément éloigné)
            }
            // Sinon 0 point (IMC très éloigné de la normale)
        }
        
        // === SCORE COMPOSITION CORPORELLE (0-25 points) ===
        if (currentBodyFat > 0) {
            if (currentBodyFat >= 10 && currentBodyFat <= 20) {
                healthScore += 25; // Taux de graisse optimal
            } else if (currentBodyFat >= 8 && currentBodyFat <= 25) {
                healthScore += 15; // Taux de graisse correct
            } else if (currentBodyFat >= 5 && currentBodyFat <= 30) {
                healthScore += 10; // Taux de graisse acceptable
            }
        }
        
        // === SCORE PROGRESSION OBJECTIF (0-25 points) ===
        if (weightGoal > 0 && currentWeight > 0) {
            // Calcul de l'écart relatif par rapport à l'objectif
            double progressPercent = Math.abs(currentWeight - weightGoal) / weightGoal;
            if (progressPercent <= 0.05) {        // À moins de 5% de l'objectif
                healthScore += 25;
            } else if (progressPercent <= 0.10) { // À moins de 10% de l'objectif
                healthScore += 15;
            } else if (progressPercent <= 0.20) { // À moins de 20% de l'objectif
                healthScore += 10;
            }
        }
        
        // === SCORE RÉGULARITÉ (0-20 points) ===
        long lastUpdate = preferencesManager.getLastBodyAnalysisUpdate();
        if (lastUpdate > 0) {
            // Calcul du nombre de jours depuis la dernière mise à jour
            long daysSinceUpdate = (System.currentTimeMillis() - lastUpdate) / (24 * 60 * 60 * 1000);
            if (daysSinceUpdate <= 7) {      // Mis à jour cette semaine
                healthScore += 20;
            } else if (daysSinceUpdate <= 14) { // Mis à jour dans les 2 semaines
                healthScore += 10;
            } else if (daysSinceUpdate <= 30) { // Mis à jour ce mois-ci
                healthScore += 5;
            }
        }
        
        // Plafonner le score à 100 points maximum
        healthScore = Math.min(100, healthScore);
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
