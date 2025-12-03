package com.example.projet_android.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projet_android.database.DatabaseManager;
import com.example.projet_android.database.entities.HealthData;
import com.example.projet_android.database.entities.FoodLog;
import com.example.projet_android.database.entities.Activity;
import com.example.projet_android.services.DataSyncService;
import com.example.projet_android.utils.PreferencesManager;

import java.util.List;

public class HealthViewModel extends AndroidViewModel {
    
    private DatabaseManager databaseManager;
    private DataSyncService dataSyncService;
    private PreferencesManager preferencesManager;
    
    private MutableLiveData<Boolean> syncInProgress = new MutableLiveData<>(false);
    private MutableLiveData<String> syncError = new MutableLiveData<>();
    
    public HealthViewModel(@NonNull Application application) {
        super(application);
        databaseManager = DatabaseManager.getInstance(application);
        dataSyncService = new DataSyncService(application);
        preferencesManager = new PreferencesManager(application);
    }
    
    // Getters pour les LiveData
    public LiveData<Boolean> getSyncInProgress() {
        return syncInProgress;
    }
    
    public LiveData<String> getSyncError() {
        return syncError;
    }
    
    // Méthodes pour obtenir les données de santé
    public LiveData<HealthData> getTodaysHealthData() {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            return databaseManager.getTodaysHealthData(userId);
        }
        return new MutableLiveData<>(null);
    }
    
    public LiveData<List<HealthData>> getRecentHealthData(int days) {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            return databaseManager.getRecentHealthData(userId, days);
        }
        return new MutableLiveData<>();
    }
    
    // Méthodes pour obtenir les données nutritionnelles
    public LiveData<List<FoodLog>> getTodaysFoodLogs() {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            return databaseManager.getTodaysFoodLogs(userId);
        }
        return new MutableLiveData<>();
    }
    
    public LiveData<Float> getTodaysTotalCalories() {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            return databaseManager.getTodaysTotalCalories(userId);
        }
        return new MutableLiveData<>(0f);
    }
    
    public LiveData<Float> getTodaysTotalProtein() {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            return databaseManager.getTodaysTotalProtein(userId);
        }
        return new MutableLiveData<>(0f);
    }
    
    public LiveData<Float> getTodaysTotalCarbs() {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            return databaseManager.getTodaysTotalCarbs(userId);
        }
        return new MutableLiveData<>(0f);
    }
    
    public LiveData<Float> getTodaysTotalFat() {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            return databaseManager.getTodaysTotalFat(userId);
        }
        return new MutableLiveData<>(0f);
    }
    
    // Méthodes pour obtenir les données d'activités
    public LiveData<List<Activity>> getTodaysActivities() {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            return databaseManager.getTodaysActivities(userId);
        }
        return new MutableLiveData<>();
    }
    
    public LiveData<Integer> getTodaysTotalCaloriesBurned() {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            return databaseManager.getTodaysTotalCaloriesBurned(userId);
        }
        return new MutableLiveData<>(0);
    }
    
    // Méthodes de synchronisation
    public void syncData() {
        syncInProgress.setValue(true);
        syncError.setValue(null);
        
        dataSyncService.syncAllData(new DataSyncService.SyncListener() {
            @Override
            public void onSyncStarted() {
                syncInProgress.postValue(true);
            }
            
            @Override
            public void onSyncCompleted() {
                syncInProgress.postValue(false);
            }
            
            @Override
            public void onSyncError(String error) {
                syncInProgress.postValue(false);
                syncError.postValue(error);
            }
            
            @Override
            public void onDataUpdated(HealthData healthData) {
                // Les données seront automatiquement mises à jour via LiveData
            }
        });
    }
    
    public void quickSync() {
        syncInProgress.setValue(true);
        syncError.setValue(null);
        
        dataSyncService.quickSync(new DataSyncService.SyncListener() {
            @Override
            public void onSyncStarted() {
                syncInProgress.postValue(true);
            }
            
            @Override
            public void onSyncCompleted() {
                syncInProgress.postValue(false);
            }
            
            @Override
            public void onSyncError(String error) {
                syncInProgress.postValue(false);
                syncError.postValue(error);
            }
            
            @Override
            public void onDataUpdated(HealthData healthData) {
                // Les données seront automatiquement mises à jour via LiveData
            }
        });
    }
    
    // Méthodes pour mettre à jour les données manuellement
    public void updateSleepData(float sleepHours) {
        dataSyncService.updateSleepData(sleepHours);
    }
    
    public void updateWaterIntake(int waterGlasses) {
        dataSyncService.updateWaterIntake(waterGlasses);
    }
    
    public void updateHeartRate(int heartRate) {
        dataSyncService.updateHeartRate(heartRate);
    }
    
    // Méthode pour ajouter une activité manuelle
    public void addActivity(String activityType, String description, int duration, 
                           int caloriesBurned, float distance, int averageHeartRate) {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            databaseManager.addActivity(userId, activityType, description, 
                                      duration, caloriesBurned, distance, averageHeartRate);
        }
    }
    
    // Vérifier si l'utilisateur est connecté
    public boolean isUserLoggedIn() {
        return preferencesManager.isUserLoggedIn() && preferencesManager.getUserId() != null;
    }
    
    // Obtenir des recommandations personnalisées
    public String getPersonalizedRecommendation() {
        if (!isUserLoggedIn()) {
            return "Connectez-vous pour obtenir des recommandations personnalisées";
        }
        
        // Logique de recommandation basée sur les objectifs et les données actuelles
        int stepsGoal = preferencesManager.getDailyStepsGoal();
        int caloriesGoal = preferencesManager.getDailyCaloriesGoal();
        
        StringBuilder recommendations = new StringBuilder();
        recommendations.append("Recommandations du jour:\n");
        recommendations.append("• Objectif de pas: ").append(String.format("%,d", stepsGoal)).append("\n");
        recommendations.append("• Objectif de calories: ").append(String.format("%,d", caloriesGoal)).append("\n");
        recommendations.append("• Buvez au moins 8 verres d'eau\n");
        recommendations.append("• Dormez 7-8 heures par nuit");
        
        return recommendations.toString();
    }
}
