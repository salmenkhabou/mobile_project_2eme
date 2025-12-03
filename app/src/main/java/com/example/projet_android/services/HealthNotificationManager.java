package com.example.projet_android.services;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.example.projet_android.MainActivity2;
import com.example.projet_android.R;

import java.util.Calendar;

/**
 * Service de gestion des notifications quotidiennes pour le Health Tracker
 * Gère les rappels quotidiens pour encourager un mode de vie sain
 */
public class HealthNotificationManager {
    private static final String TAG = "HealthNotificationManager";
    
    // Canaux de notification
    public static final String CHANNEL_DAILY_REMINDERS = "daily_reminders";
    public static final String CHANNEL_HEALTH_TIPS = "health_tips"; 
    public static final String CHANNEL_ACHIEVEMENTS = "achievements";
    public static final String CHANNEL_WATER_REMINDER = "water_reminder";
    
    // IDs de notification
    public static final int NOTIFICATION_MORNING_REMINDER = 1001;
    public static final int NOTIFICATION_EVENING_SUMMARY = 1002;
    public static final int NOTIFICATION_WATER_REMINDER = 1003;
    public static final int NOTIFICATION_STEPS_GOAL = 1004;
    public static final int NOTIFICATION_HEALTH_TIP = 1005;
    
    // Codes de requête pour les alarmes
    public static final int REQUEST_MORNING_REMINDER = 2001;
    public static final int REQUEST_EVENING_SUMMARY = 2002;
    public static final int REQUEST_WATER_REMINDER = 2003;
    public static final int REQUEST_STEPS_REMINDER = 2004;
    
    private Context context;
    private AlarmManager alarmManager;
    private NotificationManager notificationManager;
    private SharedPreferences preferences;

    public HealthNotificationManager(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.preferences = context.getSharedPreferences("health_notifications", Context.MODE_PRIVATE);
        
        createNotificationChannels();
    }

    /**
     * Crée les canaux de notification pour Android 8.0+
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canal pour les rappels quotidiens
            NotificationChannel dailyChannel = new NotificationChannel(
                CHANNEL_DAILY_REMINDERS,
                "Rappels quotidiens",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            dailyChannel.setDescription("Rappels pour vos objectifs de santé quotidiens");
            dailyChannel.enableVibration(true);
            
            // Canal pour les conseils santé
            NotificationChannel tipsChannel = new NotificationChannel(
                CHANNEL_HEALTH_TIPS,
                "Conseils santé",
                NotificationManager.IMPORTANCE_LOW
            );
            tipsChannel.setDescription("Conseils et astuces pour une vie plus saine");
            
            // Canal pour les réussites et objectifs
            NotificationChannel achievementsChannel = new NotificationChannel(
                CHANNEL_ACHIEVEMENTS,
                "Objectifs et réussites",
                NotificationManager.IMPORTANCE_HIGH
            );
            achievementsChannel.setDescription("Notifications pour vos objectifs atteints");
            achievementsChannel.enableVibration(true);
            achievementsChannel.enableLights(true);
            
            // Canal pour les rappels d'hydratation
            NotificationChannel waterChannel = new NotificationChannel(
                CHANNEL_WATER_REMINDER,
                "Rappels d'hydratation",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            waterChannel.setDescription("Rappels pour boire de l'eau régulièrement");
            
            notificationManager.createNotificationChannel(dailyChannel);
            notificationManager.createNotificationChannel(tipsChannel);
            notificationManager.createNotificationChannel(achievementsChannel);
            notificationManager.createNotificationChannel(waterChannel);
            
            Log.d(TAG, "Canaux de notification créés avec succès");
        }
    }

    /**
     * Active toutes les notifications quotidiennes par défaut
     */
    public void enableDailyNotifications() {
        scheduleMorningReminder();
        scheduleEveningReminder();
        scheduleWaterReminders();
        scheduleStepsReminders();
        
        preferences.edit().putBoolean("notifications_enabled", true).apply();
        Log.d(TAG, "Notifications quotidiennes activées");
    }

