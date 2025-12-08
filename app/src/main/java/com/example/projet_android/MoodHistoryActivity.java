package com.example.projet_android;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.example.projet_android.utils.PreferencesManager;

/**
 * Activity to display mood history and trends
 */
public class MoodHistoryActivity extends AppCompatActivity {
    
    private TextView historyTextView;
    private PreferencesManager preferencesManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create simple layout programmatically for now
        TextView textView = new TextView(this);
        textView.setText("Mood History\n\nThis feature is coming soon!\nTrack your mood regularly to see detailed history and trends.");
        textView.setTextSize(16);
        textView.setPadding(32, 32, 32, 32);
        
        setContentView(textView);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mood History");
        }
        
        preferencesManager = new PreferencesManager(this);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
