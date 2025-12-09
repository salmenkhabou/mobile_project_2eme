package com.example.projet_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import com.example.projet_android.services.GoogleFitManager;
import com.example.projet_android.services.NotificationHelper;
import com.example.projet_android.services.AuthManager;
import com.example.projet_android.services.DataSyncService;
import com.example.projet_android.services.HealthNotificationManager;
import com.example.projet_android.services.HealthNotificationService;
import com.example.projet_android.utils.PreferencesManager;
import com.example.projet_android.database.DatabaseManager;
import com.example.projet_android.database.entities.HealthData;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;

/**
 * Activité principale du tableau de bord de l'application Health Tracker
 * 
 * Cette activité constitue le cœur de l'application et fournit :
 * - Vue d'ensemble des données de santé du jour (pas, calories, sommeil)
 * - Navigation vers toutes les fonctionnalités de l'app
 * - Intégration Google Fit pour synchronisation automatique
 * - Système de notifications intelligentes
 * - Gestion de l'authentification utilisateur
 * - Tableau de bord wellness avec points et objectifs
 * 
 * Fonctionnalités intégrées :
 * - Suivi d'activité en temps réel
 * - Synchronisation avec base de données locale
 * - Mode démo pour tests sans Google Fit
 * - Notifications d'objectifs atteints
 * - Navigation vers modules spécialisés (nutrition, sommeil, humeur, etc.)
 * 
 * @author Équipe de développement Health Tracker
 * @version 2.1
 * @since 1.0
 */
public class MainActivity2 extends AppCompatActivity implements GoogleFitManager.FitnessDataListener, GoogleFitManager.AuthenticationListener {
    
    private TextView welcomeTextView;
    private TextView stepsTextView;
    private TextView caloriesTextView;    private TextView sleepTextView;
    private MaterialButton viewStepsButton;
    private MaterialButton viewCaloriesButton;    
    private MaterialButton viewSleepButton;
    private MaterialButton calendarButton;    private TextView nutritionButton;
    private TextView settingsButton;
    private TextView wellnessHubButton;
    private TextView weatherButton;
    private TextView gymFinderButton;
    private MaterialButton connectGoogleFitButton;
    
