package com.example.projet_android.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projet_android.database.entities.FoodLog;

import java.util.List;

@Dao
public interface FoodLogDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertFoodLog(FoodLog foodLog);
    
    @Update
    void updateFoodLog(FoodLog foodLog);
    
    @Delete
    void deleteFoodLog(FoodLog foodLog);
    
    @Query("SELECT * FROM food_logs WHERE userId = :userId ORDER BY timestamp DESC")
    LiveData<List<FoodLog>> getFoodLogsForUser(String userId);
    
    @Query("SELECT * FROM food_logs WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    LiveData<List<FoodLog>> getFoodLogsForDate(String userId, String date);
    
    @Query("SELECT * FROM food_logs WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    List<FoodLog> getFoodLogsForDateSync(String userId, String date);
    
    @Query("SELECT * FROM food_logs WHERE userId = :userId AND date = :date AND mealType = :mealType ORDER BY timestamp DESC")
    LiveData<List<FoodLog>> getFoodLogsForMeal(String userId, String date, String mealType);
    
    @Query("SELECT * FROM food_logs WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC, timestamp DESC")
    LiveData<List<FoodLog>> getFoodLogsBetweenDates(String userId, String startDate, String endDate);
    
    @Query("SELECT * FROM food_logs WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    LiveData<List<FoodLog>> getRecentFoodLogs(String userId, int limit);
    
    @Query("SELECT SUM(calories * quantity / 100) as totalCalories FROM food_logs WHERE userId = :userId AND date = :date")
    LiveData<Float> getTotalCaloriesForDate(String userId, String date);
    
    @Query("SELECT SUM(protein * quantity / 100) as totalProtein FROM food_logs WHERE userId = :userId AND date = :date")
    LiveData<Float> getTotalProteinForDate(String userId, String date);
    
    @Query("SELECT SUM(carbs * quantity / 100) as totalCarbs FROM food_logs WHERE userId = :userId AND date = :date")
    LiveData<Float> getTotalCarbsForDate(String userId, String date);
    
    @Query("SELECT SUM(fat * quantity / 100) as totalFat FROM food_logs WHERE userId = :userId AND date = :date")
    LiveData<Float> getTotalFatForDate(String userId, String date);
    
    @Query("SELECT * FROM food_logs WHERE userId = :userId AND barcode = :barcode ORDER BY timestamp DESC LIMIT 1")
    FoodLog getLastFoodByBarcode(String userId, String barcode);
    
    @Query("DELETE FROM food_logs WHERE userId = :userId AND date = :date")
    void deleteFoodLogsForDate(String userId, String date);
    
    @Query("SELECT DISTINCT foodName FROM food_logs WHERE userId = :userId ORDER BY foodName ASC")
    LiveData<List<String>> getFavoritesFoods(String userId);
}
