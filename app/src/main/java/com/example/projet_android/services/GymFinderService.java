package com.example.projet_android.services;

import android.content.Context;
import android.location.Location;
import com.example.projet_android.models.Gym;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GymFinderService {
    
    private static final String TAG = "GymFinderService";
    private Context context;
    
    // In a real app, you would use Google Places API key
    private static final String PLACES_API_KEY = "YOUR_GOOGLE_PLACES_API_KEY";
    
    public interface GymSearchCallback {
        void onGymsFound(List<Gym> gyms);
        void onError(String error);
    }
    
    public GymFinderService(Context context) {
        this.context = context;
    }
    
    /**
     * Find nearby gyms based on user location
     * @param userLat User's latitude
     * @param userLng User's longitude
     * @param radiusKm Search radius in kilometers
     * @param callback Callback to handle results
     */
    public void findNearbyGyms(double userLat, double userLng, double radiusKm, GymSearchCallback callback) {
        // For demonstration, we'll use mock data
        // In a real app, you would make an API call to Google Places API
        List<Gym> mockGyms = generateMockGyms(userLat, userLng, radiusKm);
        
        // Sort by distance
        Collections.sort(mockGyms, new Comparator<Gym>() {
            @Override
            public int compare(Gym g1, Gym g2) {
                return Double.compare(g1.getDistance(), g2.getDistance());
            }
        });
        
        // Simulate network delay
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onGymsFound(mockGyms);
            }
        }, 1000); // 1 second delay
    }
    
    /**
     * Search gyms by name or type
     */
    public void searchGyms(double userLat, double userLng, String query, GymSearchCallback callback) {
        List<Gym> allGyms = generateMockGyms(userLat, userLng, 10.0); // 10km radius
        List<Gym> filteredGyms = new ArrayList<>();
        
        String lowerQuery = query.toLowerCase();
        
        for (Gym gym : allGyms) {
            if (gym.getName().toLowerCase().contains(lowerQuery) ||
                gym.getGymType().toLowerCase().contains(lowerQuery) ||
                gym.getAddress().toLowerCase().contains(lowerQuery)) {
                filteredGyms.add(gym);
            }
        }
        
        // Sort by distance
        Collections.sort(filteredGyms, (g1, g2) -> Double.compare(g1.getDistance(), g2.getDistance()));
        
        new android.os.Handler().postDelayed(() -> callback.onGymsFound(filteredGyms), 800);
    }
    
    /**
     * Calculate distance between two points using Haversine formula
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Distance in km
        
        return distance;
    }
    
    /**
     * Generate mock gyms for demonstration
     * In a real app, this would be replaced with API calls
     */    private List<Gym> generateMockGyms(double userLat, double userLng, double radiusKm) {
        List<Gym> gyms = new ArrayList<>();
        
        // Generate some sample gyms around the user's location
        Object[][] gymData = {
            {userLat + 0.01, userLng + 0.01, "FitnessPark Central", "Private", 4.5, 45.0},
            {userLat - 0.005, userLng + 0.015, "Basic-Fit", "Chain", 4.2, 29.99},
            {userLat + 0.008, userLng - 0.012, "Neoness", "Chain", 4.3, 39.99},
            {userLat - 0.015, userLng - 0.008, "L'Orange Bleue", "Chain", 4.1, 34.99},
            {userLat + 0.02, userLng + 0.005, "CrossFit Box", "Boutique", 4.7, 89.99},
            {userLat - 0.01, userLng + 0.02, "Gymnase Municipal", "Public", 3.8, 15.0},
            {userLat + 0.025, userLng - 0.015, "Keep Cool", "Chain", 4.0, 24.99},
            {userLat - 0.02, userLng + 0.01, "Fitness Boutique", "Private", 4.6, 65.0},
            {userLat + 0.015, userLng + 0.025, "Amazonia", "Chain", 4.4, 49.99},
            {userLat - 0.025, userLng - 0.02, "Club Med Gym", "Private", 4.8, 79.99}
        };
        
        String[] amenitiesOptions = {
            "Cardio, Musculation, Piscine",
            "Cardio, Musculation, Cours collectifs",
            "Musculation, Sauna, Vestiaires",
            "Cardio, Cours collectifs, Parking",
            "CrossFit, Coaching personnel",
            "Musculation basique, Vestiaires",
            "Cardio, Musculation, Climatisation",
            "Coaching personnel, Nutrition",
            "Piscine, Sauna, Spa, Cours collectifs",
            "Premium, Spa, Restaurant, Piscine"
        };
        
        String[] addresses = {
            "123 Rue de la République, Centre-ville",
            "45 Avenue des Champs, Zone commerciale",
            "78 Boulevard Saint-Michel, Quartier Sud",
            "12 Rue Victor Hugo, Centre historique", 
            "156 Route de Lyon, Zone industrielle",
            "Place de la Mairie, Centre-ville",
            "89 Avenue de la Liberté, Nouveau quartier",
            "34 Rue des Roses, Quartier résidentiel",
            "67 Boulevard de la Paix, Zone commerciale",
            "90 Avenue Montparnasse, Quartier d'affaires"
        };
          for (int i = 0; i < gymData.length; i++) {
            Object[] location = gymData[i];
            double gymLat = (Double) location[0];
            double gymLng = (Double) location[1];
            String name = (String) location[2];
            String type = (String) location[3];
            float rating = ((Double) location[4]).floatValue();
            double price = (Double) location[5];
            
            double distance = calculateDistance(userLat, userLng, gymLat, gymLng);
            
            if (distance <= radiusKm) {
                Gym gym = new Gym();
                gym.setId("gym_" + i);
                gym.setName(name);
                gym.setAddress(addresses[i % addresses.length]);
                gym.setLatitude(gymLat);
                gym.setLongitude(gymLng);
                gym.setDistance(distance);
                gym.setRating(rating);
                gym.setGymType(type);
                gym.setMonthlyPrice(price);
                gym.setAmenities(amenitiesOptions[i % amenitiesOptions.length].split(", "));
                gym.setOpen(isGymOpen()); // Random open/closed status
                gym.setOpeningHours("06:00 - 23:00");
                gym.setPhoneNumber("01 23 45 67 " + (80 + i));
                gym.setWebsite("www." + name.toLowerCase().replace(" ", "") + ".com");
                
                gyms.add(gym);
            }
        }
        
        return gyms;
    }
    
    private boolean isGymOpen() {
        // Simulate random open/closed status based on current time
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        
        // Most gyms are open between 6 AM and 11 PM
        return hour >= 6 && hour <= 23;
    }
    
    /**
     * Get gym details by ID
     * In a real app, this would fetch from API or database
     */
    public void getGymDetails(String gymId, GymDetailsCallback callback) {
        // Mock implementation
        new android.os.Handler().postDelayed(() -> {
            // Return mock detailed gym data
            callback.onGymDetailsLoaded(createMockGymDetails(gymId));
        }, 500);
    }
    
    private Gym createMockGymDetails(String gymId) {
        Gym gym = new Gym();
        gym.setId(gymId);
        gym.setName("Detailed Gym Info");
        // Add more detailed information here
        return gym;
    }
    
    public interface GymDetailsCallback {
        void onGymDetailsLoaded(Gym gym);
        void onError(String error);
    }
}
