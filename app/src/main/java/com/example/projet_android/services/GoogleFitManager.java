package com.example.projet_android.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;

import com.example.projet_android.database.DatabaseManager;
import com.example.projet_android.utils.PreferencesManager;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import com.google.android.gms.fitness.data.Bucket;

public class GoogleFitManager {
    
    private static final String TAG = "GoogleFitManager";
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1000;
    public static final int GOOGLE_SIGN_IN_REQUEST_CODE = 1001;
    
    private Context context;
    private FitnessOptions fitnessOptions;
    private GoogleSignInClient googleSignInClient;
    private DatabaseManager databaseManager;
    private PreferencesManager preferencesManager;
    private boolean hasLoggedAuthError = false;
    private boolean isInDemoMode = false;
      public interface FitnessDataListener {
        void onStepsReceived(int steps);
        void onCaloriesReceived(int calories);
        void onSleepReceived(float sleepHours);
        void onError(String error);
    }
    
    public interface AuthenticationListener {
        void onSignInRequired();
        void onSignInSuccess();
        void onSignInFailed(String error);
        void onDemoModeActivated();
    }
    
    public GoogleFitManager(Context context) {
        this.context = context;
        this.databaseManager = DatabaseManager.getInstance(context);
        this.preferencesManager = new PreferencesManager(context);
        setupFitnessOptions();
        setupGoogleSignIn();
    }
      private void setupFitnessOptions() {
        fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .build();
    }
    
    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .addExtension(fitnessOptions)
                .build();
        
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }
      public boolean hasPermissions() {
        GoogleSignInAccount account = getGoogleAccount();
        return account != null && GoogleSignIn.hasPermissions(account, fitnessOptions);
    }
    
    public void requestPermissions(Activity activity) {
        GoogleSignIn.requestPermissions(
                activity,
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                getGoogleAccount(),
                fitnessOptions);
    }
    
    private GoogleSignInAccount getGoogleAccount() {
        return GoogleSignIn.getAccountForExtension(context, fitnessOptions);
    }
    
    public boolean isSignedIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        return account != null;
    }
    
    public void signIn(Activity activity, AuthenticationListener listener) {
        if (isSignedIn() && hasPermissions()) {
            listener.onSignInSuccess();
            return;
        }
        
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
    }
    
    public void signOut() {
        if (googleSignInClient != null) {
            googleSignInClient.signOut();
        }
        isInDemoMode = true;
        Log.i(TAG, "Déconnexion Google - Passage en mode démo");
    }
    
    public void enableDemoMode() {
        isInDemoMode = true;
        preferencesManager.setDemoMode(true);
        Log.i(TAG, "Mode démo activé");
    }
    
    public boolean isDemoMode() {
        return isInDemoMode || preferencesManager.isDemoMode();
    }
    
    public void handleSignInResult(Intent data, AuthenticationListener listener) {
        try {
            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult();
            if (account != null) {
                if (hasPermissions()) {
                    isInDemoMode = false;
                    preferencesManager.setDemoMode(false);
                    listener.onSignInSuccess();
                } else {
                    listener.onSignInFailed("Permissions Google Fit requises");
                }
            } else {
                listener.onSignInFailed("Connexion Google échouée");
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur de connexion Google: " + e.getMessage());
            listener.onSignInFailed(e.getMessage());
        }
    }    public void readTodaySteps(FitnessDataListener listener) {
        // Si en mode démo ou pas de connexion Google, utiliser les données simulées
        if (isDemoMode() || !isSignedIn() || !hasPermissions()) {
            if (!hasLoggedAuthError) {
                Log.i(TAG, "Mode démo ou pas de connexion Google Fit - utilisation de données simulées");
                hasLoggedAuthError = true;
            }
            getSimulatedData(listener);
            return;
        }
        
        GoogleSignInAccount account = getGoogleAccount();
        if (account == null) {
            getSimulatedData(listener);
            return;
        }
        
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        long startTime = cal.getTimeInMillis();
        long endTime = now.getTime();
        
        // Use bucketByTime(1 day) when requesting aggregation to comply with Google Fit requirements
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        
        Fitness.getHistoryClient(context, account)
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        int totalSteps = 0;

                        // Handle buckets (aggregation) if present
                        if (dataReadResponse.getBuckets() != null && !dataReadResponse.getBuckets().isEmpty()) {
                            for (Bucket bucket : dataReadResponse.getBuckets()) {
                                for (DataSet dataSet : bucket.getDataSets()) {
                                    for (DataPoint dp : dataSet.getDataPoints()) {
                                        for (Field field : dp.getDataType().getFields()) {
                                            try {
                                                int steps = dp.getValue(field).asInt();
                                                totalSteps += steps;
                                            } catch (Exception ex) {
                                                // ignore parse errors
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            for (DataSet dataSet : dataReadResponse.getDataSets()) {
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for (Field field : dp.getDataType().getFields()) {
                                        try {
                                            int steps = dp.getValue(field).asInt();
                                            totalSteps += steps;
                                        } catch (Exception ex) {
                                            // ignore parse errors
                                        }
                                    }
                                }
                            }
                        }

                        // Sauvegarder dans la base de données
                        String userId = preferencesManager.getUserId();
                        if (userId != null) {
                            databaseManager.updateTodaysSteps(userId, totalSteps, 0, 0);
                        }
                        
                        listener.onStepsReceived(totalSteps);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Erreur lors de la lecture des pas: " + e.getMessage());
                        listener.onError(e.getMessage());
                    }
                });
    }    public void readTodayCalories(FitnessDataListener listener) {
        // Si en mode démo ou pas de connexion Google, utiliser les données simulées
        if (isDemoMode() || !isSignedIn() || !hasPermissions()) {
            getSimulatedData(listener);
            return;
        }
        
        GoogleSignInAccount account = getGoogleAccount();
        if (account == null) {
            getSimulatedData(listener);
            return;
        }
        
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        long startTime = cal.getTimeInMillis();
        long endTime = now.getTime();
        
        // Add bucketByTime for aggregation
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        
        Fitness.getHistoryClient(context, account)
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        float totalCalories = 0f;

                        if (dataReadResponse.getBuckets() != null && !dataReadResponse.getBuckets().isEmpty()) {
                            for (Bucket bucket : dataReadResponse.getBuckets()) {
                                for (DataSet dataSet : bucket.getDataSets()) {
                                    for (DataPoint dp : dataSet.getDataPoints()) {
                                        for (Field field : dp.getDataType().getFields()) {
                                            try {
                                                float calories = dp.getValue(field).asFloat();
                                                totalCalories += calories;
                                            } catch (Exception ex) {
                                                // ignore parse errors
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            for (DataSet dataSet : dataReadResponse.getDataSets()) {
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for (Field field : dp.getDataType().getFields()) {
                                        try {
                                            float calories = dp.getValue(field).asFloat();
                                            totalCalories += calories;
                                        } catch (Exception ex) {
                                            // ignore parse errors
                                        }
                                    }
                                }
                            }
                        }

                        // Mettre à jour les calories dans la base de données (optionnel)
                        String userId = preferencesManager.getUserId();
                        if (userId != null) {
                            // Option: update nutrition/calories if desired
                        }
                        
                        listener.onCaloriesReceived((int) totalCalories);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Erreur lors de la lecture des calories: " + e.getMessage());
                        listener.onError(e.getMessage());
                    }
                });
    }
        // Méthode pour synchroniser toutes les données de fitness
    public void syncAllFitnessData(FitnessDataListener listener) {
        String userId = preferencesManager.getUserId();
        if (userId == null) {
            Log.w(TAG, "Utilisateur non connecté, utilisation de données simulées");
            // Utiliser des données simulées même si l'utilisateur n'est pas connecté
            getSimulatedData(listener);
            return;
        }
        
        if (hasPermissions()) {
            // Synchroniser avec Google Fit
            readTodaySteps(new FitnessDataListener() {
                @Override
                public void onStepsReceived(int steps) {                    readTodayCalories(new FitnessDataListener() {
                        @Override
                        public void onStepsReceived(int steps) {}
                        
                        @Override
                        public void onCaloriesReceived(int calories) {
                            // Calculer une distance approximative (0.7m par pas en moyenne)
                            float distance = steps * 0.0007f;
                            
                            // Sauvegarder toutes les données
                            databaseManager.updateTodaysSteps(userId, steps, calories, distance);
                            
                            listener.onStepsReceived(steps);
                            listener.onCaloriesReceived(calories);
                        }
                        
                        @Override
                        public void onSleepReceived(float sleepHours) {}
                        
                        @Override
                        public void onError(String error) {
                            listener.onError(error);
                        }
                    });
                }
                
                @Override
                public void onCaloriesReceived(int calories) {}
                
                @Override
                public void onSleepReceived(float sleepHours) {}
                
                @Override
                public void onError(String error) {
                    // Si Google Fit échoue, utiliser des données simulées
                    getSimulatedData(listener);
                }
            });
        } else {
            // Utiliser des données simulées si pas de permissions
            getSimulatedData(listener);
        }
    }
      // Méthode pour simuler des données en cas d'absence de Google Fit
    public void getSimulatedData(FitnessDataListener listener) {
        String userId = preferencesManager.getUserId();
        
        // Si pas d'userId, créer un utilisateur par défaut
        if (userId == null || userId.isEmpty()) {
            userId = "default_user";
            preferencesManager.setUserId(userId);
        }
        
        // Simuler des données réalistes
        int simulatedSteps = (int) (Math.random() * 5000) + 5000; // Entre 5000 et 10000
        int simulatedCalories = (int) (Math.random() * 500) + 1500; // Entre 1500 et 2000
        float simulatedSleep = (float) (Math.random() * 3) + 6.5f; // Entre 6.5 et 9.5 heures
        float simulatedDistance = simulatedSteps * 0.0007f; // Distance approximative
        
        // Sauvegarder les données simulées
        try {
            databaseManager.updateTodaysSteps(userId, simulatedSteps, simulatedCalories, simulatedDistance);
            databaseManager.updateTodaysSleep(userId, simulatedSleep);
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la sauvegarde des données simulées: " + e.getMessage());
        }
        
        listener.onStepsReceived(simulatedSteps);
        listener.onCaloriesReceived(simulatedCalories);
        listener.onSleepReceived(simulatedSleep);
    }
}
