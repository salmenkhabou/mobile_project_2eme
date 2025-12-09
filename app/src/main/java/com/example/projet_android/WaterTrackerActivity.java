package com.example.projet_android;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.projet_android.utils.PreferencesManager;

/**
 * Activité de suivi de l'hydratation quotidienne
 * 
 * Cette activité permet aux utilisateurs de :
 * - Suivre leur consommation d'eau quotidienne
 * - Définir des objectifs d'hydratation personnalisés
 * - Visualiser leur progression vers l'objectif quotidien
 * - Recevoir des conseils d'hydratation personnalisés
 * - Gagner des points wellness en atteignant les objectifs
 * 
 * Fonctionnalités principales :
 * - Compteur de verres d'eau avec ajout/retrait simple
 * - Barre de progression visuelle
 * - Objectifs configurables (6-12 verres par jour)
 * - Conseils adaptatifs selon le niveau d'hydratation
 * - Intégration avec le système de récompenses
 * 
 * @author Équipe de développement Health Tracker
 * @version 2.1
 * @since 2.1
 */
public class WaterTrackerActivity extends AppCompatActivity {
    
    // === COMPOSANTS DE L'INTERFACE UTILISATEUR ===
    
    /** Éléments d'affichage de la progression */
    private TextView tvWaterProgress, tvWaterGoal, tvWaterTips;
    private ProgressBar progressWater;
    
    /** Boutons d'interaction */
    private Button btnAddGlass, btnRemoveGlass, btnSetGoal;
    
    /** Cartes organisationnelles de l'interface */
    private CardView cardProgress, cardTips;
    
    // === GESTIONNAIRES ET DONNÉES ===
    
    /** Gestionnaire des préférences utilisateur */
    private PreferencesManager preferencesManager;
    
    /** Données de suivi d'hydratation */
    private int currentIntake = 0;    // Nombre de verres bus aujourd'hui
    private int dailyGoal = 8;        // Objectif quotidien en verres (défaut: 8)
      /**
     * Méthode d'initialisation de l'activité
     * Configure l'interface, charge les données et prépare les interactions
     * 
     * @param savedInstanceState État sauvegardé de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_tracker);
        
        // Initialisation du gestionnaire de préférences
        preferencesManager = new PreferencesManager(this);
        
        // Séquence d'initialisation
        initializeViews();        // Liaison des composants UI
        loadData();              // Chargement des données sauvegardées
        updateUI();              // Mise à jour de l'affichage
        setupClickListeners();   // Configuration des interactions
        
        // Configuration de la barre d'outils
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Suivi Hydratation");
        }
    }
    
    private void initializeViews() {
        tvWaterProgress = findViewById(R.id.tvWaterProgress);
        tvWaterGoal = findViewById(R.id.tvWaterGoal);
        tvWaterTips = findViewById(R.id.tvWaterTips);
        progressWater = findViewById(R.id.progressWater);
        btnAddGlass = findViewById(R.id.btnAddGlass);
        btnRemoveGlass = findViewById(R.id.btnRemoveGlass);
        btnSetGoal = findViewById(R.id.btnSetGoal);
        cardProgress = findViewById(R.id.cardProgress);
        cardTips = findViewById(R.id.cardTips);
    }
    
    private void loadData() {
        currentIntake = preferencesManager.getDailyWaterIntake();
        dailyGoal = preferencesManager.getWaterGoal();
    }
    
    private void updateUI() {
        tvWaterProgress.setText(currentIntake + " / " + dailyGoal + " glasses");
        tvWaterGoal.setText("Daily Goal: " + dailyGoal + " glasses");
        
        // Update progress bar
        int progress = (int) ((float) currentIntake / dailyGoal * 100);
        progressWater.setProgress(Math.min(progress, 100));
        
        // Update tips
        updateTips();
        
        // Check if goal is reached
        if (currentIntake >= dailyGoal) {
            Toast.makeText(this, "🎉 Daily water goal achieved!", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateTips() {
        String tips = "💧 Stay Hydrated!\n\n";
        
        if (currentIntake < dailyGoal / 3) {
            tips += "• Start your day with a glass of water\n";
            tips += "• Add lemon for extra flavor\n";
            tips += "• Set hourly reminders";
        } else if (currentIntake < (dailyGoal * 2) / 3) {
            tips += "• You're doing great! Keep going\n";
            tips += "• Drink before and after meals\n";
            tips += "• Carry a water bottle";
        } else if (currentIntake < dailyGoal) {
            tips += "• Almost there! Just a few more glasses\n";
            tips += "• Drink herbal tea for variety\n";
            tips += "• Monitor urine color for hydration";
        } else {
            tips += "• Excellent hydration! ✅\n";
            tips += "• Maintain this throughout the day\n";
            tips += "• You're setting a great example!";
        }
        
        tvWaterTips.setText(tips);
    }
    
    private void setupClickListeners() {
        btnAddGlass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIntake++;
                saveData();
                updateUI();
                
                if (currentIntake == dailyGoal) {
                    preferencesManager.addWellnessPoints(10);
                    Toast.makeText(WaterTrackerActivity.this, "🏆 Goal achieved! +10 wellness points", Toast.LENGTH_LONG).show();
                }
            }
        });
        
        btnRemoveGlass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIntake > 0) {
                    currentIntake--;
                    saveData();
                    updateUI();
                }
            }
        });
        
        btnSetGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGoalDialog();
            }
        });
    }
    
    private void showGoalDialog() {
        String[] options = {"6 glasses", "7 glasses", "8 glasses", "9 glasses", "10 glasses", "12 glasses"};
        int[] values = {6, 7, 8, 9, 10, 12};
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Set Daily Water Goal");
        builder.setItems(options, (dialog, which) -> {
            dailyGoal = values[which];
            preferencesManager.setWaterGoal(dailyGoal);
            updateUI();
            Toast.makeText(this, "Goal updated to " + dailyGoal + " glasses", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }
    
    private void saveData() {
        preferencesManager.setDailyWaterIntake(currentIntake);
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
