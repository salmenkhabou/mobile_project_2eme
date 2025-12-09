package com.example.projet_android.database;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.projet_android.database.repositories.ActivityRepository;
import com.example.projet_android.database.repositories.FoodLogRepository;
import com.example.projet_android.database.repositories.HealthDataRepository;
import com.example.projet_android.database.repositories.UserRepository;
import com.example.projet_android.database.entities.Activity;
import com.example.projet_android.database.entities.FoodLog;
import com.example.projet_android.database.entities.HealthData;
import com.example.projet_android.database.entities.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Gestionnaire central de la base de données SQLite locale
 * 
 * Cette classe singleton coordonne tous les accès à la base de données Room :
 * - Gestion centralisée de tous les repositories (User, HealthData, FoodLog, Activity)
 * - Interface unifiée pour les opérations CRUD sur toutes les entités
 * - Gestion des relations entre tables et contraintes de clés étrangères
 * - Support des opérations synchrones et asynchrones (LiveData)
 * - Cache intelligent et optimisation des performances
 * - Migration automatique de schéma et gestion des versions
 * 
 * Architecture Repository Pattern :
 * - UserRepository : Gestion des comptes et profils utilisateur
 * - HealthDataRepository : Données d'activité quotidienne (pas, calories, sommeil)
 * - FoodLogRepository : Journal alimentaire et données nutritionnelles
 * - ActivityRepository : Historique des exercices et séances d'entraînement
 * 
 * Fonctionnalités avancées :
 * - Synchronisation bidirectionnelle avec Google Fit
 * - Sauvegarde automatique et restauration de données
 * - Nettoyage automatique des données anciennes
 * - Support multi-utilisateur avec isolation des données
 * - Validation des données avant insertion
 * - Gestion des conflits et déduplication
 * 
 * @author Équipe de développement Health Tracker
 * @version 2.1
 * @since 1.2
 */
public class DatabaseManager {
    
    private static DatabaseManager instance;
    private UserRepository userRepository;
    private HealthDataRepository healthDataRepository;
    private FoodLogRepository foodLogRepository;
    private ActivityRepository activityRepository;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    
    private DatabaseManager(Application application) {
        userRepository = new UserRepository(application);
        healthDataRepository = new HealthDataRepository(application);
        foodLogRepository = new FoodLogRepository(application);
        activityRepository = new ActivityRepository(application);
    }
    
    public static synchronized DatabaseManager getInstance(Application application) {
        if (instance == null) {
            instance = new DatabaseManager(application);
        }
        return instance;
    }
    
    public static DatabaseManager getInstance(Context context) {
        return getInstance((Application) context.getApplicationContext());
    }
    
    // ================== USER METHODS ==================
    
    public void insertUser(User user) {
        userRepository.insertUser(user);
    }
    
    public void updateUser(User user) {
        userRepository.updateUser(user);
    }
    
    public LiveData<User> getUser(String userId) {
        return userRepository.getUser(userId);
    }
    