    /**
     * Désactive toutes les notifications quotidiennes
     */
    public void disableDailyNotifications() {
        cancelAllAlarms();
        preferences.edit().putBoolean("notifications_enabled", false).apply();
        Log.d(TAG, "Notifications quotidiennes désactivées");
    }

    /**
     * Programme le rappel matinal (8h00)
     */
    public void scheduleMorningReminder() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        
        // Si c'est déjà passé aujourd'hui, programmer pour demain
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        Intent intent = new Intent(context, DailyHealthReminderReceiver.class);
        intent.setAction("MORNING_REMINDER");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context, REQUEST_MORNING_REMINDER, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(),
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        );
        
        Log.d(TAG, "Rappel matinal programmé pour " + calendar.getTime());
    }

    /**
     * Programme le résumé du soir (20h00)
     */
    public void scheduleEveningReminder() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        Intent intent = new Intent(context, DailyHealthReminderReceiver.class);
        intent.setAction("EVENING_REMINDER");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context, REQUEST_EVENING_SUMMARY, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(),
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        );
        
        Log.d(TAG, "Résumé du soir programmé pour " + calendar.getTime());
    }

    /**
     * Programme les rappels d'hydratation (toutes les 2 heures, 9h-19h)
     */
    public void scheduleWaterReminders() {
        for (int hour = 9; hour <= 19; hour += 2) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            
            Intent intent = new Intent(context, WaterReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, REQUEST_WATER_REMINDER + hour, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            );
        }
        
        Log.d(TAG, "Rappels d'hydratation programmés");
    }

    /**
     * Programme les rappels d'activité (midi et 17h)
     */
    public void scheduleStepsReminders() {
        int[] reminderHours = {12, 17};
        
        for (int hour : reminderHours) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, 30);
            calendar.set(Calendar.SECOND, 0);
            
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            
            Intent intent = new Intent(context, StepsReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, REQUEST_STEPS_REMINDER + hour, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            );
        }
        
        Log.d(TAG, "Rappels d'activité programmés");
    }

    /**
     * Annule toutes les alarmes de notification
     */
    private void cancelAllAlarms() {
        // Annuler rappel matinal
        Intent morningIntent = new Intent(context, DailyHealthReminderReceiver.class);
        PendingIntent morningPendingIntent = PendingIntent.getBroadcast(
            context, REQUEST_MORNING_REMINDER, morningIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(morningPendingIntent);
        
        // Annuler rappel du soir
        Intent eveningIntent = new Intent(context, DailyHealthReminderReceiver.class);
        PendingIntent eveningPendingIntent = PendingIntent.getBroadcast(
            context, REQUEST_EVENING_SUMMARY, eveningIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(eveningPendingIntent);
        
        // Annuler rappels d'eau
        for (int hour = 9; hour <= 19; hour += 2) {
            Intent waterIntent = new Intent(context, WaterReminderReceiver.class);
            PendingIntent waterPendingIntent = PendingIntent.getBroadcast(
                context, REQUEST_WATER_REMINDER + hour, waterIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.cancel(waterPendingIntent);
        }
        
        // Annuler rappels d'activité
        int[] reminderHours = {12, 17};
        for (int hour : reminderHours) {
            Intent stepsIntent = new Intent(context, StepsReminderReceiver.class);
            PendingIntent stepsPendingIntent = PendingIntent.getBroadcast(
                context, REQUEST_STEPS_REMINDER + hour, stepsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.cancel(stepsPendingIntent);
        }
        
        Log.d(TAG, "Toutes les alarmes ont été annulées");
    }

    /**
     * Vérifie si les notifications sont activées
     */
    public boolean areNotificationsEnabled() {
        return preferences.getBoolean("notifications_enabled", true);
    }

    /**
     * Obtient le PendingIntent pour ouvrir l'application
     */
    public static PendingIntent getOpenAppIntent(Context context) {
        Intent intent = new Intent(context, MainActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
}
