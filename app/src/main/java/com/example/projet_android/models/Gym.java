package com.example.projet_android.models;

/**
 * Modèle de données pour représenter une salle de sport
 * 
 * Cette classe POJO encapsule toutes les informations d'une salle de sport :
 * - Informations de base (nom, adresse, coordonnées GPS)
 * - Données de proximité et accessibilité (distance, horaires, statut ouverture)
 * - Informations commerciales (tarifs, évaluation, contact)
 * - Détails techniques (équipements, type d'établissement)
 * 
 * Utilisée par le GymFinderActivity pour afficher et trier les résultats
 * de recherche. Compatible avec les APIs Google Places et données démo locales.
 * 
 * @author Équipe de développement Health Tracker
 * @version 2.1
 * @since 1.3
 */
public class Gym {
    
    /** Identifiant unique de la salle (Google Places ID ou local ID) */
    private String id;
    
    /** Nom commercial de la salle de sport */
    private String name;
    
    /** Adresse complète formatée */
    private String address;
    
    /** Coordonnées géographiques pour géolocalisation */
    private double latitude;
    private double longitude;
    
    /** Distance calculée depuis position utilisateur (en kilomètres) */
    private double distance;
    
    /** Note moyenne des utilisateurs sur 5 étoiles */
    private float rating;
    
    /** Numéro de téléphone pour contact direct */
    private String phoneNumber;
    
    /** Site web de l'établissement */
    private String website;
    
    /** Horaires d'ouverture formatés */
    private String openingHours;
    
    /** Statut d'ouverture actuel (ouvert/fermé) */
    private boolean isOpen;
    
    /** Type d'établissement : "Public", "Private", "Chain", "Boutique" */
    private String gymType;
    
    /** Liste des équipements disponibles */
    private String[] amenities;
    
    /** Prix d'abonnement mensuel moyen */
    private double monthlyPrice;
    
    /** URL image représentative de l'établissement */
    private String imageUrl;

    public Gym() {
        // Default constructor
    }

    public Gym(String id, String name, String address, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getGymType() {
        return gymType;
    }

    public void setGymType(String gymType) {
        this.gymType = gymType;
    }

    public String[] getAmenities() {
        return amenities;
    }

    public void setAmenities(String[] amenities) {
        this.amenities = amenities;
    }

    public double getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(double monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Helper method to get formatted distance
    public String getFormattedDistance() {
        if (distance < 1.0) {
            return String.format("%.0f m", distance * 1000);
        } else {
            return String.format("%.1f km", distance);
        }
    }

    // Helper method to get rating stars
    public String getRatingStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < Math.floor(rating)) {
                stars.append("★");
            } else if (i < rating) {
                stars.append("☆");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }

    // Helper method to check if gym is within a certain distance
    public boolean isWithinDistance(double maxDistance) {
        return distance <= maxDistance;
    }

    // Helper method to get amenities as comma-separated string
    public String getAmenitiesString() {
        if (amenities == null || amenities.length == 0) {
            return "No amenities listed";
        }
        return String.join(", ", amenities);
    }

    @Override
    public String toString() {
        return "Gym{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", distance=" + distance +
                ", rating=" + rating +
                '}';
    }
}
