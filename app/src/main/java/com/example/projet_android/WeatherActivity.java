package com.example.projet_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projet_android.models.SunData;
import com.example.projet_android.models.WeatherData;
import com.example.projet_android.services.WeatherService;
import com.example.projet_android.utils.PreferencesManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Activité dédiée aux informations météo et recommandations solaires
 */
public class WeatherActivity extends AppCompatActivity implements WeatherService.WeatherDataListener, LocationListener {
    
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    
    // Views principales
    private TextView cityNameTextView;
    private TextView temperatureTextView;
    private TextView weatherDescriptionTextView;
    private ImageView weatherIconImageView;
    private TextView feelsLikeTextView;
    private TextView humidityTextView;
    private TextView windSpeedTextView;
    private TextView uvIndexTextView;
    private ProgressBar loadingProgressBar;
    private LinearLayout weatherContentLayout;
    private Button refreshButton;
    
    // Views solaires
    private TextView sunriseTextView;
    private TextView sunsetTextView;
    private TextView daylightDurationTextView;
    private TextView morningGoldenHourTextView;
    private TextView eveningGoldenHourTextView;
    private TextView currentSunRecommendationTextView;
    
    // Views recommandations
    private LinearLayout recommendationsLayout;
    private TextView runningRecommendationTextView;
    private TextView outdoorRecommendationTextView;
    private TextView sleepRecommendationTextView;
    private ProgressBar runningScoreProgressBar;
    private ProgressBar outdoorScoreProgressBar;
    private TextView runningScoreTextView;
    private TextView outdoorScoreTextView;
    
    // Views horaires optimales
    private TextView optimalRunningTime1TextView;
    private TextView optimalRunningTime2TextView;
    private TextView nextOptimalTimeTextView;
    
    // Services
    private WeatherService weatherService;
    private LocationManager locationManager;
    private PreferencesManager preferencesManager;
    
    // Données
    private WeatherData currentWeatherData;
    private SunData currentSunData;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        
        initViews();
        initServices();
        setupClickListeners();
        
