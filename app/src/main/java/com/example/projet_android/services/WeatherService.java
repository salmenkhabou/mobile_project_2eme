package com.example.projet_android.services;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.projet_android.models.WeatherData;
import com.example.projet_android.models.SunData;
import com.example.projet_android.utils.PreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Service de gestion des données météorologiques et solaires
 * Intègre OpenWeatherMap API pour les recommandations d'activité basées sur la météo
 */
public class WeatherService {
    private static final String TAG = "WeatherService";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "YOUR_OPENWEATHER_API_KEY"; // À remplacer par votre clé API
    
    private Context context;
    private PreferencesManager preferencesManager;
    private WeatherAPI weatherAPI;
    
    public interface WeatherDataListener {
        void onWeatherReceived(WeatherData weatherData);
        void onSunDataReceived(SunData sunData);
        void onActivityRecommendation(ActivityRecommendation recommendation);
        void onError(String error);
    }
    
    public interface WeatherAPI {
        @GET("weather")
        Call<WeatherResponse> getCurrentWeather(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String language
        );
        
        @GET("forecast")
        Call<ForecastResponse> getForecast(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String language
        );
    }
    
    public WeatherService(Context context) {
        this.context = context;
        this.preferencesManager = new PreferencesManager(context);
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
                
        weatherAPI = retrofit.create(WeatherAPI.class);
    }
    
