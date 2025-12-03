package com.example.projet_android.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projet_android.database.entities.Activity;

import java.util.List;

@Dao
public interface ActivityDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertActivity(Activity activity);
    
    @Update
    void updateActivity(Activity activity);
    
    @Delete
    void deleteActivity(Activity activity);
    
    @Query("SELECT * FROM activities WHERE userId = :userId ORDER BY startTime DESC")
    LiveData<List<Activity>> getActivitiesForUser(String userId);
    
    @Query("SELECT * FROM activities WHERE userId = :userId AND date = :date ORDER BY startTime DESC")
    LiveData<List<Activity>> getActivitiesForDate(String userId, String date);
    
    @Query("SELECT * FROM activities WHERE userId = :userId AND date = :date ORDER BY startTime DESC")
    List<Activity> getActivitiesForDateSync(String userId, String date);
    
    @Query("SELECT * FROM activities WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY startTime DESC")
    LiveData<List<Activity>> getActivitiesBetweenDates(String userId, String startDate, String endDate);
    
    @Query("SELECT * FROM activities WHERE userId = :userId ORDER BY startTime DESC LIMIT :limit")
    LiveData<List<Activity>> getRecentActivities(String userId, int limit);
    
    @Query("SELECT * FROM activities WHERE userId = :userId AND activityType = :activityType ORDER BY startTime DESC")
    LiveData<List<Activity>> getActivitiesByType(String userId, String activityType);
    
    @Query("SELECT SUM(caloriesBurned) as totalCalories FROM activities WHERE userId = :userId AND date = :date")
    LiveData<Integer> getTotalCaloriesBurnedForDate(String userId, String date);
    
    @Query("SELECT SUM(duration) as totalDuration FROM activities WHERE userId = :userId AND date = :date")
    LiveData<Integer> getTotalDurationForDate(String userId, String date);
    
    @Query("SELECT SUM(distance) as totalDistance FROM activities WHERE userId = :userId AND date = :date")
    LiveData<Float> getTotalDistanceForDate(String userId, String date);
    
    @Query("SELECT AVG(averageHeartRate) as avgHeartRate FROM activities WHERE userId = :userId AND date = :date AND averageHeartRate > 0")
    LiveData<Float> getAverageHeartRateForDate(String userId, String date);
    
    @Query("SELECT SUM(caloriesBurned) as totalCalories FROM activities WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    LiveData<Integer> getTotalCaloriesBurnedBetweenDates(String userId, String startDate, String endDate);
    
    @Query("SELECT SUM(duration) as totalDuration FROM activities WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    LiveData<Integer> getTotalDurationBetweenDates(String userId, String startDate, String endDate);
    
    @Query("SELECT DISTINCT activityType FROM activities WHERE userId = :userId ORDER BY activityType ASC")
    LiveData<List<String>> getFavoriteActivityTypes(String userId);
    
    @Query("DELETE FROM activities WHERE userId = :userId AND date = :date")
    void deleteActivitiesForDate(String userId, String date);
}
