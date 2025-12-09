package com.example.projet_android.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

/**
 * Entité base de données pour stocker les données de santé quotidiennes
 * 
 * Cette entité Room représente la table principale de données d'activité :
 * - Stockage centralisé de toutes les métriques de santé par jour
 * - Relation clé étrangère avec l'utilisateur (cascade delete)
 * - Index optimisé pour requêtes rapides par utilisateur
 * - Support synchronisation bidirectionnelle Google Fit
 * - Horodatage précis pour historique temporel
 * 
 * Données trackées quotidiennement :
 * - Activité physique : pas, calories, distance, fréquence cardiaque
 * - Sommeil : heures de sommeil et qualité
 * - Hydratation : verres d'eau consommés
 * - Bien-être : score d'humeur et niveau d'énergie
 * - Nutrition : calories consommées et macronutriments
 * 
 * @author Équipe de développement Health Tracker
 * @version 2.1
 * @since 1.2
 */
@Entity(tableName = "health_data",
        foreignKeys = @ForeignKey(entity = User.class,
                                parentColumns = "userId",
                                childColumns = "userId",
                                onDelete = ForeignKey.CASCADE),
        indices = {@Index("userId")})
public class HealthData {
    
    /** Clé primaire auto-incrémentée pour chaque entrée */
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    /** ID utilisateur (clé étrangère vers table User) */
    @NonNull
    public String userId;
    
    /** Date au format YYYY-MM-DD pour requêtes optimisées */
    public String date;
    
    /** Timestamp Unix pour tri chronologique précis */
    public long timestamp;
    
    // === DONNÉES D'ACTIVITÉ PHYSIQUE ===
    
    /** Nombre de pas effectués dans la journée */
    public int steps;
    
    /** Calories brûlées par l'activité physique */
    public int calories;
    
    /** Distance totale parcourue en kilomètres */
    public float distance;
    
    /** Fréquence cardiaque moyenne en battements par minute */
    public int heartRate;
    
    /** Durée de sommeil en heures (avec décimales pour précision) */
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
