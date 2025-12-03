package com.example.projet_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projet_android.models.FoodItem;
import com.example.projet_android.services.NutritionManager;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import androidx.activity.result.ActivityResultLauncher;

public class NutritionActivity extends AppCompatActivity implements NutritionManager.NutritionListener {
    
    private TextView productNameTextView;
    private TextView brandTextView;
    private TextView caloriesTextView;
    private TextView proteinTextView;
    private TextView carbsTextView;
    private TextView fatTextView;
    private ImageView productImageView;
    private Button scanButton;
    private Button addMealButton;
    
    private NutritionManager nutritionManager;
    private FoodItem currentFoodItem;
    
    private ActivityResultLauncher<ScanOptions> barcodeLauncher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);
        
        initViews();
        setupBarcodeLauncher();
        nutritionManager = new NutritionManager(this);
    }
    
    private void initViews() {
        productNameTextView = findViewById(R.id.tv_product_name);
        brandTextView = findViewById(R.id.tv_brand);
        caloriesTextView = findViewById(R.id.tv_calories);
        proteinTextView = findViewById(R.id.tv_protein);
        carbsTextView = findViewById(R.id.tv_carbs);
        fatTextView = findViewById(R.id.tv_fat);
        productImageView = findViewById(R.id.iv_product_image);
        scanButton = findViewById(R.id.btn_scan_barcode);
        addMealButton = findViewById(R.id.btn_add_meal);
        
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBarcodeScanning();
            }
        });
        
        addMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCurrentMeal();
            }
        });
        
        addMealButton.setVisibility(View.GONE);
    }
    
    private void setupBarcodeLauncher() {
        barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                String barcode = result.getContents();
                searchProduct(barcode);
            }
        });
    }
    
    private void startBarcodeScanning() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
        options.setPrompt("Scannez le code-barres du produit");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);
        
        barcodeLauncher.launch(options);
    }
    
    private void searchProduct(String barcode) {
        Toast.makeText(this, "Recherche du produit...", Toast.LENGTH_SHORT).show();
        nutritionManager.searchProductByBarcode(barcode, this);
    }
    
    @Override
    public void onFoodFound(FoodItem foodItem) {
        runOnUiThread(() -> {
            currentFoodItem = foodItem;
            displayProductInfo(foodItem);
            addMealButton.setVisibility(View.VISIBLE);
        });
    }
    
    @Override
    public void onFoodNotFound() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Produit non trouvé dans la base de données", 
                          Toast.LENGTH_LONG).show();
            clearProductInfo();
        });
    }
    
    @Override
    public void onError(String error) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Erreur: " + error, Toast.LENGTH_LONG).show();
            clearProductInfo();
        });
    }
    
    private void displayProductInfo(FoodItem foodItem) {
        productNameTextView.setText(foodItem.getName());
        brandTextView.setText(foodItem.getBrand());
        caloriesTextView.setText(String.format("Calories: %d kcal/100g", foodItem.getCalories()));
        proteinTextView.setText(String.format("Protéines: %.1f g", foodItem.getProtein()));
        carbsTextView.setText(String.format("Glucides: %.1f g", foodItem.getCarbs()));
        fatTextView.setText(String.format("Lipides: %.1f g", foodItem.getFat()));
        
        // Ici, vous pourriez charger l'image du produit avec une bibliothèque comme Picasso ou Glide
        // Pour l'instant, on utilise une image par défaut
        productImageView.setImageResource(R.drawable.ic_launcher_foreground);
    }
    
    private void clearProductInfo() {
        productNameTextView.setText("Aucun produit scanné");
        brandTextView.setText("");
        caloriesTextView.setText("");
        proteinTextView.setText("");
        carbsTextView.setText("");
        fatTextView.setText("");
        productImageView.setImageResource(R.drawable.ic_launcher_foreground);
        addMealButton.setVisibility(View.GONE);
        currentFoodItem = null;
    }
      private void addCurrentMeal() {
        if (currentFoodItem != null) {
            // Déterminer le type de repas selon l'heure
            String mealType = determineMealType();
            
            // Quantité par défaut de 100g (peut être personnalisée plus tard)
            float quantity = 100f;
            
            // Sauvegarder dans la base de données via NutritionManager
            nutritionManager.saveFoodToDatabase(currentFoodItem, mealType, quantity);
            
            Toast.makeText(this, "Repas ajouté: " + currentFoodItem.getName() + 
                          " (" + mealType + ")", Toast.LENGTH_SHORT).show();
            
            // Retourner au dashboard
            finish();
        }
    }
    
    private String determineMealType() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        
        if (hour >= 5 && hour < 11) {
            return "breakfast";
        } else if (hour >= 11 && hour < 16) {
            return "lunch";
        } else if (hour >= 16 && hour < 22) {
            return "dinner";
        } else {
            return "snack";
        }
    }
}
