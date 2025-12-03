package com.example.projet_android.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    
    private static final String PREFS_NAME = "health_tracker_prefs";
    
    // Keys pour les préférences
    private static final String KEY_DAILY_STEPS_GOAL = "daily_steps_goal";
    private static final String KEY_DAILY_CALORIES_GOAL = "daily_calories_goal";
    private static final String KEY_DAILY_SLEEP_GOAL = "daily_sleep_goal";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_WATER_REMINDERS_ENABLED = "water_reminders_enabled";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_AGE = "user_age";
    private static final String KEY_USER_WEIGHT = "user_weight";    
    private static final String KEY_USER_HEIGHT = "user_height";
    private static final String KEY_USER_LOGGED_IN = "user_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    
    // Keys pour la synchronisation
    private static final String KEY_LAST_SYNC_TIME = "last_sync_time";
    private static final String KEY_AUTO_SYNC_ENABLED = "auto_sync_enabled";
    private static final String KEY_DEMO_MODE = "demo_mode";
    
    // Valeurs par défaut
    private static final int DEFAULT_STEPS_GOAL = 10000;
    private static final int DEFAULT_CALORIES_GOAL = 2000;
    private static final float DEFAULT_SLEEP_GOAL = 8.0f;
    
    private SharedPreferences preferences;
    
    public PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    // Objectifs de santé
    public int getDailyStepsGoal() {
        return preferences.getInt(KEY_DAILY_STEPS_GOAL, DEFAULT_STEPS_GOAL);
    }
    
    public void setDailyStepsGoal(int goal) {
        preferences.edit().putInt(KEY_DAILY_STEPS_GOAL, goal).apply();
    }
    
    public int getDailyCaloriesGoal() {
        return preferences.getInt(KEY_DAILY_CALORIES_GOAL, DEFAULT_CALORIES_GOAL);
    }
    
    public void setDailyCaloriesGoal(int goal) {
        preferences.edit().putInt(KEY_DAILY_CALORIES_GOAL, goal).apply();
    }
    
    public float getDailySleepGoal() {
        return preferences.getFloat(KEY_DAILY_SLEEP_GOAL, DEFAULT_SLEEP_GOAL);
    }
    
    public void setDailySleepGoal(float goal) {
        preferences.edit().putFloat(KEY_DAILY_SLEEP_GOAL, goal).apply();
    }
    
    // Paramètres de notifications
    public boolean areNotificationsEnabled() {
        return preferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }
    
    public void setNotificationsEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }
    
    public boolean areWaterRemindersEnabled() {
        return preferences.getBoolean(KEY_WATER_REMINDERS_ENABLED, true);
    }
    
    public void setWaterRemindersEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_WATER_REMINDERS_ENABLED, enabled).apply();
    }
    
    // Premier lancement
    public boolean isFirstLaunch() {
        return preferences.getBoolean(KEY_FIRST_LAUNCH, true);
    }
    
    public void setFirstLaunchCompleted() {
        preferences.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
    }
    
    // Informations utilisateur
    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "");
    }
    
    public void setUserName(String name) {
        preferences.edit().putString(KEY_USER_NAME, name).apply();
    }
    
    public int getUserAge() {
        return preferences.getInt(KEY_USER_AGE, 0);
    }
    
    public void setUserAge(int age) {
        preferences.edit().putInt(KEY_USER_AGE, age).apply();
    }
    
    public float getUserWeight() {
        return preferences.getFloat(KEY_USER_WEIGHT, 0f);
    }
    
    public void setUserWeight(float weight) {
        preferences.edit().putFloat(KEY_USER_WEIGHT, weight).apply();
    }
    
    public float getUserHeight() {
        return preferences.getFloat(KEY_USER_HEIGHT, 0f);
    }
    
    public void setUserHeight(float height) {
        preferences.edit().putFloat(KEY_USER_HEIGHT, height).apply();
    }
    
    // Méthodes d'authentification
    public boolean isUserLoggedIn() {
        return preferences.getBoolean(KEY_USER_LOGGED_IN, false);
    }
    
    public void setUserLoggedIn(boolean loggedIn) {
        preferences.edit().putBoolean(KEY_USER_LOGGED_IN, loggedIn).apply();
    }
    
    public String getUserId() {
        return preferences.getString(KEY_USER_ID, "");
    }
    
    public void setUserId(String userId) {
        preferences.edit().putString(KEY_USER_ID, userId).apply();
    }
    
    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, "");
    }
    
    public void setUserEmail(String email) {
        preferences.edit().putString(KEY_USER_EMAIL, email).apply();
    }
    
    public void logout() {
        preferences.edit()
                .putBoolean(KEY_USER_LOGGED_IN, false)
                .putString(KEY_USER_ID, "")
                .putString(KEY_USER_EMAIL, "")
                .apply();
    }
    
    // Méthodes de synchronisation
    public long getLastSyncTime() {
        return preferences.getLong(KEY_LAST_SYNC_TIME, 0);
    }
    
    public void setLastSyncTime(long timestamp) {
        preferences.edit().putLong(KEY_LAST_SYNC_TIME, timestamp).apply();
    }
    
    public boolean isAutoSyncEnabled() {
        return preferences.getBoolean(KEY_AUTO_SYNC_ENABLED, true);
    }
    
    public void setAutoSyncEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_AUTO_SYNC_ENABLED, enabled).apply();
    }
    
    // Méthodes pour le mode démo
    public boolean isDemoMode() {
        return preferences.getBoolean(KEY_DEMO_MODE, false);
    }
    
    public void setDemoMode(boolean demoMode) {
        preferences.edit().putBoolean(KEY_DEMO_MODE, demoMode).apply();
    }
    
    // Méthode utilitaire pour vérifier si une synchronisation est nécessaire
    public boolean needsSync() {
        long lastSync = getLastSyncTime();
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - lastSync;
        
        // Synchroniser si plus de 30 minutes depuis la dernière sync
        return timeDifference > (30 * 60 * 1000);
    }
    
    // Méthodes utilitaires
    public void clearAllPreferences() {
        preferences.edit().clear().apply();
    }
    
    public boolean hasUserProfile() {
        return !getUserName().isEmpty() && getUserAge() > 0;
    }
    
    // Calcul des recommandations personnalisées
    public int getPersonalizedCaloriesGoal() {
        if (!hasUserProfile()) {
            return DEFAULT_CALORIES_GOAL;
        }
        
        // Formule simple basée sur l'âge, le poids et la taille
        // Cette formule est simplifiée, dans une vraie app vous utiliseriez des formules plus précises
        float bmr;
        float weight = getUserWeight();
        float height = getUserHeight();
        int age = getUserAge();
        
        if (weight > 0 && height > 0 && age > 0) {
            // Formule de Harris-Benedict simplifiée (pour homme, vous pourriez ajouter le genre)
            bmr = (float) (88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age));
            return (int) (bmr * 1.55); // Facteur d'activité modéré
        }
        
        return DEFAULT_CALORIES_GOAL;
    }
    
    public int getPersonalizedStepsGoal() {
        if (!hasUserProfile()) {
            return DEFAULT_STEPS_GOAL;
        }
        
        int age = getUserAge();
        
        // Adapter l'objectif de pas selon l'âge
        if (age < 30) {
            return 12000;
        } else if (age < 50) {
            return 10000;
        } else if (age < 65) {
            return 8000;
        } else {
            return 6000;
        }
    }
    
    // Méthodes pour le suivi des notifications d'objectifs quotidiens
    
    /**
     * Vérifie si l'objectif a déjà été notifié aujourd'hui
     */
    public boolean isGoalNotifiedToday(String goalType) {
        String today = java.text.DateFormat.getDateInstance().format(new java.util.Date());
        String key = "goal_notified_" + goalType + "_" + today;
        return preferences.getBoolean(key, false);
    }
    
    /**
     * Marque un objectif comme notifié pour aujourd'hui
     */
    public void setGoalNotifiedToday(String goalType, boolean notified) {
        String today = java.text.DateFormat.getDateInstance().format(new java.util.Date());
        String key = "goal_notified_" + goalType + "_" + today;
        preferences.edit().putBoolean(key, notified).apply();
    }
    
    /**
     * Nettoie les anciennes notifications (à appeler périodiquement)
     */
    public void cleanupOldGoalNotifications() {
        SharedPreferences.Editor editor = preferences.edit();
        
        // Supprimer les notifications de plus de 7 jours
        java.util.Map<String, ?> allPrefs = preferences.getAll();
        for (String key : allPrefs.keySet()) {
            if (key.startsWith("goal_notified_")) {
                // Logique de nettoyage des anciennes clés
                editor.remove(key);
            }
        }
        
        editor.apply();
    }
}
