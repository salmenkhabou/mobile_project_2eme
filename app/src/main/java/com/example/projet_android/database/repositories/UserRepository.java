package com.example.projet_android.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.projet_android.database.AppDatabase;
import com.example.projet_android.database.dao.UserDao;
import com.example.projet_android.database.entities.User;

public class UserRepository {
    
    private UserDao userDao;
    
    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
    }
    
    // Méthodes synchrones (pour les opérations en arrière-plan)
    public void insertUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insertUser(user);
        });
    }
    
    public void updateUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.updateUser(user);
        });
    }
    
    public void deleteUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.deleteUser(user);
        });
    }
    
    // Méthodes asynchrones (LiveData)
    public LiveData<User> getUser(String userId) {
        return userDao.getUser(userId);
    }
    
    public User getUserSync(String userId) {
        return userDao.getUserSync(userId);
    }
    
    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }
    
    // Méthodes de mise à jour spécifiques
    public void updateTimestamp(String userId, long timestamp) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.updateTimestamp(userId, timestamp);
        });
    }
    
    public void updateGoals(String userId, int stepsGoal, int caloriesGoal, float sleepGoal) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.updateGoals(userId, stepsGoal, caloriesGoal, sleepGoal);
        });
    }
    
    public void updateNotificationSettings(String userId, boolean enabled) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.updateNotificationSettings(userId, enabled);
        });
    }
    
    public void updateWaterReminderSettings(String userId, boolean enabled) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.updateWaterReminderSettings(userId, enabled);
        });
    }
    
    public void updatePhysicalData(String userId, float weight, float height) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.updatePhysicalData(userId, weight, height);
        });
    }
}
