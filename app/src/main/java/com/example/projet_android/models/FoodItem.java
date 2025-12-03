package com.example.projet_android.models;

public class FoodItem {
    private String name;
    private int calories;
    private String barcode;
    private float protein;
    private float carbs;
    private float fat;
    private String brand;
    private String imageUrl;
    
    public FoodItem() {
        // Constructeur vide
    }
    
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
}
