package com.example.projet_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.projet_android.adapters.GymAdapter;
import com.example.projet_android.models.Gym;
import com.example.projet_android.services.GymFinderService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class GymFinderActivity extends AppCompatActivity implements GymAdapter.OnGymClickListener {
    
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final double DEFAULT_SEARCH_RADIUS = 5.0; // 5km
      private RecyclerView recyclerViewGyms;
    private GymAdapter gymAdapter;
    private List<Gym> gymList;
    private ProgressBar progressBar;
    private LinearLayout tvNoResults;
    private SearchView searchView;
    private ChipGroup chipGroupFilters;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fabMyLocation;
    private FloatingActionButton btnMapView;
    private TextView tvLocationStatus;
    
    private FusedLocationProviderClient fusedLocationClient;
    private GymFinderService gymFinderService;
    private Location currentLocation;
    private String currentSearchQuery = "";
    private String selectedFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_finder);
        
        initializeViews();
        setupServices();
        setupRecyclerView();
        setupSearchAndFilters();
        checkLocationPermission();
        
        // Set up toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Salles de Sport Près de Vous");
        }
    }
    
    private void initializeViews() {
        recyclerViewGyms = findViewById(R.id.recyclerViewGyms);
        progressBar = findViewById(R.id.progressBar);
        tvNoResults = findViewById(R.id.tvNoResults);
        searchView = findViewById(R.id.searchView);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        fabMyLocation = findViewById(R.id.fabMyLocation);
        btnMapView = findViewById(R.id.btnMapView);
        tvLocationStatus = findViewById(R.id.tvLocationStatus);
    }
    
    private void setupServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        gymFinderService = new GymFinderService(this);
        gymList = new ArrayList<>();
    }
    
    private void setupRecyclerView() {
        gymAdapter = new GymAdapter(this, gymList);
        gymAdapter.setOnGymClickListener(this);
        recyclerViewGyms.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewGyms.setAdapter(gymAdapter);
    }
    
    private void setupSearchAndFilters() {
        // Setup search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query;
                searchGyms();
                return true;
            }
            
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    currentSearchQuery = "";
                    loadNearbyGyms();
                }
                return true;
            }
        });
        
        // Setup filter chips
        setupFilterChips();
        
        // Setup swipe to refresh
        swipeRefresh.setOnRefreshListener(this::refreshGyms);
        swipeRefresh.setColorSchemeResources(
            R.color.primary_blue,
            R.color.wellness_accent,
            R.color.primary_green
        );
        
        // Setup FAB
        fabMyLocation.setOnClickListener(v -> getCurrentLocationAndSearch());
        
        // Setup map view button
        btnMapView.setOnClickListener(v -> openMapView());
    }
    
    private void setupFilterChips() {
        String[] filters = {"Tous", "Public", "Privé", "Chaîne", "Boutique"};
        String[] filterValues = {"all", "public", "private", "chain", "boutique"};
        
        for (int i = 0; i < filters.length; i++) {
            Chip chip = new Chip(this);
            chip.setText(filters[i]);
            chip.setCheckable(true);
            final String filterValue = filterValues[i];
            
            if (i == 0) {
                chip.setChecked(true); // "Tous" selected by default
            }
            
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedFilter = filterValue;
                    // Uncheck other chips
                    for (int j = 0; j < chipGroupFilters.getChildCount(); j++) {
                        Chip otherChip = (Chip) chipGroupFilters.getChildAt(j);
                        if (otherChip != chip) {
                            otherChip.setChecked(false);
                        }
                    }
                    applyFilters();
                }
            });
            
            chipGroupFilters.addView(chip);
        }
    }
    
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocationAndSearch();
        } else {
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
                LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationAndSearch();
            } else {
                tvLocationStatus.setText("Permission de localisation refusée. Utilisation de Paris comme localisation par défaut.");
                tvLocationStatus.setVisibility(View.VISIBLE);
                // Use default location (Paris)
                searchGymsAtLocation(48.8566, 2.3522);
            }
        }
    }
    
    private void getCurrentLocationAndSearch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        showLoading(true);
        tvLocationStatus.setText("Localisation en cours...");
        tvLocationStatus.setVisibility(View.VISIBLE);
        
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentLocation = location;
                    tvLocationStatus.setText("Localisation: " + 
                        String.format("%.4f, %.4f", location.getLatitude(), location.getLongitude()));
                    searchGymsAtLocation(location.getLatitude(), location.getLongitude());
                } else {
                    tvLocationStatus.setText("Impossible d'obtenir la localisation. Utilisation de Paris par défaut.");
                    searchGymsAtLocation(48.8566, 2.3522); // Paris coordinates
                }
            })
            .addOnFailureListener(e -> {
                tvLocationStatus.setText("Erreur de localisation. Utilisation de Paris par défaut.");
                searchGymsAtLocation(48.8566, 2.3522);
            });
    }
    
    private void searchGymsAtLocation(double lat, double lng) {
        gymFinderService.findNearbyGyms(lat, lng, DEFAULT_SEARCH_RADIUS, new GymFinderService.GymSearchCallback() {
            @Override
            public void onGymsFound(List<Gym> gyms) {
                runOnUiThread(() -> {
                    showLoading(false);
                    swipeRefresh.setRefreshing(false);
                    updateGymList(gyms);
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    swipeRefresh.setRefreshing(false);
                    Toast.makeText(GymFinderActivity.this, 
                        "Erreur: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void searchGyms() {
        if (currentLocation == null) {
            Toast.makeText(this, "Localisation non disponible", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showLoading(true);
        gymFinderService.searchGyms(currentLocation.getLatitude(), currentLocation.getLongitude(), 
            currentSearchQuery, new GymFinderService.GymSearchCallback() {
            @Override
            public void onGymsFound(List<Gym> gyms) {
                runOnUiThread(() -> {
                    showLoading(false);
                    updateGymList(gyms);
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(GymFinderActivity.this, 
                        "Erreur de recherche: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void loadNearbyGyms() {
        if (currentLocation != null) {
            searchGymsAtLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
        } else {
            getCurrentLocationAndSearch();
        }
    }
    
    private void applyFilters() {
        List<Gym> filteredList = new ArrayList<>();
        
        for (Gym gym : gymList) {
            if (selectedFilter.equals("all") || 
                gym.getGymType().toLowerCase().equals(selectedFilter)) {
                filteredList.add(gym);
            }
        }
        
        gymAdapter.updateGyms(filteredList);
        updateResultsVisibility(filteredList.size());
    }
    
    private void refreshGyms() {
        if (currentSearchQuery.isEmpty()) {
            getCurrentLocationAndSearch();
        } else {
            searchGyms();
        }
    }
    
    private void updateGymList(List<Gym> gyms) {
        gymList.clear();
        gymList.addAll(gyms);
        applyFilters(); // Apply current filters to new data
    }
      private void updateResultsVisibility(int count) {
        if (count == 0) {
            tvNoResults.setVisibility(View.VISIBLE);
            recyclerViewGyms.setVisibility(View.GONE);
        } else {
            tvNoResults.setVisibility(View.GONE);
            recyclerViewGyms.setVisibility(View.VISIBLE);
        }
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewGyms.setVisibility(show ? View.GONE : View.VISIBLE);
        if (show) {
            tvNoResults.setVisibility(View.GONE);
        }
    }
    
    private void openMapView() {
        // Open Google Maps with gym locations
        if (currentLocation != null && !gymList.isEmpty()) {
            StringBuilder url = new StringBuilder("https://www.google.com/maps/search/gym/@");
            url.append(currentLocation.getLatitude()).append(",").append(currentLocation.getLongitude());
            url.append(",15z");
            
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // If Google Maps is not installed, use web browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString())));
            }
        }
    }
    
    // GymAdapter.OnGymClickListener implementation
    @Override
    public void onGymClick(Gym gym) {
        Intent intent = new Intent(this, GymDetailActivity.class);
        intent.putExtra("gym_id", gym.getId());
        intent.putExtra("gym_name", gym.getName());
        intent.putExtra("gym_address", gym.getAddress());
        intent.putExtra("gym_rating", gym.getRating());
        intent.putExtra("gym_type", gym.getGymType());
        intent.putExtra("gym_price", gym.getMonthlyPrice());
        intent.putExtra("gym_phone", gym.getPhoneNumber());
        intent.putExtra("gym_website", gym.getWebsite());
        intent.putExtra("gym_amenities", gym.getAmenitiesString());
        intent.putExtra("gym_lat", gym.getLatitude());
        intent.putExtra("gym_lng", gym.getLongitude());
        startActivity(intent);
    }
    
    @Override
    public void onDirectionsClick(Gym gym) {
        // Open Google Maps for directions
        String uri = String.format("google.navigation:q=%f,%f", gym.getLatitude(), gym.getLongitude());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Fallback to web maps
            String webUri = String.format("https://www.google.com/maps/dir/?api=1&destination=%f,%f", 
                gym.getLatitude(), gym.getLongitude());
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUri)));
        }
    }
    
    @Override
    public void onCallClick(Gym gym) {
        if (gym.getPhoneNumber() != null && !gym.getPhoneNumber().isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + gym.getPhoneNumber()));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Numéro de téléphone non disponible", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
