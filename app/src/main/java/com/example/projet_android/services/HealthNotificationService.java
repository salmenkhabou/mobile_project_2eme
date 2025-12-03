package com.example.projet_android.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.example.projet_android.R;

/**
 * Service principal pour les notifications de santé
 * Gère les notifications d'objectifs atteints et autres événements spéciaux
 */
public class HealthNotificationService extends Service {
    private static final String TAG = "HealthNotificationService";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service de notifications de santé créé");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("GOAL_ACHIEVED".equals(action)) {
                String goalType = intent.getStringExtra("goal_type");
                int goalValue = intent.getIntExtra("goal_value", 0);
                showGoalAchievedNotification(goalType, goalValue);
            }
        }
        return START_NOT_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    /**
     * Affiche une notification pour un objectif atteint
     */
    private void showGoalAchievedNotification(String goalType, int value) {
        String title = "🎉 Objectif atteint !";
        String message = "";
        int icon = R.drawable.ic_notification_health;
        
        switch (goalType) {
            case "steps":
                message = "Félicitations ! Vous avez atteint " + value + " pas aujourd'hui !";
                icon = R.drawable.ic_steps;
                break;
            case "calories":
                message = "Bravo ! Vous avez brûlé " + value + " calories !";
                icon = R.drawable.ic_fire;
                break;
            case "water":
                message = "Excellent ! Vous avez bu " + value + " verres d'eau !";
                icon = R.drawable.ic_water_drop;
                break;
            case "sleep":
                message = "Parfait ! Vous avez dormi " + value + " heures cette nuit !";
                icon = R.drawable.ic_sleep;
                break;
            default:
                message = "Continuez comme ça, vous êtes sur la bonne voie !";
                break;
        }
        
        PendingIntent pendingIntent = HealthNotificationManager.getOpenAppIntent(this);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, HealthNotificationManager.CHANNEL_ACHIEVEMENTS)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 300, 200, 300})
                .setColor(getResources().getColor(R.color.primary_green));
        
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(HealthNotificationManager.NOTIFICATION_HEALTH_TIP, builder.build());
        
        Log.d(TAG, "Notification d'objectif atteint envoyée : " + goalType);
    }
    
    /**
     * Méthode statique pour déclencher une notification d'objectif atteint
     */
    public static void notifyGoalAchieved(Context context, String goalType, int value) {
        Intent intent = new Intent(context, HealthNotificationService.class);
        intent.setAction("GOAL_ACHIEVED");
        intent.putExtra("goal_type", goalType);
        intent.putExtra("goal_value", value);
        context.startService(intent);
    }
}