        // Vérifier les permissions et charger les données
        if (checkLocationPermission()) {
            requestLocationAndLoadWeather();
        } else {
            requestLocationPermission();
        }
    }
    
    private void initViews() {
        // Views principales météo
        cityNameTextView = findViewById(R.id.tv_city_name);
        temperatureTextView = findViewById(R.id.tv_temperature);
        weatherDescriptionTextView = findViewById(R.id.tv_weather_description);
        weatherIconImageView = findViewById(R.id.iv_weather_icon);
        feelsLikeTextView = findViewById(R.id.tv_feels_like);
        humidityTextView = findViewById(R.id.tv_humidity);
        windSpeedTextView = findViewById(R.id.tv_wind_speed);
        uvIndexTextView = findViewById(R.id.tv_uv_index);
        loadingProgressBar = findViewById(R.id.pb_loading);
        weatherContentLayout = findViewById(R.id.layout_weather_content);
        refreshButton = findViewById(R.id.btn_refresh);
        
        // Views solaires
        sunriseTextView = findViewById(R.id.tv_sunrise);
        sunsetTextView = findViewById(R.id.tv_sunset);
        daylightDurationTextView = findViewById(R.id.tv_daylight_duration);
        morningGoldenHourTextView = findViewById(R.id.tv_morning_golden_hour);
        eveningGoldenHourTextView = findViewById(R.id.tv_evening_golden_hour);
        currentSunRecommendationTextView = findViewById(R.id.tv_current_sun_recommendation);
        
        // Views recommandations
        recommendationsLayout = findViewById(R.id.layout_recommendations);
        runningRecommendationTextView = findViewById(R.id.tv_running_recommendation);
        outdoorRecommendationTextView = findViewById(R.id.tv_outdoor_recommendation);
        sleepRecommendationTextView = findViewById(R.id.tv_sleep_recommendation);
        runningScoreProgressBar = findViewById(R.id.pb_running_score);
        outdoorScoreProgressBar = findViewById(R.id.pb_outdoor_score);
        runningScoreTextView = findViewById(R.id.tv_running_score);
        outdoorScoreTextView = findViewById(R.id.tv_outdoor_score);
        
        // Views horaires optimales
        optimalRunningTime1TextView = findViewById(R.id.tv_optimal_running_time1);
        optimalRunningTime2TextView = findViewById(R.id.tv_optimal_running_time2);
        nextOptimalTimeTextView = findViewById(R.id.tv_next_optimal_time);
        
        // Configuration initiale
        showLoading(true);
    }
    
    private void initServices() {
        weatherService = new WeatherService(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        preferencesManager = new PreferencesManager(this);
    }
    
    private void setupClickListeners() {
        refreshButton.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                requestLocationAndLoadWeather();
            } else {
                Toast.makeText(this, "Permission de localisation requise", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationAndLoadWeather();            } else {
                Toast.makeText(this, "Permission requise pour obtenir la météo locale", Toast.LENGTH_LONG).show();
                // Charger les données en cache si disponibles
                loadCachedWeatherData();
            }
        }
    }
    
    private void requestLocationAndLoadWeather() {
        if (!checkLocationPermission()) return;
        
        showLoading(true);
        
        try {
            // Essayer d'obtenir la dernière localisation connue
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation == null) {
                lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            
            if (lastLocation != null) {
                loadWeatherData(lastLocation.getLatitude(), lastLocation.getLongitude());
            } else {
                // Demander une nouvelle localisation                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
                Toast.makeText(this, "Obtention de votre localisation...", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "Erreur d'accès à la localisation", Toast.LENGTH_SHORT).show();
            loadCachedWeatherData();
        }
    }
    
    private void loadWeatherData(double latitude, double longitude) {
        weatherService.getCurrentWeather(latitude, longitude, this);
    }
    
    private void loadCachedWeatherData() {
        WeatherData cachedData = preferencesManager.getCachedWeatherData();        if (cachedData != null) {
            onWeatherReceived(cachedData);
            Toast.makeText(this, "Données en cache (dernière mise à jour)", Toast.LENGTH_SHORT).show();
        } else {
            showError("Aucune donnée météo disponible");
        }
    }
    
    private void showLoading(boolean show) {
        loadingProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        weatherContentLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        recommendationsLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void showError(String message) {
        showLoading(false);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    // Implémentation des callbacks WeatherService.WeatherDataListener
    @Override
    public void onWeatherReceived(WeatherData weatherData) {
        currentWeatherData = weatherData;
        runOnUiThread(() -> {
            updateWeatherUI(weatherData);
            showLoading(false);
        });
    }
    
    @Override
    public void onSunDataReceived(SunData sunData) {
        currentSunData = sunData;
        runOnUiThread(() -> {
            updateSunUI(sunData);
        });
    }
    
    @Override
    public void onActivityRecommendation(WeatherService.ActivityRecommendation recommendation) {
        runOnUiThread(() -> {
            updateRecommendationsUI(recommendation);
        });
    }
    
    @Override
    public void onError(String error) {
        runOnUiThread(() -> {
            showError(error);
        });
    }
      private void updateWeatherUI(WeatherData weather) {
        cityNameTextView.setText(weather.getCityName() + ", " + weather.getCountry());
        temperatureTextView.setText(weather.getTemperatureString());
        weatherDescriptionTextView.setText(weather.getWeatherDescription());
        feelsLikeTextView.setText(weather.getFeelsLikeString());
        humidityTextView.setText("Humidité: " + weather.getHumidityString());
        windSpeedTextView.setText("Vent: " + weather.getWindString());
        uvIndexTextView.setText("UV: " + weather.getUvIndexString());
        
        // Mettre à jour l'icône météo
        updateWeatherIcon(weather.getWeatherIcon());
    }
      private void updateSunUI(SunData sunData) {
        sunriseTextView.setText("🌅 " + sunData.getSunriseTime());
        sunsetTextView.setText("🌇 " + sunData.getSunsetTime());
        daylightDurationTextView.setText("Durée du jour: " + sunData.getDaylightDurationString());
        morningGoldenHourTextView.setText("🌅 Heure dorée matin: " + sunData.getMorningGoldenHour());
        eveningGoldenHourTextView.setText("🌇 Heure dorée soir: " + sunData.getEveningGoldenHour());
        currentSunRecommendationTextView.setText(sunData.getCurrentSunRecommendation());
          // Heures optimales de course
        optimalRunningTime1TextView.setText("🏃‍♀️ Matin: " + sunData.getOptimalRunningTime1());
        optimalRunningTime2TextView.setText("🏃‍♀️ Soir: " + sunData.getOptimalRunningTime2());
        
        // Prochaine heure optimale
        updateNextOptimalTime(sunData);
    }
    
    private void updateRecommendationsUI(WeatherService.ActivityRecommendation recommendation) {
        runningRecommendationTextView.setText(recommendation.getRunningRecommendation());
        outdoorRecommendationTextView.setText(recommendation.getOutdoorRecommendation());
        
        if (recommendation.getSleepRecommendation() != null) {
            sleepRecommendationTextView.setText(recommendation.getSleepRecommendation());
            sleepRecommendationTextView.setVisibility(View.VISIBLE);
        } else {
            sleepRecommendationTextView.setVisibility(View.GONE);
        }
        
        // Mettre à jour les scores
        runningScoreProgressBar.setProgress(recommendation.getRunningScore() * 10); // 0-100        outdoorScoreProgressBar.setProgress(recommendation.getOutdoorScore() * 10);
        runningScoreTextView.setText(recommendation.getRunningScore() + "/10");
        outdoorScoreTextView.setText(recommendation.getOutdoorScore() + "/10");
    }
    
    private void updateWeatherIcon(String iconCode) {
        // Mapping des icônes OpenWeatherMap vers les ressources locales
        int iconResId = R.drawable.ic_weather_default;
          if (iconCode != null) {
            switch (iconCode.substring(0, 2)) {
                case "01": iconResId = R.drawable.ic_weather_sunny; break;
                case "02": iconResId = R.drawable.ic_weather_partly_cloudy; break;
                case "03":
                case "04": iconResId = R.drawable.ic_weather_cloudy; break;
                case "09":
                case "10": iconResId = R.drawable.ic_weather_rainy; break;
                case "11": iconResId = R.drawable.ic_weather_thunderstorm; break;
                case "13": iconResId = R.drawable.ic_weather_snowy; break;
                case "50": iconResId = R.drawable.ic_weather_foggy; break;
            }
        }
        
        weatherIconImageView.setImageResource(iconResId);
    }
    
    private void updateNextOptimalTime(SunData sunData) {
        long now = System.currentTimeMillis();
        String nextOptimal = "";
          // Vérifier quelle est la prochaine heure optimale
        if (sunData.isOptimalRunningTime()) {
            nextOptimal = "🏃‍♀️ C'est le moment optimal maintenant !";
        } else if (now < sunData.getOptimalRunningStart()) {
            long timeUntil = sunData.getOptimalRunningStart() - now;
            nextOptimal = "Prochaine session dans " + formatDuration(timeUntil);
        } else if (now < sunData.getOptimalRunningStart2()) {
            long timeUntil = sunData.getOptimalRunningStart2() - now;
            nextOptimal = "Prochaine session dans " + formatDuration(timeUntil);
        } else {
            nextOptimal = "Prochaine session demain matin";
        }
        
        nextOptimalTimeTextView.setText(nextOptimal);
    }
    
    private String formatDuration(long duration) {
        long hours = duration / (1000 * 60 * 60);
        long minutes = (duration % (1000 * 60 * 60)) / (1000 * 60);
          if (hours > 0) {
            return hours + "h" + String.format(Locale.getDefault(), "%02d", minutes);
        } else {
            return minutes + "min";
        }
    }
    
    // Implémentation LocationListener
    @Override
    public void onLocationChanged(Location location) {
        loadWeatherData(location.getLatitude(), location.getLongitude());
    }
    
    @Override
    public void onProviderEnabled(String provider) {}
    
    @Override
    public void onProviderDisabled(String provider) {}
}
