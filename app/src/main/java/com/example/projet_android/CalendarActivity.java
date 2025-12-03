package com.example.projet_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {
    
    private TextView dateTextView;
    private Button selectDateButton;
    private RecyclerView activitiesRecyclerView;
    private Calendar selectedDate;
    private List<String> activities;
    private ActivitiesAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        
        initViews();
        setupRecyclerView();
        setupDatePicker();
        
        // Par défaut, afficher la date d'aujourd'hui
        selectedDate = Calendar.getInstance();
        updateDateDisplay();
        loadActivitiesForDate();
    }
    
    private void initViews() {
        dateTextView = findViewById(R.id.tv_selected_date);
        selectDateButton = findViewById(R.id.btn_select_date);
        activitiesRecyclerView = findViewById(R.id.rv_activities);
    }
    
    private void setupRecyclerView() {
        activities = new ArrayList<>();
        adapter = new ActivitiesAdapter(activities);
        activitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activitiesRecyclerView.setAdapter(adapter);
    }
    
    private void setupDatePicker() {
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }
    
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateDisplay();
                loadActivitiesForDate();
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    
    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
        dateTextView.setText(dateFormat.format(selectedDate.getTime()));
    }
    
    private void loadActivitiesForDate() {
        // Simuler le chargement des activités pour la date sélectionnée
        activities.clear();
        
        // Ajouter des données d'exemple
        activities.add("Petit-déjeuner: 350 calories");
        activities.add("Marche: 30 minutes, 150 calories brûlées");
        activities.add("Déjeuner: 500 calories");
        activities.add("Course: 45 minutes, 400 calories brûlées");
        activities.add("Dîner: 450 calories");
        activities.add("Sommeil: 7h30");
        
        adapter.notifyDataSetChanged();
    }
}
