package com.example.projet_android.models;

/**
 * =============================
 * MODÈLE D'ARTICLE ALIMENTAIRE
 * =============================
 * 
 * Classe modèle représentant un produit alimentaire dans l'application Health Tracker.
 * Utilisée pour le système de nutrition avec scan de codes-barres et calcul des macronutriments.
 * 
 * INFORMATIONS NUTRITIONNELLES :
 * • 🔥 Calories par portion/100g
 * • 🥩 Protéines (grammes)
 * • 🍞 Glucides/Carbs (grammes)
 * • 🥑 Lipides/Graisses (grammes)
 * • 📊 Calculs automatiques des pourcentages nutritionnels
 * 
 * IDENTIFICATION PRODUIT :
 * • 📱 Code-barres pour scan automatique
 * • 🏷️ Nom commercial du produit
 * • 🏢 Marque/Fabricant
 * • 🖼️ URL de l'image produit
 * 
 * INTÉGRATIONS :
 * • Compatible avec l'API OpenFoodFacts
 * • Stockage en base de données SQLite locale
 * • Synchronisation avec le journal alimentaire
 * • Calculs automatiques de l'apport calorique quotidien
 * 
 * UTILISATION :
 * • Scan de codes-barres dans NutritionActivity
 * • Ajout manuel d'aliments
 * • Calcul des objectifs nutritionnels
 * • Historique de consommation alimentaire
 * 
 * @version 1.0
 * @author Équipe Health Tracker
 */
public class FoodItem {    // ============ INFORMATIONS PRODUIT ============
    private String name;        // Nom commercial du produit alimentaire
    private String barcode;     // Code-barres EAN/UPC pour identification unique
    private String brand;       // Marque ou fabricant du produit
    private String imageUrl;    // URL de l'image du produit (OpenFoodFacts, etc.)
    
    // ============ DONNÉES NUTRITIONNELLES ============
    private int calories;       // Calories pour 100g ou par portion
    private float protein;      // Protéines en grammes
    private float carbs;        // Glucides/Carbs en grammes  
    private float fat;          // Lipides/Graisses en grammes
    
    // ============ CONSTRUCTEURS ============
    
    /**
     * Constructeur par défaut
     * Utilisé pour la désérialisation et l'initialisation vide
     */
    public FoodItem() {
        // Constructeur vide requis pour la sérialisation/désérialisation
    }
    
    /**
     * Constructeur avec données essentielles
     * Initialise un produit alimentaire avec les informations de base
     * 
     * @param name Nom du produit
     * @param calories Nombre de calories
     * @param barcode Code-barres d'identification
     */
    public FoodItem(String name, int calories, String barcode) {
        this.name = name;
        this.calories = calories;
        this.barcode = barcode;
    }
    
    // Getters et setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getCalories() {
        return calories;
    }
    
    public void setCalories(int calories) {
        this.calories = calories;
    }
    
    public String getBarcode() {
        return barcode;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
    public float getProtein() {
        return protein;
    }
    
    public void setProtein(float protein) {
        this.protein = protein;
    }
    
    public float getCarbs() {
        return carbs;
    }
    
    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }
    
    public float getFat() {
        return fat;
    }
    
    public void setFat(float fat) {
        this.fat = fat;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    /**
     * Calculate nutrition for a specific quantity
     */
    public NutritionInfo getNutritionForQuantity(float quantity) {
        double factor = quantity / 100.0;
        return new NutritionInfo(
            (int) (calories * factor),
            protein * factor,
            carbs * factor,
            fat * factor
        );
    }
    
    /**
     * Inner class for nutrition information
     */
    public static class NutritionInfo {
        public final int calories;
        public final double protein;
        public final double carbs;
        public final double fat;

        public NutritionInfo(int calories, double protein, double carbs, double fat) {
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
        }
    }
}
