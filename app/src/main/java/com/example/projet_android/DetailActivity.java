package com.example.projet_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    
    private TextView titleTextView;
    private TextView currentValueTextView;
    private TextView goalTextView;
    private TextView progressTextView;
    private LineChart progressChart;
    private Button addDataButton;
    
    private String activityType;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        
        // Récupérer le type d'activité depuis l'intent
        activityType = getIntent().getStringExtra("ACTIVITY_TYPE");
        if (activityType == null) {
            activityType = "Activité";
        }
        
        initViews();
        setupChart();
        updateDisplay();
    }
    
    private void initViews() {
        titleTextView = findViewById(R.id.tv_detail_title);
        currentValueTextView = findViewById(R.id.tv_current_value);
        goalTextView = findViewById(R.id.tv_goal_value);
        progressTextView = findViewById(R.id.tv_progress_percentage);
        progressChart = findViewById(R.id.chart_progress);
        addDataButton = findViewById(R.id.btn_add_data);
        
        titleTextView.setText(activityType);
        
        addDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewData();
            }
        });
    }
    
    private void setupChart() {
        List<Entry> entries = new ArrayList<>();
        
        // Données d'exemple pour les 7 derniers jours
        switch (activityType) {
            case "Pas":
                entries.add(new Entry(1, 8500));
                entries.add(new Entry(2, 9200));
                entries.add(new Entry(3, 7800));
                entries.add(new Entry(4, 10500));
                entries.add(new Entry(5, 9800));
                entries.add(new Entry(6, 11200));
                entries.add(new Entry(7, 10000));
                break;
            case "Calories":
                entries.add(new Entry(1, 1800));
                entries.add(new Entry(2, 2100));
                entries.add(new Entry(3, 1950));
                entries.add(new Entry(4, 2200));
                entries.add(new Entry(5, 1750));
                entries.add(new Entry(6, 2000));
                entries.add(new Entry(7, 1900));
                break;
            case "Sommeil":
                entries.add(new Entry(1, 7.5f));
                entries.add(new Entry(2, 8.0f));
                entries.add(new Entry(3, 6.5f));
                entries.add(new Entry(4, 7.8f));
                entries.add(new Entry(5, 8.2f));
                entries.add(new Entry(6, 7.0f));
                entries.add(new Entry(7, 7.5f));
                break;
            default:
                entries.add(new Entry(1, 50));
                entries.add(new Entry(2, 75));
                entries.add(new Entry(3, 60));
                entries.add(new Entry(4, 80));
                entries.add(new Entry(5, 65));
                entries.add(new Entry(6, 90));
                entries.add(new Entry(7, 85));
        }
        
        LineDataSet dataSet = new LineDataSet(entries, activityType);
        dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        dataSet.setCircleColor(getResources().getColor(android.R.color.holo_blue_dark));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        
        LineData lineData = new LineData(dataSet);
        progressChart.setData(lineData);
        progressChart.invalidate(); // refresh
    }
    
    private void updateDisplay() {
        switch (activityType) {
            case "Pas":
                currentValueTextView.setText("10,000");
                goalTextView.setText("Objectif: 10,000 pas");
                progressTextView.setText("100% de l'objectif atteint");
                break;
            case "Calories":
                currentValueTextView.setText("1,900");
                goalTextView.setText("Objectif: 2,000 cal");
                progressTextView.setText("95% de l'objectif atteint");
                break;
            case "Sommeil":
                currentValueTextView.setText("7h 30min");
                goalTextView.setText("Objectif: 8h");
                progressTextView.setText("94% de l'objectif atteint");
                break;
            default:
                currentValueTextView.setText("85");
                goalTextView.setText("Objectif: 100");
                progressTextView.setText("85% de l'objectif atteint");
        }
    }
    
    private void addNewData() {
        // Simuler l'ajout de nouvelles données
        Toast.makeText(this, "Nouvelles données ajoutées pour " + activityType, 
                      Toast.LENGTH_SHORT).show();
        
        // Ici, vous pouvez ouvrir un dialog pour saisir de nouvelles données
        // ou naviguer vers un écran de saisie
    }
}
