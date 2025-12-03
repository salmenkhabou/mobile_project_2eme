package com.example.projet_android.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projet_android.database.entities.User;

@Dao
public interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(User user);
    
    @Update
    void updateUser(User user);
    
    @Delete
    void deleteUser(User user);
    
    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    LiveData<User> getUser(String userId);
    
    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    User getUserSync(String userId);
    
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);
    
    @Query("UPDATE users SET updatedAt = :timestamp WHERE userId = :userId")
    void updateTimestamp(String userId, long timestamp);
    
    @Query("UPDATE users SET dailyStepsGoal = :stepsGoal, dailyCaloriesGoal = :caloriesGoal, " +
           "dailySleepGoal = :sleepGoal WHERE userId = :userId")
    void updateGoals(String userId, int stepsGoal, int caloriesGoal, float sleepGoal);
    
    @Query("UPDATE users SET notificationsEnabled = :enabled WHERE userId = :userId")
    void updateNotificationSettings(String userId, boolean enabled);
    
    @Query("UPDATE users SET waterRemindersEnabled = :enabled WHERE userId = :userId")
    void updateWaterReminderSettings(String userId, boolean enabled);
    
    @Query("UPDATE users SET weight = :weight, height = :height WHERE userId = :userId")
    void updatePhysicalData(String userId, float weight, float height);
}
