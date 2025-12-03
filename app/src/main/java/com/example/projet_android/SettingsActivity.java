package com.example.projet_android;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projet_android.services.AuthManager;
import com.example.projet_android.services.NotificationHelper;
import com.example.projet_android.services.HealthNotificationManager;
import com.example.projet_android.utils.PreferencesManager;

public class SettingsActivity extends AppCompatActivity {
    
    private EditText etUserName;
    private EditText etUserAge;
    private EditText etUserWeight;
    private EditText etUserHeight;
    private EditText etStepsGoal;
    private EditText etCaloriesGoal;
    private EditText etSleepGoal;
    private Switch switchNotifications;
    private Switch switchWaterReminders;    
    private Button btnSaveSettings;
    private Button btnLogout;
    private TextView tvRecommendations;
      private PreferencesManager preferencesManager;
    private NotificationHelper notificationHelper;
    private HealthNotificationManager healthNotificationManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
          preferencesManager = new PreferencesManager(this);
        notificationHelper = new NotificationHelper(this);
        healthNotificationManager = new HealthNotificationManager(this);
        
        initViews();
        loadCurrentSettings();
        setupClickListeners();
    }
    
    private void initViews() {
        etUserName = findViewById(R.id.et_user_name);
        etUserAge = findViewById(R.id.et_user_age);
        etUserWeight = findViewById(R.id.et_user_weight);
        etUserHeight = findViewById(R.id.et_user_height);
        etStepsGoal = findViewById(R.id.et_steps_goal);
        etCaloriesGoal = findViewById(R.id.et_calories_goal);
        etSleepGoal = findViewById(R.id.et_sleep_goal);        
        switchNotifications = findViewById(R.id.switch_notifications);
        switchWaterReminders = findViewById(R.id.switch_water_reminders);
        btnSaveSettings = findViewById(R.id.btn_save_settings);
        btnLogout = findViewById(R.id.btn_logout);
        tvRecommendations = findViewById(R.id.tv_recommendations);
    }
    
    private void loadCurrentSettings() {
        // Charger les informations utilisateur
        etUserName.setText(preferencesManager.getUserName());
        
        int age = preferencesManager.getUserAge();
        if (age > 0) {
            etUserAge.setText(String.valueOf(age));
        }
        
        float weight = preferencesManager.getUserWeight();
        if (weight > 0) {
            etUserWeight.setText(String.valueOf(weight));
        }
        
        float height = preferencesManager.getUserHeight();
        if (height > 0) {
            etUserHeight.setText(String.valueOf(height));
        }
        
        // Charger les objectifs
        etStepsGoal.setText(String.valueOf(preferencesManager.getDailyStepsGoal()));
        etCaloriesGoal.setText(String.valueOf(preferencesManager.getDailyCaloriesGoal()));
        etSleepGoal.setText(String.valueOf(preferencesManager.getDailySleepGoal()));
        
        // Charger les paramètres de notifications
        switchNotifications.setChecked(preferencesManager.areNotificationsEnabled());
        switchWaterReminders.setChecked(preferencesManager.areWaterRemindersEnabled());
        
        updateRecommendations();
    }
    
    private void setupClickListeners() {        
        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
        
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
        
        // Mettre à jour les recommandations quand l'utilisateur change ses infos
        View.OnFocusChangeListener updateRecommendationsListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateRecommendations();
                }
            }
        };
        
        etUserAge.setOnFocusChangeListener(updateRecommendationsListener);
        etUserWeight.setOnFocusChangeListener(updateRecommendationsListener);
        etUserHeight.setOnFocusChangeListener(updateRecommendationsListener);
    }
    
    private void saveSettings() {
        try {
            // Sauvegarder les informations utilisateur
            String name = etUserName.getText().toString().trim();
            preferencesManager.setUserName(name);
            
            String ageStr = etUserAge.getText().toString().trim();
            if (!ageStr.isEmpty()) {
                int age = Integer.parseInt(ageStr);
                if (age > 0 && age < 120) {
                    preferencesManager.setUserAge(age);
                } else {
                    throw new NumberFormatException("Âge invalide");
                }
            }
            
            String weightStr = etUserWeight.getText().toString().trim();
            if (!weightStr.isEmpty()) {
                float weight = Float.parseFloat(weightStr);
                if (weight > 0 && weight < 500) {
                    preferencesManager.setUserWeight(weight);
                } else {
                    throw new NumberFormatException("Poids invalide");
                }
            }
            
            String heightStr = etUserHeight.getText().toString().trim();
            if (!heightStr.isEmpty()) {
                float height = Float.parseFloat(heightStr);
                if (height > 0 && height < 300) {
                    preferencesManager.setUserHeight(height);
                } else {
                    throw new NumberFormatException("Taille invalide");
                }
            }
            
            // Sauvegarder les objectifs
            String stepsGoalStr = etStepsGoal.getText().toString().trim();
            if (!stepsGoalStr.isEmpty()) {
                int stepsGoal = Integer.parseInt(stepsGoalStr);
                if (stepsGoal > 0) {
                    preferencesManager.setDailyStepsGoal(stepsGoal);
                }
            }
            
            String caloriesGoalStr = etCaloriesGoal.getText().toString().trim();
            if (!caloriesGoalStr.isEmpty()) {
                int caloriesGoal = Integer.parseInt(caloriesGoalStr);
                if (caloriesGoal > 0) {
                    preferencesManager.setDailyCaloriesGoal(caloriesGoal);
                }
            }
            
            String sleepGoalStr = etSleepGoal.getText().toString().trim();
            if (!sleepGoalStr.isEmpty()) {
                float sleepGoal = Float.parseFloat(sleepGoalStr);
                if (sleepGoal > 0 && sleepGoal < 24) {
                    preferencesManager.setDailySleepGoal(sleepGoal);
                }
            }
            
            // Sauvegarder les paramètres de notifications
            preferencesManager.setNotificationsEnabled(switchNotifications.isChecked());
            preferencesManager.setWaterRemindersEnabled(switchWaterReminders.isChecked());
              // Programmer les notifications selon les nouveaux paramètres
            if (switchNotifications.isChecked()) {
                // Activer le système complet de notifications quotidiennes
                healthNotificationManager.enableDailyNotifications();
                notificationHelper.scheduleStepsReminder();
                android.util.Log.d("SettingsActivity", "Notifications quotidiennes activées");
            } else {
                // Désactiver toutes les notifications quotidiennes
                healthNotificationManager.disableDailyNotifications();
                android.util.Log.d("SettingsActivity", "Notifications quotidiennes désactivées");
            }
            
            if (switchWaterReminders.isChecked()) {
                notificationHelper.scheduleWaterReminders();
            }
            
            Toast.makeText(this, "Paramètres sauvegardés avec succès!", Toast.LENGTH_SHORT).show();
            finish();
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Veuillez vérifier les valeurs saisies", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de la sauvegarde: " + e.getMessage(), 
                          Toast.LENGTH_LONG).show();
        }
    }
    
    private void updateRecommendations() {
        StringBuilder recommendations = new StringBuilder();
        recommendations.append("Recommandations personnalisées:\n\n");
        
        try {
            String ageStr = etUserAge.getText().toString().trim();
            String weightStr = etUserWeight.getText().toString().trim();
            String heightStr = etUserHeight.getText().toString().trim();
            
            if (!ageStr.isEmpty() && !weightStr.isEmpty() && !heightStr.isEmpty()) {
                int age = Integer.parseInt(ageStr);
                float weight = Float.parseFloat(weightStr);
                float height = Float.parseFloat(heightStr);
                
                // Calculer l'IMC
                float bmi = weight / ((height / 100) * (height / 100));
                
                recommendations.append(String.format("• IMC: %.1f ", bmi));
                if (bmi < 18.5) {
                    recommendations.append("(Insuffisance pondérale)\n");
                } else if (bmi < 25) {
                    recommendations.append("(Poids normal)\n");
                } else if (bmi < 30) {
                    recommendations.append("(Surpoids)\n");
                } else {
                    recommendations.append("(Obésité)\n");
                }
                
                // Recommandations d'objectifs
                PreferencesManager tempPrefs = new PreferencesManager(this);
                tempPrefs.setUserAge(age);
                tempPrefs.setUserWeight(weight);
                tempPrefs.setUserHeight(height);
                
                recommendations.append(String.format("• Objectif pas recommandé: %d\n", 
                                                   tempPrefs.getPersonalizedStepsGoal()));
                recommendations.append(String.format("• Objectif calories recommandé: %d\n", 
                                                   tempPrefs.getPersonalizedCaloriesGoal()));
            } else {
                recommendations.append("Complétez votre profil pour obtenir des recommandations personnalisées.");
            }
            
        } catch (NumberFormatException e) {
            recommendations.append("Valeurs invalides dans le profil.");
        }
        
        tvRecommendations.setText(recommendations.toString());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Déconnexion")
                .setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")
                .setPositiveButton("Oui", (dialog, which) -> performLogout())
                .setNegativeButton("Annuler", null)
                .show();
    }
    
    private void performLogout() {
        // Déconnexion
        AuthManager authManager = new AuthManager(this);
        authManager.logout();
        
        // Effacer les préférences utilisateur
        preferencesManager.logout();
        
        // Naviguer vers l'écran de login
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        
        Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();
    }
}
