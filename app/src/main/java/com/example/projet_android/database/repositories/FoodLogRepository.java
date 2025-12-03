package com.example.projet_android.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.projet_android.database.AppDatabase;
import com.example.projet_android.database.dao.FoodLogDao;
import com.example.projet_android.database.entities.FoodLog;

import java.util.List;

public class FoodLogRepository {
    
    private FoodLogDao foodLogDao;
    
    public FoodLogRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        foodLogDao = db.foodLogDao();
    }
    
    // Méthodes d'insertion et mise à jour
    public void insertFoodLog(FoodLog foodLog) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodLogDao.insertFoodLog(foodLog);
        });
    }
    
    public void updateFoodLog(FoodLog foodLog) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodLogDao.updateFoodLog(foodLog);
        });
    }
    
    public void deleteFoodLog(FoodLog foodLog) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodLogDao.deleteFoodLog(foodLog);
        });
    }
    
    // Méthodes de récupération
    public LiveData<List<FoodLog>> getFoodLogsForUser(String userId) {
        return foodLogDao.getFoodLogsForUser(userId);
    }
    
    public LiveData<List<FoodLog>> getFoodLogsForDate(String userId, String date) {
        return foodLogDao.getFoodLogsForDate(userId, date);
    }
    
    public List<FoodLog> getFoodLogsForDateSync(String userId, String date) {
        return foodLogDao.getFoodLogsForDateSync(userId, date);
    }
    
    public LiveData<List<FoodLog>> getFoodLogsForMeal(String userId, String date, String mealType) {
        return foodLogDao.getFoodLogsForMeal(userId, date, mealType);
    }
    
    public LiveData<List<FoodLog>> getFoodLogsBetweenDates(String userId, String startDate, String endDate) {
        return foodLogDao.getFoodLogsBetweenDates(userId, startDate, endDate);
    }
    
    public LiveData<List<FoodLog>> getRecentFoodLogs(String userId, int limit) {
        return foodLogDao.getRecentFoodLogs(userId, limit);
    }
    
    // Méthodes de calculs nutritionnels
    public LiveData<Float> getTotalCaloriesForDate(String userId, String date) {
        return foodLogDao.getTotalCaloriesForDate(userId, date);
    }
    
    public LiveData<Float> getTotalProteinForDate(String userId, String date) {
        return foodLogDao.getTotalProteinForDate(userId, date);
    }
    
    public LiveData<Float> getTotalCarbsForDate(String userId, String date) {
        return foodLogDao.getTotalCarbsForDate(userId, date);
    }
    
    public LiveData<Float> getTotalFatForDate(String userId, String date) {
        return foodLogDao.getTotalFatForDate(userId, date);
    }
    
    // Méthodes utilitaires
    public FoodLog getLastFoodByBarcode(String userId, String barcode) {
        return foodLogDao.getLastFoodByBarcode(userId, barcode);
    }
    
    public void deleteFoodLogsForDate(String userId, String date) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            foodLogDao.deleteFoodLogsForDate(userId, date);
        });
    }
    
    public LiveData<List<String>> getFavoritesFoods(String userId) {
        return foodLogDao.getFavoritesFoods(userId);
    }
    
    // Méthode pour ajouter un aliment rapidement
    public void addFoodItem(String userId, String date, String foodName, String mealType, 
                           int calories, float protein, float carbs, float fat, float quantity) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            FoodLog foodLog = new FoodLog(userId, date, foodName);
            foodLog.mealType = mealType;
            foodLog.calories = calories;
            foodLog.protein = protein;
            foodLog.carbs = carbs;
            foodLog.fat = fat;
            foodLog.quantity = quantity;
            foodLogDao.insertFoodLog(foodLog);
        });
    }
    
    // Méthode pour ajouter un aliment scanné
    public void addScannedFood(String userId, String date, String foodName, String brand, 
                              String barcode, String imageUrl, String mealType,
                              int calories, float protein, float carbs, float fat, float quantity) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            FoodLog foodLog = new FoodLog(userId, date, foodName);
            foodLog.brand = brand;
            foodLog.barcode = barcode;
            foodLog.imageUrl = imageUrl;
            foodLog.mealType = mealType;
            foodLog.calories = calories;
            foodLog.protein = protein;
            foodLog.carbs = carbs;
            foodLog.fat = fat;
            foodLog.quantity = quantity;
            foodLogDao.insertFoodLog(foodLog);
        });
    }
}
