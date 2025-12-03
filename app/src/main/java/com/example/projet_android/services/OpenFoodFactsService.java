package com.example.projet_android.services;

import com.example.projet_android.models.FoodItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OpenFoodFactsService {
    
    @GET("api/v0/product/{barcode}.json")
    Call<OpenFoodFactsResponse> getProduct(@Path("barcode") String barcode);
    
    public static class OpenFoodFactsResponse {
        public int status;
        public Product product;
        
        public static class Product {
            public String product_name;
            public String brands;
            public String image_url;
            public Nutriments nutriments;
            
            public static class Nutriments {
                public float energy_kcal_100g;
                public float proteins_100g;
                public float carbohydrates_100g;
                public float fat_100g;
            }
        }
    }
}