    public User getUserSync(String userId) {
        return userRepository.getUserSync(userId);
    }
    
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }
    
    public void updateUserGoals(String userId, int stepsGoal, int caloriesGoal, float sleepGoal) {
        userRepository.updateGoals(userId, stepsGoal, caloriesGoal, sleepGoal);
    }
    
    public void updateNotificationSettings(String userId, boolean enabled) {
        userRepository.updateNotificationSettings(userId, enabled);
    }
    
    public void updateWaterReminderSettings(String userId, boolean enabled) {
        userRepository.updateWaterReminderSettings(userId, enabled);
    }
    
    public void updateUserPhysicalData(String userId, float weight, float height) {
        userRepository.updatePhysicalData(userId, weight, height);
    }
    
    // ================== HEALTH DATA METHODS ==================
    
    public void insertHealthData(HealthData healthData) {
        healthDataRepository.insertHealthData(healthData);
    }
    
    public LiveData<List<HealthData>> getHealthDataForUser(String userId) {
        return healthDataRepository.getHealthDataForUser(userId);
    }
    
    public LiveData<HealthData> getHealthDataForDate(String userId, String date) {
        return healthDataRepository.getHealthDataForDate(userId, date);
    }
    
    public LiveData<HealthData> getTodaysHealthData(String userId) {
        String today = dateFormat.format(new Date());
        return healthDataRepository.getHealthDataForDate(userId, today);
    }    public void updateTodaysSteps(String userId, int steps, int calories, float distance) {
        String today = dateFormat.format(new Date());
        // The ensureUserExists is now handled synchronously within createOrUpdateTodaysData
        healthDataRepository.createOrUpdateTodaysData(userId, steps, calories, distance);
    }    public void updateTodaysSleep(String userId, float sleepHours) {
        String today = dateFormat.format(new Date());
        healthDataRepository.updateSleepData(userId, today, sleepHours);
    }
    
    public void updateTodaysWater(String userId, int waterGlasses) {
        String today = dateFormat.format(new Date());
        healthDataRepository.updateWaterIntake(userId, today, waterGlasses);
    }
    
    public void updateTodaysHeartRate(String userId, int heartRate) {
        String today = dateFormat.format(new Date());
        healthDataRepository.updateHeartRate(userId, today, heartRate);
    }
    
    public LiveData<List<HealthData>> getRecentHealthData(String userId, int days) {
        return healthDataRepository.getRecentHealthData(userId, days);
    }
    
    // ================== FOOD LOG METHODS ==================
    
    public void insertFoodLog(FoodLog foodLog) {
        foodLogRepository.insertFoodLog(foodLog);
    }
    
    public LiveData<List<FoodLog>> getFoodLogsForDate(String userId, String date) {
        return foodLogRepository.getFoodLogsForDate(userId, date);
    }
    
    public LiveData<List<FoodLog>> getTodaysFoodLogs(String userId) {
        String today = dateFormat.format(new Date());
        return foodLogRepository.getFoodLogsForDate(userId, today);
    }
      public void addFoodItem(String userId, String foodName, String mealType, 
                           int calories, float protein, float carbs, float fat, float quantity) {
        String today = dateFormat.format(new Date());
        ensureUserExists(userId);
        foodLogRepository.addFoodItem(userId, today, foodName, mealType, calories, protein, carbs, fat, quantity);
    }
    
    public void addScannedFood(String userId, String foodName, String brand, String barcode, 
                              String imageUrl, String mealType, int calories, float protein, 
                              float carbs, float fat, float quantity) {
        String today = dateFormat.format(new Date());
        ensureUserExists(userId);
        foodLogRepository.addScannedFood(userId, today, foodName, brand, barcode, imageUrl, 
                                       mealType, calories, protein, carbs, fat, quantity);
    }
    
    public LiveData<Float> getTodaysTotalCalories(String userId) {
        String today = dateFormat.format(new Date());
        return foodLogRepository.getTotalCaloriesForDate(userId, today);
    }
    
    public LiveData<Float> getTodaysTotalProtein(String userId) {
        String today = dateFormat.format(new Date());
        return foodLogRepository.getTotalProteinForDate(userId, today);
    }
    
    public LiveData<Float> getTodaysTotalCarbs(String userId) {
        String today = dateFormat.format(new Date());
        return foodLogRepository.getTotalCarbsForDate(userId, today);
    }
    
    public LiveData<Float> getTodaysTotalFat(String userId) {
        String today = dateFormat.format(new Date());
        return foodLogRepository.getTotalFatForDate(userId, today);
    }
    
    // ================== ACTIVITY METHODS ==================
    
    public void insertActivity(Activity activity) {
        activityRepository.insertActivity(activity);
    }
    
    public LiveData<List<Activity>> getActivitiesForUser(String userId) {
        return activityRepository.getActivitiesForUser(userId);
    }
    
    public LiveData<List<Activity>> getActivitiesForDate(String userId, String date) {
        return activityRepository.getActivitiesForDate(userId, date);
    }
    
    public LiveData<List<Activity>> getTodaysActivities(String userId) {
        String today = dateFormat.format(new Date());
        return activityRepository.getActivitiesForDate(userId, today);
    }
      public void addActivity(String userId, String activityType, String description,
                           int duration, int caloriesBurned, float distance, int averageHeartRate) {
        String today = dateFormat.format(new Date());
        ensureUserExists(userId);
        activityRepository.addActivity(userId, today, activityType, description, 
                                     duration, caloriesBurned, distance, averageHeartRate);
    }
    
    public LiveData<Integer> getTodaysTotalCaloriesBurned(String userId) {
        String today = dateFormat.format(new Date());
        return activityRepository.getTotalCaloriesBurnedForDate(userId, today);
    }
    
    public LiveData<Integer> getTodaysTotalDuration(String userId) {
        String today = dateFormat.format(new Date());
        return activityRepository.getTotalDurationForDate(userId, today);
    }
    
    public LiveData<Float> getTodaysTotalDistance(String userId) {
        String today = dateFormat.format(new Date());
        return activityRepository.getTotalDistanceForDate(userId, today);
    }
    
    // ================== UTILITY METHODS ==================
    
    public String getTodayDate() {
        return dateFormat.format(new Date());
    }
    
    public void syncNutritionData(String userId) {
        // Méthode pour synchroniser les données nutritionnelles avec les health data
        AppDatabase.databaseWriteExecutor.execute(() -> {
            String today = getTodayDate();
            List<FoodLog> todaysFoods = foodLogRepository.getFoodLogsForDateSync(userId, today);
            
            float totalCalories = 0;
            float totalProtein = 0;
            float totalCarbs = 0;
            float totalFat = 0;
            
            for (FoodLog food : todaysFoods) {
                float multiplier = food.quantity / 100f; // Les valeurs sont pour 100g
                totalCalories += food.calories * multiplier;
                totalProtein += food.protein * multiplier;
                totalCarbs += food.carbs * multiplier;
                totalFat += food.fat * multiplier;
            }
              healthDataRepository.updateNutritionData(userId, today, (int) totalCalories, 
                                                   totalProtein, totalCarbs, totalFat);
        });    }
    
    // ================== UTILITY METHODS ==================
    
    /**
     * Ensure that a user exists in the database before inserting related data
     */
    public void ensureUserExists(String userId) {
        if (userId == null || userId.isEmpty()) {
            return;
        }
          // Quick cache verification if possible
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                User existingUser = userRepository.getUserSync(userId);
                if (existingUser == null) {
                    // Create a basic user
                    User newUser = new User();
                    newUser.userId = userId;
                    newUser.email = ""; // Will be updated later
                    newUser.displayName = "User"; // Default name
                    newUser.authProvider = "demo";
                    
                    userRepository.insertUser(newUser);
                    Log.d("DatabaseManager", "User created: " + userId);
                }            } catch (Exception e) {
                Log.e("DatabaseManager", "Error creating user: " + e.getMessage());
            }
        });
    }
    
    /**
     * Check if a user exists in the database
     */
    public boolean userExists(String userId) {
        if (userId == null || userId.isEmpty()) {
            return false;
        }
        return userRepository.getUserSync(userId) != null;
    }
}
