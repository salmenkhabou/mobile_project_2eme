package com.example.projet_android.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

@Entity(tableName = "activities",
        foreignKeys = @ForeignKey(entity = User.class,
                                parentColumns = "userId",
                                childColumns = "userId",
                                onDelete = ForeignKey.CASCADE),
        indices = {@Index("userId")})
public class Activity {
    
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    @NonNull
    public String userId;
    public String date; // Format YYYY-MM-DD
    public long startTime;
    public long endTime;
    
    // Type d'activité
    public String activityType; // "walking", "running", "cycling", "gym", etc.
    public String description;
    
    // Données de l'activité
    public int duration; // en minutes
    public int caloriesBurned;
    public float distance; // en km
    public int averageHeartRate;
      public Activity() {
        this.userId = ""; // Sera défini lors de l'insertion
        this.startTime = System.currentTimeMillis();
    }
      @Ignore
    public Activity(String userId, String date, String activityType) {
        this();
        this.userId = userId;
        this.date = date;
        this.activityType = activityType;
    }
}
