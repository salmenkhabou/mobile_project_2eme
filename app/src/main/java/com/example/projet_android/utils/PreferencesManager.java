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
      // Keys pour les nouvelles fonctionnalités wellness
    private static final String KEY_WELLNESS_SCORE = "wellness_score";
    private static final String KEY_WELLNESS_STREAK = "wellness_streak";
    private static final String KEY_WELLNESS_POINTS = "wellness_points";
    private static final String KEY_DAILY_WATER_INTAKE = "daily_water_intake";
    private static final String KEY_LAST_NIGHT_SLEEP = "last_night_sleep";
    private static final String KEY_HAS_EATEN_VEGETABLES = "has_eaten_vegetables";
    private static final String KEY_HAS_EATEN_FRUITS = "has_eaten_fruits";
    private static final String KEY_HAS_MEDITATED = "has_meditated";
    private static final String KEY_HAS_RECORDED_MOOD = "has_recorded_mood";
    private static final String KEY_WEEKLY_WORKOUTS = "weekly_workouts";
    private static final String KEY_DAILY_STEPS = "daily_steps";
    
    // Keys pour le mood tracking
    private static final String KEY_DAILY_MOOD = "daily_mood";
    private static final String KEY_HAS_LOGGED_MOOD_TODAY = "has_logged_mood_today";
    
    // Keys pour les objectifs personnalisés
    private static final String KEY_WATER_GOAL = "water_goal";
    private static final int DEFAULT_WATER_GOAL = 8; // 8 verres d'eau par jour
    
    // Keys pour les données météo
    private static final String KEY_CACHED_WEATHER_DATA = "cached_weather_data";
    private static final String KEY_LAST_WEATHER_UPDATE = "last_weather_update";
    
    // Keys pour les défis
    private static final String KEY_CHALLENGE_PREFIX = "challenge_completed_";
    
    // Valeurs par défaut
    private static final int DEFAULT_STEPS_GOAL = 10000;
    private static final int DEFAULT_CALORIES_GOAL = 2000;
    private static final float DEFAULT_SLEEP_GOAL = 8.0f;
    
    private SharedPreferences preferences;
    
    public PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public SharedPreferences getSharedPreferences() {
        return preferences;
    }
    
    public SharedPreferences.Editor getEditor() {
        return preferences.edit();
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
    
    // === NOUVELLES MÉTHODES WELLNESS ===
    
    public int getWellnessScore() {
        return preferences.getInt(KEY_WELLNESS_SCORE, 0);
    }
    
    public void setWellnessScore(int score) {
        preferences.edit().putInt(KEY_WELLNESS_SCORE, score).apply();
    }
    
    public int getCurrentWellnessStreak() {
        return preferences.getInt(KEY_WELLNESS_STREAK, 0);
    }
    
    public void incrementWellnessStreak() {
        int currentStreak = getCurrentWellnessStreak();
        preferences.edit().putInt(KEY_WELLNESS_STREAK, currentStreak + 1).apply();
    }
    
    public void resetWellnessStreak() {
        preferences.edit().putInt(KEY_WELLNESS_STREAK, 0).apply();
    }
    
    public int getWellnessPoints() {
        return preferences.getInt(KEY_WELLNESS_POINTS, 0);
    }
    
    public void addWellnessPoints(int points) {
        int currentPoints = getWellnessPoints();
        preferences.edit().putInt(KEY_WELLNESS_POINTS, currentPoints + points).apply();
    }
    
    // === TRACKING QUOTIDIEN ===
    
    public int getDailyWaterIntake() {
        return preferences.getInt(KEY_DAILY_WATER_INTAKE, 0);
    }
    
    public void setDailyWaterIntake(int glasses) {
        preferences.edit().putInt(KEY_DAILY_WATER_INTAKE, glasses).apply();
    }
    
    public void incrementWaterIntake() {
        int current = getDailyWaterIntake();
        setDailyWaterIntake(current + 1);
    }
    
    public int getLastNightSleep() {
        return preferences.getInt(KEY_LAST_NIGHT_SLEEP, 0);
    }
    
    public void setLastNightSleep(int hours) {
        preferences.edit().putInt(KEY_LAST_NIGHT_SLEEP, hours).apply();
    }
    
    public boolean hasEatenVegetables() {
        return preferences.getBoolean(KEY_HAS_EATEN_VEGETABLES, false);
    }
    
    public void setHasEatenVegetables(boolean eaten) {
        preferences.edit().putBoolean(KEY_HAS_EATEN_VEGETABLES, eaten).apply();
    }
    
    public boolean hasEatenFruits() {
        return preferences.getBoolean(KEY_HAS_EATEN_FRUITS, false);
    }
    
    public void setHasEatenFruits(boolean eaten) {
        preferences.edit().putBoolean(KEY_HAS_EATEN_FRUITS, eaten).apply();
    }
    
    public boolean hasMeditated() {
        return preferences.getBoolean(KEY_HAS_MEDITATED, false);
    }
    
    public void setHasMeditated(boolean meditated) {
        preferences.edit().putBoolean(KEY_HAS_MEDITATED, meditated).apply();
    }
    
    public boolean hasRecordedMood() {
        return preferences.getBoolean(KEY_HAS_RECORDED_MOOD, false);
    }
    
    public void setHasRecordedMood(boolean recorded) {
        preferences.edit().putBoolean(KEY_HAS_RECORDED_MOOD, recorded).apply();
    }
    
    public int getWeeklyWorkouts() {
        return preferences.getInt(KEY_WEEKLY_WORKOUTS, 0);
    }
    
    public void incrementWeeklyWorkouts() {
        int current = getWeeklyWorkouts();
        preferences.edit().putInt(KEY_WEEKLY_WORKOUTS, current + 1).apply();
    }
    
    public int getDailySteps() {
        return preferences.getInt(KEY_DAILY_STEPS, 0);
    }
    
    public void setDailySteps(int steps) {
        preferences.edit().putInt(KEY_DAILY_STEPS, steps).apply();
    }
    
    // === GESTION DES DÉFIS ===
    
    public boolean isChallengeCompleted(String challengeId) {
        return preferences.getBoolean(KEY_CHALLENGE_PREFIX + challengeId, false);
    }
    
    public void markChallengeCompleted(String challengeId) {
        preferences.edit().putBoolean(KEY_CHALLENGE_PREFIX + challengeId, true).apply();
    }
    
    public void resetDailyChallenges() {
        SharedPreferences.Editor editor = preferences.edit();
        
        // Réinitialiser les défis quotidiens
        editor.putBoolean(KEY_CHALLENGE_PREFIX + "steps_10k_today", false);
        editor.putBoolean(KEY_CHALLENGE_PREFIX + "meditation_10min_today", false);
        editor.putBoolean(KEY_CHALLENGE_PREFIX + "hydration_8glasses_today", false);
        
        // Réinitialiser les données quotidiennes
        editor.putInt(KEY_DAILY_WATER_INTAKE, 0);
        editor.putBoolean(KEY_HAS_EATEN_VEGETABLES, false);
        editor.putBoolean(KEY_HAS_EATEN_FRUITS, false);
        editor.putBoolean(KEY_HAS_MEDITATED, false);
        editor.putBoolean(KEY_HAS_RECORDED_MOOD, false);
        
        editor.apply();
    }
    
    public void resetWeeklyChallenges() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_WEEKLY_WORKOUTS, 0);
        editor.apply();
    }
    
    // === DONNÉES MÉTÉO ===
    
    public void saveWeatherData(com.example.projet_android.models.WeatherData weatherData) {
        // Sauvegarder les données météo en JSON ou format simple
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("weather_city", weatherData.getCityName());
        editor.putFloat("weather_temperature", (float) weatherData.getTemperature());
        editor.putFloat("weather_feels_like", (float) weatherData.getFeelsLike());
        editor.putInt("weather_humidity", weatherData.getHumidity());
        editor.putString("weather_condition", weatherData.getWeatherCondition());
        editor.putString("weather_description", weatherData.getWeatherDescription());
        editor.putLong(KEY_LAST_WEATHER_UPDATE, System.currentTimeMillis());
        editor.apply();
    }
    
    public com.example.projet_android.models.WeatherData getCachedWeatherData() {
        long lastUpdate = preferences.getLong(KEY_LAST_WEATHER_UPDATE, 0);
        
        // Si les données ont plus de 30 minutes, les considérer comme périmées
        if (System.currentTimeMillis() - lastUpdate > 30 * 60 * 1000) {
            return null;
        }
        
        String city = preferences.getString("weather_city", null);
        if (city == null) return null;
        
        com.example.projet_android.models.WeatherData weatherData = new com.example.projet_android.models.WeatherData();
        weatherData.setCityName(city);
        weatherData.setTemperature(preferences.getFloat("weather_temperature", 0));
        weatherData.setFeelsLike(preferences.getFloat("weather_feels_like", 0));
        weatherData.setHumidity(preferences.getInt("weather_humidity", 0));
        weatherData.setWeatherCondition(preferences.getString("weather_condition", ""));
        weatherData.setWeatherDescription(preferences.getString("weather_description", ""));
        weatherData.setTimestamp(lastUpdate);
        
        return weatherData;
    }
    
    // === RÉINITIALISATION PÉRIODIQUE ===
    
    /**
     * Réinitialise les données quotidiennes (à appeler à minuit)
     */
    public void resetDailyData() {
        resetDailyChallenges();
        
        // Vérifier si l'utilisateur maintient sa série wellness
        boolean maintainStreak = checkWellnessStreakCriteria();
        if (maintainStreak) {
            incrementWellnessStreak();
        } else {
            resetWellnessStreak();
        }
    }
    
    /**
     * Vérifie si l'utilisateur mérite de maintenir sa série wellness
     */
    private boolean checkWellnessStreakCriteria() {
        // Critères minimum pour maintenir la série (au moins 2 sur 4)
        int criteria = 0;
        
        if (getDailySteps() >= getDailyStepsGoal() / 2) criteria++; // Au moins 50% des pas
        if (getDailyWaterIntake() >= 4) criteria++; // Au moins 4 verres d'eau
        if (hasEatenVegetables() || hasEatenFruits()) criteria++; // Fruits ou légumes
        if (hasMeditated() || hasRecordedMood()) criteria++; // Bien-être mental
        
        return criteria >= 2;
    }
    
    // ==================== HYDRATION TRACKER METHODS ====================
    
    public int getDailyWaterGoal() {
        return preferences.getInt("daily_water_goal", 2500); // 2.5L default
    }
    
    public void setDailyWaterGoal(int goal) {
        preferences.edit().putInt("daily_water_goal", goal).apply();
    }
    
    public int getWaterIntake() {
        return preferences.getInt("water_intake", 0);
    }
    
    public void setWaterIntake(int intake) {
        preferences.edit().putInt("water_intake", intake).apply();
    }
    
    public long getLastWaterIntakeTime() {
        return preferences.getLong("last_water_intake_time", 0);
    }
    
    public void setLastWaterIntakeTime(long time) {
        preferences.edit().putLong("last_water_intake_time", time).apply();
    }
    
    // ==================== SLEEP ANALYSIS METHODS ====================
    
    public double getSleepHours() {
        return Double.longBitsToDouble(preferences.getLong("sleep_hours", Double.doubleToLongBits(0.0)));
    }
    
    public void setSleepHours(double hours) {
        preferences.edit().putLong("sleep_hours", Double.doubleToLongBits(hours)).apply();
    }
    
    public int getSleepGoal() {
        return preferences.getInt("sleep_goal", 8); // 8 hours default
    }
    
    public void setSleepGoal(int goal) {
        preferences.edit().putInt("sleep_goal", goal).apply();
    }
    
    public double getWeeklySleepAverage() {
        return Double.longBitsToDouble(preferences.getLong("weekly_sleep_average", Double.doubleToLongBits(0.0)));
    }
    
    public void setWeeklySleepAverage(double average) {
        preferences.edit().putLong("weekly_sleep_average", Double.doubleToLongBits(average)).apply();
    }
    
    public long getBedtime() {
        return preferences.getLong("bedtime", 0);
    }
    
    public void setBedtime(long time) {
        preferences.edit().putLong("bedtime", time).apply();
    }
    
    public long getWakeTime() {
        return preferences.getLong("wake_time", 0);
    }
    
    public void setWakeTime(long time) {
        preferences.edit().putLong("wake_time", time).apply();
    }
    
    // ==================== MOOD TRACKER METHODS ====================
    
    public int getTodayMood() {
        return preferences.getInt("today_mood", 0);
    }
    
    public void setTodayMood(int mood) {
        preferences.edit().putInt("today_mood", mood).apply();
    }
    
    public int getMoodStreak() {
        return preferences.getInt("mood_streak", 0);
    }
    
    public void setMoodStreak(int streak) {
        preferences.edit().putInt("mood_streak", streak).apply();
    }
    
    public long getLastMoodEntryTime() {
        return preferences.getLong("last_mood_entry_time", 0);
    }
    
    public void setLastMoodEntryTime(long time) {
        preferences.edit().putLong("last_mood_entry_time", time).apply();
    }
    
    public String getMoodTags() {
        return preferences.getString("mood_tags", "");
    }
    
    public void setMoodTags(String tags) {
        preferences.edit().putString("mood_tags", tags).apply();
    }
    
    public int getWeeklyAverageMood() {
        return preferences.getInt("weekly_average_mood", 0);
    }
    
    public void setWeeklyAverageMood(int mood) {
        preferences.edit().putInt("weekly_average_mood", mood).apply();
    }
    
    // ==================== BODY ANALYSIS METHODS ====================
    
    public double getCurrentWeight() {
        return Double.longBitsToDouble(preferences.getLong("current_weight", Double.doubleToLongBits(0.0)));
    }
    
    public void setCurrentWeight(double weight) {
        preferences.edit().putLong("current_weight", Double.doubleToLongBits(weight)).apply();
    }
    
    public double getCurrentHeight() {
        return Double.longBitsToDouble(preferences.getLong("current_height", Double.doubleToLongBits(0.0)));
    }
    
    public void setCurrentHeight(double height) {
        preferences.edit().putLong("current_height", Double.doubleToLongBits(height)).apply();
    }
    
    public double getBodyFatPercentage() {
        return Double.longBitsToDouble(preferences.getLong("body_fat_percentage", Double.doubleToLongBits(0.0)));
    }
    
    public void setBodyFatPercentage(double bodyFat) {
        preferences.edit().putLong("body_fat_percentage", Double.doubleToLongBits(bodyFat)).apply();
    }
    
    public double getMuscleMass() {
        return Double.longBitsToDouble(preferences.getLong("muscle_mass", Double.doubleToLongBits(0.0)));
    }
    
    public void setMuscleMass(double muscleMass) {
        preferences.edit().putLong("muscle_mass", Double.doubleToLongBits(muscleMass)).apply();
    }
    
    public double getWeightGoal() {
        return Double.longBitsToDouble(preferences.getLong("weight_goal", Double.doubleToLongBits(0.0)));
    }
    
    public void setWeightGoal(double goal) {
        preferences.edit().putLong("weight_goal", Double.doubleToLongBits(goal)).apply();
    }
    
    public long getLastBodyAnalysisUpdate() {
        return preferences.getLong("last_body_analysis_update", 0);
    }
    
    public void setLastBodyAnalysisUpdate(long time) {
        preferences.edit().putLong("last_body_analysis_update", time).apply();
    }
    
    // ==================== MEDITATION METHODS ====================
    
    public int getMeditationStreak() {
        return preferences.getInt("meditation_streak", 0);
    }
    
    public void setMeditationStreak(int streak) {
        preferences.edit().putInt("meditation_streak", streak).apply();
    }
    
    public int getTotalMeditationTime() {
        return preferences.getInt("total_meditation_time", 0); // in minutes
    }
    
    public void setTotalMeditationTime(int time) {
        preferences.edit().putInt("total_meditation_time", time).apply();
    }
    
    public int getTodayMeditationTime() {
        return preferences.getInt("today_meditation_time", 0); // in minutes
    }
    
    public void setTodayMeditationTime(int time) {
        preferences.edit().putInt("today_meditation_time", time).apply();
    }
    
    // Nutrition tracking methods
    public void setDailyCalories(int calories) {
        preferences.edit().putInt("daily_calories_consumed", calories).apply();
    }
    
    public int getDailyCalories() {
        return preferences.getInt("daily_calories_consumed", 0);
    }
    
    public void setDailyProtein(int protein) {
        preferences.edit().putInt("daily_protein_consumed", protein).apply();
    }
    
    public int getDailyProtein() {
        return preferences.getInt("daily_protein_consumed", 0);
    }
    
    public void setDailyCarbs(int carbs) {
        preferences.edit().putInt("daily_carbs_consumed", carbs).apply();
    }
    
    public int getDailyCarbs() {
        return preferences.getInt("daily_carbs_consumed", 0);
    }
    
    public void setDailyFat(int fat) {
        preferences.edit().putInt("daily_fat_consumed", fat).apply();
    }
    
    public int getDailyFat() {
        return preferences.getInt("daily_fat_consumed", 0);
    }
    
    // Meditation tracking methods
    public void setTotalMeditationMinutes(int minutes) {
        preferences.edit().putInt("total_meditation_minutes", minutes).apply();
    }
    
    public int getTotalMeditationMinutes() {
        return preferences.getInt("total_meditation_minutes", 0);
    }
    
    public void setMeditationSessionsCount(int count) {
        preferences.edit().putInt("meditation_sessions_count", count).apply();
    }
    
    public int getMeditationSessionsCount() {
        return preferences.getInt("meditation_sessions_count", 0);
    }
    
    public void setHasMeditatedToday(boolean meditated) {
        String today = java.text.DateFormat.getDateInstance().format(new java.util.Date());
        preferences.edit().putBoolean("meditated_" + today, meditated).apply();
    }
    
    public boolean hasMeditatedToday() {
        String today = java.text.DateFormat.getDateInstance().format(new java.util.Date());
        return preferences.getBoolean("meditated_" + today, false);
    }
    
    public boolean hasMeditatedYesterday() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
        String yesterday = java.text.DateFormat.getDateInstance().format(cal.getTime());
        return preferences.getBoolean("meditated_" + yesterday, false);
    }
    
    public void setLastMeditationStart(long timestamp) {
        preferences.edit().putLong("last_meditation_start", timestamp).apply();
    }
    
    public long getLastMeditationStart() {
        return preferences.getLong("last_meditation_start", 0);
    }
    
    // Wellness tracking methods
    public void setWellnessStreak(int streak) {
        preferences.edit().putInt(KEY_WELLNESS_STREAK, streak).apply();
    }
    
    public int getWellnessStreak() {
        return preferences.getInt(KEY_WELLNESS_STREAK, 0);
    }
    
    public void setStreakUpdatedToday(boolean updated) {
        String today = java.text.DateFormat.getDateInstance().format(new java.util.Date());
        preferences.edit().putBoolean("streak_updated_" + today, updated).apply();
    }
      public boolean hasStreakBeenUpdatedToday() {
        String today = java.text.DateFormat.getDateInstance().format(new java.util.Date());
        return preferences.getBoolean("streak_updated_" + today, false);
    }
    
    // === MOOD TRACKING METHODS ===
    
    public void setDailyMood(String mood) {
        String today = java.text.DateFormat.getDateInstance().format(new java.util.Date());
        preferences.edit().putString(KEY_DAILY_MOOD + "_" + today, mood).apply();
    }
    
    public String getDailyMood() {
        String today = java.text.DateFormat.getDateInstance().format(new java.util.Date());
        return preferences.getString(KEY_DAILY_MOOD + "_" + today, "");
    }
    
    public boolean hasLoggedMoodToday() {
        String today = java.text.DateFormat.getDateInstance().format(new java.util.Date());
        return preferences.getBoolean(KEY_HAS_LOGGED_MOOD_TODAY + "_" + today, false);
    }
    
    public void setHasLoggedMoodToday(boolean logged) {
        String today = java.text.DateFormat.getDateInstance().format(new java.util.Date());
        preferences.edit().putBoolean(KEY_HAS_LOGGED_MOOD_TODAY + "_" + today, logged).apply();
    }
    
    // === GOAL METHODS ===
    
    public int getWaterGoal() {
        return preferences.getInt(KEY_WATER_GOAL, DEFAULT_WATER_GOAL);
    }
    
    public void setWaterGoal(int goal) {
        preferences.edit().putInt(KEY_WATER_GOAL, goal).apply();
    }
    
    public int getStepsGoal() {
        return getDailyStepsGoal(); // Use existing method
    }
}
