package com.example.projet_android.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.example.projet_android.MainActivity2;
import com.example.projet_android.R;

import java.util.Random;

/**
 * Receiver pour les notifications quotidiennes de santé
 * Gère les rappels matinaux et les résumés du soir
 */
public class DailyHealthReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "DailyHealthReminder";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Notification déclenchée: " + action);

        if ("MORNING_REMINDER".equals(action)) {
            showMorningReminder(context);
        } else if ("EVENING_REMINDER".equals(action)) {
            showEveningReminder(context);
        }
    }

    /**
     * Affiche le rappel matinal motivant
     */
    private void showMorningReminder(Context context) {
        String[] morningMessages = {
            "🌅 Bonjour ! Commencez votre journée en forme !",
            "💪 Nouvelle journée, nouveaux objectifs santé !",
            "🚶‍♀️ Prêt(e) pour une journée active ?",
            "🌟 Votre santé vous attend, c'est parti !",
            "☀️ Bon matin ! N'oubliez pas de bouger aujourd'hui !",
            "🎯 Objectif du jour : prendre soin de votre santé !",
            "🏃‍♀️ Une journée parfaite pour être en forme !"
        };

        String[] morningTips = {
            "Buvez un grand verre d'eau pour bien démarrer",
            "Quelques étirements réveilleront votre corps",
            "Prenez un petit-déjeuner équilibré",
            "Planifiez 30 minutes d'activité aujourd'hui",
            "Respirez profondément et souriez !",
            "Fixez-vous un objectif de pas pour la journée",
            "Hydratez-vous régulièrement toute la journée"
        };

        Random random = new Random();
        String message = morningMessages[random.nextInt(morningMessages.length)];
        String tip = morningTips[random.nextInt(morningTips.length)];

        PendingIntent pendingIntent = HealthNotificationManager.getOpenAppIntent(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, HealthNotificationManager.CHANNEL_DAILY_REMINDERS)
                .setSmallIcon(R.drawable.ic_notification_health)
                .setContentTitle("Health Tracker - Bon matin !")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message + "\n\n💡 Conseil du jour : " + tip))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_steps, "Voir mes objectifs", pendingIntent)
                .setColor(context.getResources().getColor(R.color.primary_green));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(HealthNotificationManager.NOTIFICATION_MORNING_REMINDER, builder.build());

        Log.d(TAG, "Rappel matinal envoyé");
    }

    /**
     * Affiche le résumé du soir
     */
    private void showEveningReminder(Context context) {
        String[] eveningMessages = {
            "🌙 Bonsoir ! Comment s'est passée votre journée santé ?",
            "📊 Il est temps de faire le bilan de votre journée !",
            "🎯 Avez-vous atteint vos objectifs aujourd'hui ?",
            "⭐ Consultez vos progrès de la journée",
            "💤 Préparez une bonne nuit de repos réparateur",
            "📈 Voyons ensemble vos accomplissements du jour",
            "🏆 Chaque pas compte, regardez vos progrès !"
        };

        String[] eveningTips = {
            "Une bonne nuit de sommeil aide à récupérer",
            "Évitez les écrans 1h avant le coucher",
            "Planifiez vos objectifs pour demain",
            "Félicitez-vous pour vos efforts aujourd'hui",
            "Préparez vos affaires pour une matinée active",
            "Respirez profondément pour vous détendre",
            "Hydratez-vous une dernière fois"
        };

        Random random = new Random();
        String message = eveningMessages[random.nextInt(eveningMessages.length)];
        String tip = eveningTips[random.nextInt(eveningTips.length)];

        PendingIntent pendingIntent = HealthNotificationManager.getOpenAppIntent(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, HealthNotificationManager.CHANNEL_DAILY_REMINDERS)
                .setSmallIcon(R.drawable.ic_notification_health)
                .setContentTitle("Health Tracker - Bilan du soir")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message + "\n\n💡 Conseil du soir : " + tip))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_chart, "Voir mon bilan", pendingIntent)
                .setColor(context.getResources().getColor(R.color.secondary_blue));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(HealthNotificationManager.NOTIFICATION_EVENING_SUMMARY, builder.build());

        Log.d(TAG, "Résumé du soir envoyé");
    }
}
