package com.example.twovn;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twovn.adapter.CartAdapter;
import com.example.twovn.model.Cart;
import com.example.twovn.model.Product;
import com.example.twovn.repo.CartRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.badge.BadgeDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements CartAdapter.OnQuantityChangeListener {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartProductList = new ArrayList<>();
    private TextView totalAmountTextView;
    private Button buttonCheckout;
    private BottomNavigationView bottomNavigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(getContext(), cartProductList, this, true);

        recyclerView.setAdapter(cartAdapter);

        totalAmountTextView = view.findViewById(R.id.totalAmountTextView);
        buttonCheckout = view.findViewById(R.id.buttonCheckout);
        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToCheckout();
            }
        });

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);

        loadCartProducts();

        return view;
    }

    @Override
    public void onQuantityChanged() {
        updateTotalAmount();
        updateCartBadge();
    }

    private void updateTotalAmount() {
        double totalAmount = 0;
        for (Product product : cartProductList) {
            totalAmount += product.getPrice() * product.getQuantity();
        }
        totalAmountTextView.setText(String.format("%,.0f đ", totalAmount));
    }

    private void updateCartBadge() {
        int itemCount = cartAdapter.getItemCount();

        if (itemCount > 0) {
            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.navigation_giohang);
            badge.setNumber(itemCount);
            badge.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            badge.setVisible(true);
        } else {
            bottomNavigationView.removeBadge(R.id.navigation_giohang);
        }
    }

    private void loadCartProducts() {
        CartRepository cartRepository = CartRepository.getInstance(getContext());
        cartRepository.getCartProducts(new Callback<List<Cart>>() {
            @Override
            public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartProductList.clear();
                    for (Cart cartItem : response.body()) {
                        Product product = new Product(
                                cartItem.getProductId(),
                                cartItem.getName(),
                                cartItem.getImageUrl(),
                                cartItem.getPrice()
                        );
                        product.setQuantity(1);
                        cartProductList.add(product);
                    }
                    cartAdapter.notifyDataSetChanged();
                    updateTotalAmount();
                    updateCartBadge();
                } else {
                    Toast.makeText(getContext(), "Failed to load cart products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cart>> call, Throwable t) {
                Intent intent = new Intent(getActivity(), LoginSignUpActivity.class);
                startActivity(intent);
                Toast.makeText(getContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedToCheckout() {
        if (totalAmountTextView.getText().toString().equals("0 đ")){
            Toast.makeText(getContext(), "Bạn không có sản phẩm nào trong giỏ hàng", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(getActivity(), OrderSummaryActivity.class);
            intent.putExtra("totalAmount", totalAmountTextView.getText().toString());
            intent.putParcelableArrayListExtra("cartProductList", new ArrayList<>(cartProductList));
            startActivity(intent);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartProducts();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Picasso.get().cancelTag(getContext());
    }
}
