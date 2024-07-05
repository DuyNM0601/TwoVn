package com.example.twovn.service;

import com.example.twovn.model.Cart;
import com.example.twovn.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CartService {
    @GET("cart/user/{userId}")
    Call<List<Cart>> getCartProducts(@Path("userId") String userId);
    @POST("/cart")
    Call<Void> addToCart(@Body Cart cartItem);
    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") String productId);
}
