package com.example.twovn.repo;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.twovn.api.APIClient;
import com.example.twovn.model.Cart;
import com.example.twovn.model.Product;
import com.example.twovn.service.CartService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CartRepository {

    private static CartRepository instance;
    private CartService cartService;
    private SharedPreferences sharedPreferences;
    private Context context;

    private CartRepository(Context context) {
        this.context = context;
        Retrofit retrofit = APIClient.getClient();
        cartService = retrofit.create(CartService.class);
        sharedPreferences = context.getSharedPreferences("MySession", Context.MODE_PRIVATE);
    }

    public static synchronized CartRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CartRepository(context);
        }
        return instance;
    }

    public void addProductToCart(String productId) {
        String userId = sharedPreferences.getString("userId", null);
        if (userId != null) {
            Cart cartItem = new Cart(userId, productId); // Assuming productId is sufficient for cart item creation
            cartService.addToCart(cartItem).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Product added to cart successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to add product to cart.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Error adding product to cart.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(context, "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getCartProducts(Callback<List<Cart>> callback) {
        String userId = sharedPreferences.getString("userId", null);
        if (userId != null) {
            cartService.getCartProducts(userId).enqueue(callback);
        } else {
            // Handle case where user ID is not available
            callback.onFailure(null, new Throwable("User not logged in"));
        }
    }

    public void getProductById(String productId, Callback<Product> callback) {
        cartService.getProductById(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new Throwable("Failed to load product"));
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void addProductToLocalCart(Product product) {
        // Implement local cart management if needed
    }

    public void clearCart() {
        // Implement clear cart if needed
    }
}
