package com.example.twovn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twovn.adapter.ProductAdapter;
import com.example.twovn.model.Product;
import com.example.twovn.repo.CartRepository;
import com.example.twovn.repo.ProductRepository;
import com.example.twovn.service.ProductService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetail extends AppCompatActivity {

    private TextView textViewName, textViewPrice, textViewDescription;
    private ImageView imageViewProduct;
    private RecyclerView recyclerViewRelatedProducts;
    private ProductAdapter productAdapter;
    private List<Product> relatedProducts = new ArrayList<>();
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        textViewName = findViewById(R.id.textViewName);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewDescription = findViewById(R.id.textViewDescription);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        recyclerViewRelatedProducts = findViewById(R.id.recyclerViewRelatedProducts);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Setup RecyclerView
        recyclerViewRelatedProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        productAdapter = new ProductAdapter(this, relatedProducts);
        recyclerViewRelatedProducts.setAdapter(productAdapter);

        String productId = getIntent().getStringExtra("productId");
        if (productId != null) {
            fetchProductDetails(productId);
            fetchRelatedProducts(productId);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_add_to_cart) {
                    addToCart(currentProduct);
                    return true;
                }
                return false;
            }
        });
    }

    private void fetchProductDetails(String productId) {
        ProductService productService = ProductRepository.getProductService();
        productService.getProductById(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentProduct = response.body();
                    textViewName.setText(currentProduct.getName());
                    textViewPrice.setText(String.format("%,dđ", currentProduct.getPrice()));
                    textViewDescription.setText(currentProduct.getDescription());
                    Picasso.get().load(currentProduct.getUrlImg()).into(imageViewProduct); // Load image using Picasso
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void fetchRelatedProducts(String productId) {
        ProductService productService = ProductRepository.getProductService();
        productService.getAllProduct().enqueue(new Callback<Product[]>() {
            @Override
            public void onResponse(Call<Product[]> call, Response<Product[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Product product : response.body()) {
                        if (!product.get_id().equals(productId)) {
                            relatedProducts.add(product);
                        }
                    }
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Product[]> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void addToCart(Product product) {
        if (product != null) {
            if (checkUserLoggedIn()) {
                CartRepository.getInstance(this).addProductToCart(product.get_id());
            } else {
                Intent intent = new Intent(this, LoginSignUpActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Bạn cần đăng nhập để thực hiện thao tác này", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }
}
