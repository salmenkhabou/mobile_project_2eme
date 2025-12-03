package com.example.projet_android.services;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.projet_android.MainActivity2;
import com.example.projet_android.R;

import java.util.Calendar;

public class NotificationHelper {
    
    private static final String CHANNEL_ID = "health_reminders";
    private static final String CHANNEL_NAME = "Rappels de Santé";
    private static final String CHANNEL_DESCRIPTION = "Notifications pour les rappels d'activité et de santé";
    
    private Context context;
    private NotificationManager notificationManager;
    
    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    public void showStepsReminder() {
        showNotification(
                1,
                "🚶 Temps de bouger !",
                "Vous n'avez pas encore atteint votre objectif de pas aujourd'hui"
        );
    }
    
    public void showWaterReminder() {
        showNotification(
                2,
                "💧 N'oubliez pas de boire !",
                "Il est temps de boire un verre d'eau"
        );
    }
    
    public void showMealReminder() {
        showNotification(
                3,
                "🍽️ Temps de manger !",
                "N'oubliez pas d'enregistrer votre repas"
        );
    }
    
    public void showSleepReminder() {
        showNotification(
                4,
                "😴 Temps de dormir !",
                "Il est temps de vous préparer pour une bonne nuit de sommeil"
        );
    }
    
    private void showNotification(int notificationId, String title, String content) {
        Intent intent = new Intent(context, MainActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            // Permission de notification non accordée
            e.printStackTrace();
        }
    }
    
    public void scheduleWaterReminders() {
        // Programmer des rappels d'eau toutes les 2 heures entre 8h et 20h
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        for (int hour = 8; hour <= 20; hour += 2) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            
            // Si l'heure est déjà passée aujourd'hui, programmer pour demain
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            Intent intent = new Intent(context, WaterReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    hour, // Utiliser l'heure comme ID unique
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            if (alarmManager != null) {
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, // Répéter chaque jour
                        pendingIntent
                );
            }
        }
    }
    
    public void scheduleStepsReminder() {
        // Programmer un rappel à 18h pour les pas
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        Intent intent = new Intent(context, StepsReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                100,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }
}
