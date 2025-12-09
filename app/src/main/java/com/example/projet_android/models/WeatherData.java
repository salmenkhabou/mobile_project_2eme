package com.example.projet_android.models;

/**
 * ================================
 * MODÈLE DE DONNÉES MÉTÉOROLOGIQUES
 * ================================
 * 
 * Classe modèle représentant les données météorologiques complètes utilisées
 * dans l'application Health Tracker pour fournir des conseils personnalisés.
 * 
 * DONNÉES MÉTÉO PRINCIPALES :
 * • 🌡️ Température réelle et ressentie (°C)
 * • 💧 Taux d'humidité atmosphérique (%)
 * • 📊 Pression atmosphérique (hPa)
 * • 💨 Vitesse et direction du vent
 * • ☀️ Index UV pour protection solaire
 * • ☁️ Couverture nuageuse (%)
 * • 👁️ Visibilité atmosphérique
 * 
 * INFORMATIONS CONTEXTUELLES :
 * • Condition météo générale (ensoleillé, pluvieux, etc.)
 * • Description détaillée des conditions
 * • Icône météo pour affichage visuel
 * • Localisation (ville, pays)
 * • Timestamp de récupération des données
 * 
 * UTILISATION DANS L'APP :
 * • Conseils d'activité physique adaptés à la météo
 * • Recommandations d'hydratation selon température/humidité
 * • Alertes protection solaire basées sur l'index UV
 * • Suggestions d'exercices intérieur/extérieur
 * 
 * @version 1.0
 * @author Équipe Health Tracker
 */
public class WeatherData {    // ============ DONNÉES MÉTÉOROLOGIQUES PRINCIPALES ============
    private double temperature;        // Température en degrés Celsius
    private double feelsLike;         // Température ressentie en °C
    private int humidity;             // Taux d'humidité atmosphérique (0-100%)
    private double pressure;          // Pression atmosphérique en hPa
    private double windSpeed;         // Vitesse du vent en km/h
    private int windDirection;        // Direction du vent en degrés (0-360°)
    private double visibility;        // Visibilité en kilomètres
    private double uvIndex;           // Index UV (0-11+) pour protection solaire
    private int cloudCover;           // Couverture nuageuse en pourcentage (0-100%)
    
    // ============ INFORMATIONS DESCRIPTIVES ============
    private String weatherCondition;   // Condition principale (Clear, Rain, Clouds, etc.)
    private String weatherDescription; // Description détaillée des conditions
    private String weatherIcon;        // Code d'icône pour affichage visuel
    
    // ============ DONNÉES DE LOCALISATION ============
    private String cityName;           // Nom de la ville
    private String country;            // Code pays (ex: "FR", "US")
    private long timestamp;            // Timestamp de récupération des données
    
    // ============ CONSTRUCTEURS ============
    
    /**
     * Constructeur par défaut
     * Utilisé pour la désérialisation JSON et l'initialisation vide
     */
    public WeatherData() {}
    
    /**
     * Constructeur simplifié avec données essentielles
     * Initialise automatiquement le timestamp à la création
     * 
     * @param temperature Température en degrés Celsius
     * @param humidity Taux d'humidité (0-100%)
     * @param weatherCondition Condition météorologique principale
     */
    public WeatherData(double temperature, int humidity, String weatherCondition) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.weatherCondition = weatherCondition;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters et Setters
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    
    public double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }
    
    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }
    
    public double getPressure() { return pressure; }
    public void setPressure(double pressure) { this.pressure = pressure; }
    
    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
    
    public int getWindDirection() { return windDirection; }
    public void setWindDirection(int windDirection) { this.windDirection = windDirection; }
    
    public double getVisibility() { return visibility; }
    public void setVisibility(double visibility) { this.visibility = visibility; }
    
    public double getUvIndex() { return uvIndex; }
    public void setUvIndex(double uvIndex) { this.uvIndex = uvIndex; }
    
    public int getCloudCover() { return cloudCover; }
    public void setCloudCover(int cloudCover) { this.cloudCover = cloudCover; }
    
    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }
    
    public String getWeatherDescription() { return weatherDescription; }
    public void setWeatherDescription(String weatherDescription) { this.weatherDescription = weatherDescription; }
    
    public String getWeatherIcon() { return weatherIcon; }
    public void setWeatherIcon(String weatherIcon) { this.weatherIcon = weatherIcon; }
    
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    // Méthodes utilitaires
    public String getTemperatureString() {
        return Math.round(temperature) + "°C";
    }
    
    public String getFeelsLikeString() {
        return "Ressenti " + Math.round(feelsLike) + "°C";
    }
    
    public String getWindString() {
        return Math.round(windSpeed) + " km/h";
    }
    
    public String getHumidityString() {
        return humidity + "%";
    }
    
    public String getUvIndexString() {
        if (uvIndex <= 2) return "Faible (" + Math.round(uvIndex) + ")";
        else if (uvIndex <= 5) return "Modéré (" + Math.round(uvIndex) + ")";
        else if (uvIndex <= 7) return "Élevé (" + Math.round(uvIndex) + ")";
        else if (uvIndex <= 10) return "Très élevé (" + Math.round(uvIndex) + ")";
        else return "Extrême (" + Math.round(uvIndex) + ")";
    }
    
    public String getVisibilityString() {
        return Math.round(visibility) + " km";
    }
    
    public boolean isGoodForOutdoorActivity() {
        return !weatherCondition.equals("Rain") && 
               !weatherCondition.equals("Thunderstorm") && 
               temperature > 0 && temperature < 35;
    }
    
    public boolean isGoodForRunning() {
        return isGoodForOutdoorActivity() && 
               temperature >= 5 && temperature <= 25 && 
               windSpeed < 20;
    }
    
    @Override
    public String toString() {
        return "WeatherData{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", condition='" + weatherCondition + '\'' +
                ", city='" + cityName + '\'' +
                '}';
    }
}
