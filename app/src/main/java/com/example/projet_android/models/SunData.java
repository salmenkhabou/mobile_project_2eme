package com.example.projet_android.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Modèle pour les données solaires et les heures optimales d'activité
 */
public class SunData {
    private long sunrise;
    private long sunset;
    private long morningGoldenHourStart;
    private long morningGoldenHourEnd;
    private long eveningGoldenHourStart;
    private long eveningGoldenHourEnd;
    private long optimalRunningStart;
    private long optimalRunningEnd;
    private long optimalRunningStart2;
    private long optimalRunningEnd2;
    
    // Constructeur
    public SunData() {}
    
    public SunData(long sunrise, long sunset) {
        this.sunrise = sunrise;
        this.sunset = sunset;
    }
    
    // Getters et Setters
    public long getSunrise() { return sunrise; }
    public void setSunrise(long sunrise) { this.sunrise = sunrise; }
    
    public long getSunset() { return sunset; }
    public void setSunset(long sunset) { this.sunset = sunset; }
    
    public long getMorningGoldenHourStart() { return morningGoldenHourStart; }
    public void setMorningGoldenHourStart(long morningGoldenHourStart) { this.morningGoldenHourStart = morningGoldenHourStart; }
    
    public long getMorningGoldenHourEnd() { return morningGoldenHourEnd; }
    public void setMorningGoldenHourEnd(long morningGoldenHourEnd) { this.morningGoldenHourEnd = morningGoldenHourEnd; }
    
    public long getEveningGoldenHourStart() { return eveningGoldenHourStart; }
    public void setEveningGoldenHourStart(long eveningGoldenHourStart) { this.eveningGoldenHourStart = eveningGoldenHourStart; }
    
    public long getEveningGoldenHourEnd() { return eveningGoldenHourEnd; }
    public void setEveningGoldenHourEnd(long eveningGoldenHourEnd) { this.eveningGoldenHourEnd = eveningGoldenHourEnd; }
    
    public long getOptimalRunningStart() { return optimalRunningStart; }
    public void setOptimalRunningStart(long optimalRunningStart) { this.optimalRunningStart = optimalRunningStart; }
    
    public long getOptimalRunningEnd() { return optimalRunningEnd; }
    public void setOptimalRunningEnd(long optimalRunningEnd) { this.optimalRunningEnd = optimalRunningEnd; }
    
    public long getOptimalRunningStart2() { return optimalRunningStart2; }
    public void setOptimalRunningStart2(long optimalRunningStart2) { this.optimalRunningStart2 = optimalRunningStart2; }
    
    public long getOptimalRunningEnd2() { return optimalRunningEnd2; }
    public void setOptimalRunningEnd2(long optimalRunningEnd2) { this.optimalRunningEnd2 = optimalRunningEnd2; }
    
    // Méthodes utilitaires pour formater les heures
    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    public String getSunriseTime() {
        return formatTime(sunrise);
    }
    
    public String getSunsetTime() {
        return formatTime(sunset);
    }
    
    public String getMorningGoldenHour() {
        return formatTime(morningGoldenHourStart) + " - " + formatTime(morningGoldenHourEnd);
    }
    
    public String getEveningGoldenHour() {
        return formatTime(eveningGoldenHourStart) + " - " + formatTime(eveningGoldenHourEnd);
    }
    
    public String getOptimalRunningTime1() {
        return formatTime(optimalRunningStart) + " - " + formatTime(optimalRunningEnd);
    }
    
    public String getOptimalRunningTime2() {
        return formatTime(optimalRunningStart2) + " - " + formatTime(optimalRunningEnd2);
    }
    
    public long getDaylightDuration() {
        return sunset - sunrise;
    }
    
    public String getDaylightDurationString() {
        long duration = getDaylightDuration();
        long hours = duration / (1000 * 60 * 60);
        long minutes = (duration % (1000 * 60 * 60)) / (1000 * 60);
        return hours + "h" + String.format("%02d", minutes);
    }
    
    /**
     * Vérifie si c'est actuellement l'heure dorée du matin
     */
    public boolean isCurrentlyMorningGoldenHour() {
        long now = System.currentTimeMillis();
        return now >= morningGoldenHourStart && now <= morningGoldenHourEnd;
    }
    
    /**
     * Vérifie si c'est actuellement l'heure dorée du soir
     */
    public boolean isCurrentlyEveningGoldenHour() {
        long now = System.currentTimeMillis();
        return now >= eveningGoldenHourStart && now <= eveningGoldenHourEnd;
    }
    
    /**
     * Vérifie si c'est un moment optimal pour courir
     */
    public boolean isOptimalRunningTime() {
        long now = System.currentTimeMillis();
        return (now >= optimalRunningStart && now <= optimalRunningEnd) ||
               (now >= optimalRunningStart2 && now <= optimalRunningEnd2);
    }
    
    /**
     * Retourne le temps restant jusqu'au coucher du soleil
     */
    public long getTimeUntilSunset() {
        return Math.max(0, sunset - System.currentTimeMillis());
    }
    
    /**
     * Retourne le temps restant jusqu'au lever du soleil
     */
    public long getTimeUntilSunrise() {
        long now = System.currentTimeMillis();
        long nextSunrise = sunrise;
        
        // Si le lever de soleil est déjà passé aujourd'hui, calculer pour demain
        if (nextSunrise < now) {
            nextSunrise += 24 * 60 * 60 * 1000; // Ajouter 24h
        }
        
        return Math.max(0, nextSunrise - now);
    }
    
    /**
     * Retourne une recommandation basée sur l'heure actuelle
     */
    public String getCurrentSunRecommendation() {
        long now = System.currentTimeMillis();
        
        if (isCurrentlyMorningGoldenHour()) {
            return "🌅 C'est l'heure dorée matinale ! Parfait pour la méditation et la course douce.";
        } else if (isCurrentlyEveningGoldenHour()) {
            return "🌇 Heure dorée du soir ! Idéal pour se détendre et admirer le coucher de soleil.";
        } else if (isOptimalRunningTime()) {
            return "🏃‍♀️ Moment optimal pour courir ! Temperature et luminosité parfaites.";
        } else if (now < sunrise) {
            return "🌙 Encore tôt ! Le soleil se lève dans " + formatDuration(getTimeUntilSunrise()) + ".";
        } else if (now > sunset) {
            return "🌜 Le soleil s'est couché. Préparez-vous pour une bonne nuit de repos.";
        } else {
            return "☀️ Belle journée ! Consultez les heures optimales pour vos activités.";
        }
    }
    
    /**
     * Formate une durée en millisecondes en format lisible
     */
    private String formatDuration(long duration) {
        long hours = duration / (1000 * 60 * 60);
        long minutes = (duration % (1000 * 60 * 60)) / (1000 * 60);
        
        if (hours > 0) {
            return hours + "h" + String.format("%02d", minutes);
        } else {
            return minutes + "min";
        }
    }
    
    @Override
    public String toString() {
        return "SunData{" +
                "sunrise=" + getSunriseTime() +
                ", sunset=" + getSunsetTime() +
                ", daylight=" + getDaylightDurationString() +
                '}';
    }
}
