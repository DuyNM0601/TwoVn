package com.example.twovn;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
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
                    textViewPrice.setText(String.valueOf(currentProduct.getPrice()));
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
            CartRepository.getInstance().addProductToCart(product);
            Toast.makeText(this, "Đã thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        }
    }
}