    // New card variables
    private View cardWaterTracker;
    private View cardMoodTracker;
    private View cardSleepTracker;
    private TextView authStatusTextView;
    private GoogleFitManager googleFitManager;
    private PreferencesManager preferencesManager;
    private NotificationHelper notificationHelper;    private AuthManager authManager;
    private DatabaseManager databaseManager;
    private DataSyncService dataSyncService;
    private HealthNotificationManager healthNotificationManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);        
        preferencesManager = new PreferencesManager(this);
        notificationHelper = new NotificationHelper(this);        authManager = new AuthManager(this);
        databaseManager = DatabaseManager.getInstance(this);
        dataSyncService = new DataSyncService(this);
        healthNotificationManager = new HealthNotificationManager(this);
        
        // S'assurer qu'un utilisateur par défaut existe
        ensureDefaultUser();
        
        initViews();
        setupClickListeners();
        setupGoogleFit();
        setupNotifications();
        updateDashboard();
        
        // Personnaliser le message de bienvenue
        updateWelcomeMessage();
    }
      private void initViews() {
        welcomeTextView = findViewById(R.id.tv_welcome);
        stepsTextView = findViewById(R.id.tv_steps_count);
        caloriesTextView = findViewById(R.id.tv_calories_count);
        sleepTextView = findViewById(R.id.tv_sleep_count);
        viewStepsButton = findViewById(R.id.btn_view_steps);        viewCaloriesButton = findViewById(R.id.btn_view_calories);        
        viewSleepButton = findViewById(R.id.btn_view_sleep);        
        // calendarButton = findViewById(R.id.btn_calendar); // TODO: Add calendar button to layout
        nutritionButton = findViewById(R.id.btn_nutrition);
        settingsButton = findViewById(R.id.btn_settings);
        wellnessHubButton = findViewById(R.id.btn_wellness_hub);
        weatherButton = findViewById(R.id.btn_weather);        gymFinderButton = findViewById(R.id.btn_gym_finder);
        
        // Initialize new card views
        cardWaterTracker = findViewById(R.id.card_water_tracker);
        cardMoodTracker = findViewById(R.id.card_mood_tracker);
        cardSleepTracker = findViewById(R.id.card_sleep_tracker);
        
        // Try to find the optional Google Fit connection elements
        connectGoogleFitButton = findViewById(R.id.btn_connect_google_fit);
        authStatusTextView = findViewById(R.id.tv_auth_status);
    }
      private void setupClickListeners() {
        if (viewStepsButton != null) {
            viewStepsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDetailActivity("Pas");
                }
            });        }
        
        if (viewCaloriesButton != null) {
            viewCaloriesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDetailActivity("Calories");
                }
            });        }
        
        if (viewSleepButton != null) {
            viewSleepButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDetailActivity("Sommeil");
                }
            });
        }
        
        // Only set calendar button listener if button exists
        if (calendarButton != null) {
            calendarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity2.this, CalendarActivity.class);
                    startActivity(intent);
                }
            });        }
        
        if (nutritionButton != null) {
            nutritionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity2.this, NutritionActivity.class);
                    startActivity(intent);
                }
            });
        }
        
        if (settingsButton != null) {
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity2.this, SettingsActivity.class);
                    startActivity(intent);
                }
            });        }
        
        // Ajouter un listener long click pour le logout sur le bouton settings
        if (settingsButton != null) {
            settingsButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showLogoutDialog();
                    return true;
                }
            });
        }
        
        // Nouveaux boutons wellness
        if (wellnessHubButton != null) {
            wellnessHubButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity2.this, WellnessHubActivity.class);
                    startActivity(intent);
                }
            });
        }
          if (weatherButton != null) {
            weatherButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity2.this, WeatherActivity.class);
                    startActivity(intent);
                }
            });
        }
          if (gymFinderButton != null) {
            gymFinderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Toast.makeText(MainActivity2.this, "Launching Gym Finder...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity2.this, GymFinderActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity2.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });
        } else {
            // Debug: Le bouton n'est pas trouvé
            Toast.makeText(this, "Gym Finder button not found!", Toast.LENGTH_LONG).show();
        }
          // New card click listeners
        if (cardWaterTracker != null) {
            cardWaterTracker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity2.this, WaterTrackerActivity.class);
                    startActivity(intent);
                }
            });
        }
        
        if (cardMoodTracker != null) {
            cardMoodTracker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity2.this, MoodTrackerActivity.class);
                    startActivity(intent);
                }
            });
        }
        
        if (cardSleepTracker != null) {
            cardSleepTracker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity2.this, SleepTrackerActivity.class);
                    startActivity(intent);
                }
            });
        }
        
        // Configuration du bouton de connexion Google Fit (optionnel)
        if (connectGoogleFitButton != null) {
            connectGoogleFitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectToGoogleFit();
                }
            });
        }
    }
    
    private void openDetailActivity(String activityType) {
        Intent intent = new Intent(MainActivity2.this, DetailActivity.class);
        intent.putExtra("ACTIVITY_TYPE", activityType);
        startActivity(intent);
    }
      private void setupGoogleFit() {
        googleFitManager = new GoogleFitManager(this);
        updateAuthStatus();
        
        // Si pas de connexion Google Fit, utiliser le mode démo
        if (!googleFitManager.isSignedIn() || !googleFitManager.hasPermissions()) {
            googleFitManager.enableDemoMode();
            updateAuthStatus();
        }
    }
    
    private void connectToGoogleFit() {
        if (!googleFitManager.isSignedIn()) {
            googleFitManager.signIn(this, this);
        } else if (!googleFitManager.hasPermissions()) {
            googleFitManager.requestPermissions(this);
        } else {
            Toast.makeText(this, "Déjà connecté à Google Fit", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateAuthStatus() {
        String status;
        if (googleFitManager.isSignedIn() && googleFitManager.hasPermissions()) {
            status = "🟢 Connecté à Google Fit";
            if (connectGoogleFitButton != null) {
                connectGoogleFitButton.setText("Connecté");
                connectGoogleFitButton.setEnabled(false);
            }
        } else if (googleFitManager.isDemoMode()) {
            status = "🟡 Mode démo activé";
            if (connectGoogleFitButton != null) {
                connectGoogleFitButton.setText("Se connecter");
                connectGoogleFitButton.setEnabled(true);
            }
        } else {
            status = "🔴 Non connecté";
            if (connectGoogleFitButton != null) {
                connectGoogleFitButton.setText("Se connecter");
                connectGoogleFitButton.setEnabled(true);
            }
        }
        
        if (authStatusTextView != null) {
            authStatusTextView.setText(status);
        }
    }
      private void ensureDefaultUser() {
        String userId = preferencesManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            // Créer un utilisateur par défaut pour le mode démo
            userId = "default_user";
            preferencesManager.setUserId(userId);
            preferencesManager.setUserName("Utilisateur Demo");
            
            // Créer l'utilisateur dans la base de données
            databaseManager.ensureUserExists(userId);
            
            // Informer l'utilisateur du mode démo
            Toast.makeText(this, "Mode démo activé - Données simulées", Toast.LENGTH_LONG).show();
        }
    }
      private void syncAndLoadData() {
        // Une seule synchronisation au lieu de deux
        dataSyncService.syncAllData(new DataSyncService.SyncListener() {
            @Override
            public void onSyncStarted() {
                // Optionnel : afficher un indicateur de chargement
            }
            
            @Override
            public void onSyncCompleted() {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity2.this, "Données synchronisées", Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onSyncError(String error) {
                runOnUiThread(() -> {
                    // En cas d'erreur, utiliser les données de fallback
                    googleFitManager.getSimulatedData(MainActivity2.this);
                });
            }
            
            @Override
            public void onDataUpdated(HealthData healthData) {
                // Les données seront automatiquement mises à jour via les observers
            }
        });
    }
    
    private void observeDatabaseData() {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            // Observer les données de santé du jour
            databaseManager.getTodaysHealthData(userId).observe(this, new Observer<HealthData>() {
                @Override
                public void onChanged(HealthData healthData) {
                    if (healthData != null) {
                        updateUIWithHealthData(healthData);
                    }
                }
            });
        }
    }
    
    private void updateUIWithHealthData(HealthData healthData) {
        // Mettre à jour les TextViews avec les données de la base
        stepsTextView.setText(String.format("%,d pas", healthData.steps));
        caloriesTextView.setText(String.format("%,d cal", healthData.calories));
        
        // Afficher les heures de sommeil
        int hours = (int) healthData.sleepHours;
        int minutes = (int) ((healthData.sleepHours - hours) * 60);
        sleepTextView.setText(String.format("%dh %02dmin", hours, minutes));
    }
      private void setupNotifications() {
        // Configuration des notifications quotidiennes complètes
        if (preferencesManager.areNotificationsEnabled()) {
            // Activer le système complet de notifications quotidiennes
            healthNotificationManager.enableDailyNotifications();
            
            // Anciennes notifications pour compatibilité
            notificationHelper.scheduleStepsReminder();
        }
        
        if (preferencesManager.areWaterRemindersEnabled()) {
            notificationHelper.scheduleWaterReminders();
        }
        
        // Log pour confirmer l'activation des notifications
        android.util.Log.d("MainActivity2", "Système de notifications quotidiennes activé");
    }
    
    private void updateWelcomeMessage() {
        String userName = preferencesManager.getUserName();
        if (!userName.isEmpty()) {
            welcomeTextView.setText("Bonjour " + userName + " ! Voici votre résumé du jour");
        } else {
            welcomeTextView.setText("Bonjour ! Voici votre résumé du jour");
        }
    }
    
    private void updateDashboard() {
        updateWelcomeMessage();
        
        // Synchroniser et charger les données
        syncAndLoadData();
        
        // Observer les données de la base de données
        observeDatabaseData();
    }
    
    // Implémentation des méthodes de GoogleFitManager.FitnessDataListener    @Override
    public void onStepsReceived(int steps) {
        runOnUiThread(() -> {
            stepsTextView.setText(String.format("%,d pas", steps));
            // Vérifier si l'objectif de pas est atteint (10,000 pas)
            checkStepsGoal(steps);
        });
    }
      @Override
    public void onCaloriesReceived(int calories) {
        runOnUiThread(() -> {
            caloriesTextView.setText(String.format("%,d cal", calories));
            // Vérifier si l'objectif de calories est atteint (2000 calories)
            checkCaloriesGoal(calories);
        });
    }
      @Override
    public void onSleepReceived(float sleepHours) {
        int hours = (int) sleepHours;
        int minutes = (int) ((sleepHours - hours) * 60);
        runOnUiThread(() -> {
            sleepTextView.setText(String.format("%dh %02dmin", hours, minutes));
            // Vérifier si l'objectif de sommeil est atteint (7-8 heures)
            checkSleepGoal((int)sleepHours);
        });
    }
    
    @Override
    public void onError(String error) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Erreur: " + error, Toast.LENGTH_SHORT).show();
            // Utiliser des données par défaut en cas d'erreur
            stepsTextView.setText("10,000 pas");
            caloriesTextView.setText("1,900 cal");
            sleepTextView.setText("7h 30min");
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir les données quand l'utilisateur revient sur l'écran
        updateDashboard();
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
        // Déconnexion via AuthManager
        authManager.logout();
        
        // Effacer les préférences utilisateur
        preferencesManager.logout();
        
        // Naviguer vers l'écran de login
        Intent intent = new Intent(MainActivity2.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        
        Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == GoogleFitManager.GOOGLE_SIGN_IN_REQUEST_CODE) {
            googleFitManager.handleSignInResult(data, this);
        } else if (requestCode == 1000) { // GOOGLE_FIT_PERMISSIONS_REQUEST_CODE
            if (resultCode == RESULT_OK) {
                onSignInSuccess();
            } else {
                onSignInFailed("Permissions refusées");
            }
        }
    }
    
    // Implémentation des méthodes de GoogleFitManager.AuthenticationListener
    @Override
    public void onSignInRequired() {
        runOnUiThread(() -> {
            updateAuthStatus();
            Toast.makeText(this, "Connexion Google requise", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public void onSignInSuccess() {
        runOnUiThread(() -> {
            updateAuthStatus();
            Toast.makeText(this, "Connexion Google Fit réussie!", Toast.LENGTH_SHORT).show();
            // Relancer la synchronisation avec les vraies données
            syncAndLoadData();
        });
    }
    
    @Override
    public void onSignInFailed(String error) {
        runOnUiThread(() -> {
            updateAuthStatus();
            Toast.makeText(this, "Connexion échouée: " + error, Toast.LENGTH_LONG).show();
            // Continuer en mode démo
            googleFitManager.enableDemoMode();
            updateAuthStatus();
        });
    }
    
    @Override
    public void onDemoModeActivated() {
        runOnUiThread(() -> {
            updateAuthStatus();
            Toast.makeText(this, "Mode démo activé - Données simulées", Toast.LENGTH_SHORT).show();
        });
    }
    
    /**
     * Vérifie si l'objectif de pas quotidien est atteint
     */
    private void checkStepsGoal(int steps) {
        // Objectif standard de 10,000 pas
        if (steps >= 10000) {
            boolean alreadyNotified = preferencesManager.isGoalNotifiedToday("steps");
            if (!alreadyNotified) {
                HealthNotificationService.notifyGoalAchieved(this, "steps", steps);
                preferencesManager.setGoalNotifiedToday("steps", true);
                android.util.Log.d("MainActivity2", "Objectif de pas atteint: " + steps);
            }
        }
        
        // Objectifs progressifs pour encourager
        if (steps >= 5000 && steps < 10000) {
            boolean alreadyNotified = preferencesManager.isGoalNotifiedToday("steps_halfway");
            if (!alreadyNotified) {
                // Notification d'encouragement à mi-parcours
                preferencesManager.setGoalNotifiedToday("steps_halfway", true);
            }
        }
    }
    
    /**
     * Vérifie si l'objectif de calories quotidien est atteint
     */
    private void checkCaloriesGoal(int calories) {
        // Objectif standard de 2000 calories brûlées
        if (calories >= 2000) {
            boolean alreadyNotified = preferencesManager.isGoalNotifiedToday("calories");
            if (!alreadyNotified) {
                HealthNotificationService.notifyGoalAchieved(this, "calories", calories);
                preferencesManager.setGoalNotifiedToday("calories", true);
                android.util.Log.d("MainActivity2", "Objectif de calories atteint: " + calories);
            }
        }
    }
    
    /**
     * Vérifie si l'objectif de sommeil est atteint
     */
    private void checkSleepGoal(int sleepHours) {
        // Objectif de 7-9 heures de sommeil
        if (sleepHours >= 7 && sleepHours <= 9) {
            boolean alreadyNotified = preferencesManager.isGoalNotifiedToday("sleep");
            if (!alreadyNotified) {
                HealthNotificationService.notifyGoalAchieved(this, "sleep", sleepHours);
                preferencesManager.setGoalNotifiedToday("sleep", true);
                android.util.Log.d("MainActivity2", "Objectif de sommeil atteint: " + sleepHours + "h");
            }
        }
    }
    
    /**
     * Méthode publique pour déclencher une notification d'hydratation
     */
    public void notifyWaterGoalAchieved(int glassesCount) {
        HealthNotificationService.notifyGoalAchieved(this, "water", glassesCount);
        android.util.Log.d("MainActivity2", "Objectif d'hydratation atteint: " + glassesCount + " verres");
    }
}
