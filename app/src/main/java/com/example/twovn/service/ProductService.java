package com.example.twovn.service;

import com.example.twovn.model.Product;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductService {
    String PRODUCTS = "Product";

    @GET(PRODUCTS)
    Call<Product[]> getAllProduct();

    @GET(PRODUCTS + "/{id}")
    Call<Product> getProductById (@Path("id") Object id);

    @POST(PRODUCTS)
    Call<Product> createProduct(@Body Product product);

    @PUT(PRODUCTS + "/{id}")
    Call<Product> updateProduct(@Path("id") Object id, @Body Product product);

    @DELETE(PRODUCTS + "/{id}")
    Call<Product> deleteProduct(@Path("id") Object id);
}
