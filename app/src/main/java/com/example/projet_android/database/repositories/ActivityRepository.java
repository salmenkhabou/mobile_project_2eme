package com.example.projet_android.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.projet_android.database.AppDatabase;
import com.example.projet_android.database.dao.ActivityDao;
import com.example.projet_android.database.entities.Activity;

import java.util.List;

public class ActivityRepository {
    
    private ActivityDao activityDao;
    
    public ActivityRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        activityDao = db.activityDao();
    }
    
    // Méthodes d'insertion et mise à jour
    public void insertActivity(Activity activity) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            activityDao.insertActivity(activity);
        });
    }
    
    public void updateActivity(Activity activity) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            activityDao.updateActivity(activity);
        });
    }
    
    public void deleteActivity(Activity activity) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            activityDao.deleteActivity(activity);
        });
    }
    
    // Méthodes de récupération
    public LiveData<List<Activity>> getActivitiesForUser(String userId) {
        return activityDao.getActivitiesForUser(userId);
    }
    
    public LiveData<List<Activity>> getActivitiesForDate(String userId, String date) {
        return activityDao.getActivitiesForDate(userId, date);
    }
    
    public List<Activity> getActivitiesForDateSync(String userId, String date) {
        return activityDao.getActivitiesForDateSync(userId, date);
    }
    
    public LiveData<List<Activity>> getActivitiesBetweenDates(String userId, String startDate, String endDate) {
        return activityDao.getActivitiesBetweenDates(userId, startDate, endDate);
    }
    
    public LiveData<List<Activity>> getRecentActivities(String userId, int limit) {
        return activityDao.getRecentActivities(userId, limit);
    }
    
    public LiveData<List<Activity>> getActivitiesByType(String userId, String activityType) {
        return activityDao.getActivitiesByType(userId, activityType);
    }
    
    // Méthodes de calculs statistiques
    public LiveData<Integer> getTotalCaloriesBurnedForDate(String userId, String date) {
        return activityDao.getTotalCaloriesBurnedForDate(userId, date);
    }
    
    public LiveData<Integer> getTotalDurationForDate(String userId, String date) {
        return activityDao.getTotalDurationForDate(userId, date);
    }
    
    public LiveData<Float> getTotalDistanceForDate(String userId, String date) {
        return activityDao.getTotalDistanceForDate(userId, date);
    }
    
    public LiveData<Float> getAverageHeartRateForDate(String userId, String date) {
        return activityDao.getAverageHeartRateForDate(userId, date);
    }
    
    public LiveData<Integer> getTotalCaloriesBurnedBetweenDates(String userId, String startDate, String endDate) {
        return activityDao.getTotalCaloriesBurnedBetweenDates(userId, startDate, endDate);
    }
    
    public LiveData<Integer> getTotalDurationBetweenDates(String userId, String startDate, String endDate) {
        return activityDao.getTotalDurationBetweenDates(userId, startDate, endDate);
    }
    
    // Méthodes utilitaires
    public LiveData<List<String>> getFavoriteActivityTypes(String userId) {
        return activityDao.getFavoriteActivityTypes(userId);
    }
    
    public void deleteActivitiesForDate(String userId, String date) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            activityDao.deleteActivitiesForDate(userId, date);
        });
    }
    
    // Méthode pour ajouter une activité rapidement
    public void addActivity(String userId, String date, String activityType, String description,
                           int duration, int caloriesBurned, float distance, int averageHeartRate) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Activity activity = new Activity(userId, date, activityType);
            activity.description = description;
            activity.duration = duration;
            activity.caloriesBurned = caloriesBurned;
            activity.distance = distance;
            activity.averageHeartRate = averageHeartRate;
            activity.endTime = activity.startTime + (duration * 60 * 1000); // Conversion minutes en millisecondes
            activityDao.insertActivity(activity);
        });
    }
    
    // Méthode pour démarrer une activité en temps réel
    public long startActivity(String userId, String date, String activityType, String description) {
        Activity activity = new Activity(userId, date, activityType);
        activity.description = description;
        activity.startTime = System.currentTimeMillis();
        
        final long[] activityId = {0};
        AppDatabase.databaseWriteExecutor.execute(() -> {
            activityId[0] = activityDao.insertActivity(activity);
        });
        
        return activityId[0];
    }
    
    // Méthode pour terminer une activité en temps réel
    public void endActivity(long activityId, int caloriesBurned, float distance, int averageHeartRate) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Cette méthode nécessiterait une requête par ID
            // Pour simplifier, on peut utiliser une mise à jour basée sur l'heure de fin
        });
    }
}
