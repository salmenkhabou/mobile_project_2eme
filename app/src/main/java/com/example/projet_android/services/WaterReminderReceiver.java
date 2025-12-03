package com.example.projet_android.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.example.projet_android.R;

import java.util.Random;

/**
 * Receiver pour les rappels d'hydratation
 * Envoie des notifications pour encourager à boire de l'eau
 */
public class WaterReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "WaterReminder";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Rappel d'hydratation déclenché");
        showWaterReminder(context);
    }
    
    /**
     * Affiche une notification de rappel d'hydratation
     */
    private void showWaterReminder(Context context) {
        String[] waterMessages = {
            "💧 Il est temps de boire un peu d'eau !",
            "🚰 Hydratez-vous pour rester en forme !",
            "💦 Votre corps a besoin d'eau, pensez-y !",
            "🥤 Un petit verre d'eau vous fera du bien",
            "🌊 Restez hydraté(e) toute la journée !",
            "💧 L'eau, c'est la vie ! N'oubliez pas de boire",
            "🚿 Votre peau vous remerciera de boire de l'eau"
        };

        String[] waterTips = {
            "Boire 8 verres d'eau par jour est recommandé",
            "L'eau aide à éliminer les toxines",
            "Une bonne hydratation améliore la concentration",
            "L'eau aide à maintenir une peau saine",
            "Boire de l'eau booste votre métabolisme",
            "L'hydratation aide à réguler la température corporelle",
            "L'eau facilite la digestion et le transport des nutriments"
        };

        Random random = new Random();
        String message = waterMessages[random.nextInt(waterMessages.length)];
        String tip = waterTips[random.nextInt(waterTips.length)];

        PendingIntent pendingIntent = HealthNotificationManager.getOpenAppIntent(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, HealthNotificationManager.CHANNEL_WATER_REMINDER)
                .setSmallIcon(R.drawable.ic_water_drop)
                .setContentTitle("Rappel d'hydratation")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message + "\n\n💡 Le saviez-vous ? " + tip))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(context.getResources().getColor(R.color.secondary_blue));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(HealthNotificationManager.NOTIFICATION_WATER_REMINDER, builder.build());

        Log.d(TAG, "Notification d'hydratation envoyée");
    }
}
