package com.example.projet_android.models;

/**
 * Modèle pour les données météorologiques
 */
public class WeatherData {
    private double temperature;
    private double feelsLike;
    private int humidity;
    private double pressure;
    private double windSpeed;
    private int windDirection;
    private double visibility;
    private double uvIndex;
    private int cloudCover;
    private String weatherCondition;
    private String weatherDescription;
    private String weatherIcon;
    private String cityName;
    private String country;
    private long timestamp;
    
    // Constructeurs
    public WeatherData() {}
    
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
