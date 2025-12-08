package com.example.projet_android.models;

/**
 * Modèle pour les défis de bien-être
 */
public class Challenge {
    private String id;
    private String title;
    private String description;
    private String type; // daily, weekly, monthly
    private boolean isCompleted;
    private int points;
    private long createdAt;
    private long completedAt;
    private int progress;
    private int maxProgress;
    
    public Challenge(String title, String description, String type, boolean isCompleted, int points) {
        this.id = generateId(title, type);
        this.title = title;
        this.description = description;
        this.type = type;
        this.isCompleted = isCompleted;
        this.points = points;
        this.createdAt = System.currentTimeMillis();
        this.progress = 0;
        this.maxProgress = 1;
    }
    
    private String generateId(String title, String type) {
        return type + "_" + title.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }
    
    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { 
        this.isCompleted = completed;
        if (completed && completedAt == 0) {
            this.completedAt = System.currentTimeMillis();
        }
    }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getCompletedAt() { return completedAt; }
    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }
    
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    
    public int getMaxProgress() { return maxProgress; }
    public void setMaxProgress(int maxProgress) { this.maxProgress = maxProgress; }
    
    public float getProgressPercentage() {
        if (maxProgress == 0) return 0f;
        return (float) progress / maxProgress * 100f;
    }
    
    public String getTypeDisplayName() {
        switch (type.toLowerCase()) {
            case "daily": return "Quotidien";
            case "weekly": return "Hebdomadaire";
            case "monthly": return "Mensuel";
            default: return "Défi";
        }
    }
    
    public String getPointsText() {
        return points + " pts";
    }
}
