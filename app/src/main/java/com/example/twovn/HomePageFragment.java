package com.example.twovn;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.twovn.adapter.ImagePagerAdapter;
import com.example.twovn.adapter.ProductAdapter;
import com.example.twovn.model.Product;
import com.example.twovn.repo.ProductRepository;
import com.example.twovn.service.ProductService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePageFragment extends Fragment {

    private ViewPager2 viewPager;
    private Handler sliderHandler = new Handler();
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.banner1);
        imageList.add(R.drawable.banner3);
        imageList.add(R.drawable.banner2);

        ImagePagerAdapter pagerAdapter = new ImagePagerAdapter(getContext(), imageList);
        viewPager.setAdapter(pagerAdapter);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint("Bạn muốn mua gì hôm nay...");  // Ensure hint is set in code

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(getContext(), productList);
        recyclerView.setAdapter(productAdapter);

        // Fetch products from API
        fetchProducts();

        // Start the auto-scrolling
        startAutoScroll();

        return view;
    }

    private void startAutoScroll() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % 3;  // Assuming 3 images
                viewPager.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 4000); // Auto-scroll every 4 seconds
            }
        };
        sliderHandler.postDelayed(runnable, 2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sliderHandler.removeCallbacksAndMessages(null);
    }

    private void fetchProducts() {
        ProductService productService = ProductRepository.getProductService();
        productService.getAllProduct().enqueue(new Callback<Product[]>() {
            @Override
            public void onResponse(Call<Product[]> call, Response<Product[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Product product : response.body()) {
                        productList.add(product);
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
}
