package com.example.projet_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class GymDetailActivity extends AppCompatActivity {
    
    private TextView tvGymName, tvGymAddress, tvGymRating, tvGymType, tvGymPrice;
    private TextView tvGymPhone, tvGymWebsite, tvGymHours, tvGymDescription;
    private ImageView imgGymIcon, imgGymStatus;
    private ChipGroup chipGroupAmenities;
    private MaterialButton btnDirections, btnCall, btnWebsite, btnFavorite;
    private CardView cardContact, cardAmenities, cardInfo;
    
    private String gymId, gymName, gymAddress, gymPhone, gymWebsite, gymAmenities;
    private float gymRating;
    private double gymPrice, gymLat, gymLng;
    private String gymType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_detail);
        
        initializeViews();
        loadGymData();
        setupClickListeners();
        
        // Set up toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Détails de la Salle");
        }
    }
    
    private void initializeViews() {
        tvGymName = findViewById(R.id.tvGymName);
        tvGymAddress = findViewById(R.id.tvGymAddress);
        tvGymRating = findViewById(R.id.tvGymRating);
        tvGymType = findViewById(R.id.tvGymType);
        tvGymPrice = findViewById(R.id.tvGymPrice);
        tvGymPhone = findViewById(R.id.tvGymPhone);
        tvGymWebsite = findViewById(R.id.tvGymWebsite);
        tvGymHours = findViewById(R.id.tvGymHours);
        tvGymDescription = findViewById(R.id.tvGymDescription);
        imgGymIcon = findViewById(R.id.imgGymIcon);
        imgGymStatus = findViewById(R.id.imgGymStatus);
        chipGroupAmenities = findViewById(R.id.chipGroupAmenities);
        btnDirections = findViewById(R.id.btnDirections);
        btnCall = findViewById(R.id.btnCall);
        btnWebsite = findViewById(R.id.btnWebsite);
        btnFavorite = findViewById(R.id.btnFavorite);
        cardContact = findViewById(R.id.cardContact);
        cardAmenities = findViewById(R.id.cardAmenities);
        cardInfo = findViewById(R.id.cardInfo);
    }
    
    private void loadGymData() {
        Intent intent = getIntent();
        gymId = intent.getStringExtra("gym_id");
        gymName = intent.getStringExtra("gym_name");
        gymAddress = intent.getStringExtra("gym_address");
        gymRating = intent.getFloatExtra("gym_rating", 0);
        gymType = intent.getStringExtra("gym_type");
        gymPrice = intent.getDoubleExtra("gym_price", 0);
        gymPhone = intent.getStringExtra("gym_phone");
        gymWebsite = intent.getStringExtra("gym_website");
        gymAmenities = intent.getStringExtra("gym_amenities");
        gymLat = intent.getDoubleExtra("gym_lat", 0);
        gymLng = intent.getDoubleExtra("gym_lng", 0);
        
        updateUI();
    }
    
    private void updateUI() {
        tvGymName.setText(gymName);
        tvGymAddress.setText(gymAddress);
        tvGymRating.setText(String.format("%.1f", gymRating));
        tvGymType.setText(gymType);
        tvGymPrice.setText(String.format("%.0f€/mois", gymPrice));
        
        // Set gym type icon
        setGymTypeIcon(gymType);
        
        // Set rating color
        if (gymRating >= 4.5) {
            tvGymRating.setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.primary_green));
        } else if (gymRating >= 4.0) {
            tvGymRating.setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.wellness_accent));
        } else {
            tvGymRating.setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.text_secondary));
        }
        
        // Contact info
        if (gymPhone != null && !gymPhone.isEmpty()) {
            tvGymPhone.setText(gymPhone);
        } else {
            tvGymPhone.setText("Non disponible");
            btnCall.setEnabled(false);
        }
        
        if (gymWebsite != null && !gymWebsite.isEmpty()) {
            tvGymWebsite.setText(gymWebsite);
        } else {
            tvGymWebsite.setText("Non disponible");
            btnWebsite.setEnabled(false);
        }
        
        // Hours (mock data)
        tvGymHours.setText("Lun-Ven: 06:00 - 23:00\nSam-Dim: 08:00 - 20:00");
        
        // Description (mock data)
        tvGymDescription.setText(generateGymDescription());
        
        // Amenities
        setupAmenities();
    }
    
    private void setGymTypeIcon(String gymType) {
        switch (gymType.toLowerCase()) {
            case "public":
                imgGymIcon.setImageResource(R.drawable.ic_gym_public);
                imgGymIcon.setColorFilter(ContextCompat.getColor(this, R.color.primary_blue));
                break;
            case "privé":
            case "private":
                imgGymIcon.setImageResource(R.drawable.ic_gym_private);
                imgGymIcon.setColorFilter(ContextCompat.getColor(this, R.color.primary_purple));
                break;
            case "chaîne":
            case "chain":
                imgGymIcon.setImageResource(R.drawable.ic_gym_chain);
                imgGymIcon.setColorFilter(ContextCompat.getColor(this, R.color.wellness_accent));
                break;
            case "boutique":
                imgGymIcon.setImageResource(R.drawable.ic_gym_boutique);
                imgGymIcon.setColorFilter(ContextCompat.getColor(this, R.color.primary_green));
                break;
            default:
                imgGymIcon.setImageResource(R.drawable.ic_gym_default);
                imgGymIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary));
                break;
        }
    }
    
    private void setupAmenities() {
        chipGroupAmenities.removeAllViews();
        
        if (gymAmenities != null && !gymAmenities.isEmpty()) {
            String[] amenities = gymAmenities.split(", ");
            
            for (String amenity : amenities) {
                Chip chip = new Chip(this);
                chip.setText(amenity.trim());
                chip.setChipBackgroundColorResource(R.color.background_light);
                chip.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                chip.setChipStrokeColorResource(R.color.wellness_accent);
                chip.setChipStrokeWidth(2f);
                chipGroupAmenities.addView(chip);
            }
        } else {
            Chip chip = new Chip(this);
            chip.setText("Équipements non spécifiés");
            chip.setChipBackgroundColorResource(R.color.background_light);
            chip.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            chipGroupAmenities.addView(chip);
        }
    }
    
    private String generateGymDescription() {
        StringBuilder description = new StringBuilder();
        
        switch (gymType.toLowerCase()) {
            case "public":
                description.append("Salle de sport municipale offrant des équipements de base à prix abordable. ");
                description.append("Idéale pour les résidents locaux cherchant à maintenir leur forme physique.");
                break;
            case "privé":
            case "private":
                description.append("Salle de sport privée avec des équipements haut de gamme et un service personnalisé. ");
                description.append("Coaching individuel et programmes sur mesure disponibles.");
                break;
            case "chaîne":
            case "chain":
                description.append("Membre d'une chaîne reconnue offrant des standards de qualité élevés. ");
                description.append("Accès à plusieurs clubs et programmes diversifiés.");
                break;
            case "boutique":
                description.append("Salle de sport boutique spécialisée avec une approche unique du fitness. ");
                description.append("Ambiance conviviale et communauté sportive active.");
                break;
            default:
                description.append("Salle de sport moderne équipée pour répondre à tous vos besoins de remise en forme. ");
                description.append("Personnel qualifié et atmosphère motivante.");
        }
        
        return description.toString();
    }
    
    private void setupClickListeners() {
        btnDirections.setOnClickListener(v -> openDirections());
        btnCall.setOnClickListener(v -> makePhoneCall());
        btnWebsite.setOnClickListener(v -> openWebsite());
        btnFavorite.setOnClickListener(v -> toggleFavorite());
    }
    
    private void openDirections() {
        if (gymLat != 0 && gymLng != 0) {
            String uri = String.format("google.navigation:q=%f,%f", gymLat, gymLng);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Fallback to web maps
                String webUri = String.format("https://www.google.com/maps/dir/?api=1&destination=%f,%f", 
                    gymLat, gymLng);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUri)));
            }
        } else {
            Toast.makeText(this, "Coordonnées GPS non disponibles", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void makePhoneCall() {
        if (gymPhone != null && !gymPhone.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + gymPhone));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Numéro de téléphone non disponible", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void openWebsite() {
        if (gymWebsite != null && !gymWebsite.isEmpty()) {
            String url = gymWebsite;
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Impossible d'ouvrir le site web", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Site web non disponible", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void toggleFavorite() {
        // TODO: Implement favorite functionality with SharedPreferences or database
        Toast.makeText(this, "Fonctionnalité des favoris à venir!", Toast.LENGTH_SHORT).show();
        
        // Change button appearance to show it's favorited
        if (btnFavorite.getText().equals("Ajouter aux Favoris")) {
            btnFavorite.setText("Supprimer des Favoris");
            btnFavorite.setIconResource(R.drawable.ic_favorite_filled);
        } else {
            btnFavorite.setText("Ajouter aux Favoris");
            btnFavorite.setIconResource(R.drawable.ic_favorite_border);
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