    /**
     * Récupère les données météo actuelles pour une localisation
     */
    public void getCurrentWeather(double latitude, double longitude, WeatherDataListener listener) {
        Call<WeatherResponse> call = weatherAPI.getCurrentWeather(
            latitude, longitude, API_KEY, "metric", "fr"
        );
        
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    
                    // Convertir en WeatherData
                    WeatherData weatherData = convertToWeatherData(weatherResponse);
                    listener.onWeatherReceived(weatherData);
                    
                    // Extraire les données solaires
                    SunData sunData = extractSunData(weatherResponse);
                    listener.onSunDataReceived(sunData);
                    
                    // Générer des recommandations d'activité
                    ActivityRecommendation recommendation = generateActivityRecommendation(weatherData, sunData);
                    listener.onActivityRecommendation(recommendation);
                    
                    // Sauvegarder en cache
                    preferencesManager.saveWeatherData(weatherData);
                    
                } else {
                    listener.onError("Erreur API météo: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e(TAG, "Erreur réseau météo", t);
                listener.onError("Erreur réseau: " + t.getMessage());
                
                // Utiliser les données en cache si disponibles
                WeatherData cachedData = preferencesManager.getCachedWeatherData();
                if (cachedData != null) {
                    listener.onWeatherReceived(cachedData);
                }
            }
        });
    }
    
    /**
     * Convertit la réponse API en objet WeatherData
     */
    private WeatherData convertToWeatherData(WeatherResponse response) {
        WeatherData data = new WeatherData();
        data.setTemperature(response.main.temp);
        data.setFeelsLike(response.main.feels_like);
        data.setHumidity(response.main.humidity);
        data.setPressure(response.main.pressure);
        data.setWindSpeed(response.wind.speed);
        data.setWindDirection(response.wind.deg);
        data.setVisibility(response.visibility / 1000.0); // Convertir en km
        data.setUvIndex(calculateUVIndex(response)); // Estimation basée sur l'heure et la couverture nuageuse
        data.setCloudCover(response.clouds.all);
        data.setWeatherCondition(response.weather[0].main);
        data.setWeatherDescription(response.weather[0].description);
        data.setWeatherIcon(response.weather[0].icon);
        data.setCityName(response.name);
        data.setCountry(response.sys.country);
        data.setTimestamp(System.currentTimeMillis());
        
        return data;
    }
    
    /**
     * Extrait les données solaires de la réponse météo
     */
    private SunData extractSunData(WeatherResponse response) {
        SunData sunData = new SunData();
        sunData.setSunrise(response.sys.sunrise * 1000L); // Convertir en millisecondes
        sunData.setSunset(response.sys.sunset * 1000L);
        
        // Calculer les heures optimales d'activité
        calculateOptimalActivityTimes(sunData);
        
        return sunData;
    }
    
    /**
     * Calcule les heures optimales d'activité basées sur le soleil
     */
    private void calculateOptimalActivityTimes(SunData sunData) {
        long sunrise = sunData.getSunrise();
        long sunset = sunData.getSunset();
        
        // Heure dorée du matin (1h après le lever du soleil)
        long morningGoldenHour = sunrise + (60 * 60 * 1000);
        sunData.setMorningGoldenHourStart(sunrise);
        sunData.setMorningGoldenHourEnd(morningGoldenHour);
        
        // Heure dorée du soir (1h avant le coucher du soleil)
        long eveningGoldenHour = sunset - (60 * 60 * 1000);
        sunData.setEveningGoldenHourStart(eveningGoldenHour);
        sunData.setEveningGoldenHourEnd(sunset);
        
        // Temps optimal pour courir (éviter 11h-15h en été)
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 6);
        cal.set(Calendar.MINUTE, 0);
        sunData.setOptimalRunningStart(cal.getTimeInMillis());
        
        cal.set(Calendar.HOUR_OF_DAY, 10);
        sunData.setOptimalRunningEnd(cal.getTimeInMillis());
        
        // Deuxième fenêtre de course
        cal.set(Calendar.HOUR_OF_DAY, 17);
        sunData.setOptimalRunningStart2(cal.getTimeInMillis());
        
        cal.set(Calendar.HOUR_OF_DAY, 19);
        sunData.setOptimalRunningEnd2(cal.getTimeInMillis());
    }
    
    /**
     * Génère des recommandations d'activité basées sur la météo et le soleil
     */
    private ActivityRecommendation generateActivityRecommendation(WeatherData weather, SunData sun) {
        ActivityRecommendation recommendation = new ActivityRecommendation();
        
        double temp = weather.getTemperature();
        String condition = weather.getWeatherCondition();
        long currentTime = System.currentTimeMillis();
        
        // Recommandations basées sur la température
        if (temp >= 25 && temp <= 30) {
            recommendation.setRunningRecommendation("🏃‍♀️ Température idéale pour courir ! Pensez à vous hydrater.");
            recommendation.setRunningScore(9);
        } else if (temp > 30) {
            recommendation.setRunningRecommendation("🌡️ Trop chaud ! Courez tôt le matin ou en soirée.");
            recommendation.setRunningScore(4);
        } else if (temp < 5) {
            recommendation.setRunningRecommendation("🧥 Trop froid ! Course en salle ou activité intérieure recommandée.");
            recommendation.setRunningScore(3);
        } else {
            recommendation.setRunningRecommendation("👟 Bonne température pour courir avec équipement adapté.");
            recommendation.setRunningScore(7);
        }
        
        // Recommandations basées sur les conditions météo
        if (condition.equals("Rain")) {
            recommendation.setOutdoorScore(2);
            recommendation.setOutdoorRecommendation("🌧️ Pluie détectée ! Privilégiez les activités en intérieur.");
        } else if (condition.equals("Clear")) {
            recommendation.setOutdoorScore(10);
            recommendation.setOutdoorRecommendation("☀️ Temps parfait pour toutes les activités extérieures !");
        } else if (condition.equals("Clouds")) {
            recommendation.setOutdoorScore(8);
            recommendation.setOutdoorRecommendation("☁️ Temps nuageux, idéal pour éviter les coups de soleil !");
        }
        
        // Recommandations solaires
        if (currentTime >= sun.getMorningGoldenHourStart() && currentTime <= sun.getMorningGoldenHourEnd()) {
            recommendation.setSunRecommendation("🌅 C'est l'heure dorée du matin ! Parfait pour la course et les photos !");
        } else if (currentTime >= sun.getEveningGoldenHourStart() && currentTime <= sun.getEveningGoldenHourEnd()) {
            recommendation.setSunRecommendation("🌇 Heure dorée du soir ! Idéal pour se détendre et méditer.");
        } else if (isOptimalRunningTime(currentTime, sun)) {
            recommendation.setSunRecommendation("🏃‍♀️ C'est le moment optimal pour courir ! Profitez-en !");
        } else {
            recommendation.setSunRecommendation("🌞 Consultez les heures optimales pour planifier vos activités.");
        }
        
        // Recommandations de sommeil basées sur le soleil
        long timeToSunset = sun.getSunset() - currentTime;
        if (timeToSunset < 2 * 60 * 60 * 1000 && timeToSunset > 0) { // 2h avant coucher
            recommendation.setSleepRecommendation("😴 Le soleil se couche bientôt. Préparez-vous pour une bonne nuit !");
        }
        
        return recommendation;
    }
    
    /**
     * Vérifie si c'est un moment optimal pour courir
     */
    private boolean isOptimalRunningTime(long currentTime, SunData sun) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTime);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        
        // Matin: 6h-10h ou Soir: 17h-19h
        return (hour >= 6 && hour <= 10) || (hour >= 17 && hour <= 19);
    }
    
    /**
     * Calcule approximativement l'index UV basé sur l'heure et la couverture nuageuse
     */
    private double calculateUVIndex(WeatherResponse response) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        double cloudCover = response.clouds.all;
        
        // Index UV basique basé sur l'heure (pic à midi)
        double baseUV = 0;
        if (hour >= 10 && hour <= 14) {
            baseUV = 8.0; // UV élevé en milieu de journée
        } else if (hour >= 8 && hour <= 16) {
            baseUV = 5.0; // UV modéré
        } else {
            baseUV = 1.0; // UV faible
        }
        
        // Réduction basée sur la couverture nuageuse
        double uvReduction = cloudCover / 100.0;
        return Math.max(0, baseUV * (1 - uvReduction * 0.7));
    }
    
    /**
     * Classes internes pour la réponse API
     */
    public static class WeatherResponse {
        public Main main;
        public Wind wind;
        public Clouds clouds;
        public Weather[] weather;
        public Sys sys;
        public String name;
        public int visibility;
        
        public static class Main {
            public double temp;
            public double feels_like;
            public int humidity;
            public double pressure;
        }
        
        public static class Wind {
            public double speed;
            public int deg;
        }
        
        public static class Clouds {
            public int all;
        }
        
        public static class Weather {
            public String main;
            public String description;
            public String icon;
        }
        
        public static class Sys {
            public String country;
            public long sunrise;
            public long sunset;
        }
    }
    
    public static class ForecastResponse {
        // Structure pour les prévisions à 5 jours (à implémenter si nécessaire)
    }
    
    /**
     * Classe pour les recommandations d'activité
     */
    public static class ActivityRecommendation {
        private String runningRecommendation;
        private int runningScore;
        private String outdoorRecommendation;
        private int outdoorScore;
        private String sunRecommendation;
        private String sleepRecommendation;
        
        // Getters et setters
        public String getRunningRecommendation() { return runningRecommendation; }
        public void setRunningRecommendation(String runningRecommendation) { this.runningRecommendation = runningRecommendation; }
        
        public int getRunningScore() { return runningScore; }
        public void setRunningScore(int runningScore) { this.runningScore = runningScore; }
        
        public String getOutdoorRecommendation() { return outdoorRecommendation; }
        public void setOutdoorRecommendation(String outdoorRecommendation) { this.outdoorRecommendation = outdoorRecommendation; }
        
        public int getOutdoorScore() { return outdoorScore; }
        public void setOutdoorScore(int outdoorScore) { this.outdoorScore = outdoorScore; }
        
        public String getSunRecommendation() { return sunRecommendation; }
        public void setSunRecommendation(String sunRecommendation) { this.sunRecommendation = sunRecommendation; }
        
        public String getSleepRecommendation() { return sleepRecommendation; }
        public void setSleepRecommendation(String sleepRecommendation) { this.sleepRecommendation = sleepRecommendation; }
    }
}
