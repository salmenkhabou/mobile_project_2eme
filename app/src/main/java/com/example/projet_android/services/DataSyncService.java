package com.example.projet_android.services;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.projet_android.database.DatabaseManager;
import com.example.projet_android.database.entities.HealthData;
import com.example.projet_android.utils.PreferencesManager;

import java.util.List;

public class DataSyncService {
    
    private static final String TAG = "DataSyncService";
    private Context context;
    private DatabaseManager databaseManager;
    private PreferencesManager preferencesManager;
    private GoogleFitManager googleFitManager;
    
    public interface SyncListener {
        void onSyncStarted();
        void onSyncCompleted();
        void onSyncError(String error);
        void onDataUpdated(HealthData healthData);
    }
    
    public DataSyncService(Context context) {
        this.context = context;
        this.databaseManager = DatabaseManager.getInstance(context);
        this.preferencesManager = new PreferencesManager(context);
        this.googleFitManager = new GoogleFitManager(context);
    }
    
    // Synchronisation complète des données
    public void syncAllData(SyncListener listener) {
        String userId = preferencesManager.getUserId();
        if (userId == null) {
            listener.onSyncError("Utilisateur non connecté");
            return;
        }
        
        listener.onSyncStarted();
        
        // Synchroniser les données de fitness
        googleFitManager.syncAllFitnessData(new GoogleFitManager.FitnessDataListener() {
            @Override
            public void onStepsReceived(int steps) {
                Log.d(TAG, "Données de pas synchronisées: " + steps);
            }
            
            @Override
            public void onCaloriesReceived(int calories) {
                Log.d(TAG, "Données de calories synchronisées: " + calories);
            }
            
            @Override
            public void onSleepReceived(float sleepHours) {
                Log.d(TAG, "Données de sommeil synchronisées: " + sleepHours);
                
                // Synchroniser les données nutritionnelles
                databaseManager.syncNutritionData(userId);
                
                // Récupérer les données mises à jour
                LiveData<HealthData> todaysData = databaseManager.getTodaysHealthData(userId);
                todaysData.observeForever(new Observer<HealthData>() {
                    @Override
                    public void onChanged(HealthData healthData) {
                        if (healthData != null) {
                            listener.onDataUpdated(healthData);
                            listener.onSyncCompleted();
                        }
                        todaysData.removeObserver(this);
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Erreur de synchronisation: " + error);
                listener.onSyncError(error);
            }
        });
    }
    
    // Synchronisation rapide (seulement les pas et calories)
    public void quickSync(SyncListener listener) {
        String userId = preferencesManager.getUserId();
        if (userId == null) {
            listener.onSyncError("Utilisateur non connecté");
            return;
        }
        
        listener.onSyncStarted();
        
        googleFitManager.readTodaySteps(new GoogleFitManager.FitnessDataListener() {
            @Override
            public void onStepsReceived(int steps) {
                googleFitManager.readTodayCalories(new GoogleFitManager.FitnessDataListener() {
                    @Override
                    public void onCaloriesReceived(int calories) {
                        float distance = steps * 0.0007f; // Distance approximative
                        databaseManager.updateTodaysSteps(userId, steps, calories, distance);
                        
                        // Récupérer les données mises à jour
                        LiveData<HealthData> todaysData = databaseManager.getTodaysHealthData(userId);
                        todaysData.observeForever(new Observer<HealthData>() {
                            @Override
                            public void onChanged(HealthData healthData) {
                                if (healthData != null) {
                                    listener.onDataUpdated(healthData);
                                    listener.onSyncCompleted();
                                }
                                todaysData.removeObserver(this);
                            }
                        });
                    }
                    
                    @Override
                    public void onStepsReceived(int steps) {}
                    @Override
                    public void onSleepReceived(float sleepHours) {}
                    @Override
                    public void onError(String error) {
                        listener.onSyncError(error);
                    }
                });
            }
            
            @Override
            public void onCaloriesReceived(int calories) {}
            @Override
            public void onSleepReceived(float sleepHours) {}
            @Override
            public void onError(String error) {
                listener.onSyncError(error);
            }
        });
    }
    
    // Mise à jour manuelle des données de sommeil
    public void updateSleepData(float sleepHours) {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            databaseManager.updateTodaysSleep(userId, sleepHours);
        }
    }
    
    // Mise à jour manuelle de la consommation d'eau
    public void updateWaterIntake(int waterGlasses) {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            databaseManager.updateTodaysWater(userId, waterGlasses);
        }
    }
    
    // Mise à jour manuelle du rythme cardiaque
    public void updateHeartRate(int heartRate) {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            databaseManager.updateTodaysHeartRate(userId, heartRate);
        }
    }
    
    // Méthode pour vérifier si une synchronisation automatique est nécessaire
    public boolean needsSync() {
        long lastSync = preferencesManager.getLastSyncTime();
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - lastSync;
        
        // Synchroniser si plus de 30 minutes depuis la dernière sync
        return timeDifference > (30 * 60 * 1000);
    }
    
    // Marquer la dernière synchronisation
    public void markLastSync() {
        preferencesManager.setLastSyncTime(System.currentTimeMillis());
    }
      // Synchronisation automatique en arrière-plan
    public void autoSync() {
        String userId = preferencesManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "Pas d'utilisateur configuré pour la synchronisation automatique");
            return;
        }
        
        if (needsSync()) {
            quickSync(new SyncListener() {
                @Override
                public void onSyncStarted() {
                    Log.d(TAG, "Synchronisation automatique démarrée");
                }
                
                @Override
                public void onSyncCompleted() {
                    markLastSync();
                    Log.d(TAG, "Synchronisation automatique terminée");
                }
                
                @Override
                public void onSyncError(String error) {
                    Log.e(TAG, "Erreur de synchronisation automatique: " + error);
                }
                
                @Override
                public void onDataUpdated(HealthData healthData) {
                    Log.d(TAG, "Données mises à jour automatiquement");
                }
            });
        }
    }
}
