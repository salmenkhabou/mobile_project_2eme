package com.example.projet_android.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.projet_android.database.AppDatabase;
import com.example.projet_android.database.dao.HealthDataDao;
import com.example.projet_android.database.entities.HealthData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HealthDataRepository {
    
    private HealthDataDao healthDataDao;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    
    public HealthDataRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        healthDataDao = db.healthDataDao();
    }
    
    // Méthodes d'insertion et mise à jour
    public void insertHealthData(HealthData healthData) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            healthDataDao.insertHealthData(healthData);
        });
    }
    
    public void updateHealthData(HealthData healthData) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            healthDataDao.updateHealthData(healthData);
        });
    }
    
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
            healthDataDao.updateActivityData(userId, date, steps, calories, distance);
        });
    }
    
    public void updateSleepData(String userId, String date, float sleepHours) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            healthDataDao.updateSleepData(userId, date, sleepHours);
        });
    }
    
    public void updateHeartRate(String userId, String date, int heartRate) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            healthDataDao.updateHeartRate(userId, date, heartRate);
        });
    }
    
    public void updateWaterIntake(String userId, String date, int waterGlasses) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            healthDataDao.updateWaterIntake(userId, date, waterGlasses);
        });
    }
    
    public void updateNutritionData(String userId, String date, int calories, float protein, float carbs, float fat) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            healthDataDao.updateNutritionData(userId, date, calories, protein, carbs, fat);
        });
    }
    
    // Méthodes utilitaires pour créer/obtenir les données du jour
    public void createOrUpdateTodaysData(String userId, int steps, int calories, float distance) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
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
