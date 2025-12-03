package com.example.projet_android.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

@Entity(tableName = "health_data",
        foreignKeys = @ForeignKey(entity = User.class,
                                parentColumns = "userId",
                                childColumns = "userId",
                                onDelete = ForeignKey.CASCADE),
        indices = {@Index("userId")})
public class HealthData {
    
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    @NonNull
    public String userId;
    public String date; // Format YYYY-MM-DD
    public long timestamp;
    
    // Données d'activité
    public int steps;
    public int calories;
    public float distance; // en km
    public int heartRate; // BPM moyen
    public float sleepHours;
    
    // Données nutritionnelles (calculées)
    public int totalCaloriesConsumed;
    public float totalProtein;
    public float totalCarbs;
    public float totalFat;
    
    // Hydratation
    public int waterGlasses; // nombre de verres d'eau
      public HealthData() {
        this.userId = ""; // Sera défini lors de l'insertion
        this.timestamp = System.currentTimeMillis();
    }
      @Ignore
    public HealthData(String userId, String date) {
        this();
        this.userId = userId;
        this.date = date;
    }
}
