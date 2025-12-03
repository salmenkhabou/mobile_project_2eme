package com.example.projet_android.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projet_android.database.entities.HealthData;

import java.util.List;

@Dao
public interface HealthDataDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertHealthData(HealthData healthData);
    
    @Update
    void updateHealthData(HealthData healthData);
    
    @Delete
    void deleteHealthData(HealthData healthData);
    
    @Query("SELECT * FROM health_data WHERE userId = :userId ORDER BY timestamp DESC")
    LiveData<List<HealthData>> getHealthDataForUser(String userId);
    
    @Query("SELECT * FROM health_data WHERE userId = :userId AND date = :date LIMIT 1")
    LiveData<HealthData> getHealthDataForDate(String userId, String date);
    
    @Query("SELECT * FROM health_data WHERE userId = :userId AND date = :date LIMIT 1")
    HealthData getHealthDataForDateSync(String userId, String date);
    
    @Query("SELECT * FROM health_data WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    LiveData<List<HealthData>> getHealthDataBetweenDates(String userId, String startDate, String endDate);
    
    @Query("SELECT * FROM health_data WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    LiveData<List<HealthData>> getRecentHealthData(String userId, int limit);
    
    @Query("UPDATE health_data SET steps = :steps, calories = :calories, distance = :distance WHERE userId = :userId AND date = :date")
    void updateActivityData(String userId, String date, int steps, int calories, float distance);
    
    @Query("UPDATE health_data SET sleepHours = :sleepHours WHERE userId = :userId AND date = :date")
    void updateSleepData(String userId, String date, float sleepHours);
    
    @Query("UPDATE health_data SET heartRate = :heartRate WHERE userId = :userId AND date = :date")
    void updateHeartRate(String userId, String date, int heartRate);
    
    @Query("UPDATE health_data SET waterGlasses = :waterGlasses WHERE userId = :userId AND date = :date")
    void updateWaterIntake(String userId, String date, int waterGlasses);
    
    @Query("UPDATE health_data SET totalCaloriesConsumed = :calories, totalProtein = :protein, " +
           "totalCarbs = :carbs, totalFat = :fat WHERE userId = :userId AND date = :date")
    void updateNutritionData(String userId, String date, int calories, float protein, float carbs, float fat);
    
    @Query("SELECT AVG(steps) as avgSteps FROM health_data WHERE userId = :userId AND date >= :startDate")
    LiveData<Float> getAverageSteps(String userId, String startDate);
    
    @Query("SELECT SUM(steps) as totalSteps FROM health_data WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    LiveData<Integer> getTotalStepsBetweenDates(String userId, String startDate, String endDate);
    
    @Query("SELECT AVG(sleepHours) as avgSleep FROM health_data WHERE userId = :userId AND date >= :startDate")
    LiveData<Float> getAverageSleep(String userId, String startDate);
}
