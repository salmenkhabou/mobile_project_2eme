package com.example.projet_android.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

@Entity(tableName = "food_logs",
        foreignKeys = @ForeignKey(entity = User.class,
                                parentColumns = "userId",
                                childColumns = "userId",
                                onDelete = ForeignKey.CASCADE),
        indices = {@Index("userId")})
public class FoodLog {
    
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    @NonNull
    public String userId;
    public String date; // Format YYYY-MM-DD
    public long timestamp;
    
    // Informations sur l'aliment
    public String foodName;
    public String brand;
    public String barcode;
    public String imageUrl;
    
    // Valeurs nutritionnelles (pour 100g)
    public int calories;
    public float protein;
    public float carbs;
    public float fat;
    
    // Quantité consommée
    public float quantity; // en grammes
    public String mealType; // "breakfast", "lunch", "dinner", "snack"
      public FoodLog() {
        this.userId = ""; // Sera défini lors de l'insertion
        this.timestamp = System.currentTimeMillis();
    }
      @Ignore
    public FoodLog(String userId, String date, String foodName) {
        this();
        this.userId = userId;
        this.date = date;
        this.foodName = foodName;
    }
}
