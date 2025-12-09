package com.example.projet_android.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.projet_android.database.AppDatabase;
import com.example.projet_android.database.dao.HealthDataDao;
import com.example.projet_android.database.entities.HealthData;
import com.example.projet_android.database.entities.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ========================================
 * REPOSITORY DONNÉES DE SANTÉ - COUCHE D'ACCÈS
 * ========================================
 * 
 * Repository principal pour la gestion des données de santé dans l'application Health Tracker.
 * Implémente le pattern Repository pour abstraire l'accès aux données SQLite.
 * 
 * RESPONSABILITÉS PRINCIPALES :
 * • Interface unifiée entre les ViewModels et la base de données
 * • Gestion asynchrone des opérations CRUD sur les données de santé
 * • Formatage et validation des dates pour les requêtes
 * • Cache et optimisation des requêtes fréquentes
 * • Abstraction de la complexité des opérations SQL
 * 
 * DONNÉES GÉRÉES :
 * • 📊 Métriques de santé quotidiennes (poids, IMC, pression artérielle)
 * • 🚶 Données d'activité physique (pas, calories brûlées)
 * • 💧 Suivi d'hydratation et consommation d'eau
 * • 😴 Données de sommeil et qualité du repos
 * • 🎯 Progression vers les objectifs santé
 * 
 * FONCTIONNALITÉS AVANCÉES :
 * • Requêtes par plages de dates pour graphiques et analyses
 * • Support LiveData pour mise à jour réactive de l'UI
 * • Opérations synchrones et asynchrones selon les besoins
 * • Gestion des relations entre utilisateurs et données
 * 
 * ARCHITECTURE :
 * • Pattern Repository avec abstraction DAO
 * • Exécution asynchrone avec ThreadPoolExecutor
 * • Observable avec LiveData pour l'architecture MVVM
 * • Formatage standardisé des dates (yyyy-MM-dd)
 * 
 * @version 1.0
 * @author Équipe Health Tracker
 */
public class HealthDataRepository {
      // ============ COMPOSANTS D'ACCÈS AUX DONNÉES ============
    private HealthDataDao healthDataDao;  // Interface DAO pour les opérations SQL
    private AppDatabase database;         // Instance de la base de données Room
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());  // Format standardisé des dates
    
    /**
     * Constructeur du repository
     * Initialise la base de données et récupère l'instance DAO
     * 
     * @param application Contexte application pour initialiser Room
     */
    public HealthDataRepository(Application application) {
        database = AppDatabase.getDatabase(application);
        healthDataDao = database.healthDataDao();
    }
    
    // ============ OPÉRATIONS D'ÉCRITURE (CRUD) ============
    
    /**
     * Insertion asynchrone de nouvelles données de santé
     * Exécute l'opération en arrière-plan pour ne pas bloquer l'UI
     * 
     * @param healthData Données de santé à insérer en base
     */
    public void insertHealthData(HealthData healthData) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            healthDataDao.insertHealthData(healthData);
        });
    }
    
    /**
     * Mise à jour asynchrone des données de santé existantes
     * Met à jour toutes les colonnes de l'enregistrement
     * 
     * @param healthData Données de santé modifiées à sauvegarder
     */
    public void updateHealthData(HealthData healthData) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            healthDataDao.updateHealthData(healthData);
        });
    }
    
    /**
     * Suppression asynchrone de données de santé
     * Supprime définitivement l'enregistrement de la base
     * 
     * @param healthData Données de santé à supprimer
     */
    public void deleteHealthData(HealthData healthData) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            healthDataDao.deleteHealthData(healthData);
        });
    }
    
    // Méthodes de récupération
    public LiveData<List<HealthData>> getHealthDataForUser(String userId) {
        return healthDataDao.getHealthDataForUser(userId);
    }
    
    public LiveData<HealthData> getHealthDataForDate(String userId, String date) {
        return healthDataDao.getHealthDataForDate(userId, date);
    }
    
    public HealthData getHealthDataForDateSync(String userId, String date) {
        return healthDataDao.getHealthDataForDateSync(userId, date);
    }
    
    public LiveData<List<HealthData>> getHealthDataBetweenDates(String userId, String startDate, String endDate) {
        return healthDataDao.getHealthDataBetweenDates(userId, startDate, endDate);
    }
    
    public LiveData<List<HealthData>> getRecentHealthData(String userId, int limit) {
        return healthDataDao.getRecentHealthData(userId, limit);
    }
    
    // Méthodes de mise à jour spécifiques
    
    public void updateActivityData(String userId, String date, int steps, int calories, float distance) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            ensureUserExistsSync(userId);
            healthDataDao.updateActivityData(userId, date, steps, calories, distance);
        });
    }
      public void updateSleepData(String userId, String date, float sleepHours) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            ensureUserExistsSync(userId);
            healthDataDao.updateSleepData(userId, date, sleepHours);
        });
    }
    
    public void updateHeartRate(String userId, String date, int heartRate) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            ensureUserExistsSync(userId);
            healthDataDao.updateHeartRate(userId, date, heartRate);
        });
    }
    
    public void updateWaterIntake(String userId, String date, int waterGlasses) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            ensureUserExistsSync(userId);
            healthDataDao.updateWaterIntake(userId, date, waterGlasses);
        });
    }
    
    public void updateNutritionData(String userId, String date, int calories, float protein, float carbs, float fat) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            ensureUserExistsSync(userId);
            healthDataDao.updateNutritionData(userId, date, calories, protein, carbs, fat);
        });
    }
      // Méthodes utilitaires pour créer/obtenir les données du jour
    public void createOrUpdateTodaysData(String userId, int steps, int calories, float distance) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Ensure user exists first - synchronously within the same transaction
            ensureUserExistsSync(userId);
            
            String today = dateFormat.format(new Date());
            HealthData existingData = healthDataDao.getHealthDataForDateSync(userId, today);
            
            if (existingData == null) {
                HealthData newData = new HealthData(userId, today);
                newData.steps = steps;
                newData.calories = calories;
                newData.distance = distance;
                healthDataDao.insertHealthData(newData);
            } else {
                healthDataDao.updateActivityData(userId, today, steps, calories, distance);
            }
        });
    }
    
    /**
     * Ensure user exists synchronously within database transaction
     */
    private void ensureUserExistsSync(String userId) {
        if (userId == null || userId.isEmpty()) {
            return;
        }
        
        try {
            // Check if user exists using UserDao directly
            User existingUser = database.userDao().getUserSync(userId);
            if (existingUser == null) {
                // Create a basic user synchronously
                User newUser = new User();
                newUser.userId = userId;
                newUser.email = ""; // Will be updated later
                newUser.displayName = "User"; // Default name
                newUser.authProvider = "demo";
                
                database.userDao().insertUser(newUser);
            }
        } catch (Exception e) {
            // If user creation fails, log but don't crash
            e.printStackTrace();
        }
    }
    
    // Méthodes de statistiques
    public LiveData<Float> getAverageSteps(String userId, String startDate) {
        return healthDataDao.getAverageSteps(userId, startDate);
    }
    
    public LiveData<Integer> getTotalStepsBetweenDates(String userId, String startDate, String endDate) {
        return healthDataDao.getTotalStepsBetweenDates(userId, startDate, endDate);
    }
    
    public LiveData<Float> getAverageSleep(String userId, String startDate) {
        return healthDataDao.getAverageSleep(userId, startDate);
    }
}
