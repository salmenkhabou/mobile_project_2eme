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
 * Receiver pour les rappels d'activité physique
 * Envoie des notifications pour encourager à bouger et faire des pas
 */
public class StepsReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "StepsReminder";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Rappel d'activité déclenché");
        showStepsReminder(context);
    }
    
    /**
     * Affiche une notification de rappel d'activité
     */
    private void showStepsReminder(Context context) {
        String[] stepMessages = {
            "🚶‍♀️ Il est temps de bouger un peu !",
            "🏃‍♂️ Que diriez-vous d'une petite marche ?",
            "👟 Vos jambes ont envie de se dégourdir !",
            "🚶‍♂️ Bougez, votre corps vous remerciera !",
            "🏃‍♀️ Quelques pas de plus vers vos objectifs !",
            "💪 L'activité physique, c'est maintenant !",
            "🎯 Rapprochez-vous de votre objectif de pas !"
        };

        String[] activityTips = {
            "10 000 pas par jour, c'est l'idéal pour la santé",
            "Même une marche de 5 minutes fait du bien",
            "L'exercice améliore l'humeur et l'énergie",
            "Prendre les escaliers compte comme de l'exercice",
            "La marche renforce le système cardiovasculaire",
            "L'activité régulière aide à mieux dormir",
            "Bouger améliore la circulation sanguine"
        };

        Random random = new Random();
        String message = stepMessages[random.nextInt(stepMessages.length)];
        String tip = activityTips[random.nextInt(activityTips.length)];

        PendingIntent pendingIntent = HealthNotificationManager.getOpenAppIntent(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, HealthNotificationManager.CHANNEL_DAILY_REMINDERS)
                .setSmallIcon(R.drawable.ic_steps)
                .setContentTitle("Rappel d'activité")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message + "\n\n💡 Bon à savoir : " + tip))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_steps, "Voir mes pas", pendingIntent)
                .setColor(context.getResources().getColor(R.color.steps_color));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(HealthNotificationManager.NOTIFICATION_STEPS_GOAL, builder.build());

        Log.d(TAG, "Notification d'activité envoyée");
    }
}
