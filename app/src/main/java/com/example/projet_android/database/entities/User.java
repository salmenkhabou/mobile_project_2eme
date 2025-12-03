package com.example.projet_android.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class User {
      @PrimaryKey
    @NonNull
    public String userId;
    
    public String email;
    public String displayName;
    public String authProvider; // "email", "google"
    public int age;
    public float weight;
    public float height;
    public long createdAt;
    public long updatedAt;
    
    // Objectifs
    public int dailyStepsGoal;
    public int dailyCaloriesGoal;
    public float dailySleepGoal;
    
    // Paramètres
    public boolean notificationsEnabled;
    public boolean waterRemindersEnabled;
      public User() {
        this.userId = ""; // Sera défini lors de l'insertion
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.dailyStepsGoal = 10000;
        this.dailyCaloriesGoal = 2000;
        this.dailySleepGoal = 8.0f;
        this.notificationsEnabled = true;
        this.waterRemindersEnabled = true;
    }
      @Ignore
    public User(String userId, String email, String displayName, String authProvider) {
        this();
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.authProvider = authProvider;
    }
}
