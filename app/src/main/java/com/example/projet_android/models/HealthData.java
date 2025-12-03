package com.example.projet_android.models;

public class HealthData {
    private int steps;
    private int calories;
    private float sleepHours;
    private String date;
    private int heartRate;
    private float distance;
    
    public HealthData() {
        // Constructeur vide requis pour Firebase
    }
    
    public HealthData(int steps, int calories, float sleepHours, String date) {
        this.steps = steps;
        this.calories = calories;
        this.sleepHours = sleepHours;
        this.date = date;
    }
    
    // Getters et setters
    public int getSteps() {
        return steps;
    }
    
    public void setSteps(int steps) {
        this.steps = steps;
    }
    
    public int getCalories() {
        return calories;
    }
    
    public void setCalories(int calories) {
        this.calories = calories;
    }
    
    public float getSleepHours() {
        return sleepHours;
    }
    
    public void setSleepHours(float sleepHours) {
        this.sleepHours = sleepHours;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public int getHeartRate() {
        return heartRate;
    }
    
    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }
    
    public float getDistance() {
        return distance;
    }
    
    public void setDistance(float distance) {
        this.distance = distance;
    }
}
