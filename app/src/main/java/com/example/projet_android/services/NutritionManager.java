package com.example.projet_android.services;

import android.content.Context;
import android.util.Log;

import com.example.projet_android.models.FoodItem;
import com.example.projet_android.database.DatabaseManager;
import com.example.projet_android.utils.PreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NutritionManager {
    
    private static final String TAG = "NutritionManager";
    private static final String BASE_URL = "https://world.openfoodfacts.org/";
    
    private OpenFoodFactsService service;
    private DatabaseManager databaseManager;
    private PreferencesManager preferencesManager;
    private Context context;
    
    public interface NutritionListener {
        void onFoodFound(FoodItem foodItem);
        void onFoodNotFound();
        void onError(String error);
    }
      public NutritionManager(Context context) {
        this.context = context;
        this.databaseManager = DatabaseManager.getInstance(context);
        this.preferencesManager = new PreferencesManager(context);
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        service = retrofit.create(OpenFoodFactsService.class);
    }
    
    public void searchProductByBarcode(String barcode, NutritionListener listener) {
        Call<OpenFoodFactsService.OpenFoodFactsResponse> call = service.getProduct(barcode);
        
        call.enqueue(new Callback<OpenFoodFactsService.OpenFoodFactsResponse>() {
            @Override
            public void onResponse(Call<OpenFoodFactsService.OpenFoodFactsResponse> call, 
                                 Response<OpenFoodFactsService.OpenFoodFactsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OpenFoodFactsService.OpenFoodFactsResponse apiResponse = response.body();
                    
                    if (apiResponse.status == 1 && apiResponse.product != null) {
                        FoodItem foodItem = convertToFoodItem(apiResponse.product, barcode);
                        listener.onFoodFound(foodItem);
                    } else {
                        listener.onFoodNotFound();
                    }
                } else {
                    listener.onError("Erreur de réponse du serveur");
                }
            }
            
            @Override
            public void onFailure(Call<OpenFoodFactsService.OpenFoodFactsResponse> call, Throwable t) {
                Log.e(TAG, "Erreur lors de la recherche: " + t.getMessage());
                listener.onError(t.getMessage());
            }
        });
    }
    
    private FoodItem convertToFoodItem(OpenFoodFactsService.OpenFoodFactsResponse.Product product, String barcode) {
        FoodItem foodItem = new FoodItem();
        
        foodItem.setName(product.product_name != null ? product.product_name : "Produit inconnu");
        foodItem.setBrand(product.brands != null ? product.brands : "");
        foodItem.setBarcode(barcode);
        foodItem.setImageUrl(product.image_url != null ? product.image_url : "");
        
        if (product.nutriments != null) {
            foodItem.setCalories((int) product.nutriments.energy_kcal_100g);
            foodItem.setProtein(product.nutriments.proteins_100g);
            foodItem.setCarbs(product.nutriments.carbohydrates_100g);
            foodItem.setFat(product.nutriments.fat_100g);
        }
        
        return foodItem;
    }
    
    // Méthode pour obtenir des recommandations nutritionnelles simples
    public String getNutritionalRecommendation(int dailyCalories, float dailyProtein, float dailyCarbs, float dailyFat) {
        StringBuilder recommendation = new StringBuilder();
        
        // Recommandations basées sur des valeurs moyennes
        if (dailyCalories < 1200) {
            recommendation.append("• Augmentez votre apport calorique\n");
        } else if (dailyCalories > 2500) {
            recommendation.append("• Réduisez votre apport calorique\n");
        }
        
        if (dailyProtein < 50) {
            recommendation.append("• Consommez plus de protéines (viande, poisson, légumineuses)\n");
        }
        
        if (dailyFat > 70) {
            recommendation.append("• Réduisez les graisses saturées\n");
        }
        
        if (dailyCarbs < 130) {
            recommendation.append("• Ajoutez des glucides complexes (céréales complètes)\n");
        }
        
        if (recommendation.length() == 0) {
            recommendation.append("• Votre alimentation semble équilibrée !");
        }
          return recommendation.toString();
    }
    
    // Méthode pour ajouter un aliment scanné à la base de données
    public void saveFoodToDatabase(FoodItem foodItem, String mealType, float quantity) {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            databaseManager.addScannedFood(
                userId,
                foodItem.getName(),
                foodItem.getBrand(),
                foodItem.getBarcode(),
                foodItem.getImageUrl(),
                mealType,
                foodItem.getCalories(),
                foodItem.getProtein(),
                foodItem.getCarbs(),
                foodItem.getFat(),
                quantity
            );
            
            // Synchroniser les données nutritionnelles
            databaseManager.syncNutritionData(userId);
        }
    }
    
    // Méthode pour ajouter un aliment manuel à la base de données
    public void addFoodToDatabase(String foodName, String mealType, int calories, 
                                 float protein, float carbs, float fat, float quantity) {
        String userId = preferencesManager.getUserId();
        if (userId != null) {
            databaseManager.addFoodItem(userId, foodName, mealType, calories, protein, carbs, fat, quantity);
            
            // Synchroniser les données nutritionnelles
            databaseManager.syncNutritionData(userId);
        }
    }
    
    // Interface étendue pour sauvegarder automatiquement
    public interface NutritionSaveListener extends NutritionListener {
        void onFoodSaved(FoodItem foodItem);
        
        @Override
        default void onFoodFound(FoodItem foodItem) {
            // Par défaut, sauvegarder l'aliment trouvé
            onFoodSaved(foodItem);
        }
    }
    
    // Méthode pour scanner et sauvegarder automatiquement
    public void scanAndSaveFood(String barcode, String mealType, float quantity, NutritionSaveListener listener) {
        searchProductByBarcode(barcode, new NutritionListener() {
            @Override
            public void onFoodFound(FoodItem foodItem) {
                saveFoodToDatabase(foodItem, mealType, quantity);
                listener.onFoodSaved(foodItem);
            }
            
            @Override
            public void onFoodNotFound() {
                listener.onFoodNotFound();
            }
            
            @Override
            public void onError(String error) {
                listener.onError(error);
            }
        });
    }
}
