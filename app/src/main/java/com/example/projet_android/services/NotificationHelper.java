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

/**
 * ========================================
 * GESTIONNAIRE DE NOTIFICATIONS DE SANTÉ
 * ========================================
 * 
 * Service utilitaire pour la gestion des notifications push de l'application Health Tracker.
 * 
 * FONCTIONNALITÉS PRINCIPALES :
 * • Création et gestion du canal de notifications Android
 * • Notifications de rappels santé (pas, hydratation, repas, sommeil)
 * • Programmation de rappels automatiques périodiques
 * • Gestion des alarmes récurrentes avec AlarmManager
 * • Interface unifiée pour tous types de notifications santé
 * 
 * TYPES DE RAPPELS GÉRÉS :
 * • 🚶 Rappels d'activité physique (pas quotidiens)
 * • 💧 Rappels d'hydratation (toutes les 2h de 8h-20h)
 * • 🍽️ Rappels de repas et nutrition
 * • 😴 Rappels de sommeil et repos
 * 
 * @version 1.0
 * @author Équipe Health Tracker
 */
public class NotificationHelper {
      // ============ CONSTANTES DE CONFIGURATION ============
    private static final String CHANNEL_ID = "health_reminders";
    private static final String CHANNEL_NAME = "Rappels de Santé";
    private static final String CHANNEL_DESCRIPTION = "Notifications pour les rappels d'activité et de santé";
    
    // ============ VARIABLES MEMBRES ============
    private Context context;  // Contexte de l'application Android
    private NotificationManager notificationManager;  // Gestionnaire système des notifications
    
    /**
     * Constructeur principal du gestionnaire de notifications
     * Initialise le canal de notifications et configure le service système
     * 
     * @param context Contexte de l'application Android
     */
    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }
      /**
     * Crée le canal de notification pour Android 8.0+
     * Requis pour afficher des notifications sur les versions récentes d'Android
     */
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
    
    // ============ MÉTHODES DE NOTIFICATIONS SPÉCIALISÉES ============
    
    /**
     * Affiche un rappel pour l'activité physique quotidienne
     * Rappelle à l'utilisateur d'atteindre son objectif de pas
     */
    public void showStepsReminder() {
        showNotification(
                1,
                "🚶 Temps de bouger !",
                "Vous n'avez pas encore atteint votre objectif de pas aujourd'hui"
        );
    }
    
    /**
     * Affiche un rappel d'hydratation
     * Encourage l'utilisateur à boire de l'eau régulièrement
     */
    public void showWaterReminder() {
        showNotification(
                2,
                "💧 N'oubliez pas de boire !",
                "Il est temps de boire un verre d'eau"
        );
    }
    
    /**
     * Affiche un rappel pour l'enregistrement des repas
     * Aide au suivi nutritionnel quotidien
     */
    public void showMealReminder() {
        showNotification(
                3,
                "🍽️ Temps de manger !",
                "N'oubliez pas d'enregistrer votre repas"
        );
    }
    
    /**
     * Affiche un rappel pour le sommeil
     * Encourage une routine de sommeil saine
     */
    public void showSleepReminder() {
        showNotification(
                4,
                "😴 Temps de dormir !",
                "Il est temps de vous préparer pour une bonne nuit de sommeil"
        );
    }
      /**
     * Méthode générique pour afficher une notification
     * Configure l'intent de redirection et construit la notification avec les paramètres fournis
     * 
     * @param notificationId ID unique de la notification
     * @param title Titre de la notification
     * @param content Contenu descriptif de la notification
     */
    private void showNotification(int notificationId, String title, String content) {
        // Configuration de l'intent pour rediriger vers l'activité principale
        Intent intent = new Intent(context, MainActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        // Création du PendingIntent pour l'action de clic sur la notification
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Construction de la notification avec tous les paramètres
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)  // Icône de la notification
                .setContentTitle(title)  // Titre principal
                .setContentText(content)  // Message descriptif
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)  // Priorité normale
                .setContentIntent(pendingIntent)  // Action au clic
                .setAutoCancel(true);  // Suppression automatique au clic
        
        // Affichage de la notification avec gestion des erreurs de permissions
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            // Permission de notification non accordée par l'utilisateur
            e.printStackTrace();
        }
    }
      // ============ MÉTHODES DE PROGRAMMATION D'ALARMES ============
    
    /**
     * Programme des rappels automatiques d'hydratation
     * Configure des alarmes récurrentes toutes les 2 heures entre 8h et 20h
     * Utilise AlarmManager pour garantir l'exécution même en arrière-plan
     */
    public void scheduleWaterReminders() {
        // Obtention du service AlarmManager pour programmer les rappels
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        // Boucle pour créer des rappels toutes les 2 heures (8h, 10h, 12h, 14h, 16h, 18h, 20h)
        for (int hour = 8; hour <= 20; hour += 2) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            
            // Si l'heure est déjà passée aujourd'hui, programmer pour demain
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            // Configuration de l'intent pour déclencher le BroadcastReceiver
            Intent intent = new Intent(context, WaterReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    hour, // Utiliser l'heure comme ID unique pour éviter les conflits
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            // Programmation de l'alarme récurrente quotidienne
            if (alarmManager != null) {
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,  // Type d'alarme avec réveil du système
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, // Répéter chaque jour (24h)
                        pendingIntent
                );
            }
        }
    }
    
    /**
     * Programme un rappel quotidien pour l'activité physique
     * Déclenche une notification à 18h pour rappeler l'objectif de pas
     * Utilise une alarme récurrente pour maintenir la motivation quotidienne
     */
    public void scheduleStepsReminder() {
        // Configuration du calendrier pour 18h (heure optimale pour vérifier les pas)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        
        // Si 18h est déjà passé aujourd'hui, programmer pour demain
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        // Configuration de l'intent pour le BroadcastReceiver des pas
        Intent intent = new Intent(context, StepsReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                100,  // ID unique pour le rappel des pas
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Programmation de l'alarme quotidienne à 18h
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,  // Répétition quotidienne
                    pendingIntent
            );
        }
    }
}
