package com.example.twovn;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.twovn.adapter.CartAdapter;
import com.example.twovn.adapter.OrderAdapter;
import com.example.twovn.adapter.ProductAdapter;
import com.example.twovn.model.Account;
import com.example.twovn.model.Order;
import com.example.twovn.model.OrderDetail;
import com.example.twovn.model.Product;
import com.example.twovn.repo.AccountRepository;
import com.example.twovn.repo.OrderRepository;
import com.example.twovn.repo.OrderDetailRepository;
import com.example.twovn.repo.ProductRepository;
import com.example.twovn.service.OrderDetailService;
import com.example.twovn.service.OrderService;
import com.example.twovn.service.ProductService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderSuccessful extends Fragment {

    TextView nameText, phoneText, addressText;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderDetail> orderDetailList = new ArrayList<>();
    TextView totalPriceText, tv_donhang;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_successful, container, false);

        nameText = view.findViewById(R.id.orderName);
        phoneText = view.findViewById(R.id.orderPhone);
        addressText = view.findViewById(R.id.orderAddress);
        recyclerView = view.findViewById(R.id.recyclerViewOrder);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        orderAdapter = new OrderAdapter(getContext(), orderDetailList);
        recyclerView.setAdapter(orderAdapter);
        totalPriceText = view.findViewById(R.id.totalPrice);
        tv_donhang = view.findViewById(R.id.tv_donhang);
        loadUserInfo(); // Load user information
        fetchOrderAndDetails(); // Fetch order and order details
        tv_donhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QldnActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void loadUserInfo() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySession", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            AccountRepository.getAccountService().getAccountById(userId).enqueue(new Callback<Account>() {
                @Override
                public void onResponse(Call<Account> call, Response<Account> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Account account = response.body();
                        nameText.setText("Tên người mua: "+ account.getUserName());
                        phoneText.setText("Số điện thoại: "+ account.getPhoneNumber());
                        addressText.setText("Địa chỉ nhận hàng: "+ account.getAddress());

                    }
                }

                @Override
                public void onFailure(Call<Account> call, Throwable t) {
                    Log.e("OrderSuccessful", "Error loading user info: " + t.getMessage());
                }
            });
        } else {
            Log.e("OrderSuccessful", "UserId is null");
        }
    }

    private void fetchOrderAndDetails() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySession", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            OrderRepository.getOrderService().getOrdersByUserId(userId).enqueue(new Callback<Order[]>() {
                @Override
                public void onResponse(Call<Order[]> call, Response<Order[]> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        Order[] orders = response.body();

                        if (orders.length > 0) {
                            Order latestOrder = orders[orders.length - 1];
                            String orderId = latestOrder.getId();

                            OrderDetailRepository.getOrderDetailService().getOrderDetailsByOrderId(orderId).enqueue(new Callback<OrderDetail[]>() {
                                @Override
                                public void onResponse(Call<OrderDetail[]> call, Response<OrderDetail[]> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        List<OrderDetail> orderDetails = new ArrayList<>();
                                        double totalPrice = 0;
                                        for (OrderDetail detail : response.body()) {
                                            if (detail.getProductId() != null) {
                                                orderDetails.add(detail);
                                                totalPrice += (detail.getPrice() * detail.getQuantity());
                                                Log.e("OrderSuccessful", "Product ID is null for OrderDetail: " + totalPrice);

                                            } else {
                                                Log.e("OrderSuccessful", "Product ID is null for OrderDetail: " + detail.get_id());
                                            }
                                        }
                                        totalPrice += 30000;

                                        orderDetailList.addAll(orderDetails);
                                        orderAdapter.setOrderDetailList(orderDetailList);

                                        totalPriceText.setText(String.format("%,dđ", (int) totalPrice));

                                    } else {
                                        Log.e("OrderSuccessful", "Failed to get order details. Code: " + response.code());
                                    }
                                }

                                @Override
                                public void onFailure(Call<OrderDetail[]> call, Throwable t) {
                                    Log.e("OrderSuccessful", "Failed to fetch order details: " + t.getMessage());
                                }
                            });
                        } else {
                            Log.e("OrderSuccessful", "No orders found");
                        }
                    } else {
                        Log.e("OrderSuccessful", "Failed to get orders. Code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Order[]> call, Throwable t) {
                    Log.e("OrderSuccessful", "Failed to get orders: " + t.getMessage());
                }
            });
        } else {
            Log.e("OrderSuccessful", "UserId is null");
        }
    }
}
