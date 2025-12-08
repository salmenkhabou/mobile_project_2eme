package com.example.projet_android.models;

/**
 * Modèle pour les conseils de bien-être quotidiens
 */
public class WellnessTip {
    private String title;
    private String description;
    private String category;
    private String iconEmoji;
    private boolean isRead;
    private long timestamp;
    
    public WellnessTip(String title, String description, String category) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
        this.iconEmoji = getEmojiForCategory(category);
    }
    
    private String getEmojiForCategory(String category) {
        switch (category.toLowerCase()) {
            case "hydration": return "💧";
            case "meditation": return "🧘";
            case "nutrition": return "🥗";
            case "activity": return "🏃";
            case "sleep": return "😴";
            case "sun": return "🌞";
            case "strength": return "💪";
            case "mental": return "🧠";
            case "mobility": return "🚶";
            case "breathing": return "💚";
            case "motivation": return "🎉";
            case "relaxation": return "🛀";
            case "digital_wellness": return "📱";
            case "nature": return "🌿";
            case "social": return "👥";
            case "cooking": return "🍳";
            case "planning": return "📝";
            case "organization": return "🧹";
            case "reflection": return "😌";
            default: return "✨";
        }
    }
    
    // Getters et Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getIconEmoji() { return iconEmoji; }
    public void setIconEmoji(String iconEmoji) { this.iconEmoji = iconEmoji; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
