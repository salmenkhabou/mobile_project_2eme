package com.example.projet_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.example.projet_android.models.FoodItem;
import com.example.projet_android.services.NutritionManager;
import com.example.projet_android.utils.PreferencesManager;
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
            showQuantityDialog();
        }
    }
    
    private void showQuantityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How much did you eat?");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_quantity_input, null);
        EditText quantityInput = dialogView.findViewById(R.id.et_quantity);
        TextView unitLabel = dialogView.findViewById(R.id.tv_unit);
        
        quantityInput.setText("100"); // Default 100g
        unitLabel.setText("grams");
        
        builder.setView(dialogView);
        builder.setPositiveButton("Add Meal", (dialog, which) -> {
            try {
                float quantity = Float.parseFloat(quantityInput.getText().toString());
                if (quantity > 0) {
                    addMealToDatabase(quantity);
                } else {
                    Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void addMealToDatabase(float quantity) {
        // Determine meal type based on time
        String mealType = determineMealType();
        
        // Calculate nutrition for the specific quantity
        FoodItem.NutritionInfo nutrition = currentFoodItem.getNutritionForQuantity(quantity);
        
        // Save to database
        nutritionManager.saveFoodToDatabase(currentFoodItem, mealType, quantity);
        
        // Update daily totals
        updateDailyNutrition(nutrition);
        
        // Award points for tracking
        PreferencesManager prefs = new PreferencesManager(this);
        prefs.addWellnessPoints(10);
        
        // Show success message
        showMealAddedDialog(nutrition, quantity);
    }
    
    private void updateDailyNutrition(FoodItem.NutritionInfo nutrition) {
        PreferencesManager prefs = new PreferencesManager(this);
        
        // Update daily totals
        int currentCalories = prefs.getDailyCalories();
        int currentProtein = prefs.getDailyProtein();
        int currentCarbs = prefs.getDailyCarbs();
        int currentFat = prefs.getDailyFat();
        
        prefs.setDailyCalories(currentCalories + nutrition.calories);
        prefs.setDailyProtein(currentProtein + (int) nutrition.protein);
        prefs.setDailyCarbs(currentCarbs + (int) nutrition.carbs);
        prefs.setDailyFat(currentFat + (int) nutrition.fat);
    }
    
    private void showMealAddedDialog(FoodItem.NutritionInfo nutrition, float quantity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Meal Added Successfully! 🍽️");
        
        StringBuilder message = new StringBuilder();
        message.append("Added: ").append(currentFoodItem.getName());
        message.append(" (").append((int) quantity).append("g)\n\n");
        message.append("Nutrition added:\n");
        message.append("📊 Calories: ").append(nutrition.calories).append(" kcal\n");
        message.append("🥩 Protein: ").append(String.format("%.1f", nutrition.protein)).append("g\n");
        message.append("🍞 Carbs: ").append(String.format("%.1f", nutrition.carbs)).append("g\n");
        message.append("🥑 Fat: ").append(String.format("%.1f", nutrition.fat)).append("g\n\n");
        message.append("🏆 +10 wellness points earned!");
        
        builder.setMessage(message.toString());
        builder.setPositiveButton("View Daily Summary", (dialog, which) -> {
            showDailySummary();
        });
        builder.setNegativeButton("Add More", (dialog, which) -> {
            clearProductInfo();
        });
        builder.show();
    }
    
    private void showDailySummary() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Today's Nutrition Summary 📊");
        
        PreferencesManager prefs = new PreferencesManager(this);
        int totalCalories = prefs.getDailyCalories();
        int totalProtein = prefs.getDailyProtein();
        int totalCarbs = prefs.getDailyCarbs();
        int totalFat = prefs.getDailyFat();
        
        // Calculate targets (basic calculation)
        int targetCalories = 2000; // Could be personalized based on user profile
        int targetProtein = 150;
        int targetCarbs = 250;
        int targetFat = 65;
        
        StringBuilder summary = new StringBuilder();
        summary.append("Today's totals:\n\n");
        summary.append(String.format("🔥 Calories: %d / %d (%.0f%%)\n", 
            totalCalories, targetCalories, (totalCalories / (float) targetCalories) * 100));
        summary.append(String.format("🥩 Protein: %dg / %dg (%.0f%%)\n", 
            totalProtein, targetProtein, (totalProtein / (float) targetProtein) * 100));
        summary.append(String.format("🍞 Carbs: %dg / %dg (%.0f%%)\n", 
            totalCarbs, targetCarbs, (totalCarbs / (float) targetCarbs) * 100));
        summary.append(String.format("🥑 Fat: %dg / %dg (%.0f%%)\n\n", 
            totalFat, targetFat, (totalFat / (float) targetFat) * 100));
        
        // Add recommendations
        summary.append("💡 Recommendations:\n");
        if (totalProtein < targetProtein * 0.8) {
            summary.append("• Consider adding more protein-rich foods\n");
        }
        if (totalCalories < targetCalories * 0.8) {
            summary.append("• You may need more calories for your goals\n");
        }
        if (totalCalories > targetCalories * 1.2) {
            summary.append("• Consider lighter meals for the rest of the day\n");
        }
        
        builder.setMessage(summary.toString());
        builder.setPositiveButton("Got it!", null);
        builder.setNeutralButton("View History", (dialog, which) -> {
            // TODO: Implement nutrition history view
            Toast.makeText(this, "Nutrition history coming soon!", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }
    
    private void showMealPlanningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Meal Planning Assistant 🍽️");
        
        String[] mealTypes = {"Breakfast Ideas", "Lunch Ideas", "Dinner Ideas", "Healthy Snacks"};
        
        builder.setItems(mealTypes, (dialog, which) -> {
            String[] suggestions = getMealSuggestions(mealTypes[which]);
            showMealSuggestions(mealTypes[which], suggestions);
        });
        
        builder.show();
    }
    
    private String[] getMealSuggestions(String mealType) {
        switch (mealType) {
            case "Breakfast Ideas":
                return new String[]{
                    "🥣 Oatmeal with berries and nuts (350 cal)",
                    "🍳 Scrambled eggs with avocado toast (400 cal)",
                    "🥤 Protein smoothie with banana (300 cal)",
                    "🥐 Greek yogurt with granola (280 cal)"
                };
            case "Lunch Ideas":
                return new String[]{
                    "🥗 Quinoa salad with grilled chicken (450 cal)",
                    "🌯 Turkey and veggie wrap (380 cal)",
                    "🍲 Lentil soup with whole grain bread (350 cal)",
                    "🐟 Salmon with roasted vegetables (420 cal)"
                };
            case "Dinner Ideas":
                return new String[]{
                    "🍗 Grilled chicken with sweet potato (500 cal)",
                    "🍝 Whole wheat pasta with vegetables (480 cal)",
                    "🥩 Lean beef stir-fry with brown rice (520 cal)",
                    "🐠 Baked fish with quinoa (450 cal)"
                };
            case "Healthy Snacks":
                return new String[]{
                    "🥜 Mixed nuts and dried fruit (200 cal)",
                    "🍎 Apple with almond butter (180 cal)",
                    "🥕 Hummus with vegetable sticks (150 cal)",
                    "🍓 Greek yogurt with berries (120 cal)"
                };
            default:
                return new String[]{"No suggestions available"};
        }
    }
    
    private void showMealSuggestions(String mealType, String[] suggestions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(mealType);
        
        StringBuilder message = new StringBuilder();
        message.append("Here are some healthy options:\n\n");
        for (String suggestion : suggestions) {
            message.append(suggestion).append("\n\n");
        }
        
        builder.setMessage(message.toString());
        builder.setPositiveButton("Thanks!", null);
        builder.show();
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
