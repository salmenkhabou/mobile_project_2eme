package com.example.projet_android.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

/**
 * Entité utilisateur pour la gestion des comptes et profils
 * 
 * Cette entité Room centralise toutes les informations utilisateur :
 * - Données d'authentification et autorisation
 * - Profil personnel (âge, poids, taille) pour calculs personnalisés
 * - Objectifs de santé configurables par l'utilisateur
 * - Préférences de notifications et rappels
 * - Historique de création et modification du profil
 * 
 * Relation maître pour toutes les autres entités de données de santé.
 * Suppression en cascade pour nettoyer automatiquement les données associées.
 * 
 * @author Équipe de développement Health Tracker
 * @version 2.1
 * @since 1.0
 */
@Entity(tableName = "users")
public class User {
    
    /** Identifiant unique utilisateur (clé primaire) */
    @PrimaryKey
    @NonNull
    public String userId;
    
    // === DONNÉES D'AUTHENTIFICATION ===
    
    /** Adresse email pour connexion */
    public String email;
    
    /** Nom d'affichage dans l'application */
    public String displayName;
    
    /** Fournisseur d'authentification : "email", "google" */
    public String authProvider;
    
    // === PROFIL PERSONNEL ===
    
    /** Âge en années pour calculs métaboliques */
    public int age;
    
    /** Poids actuel en kilogrammes */
    public float weight;
    
    /** Taille en centimètres */
    public float height;
    
    /** Timestamp de création du compte */
    public long createdAt;
    
    /** Timestamp de dernière modification */
    public long updatedAt;
    
    // === OBJECTIFS PERSONNALISÉS ===
    
    /** Objectif quotidien de pas */
    public int dailyStepsGoal;
    
    /** Objectif quotidien de calories brûlées */
    public int dailyCaloriesGoal;
    
    /** Objectif quotidien de sommeil en heures */
    public float dailySleepGoal;
    
    // === PRÉFÉRENCES UTILISATEUR ===
    
    /** Activation des notifications générales */
    public boolean notificationsEnabled;
    
    /** Activation des rappels d'hydratation */
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
