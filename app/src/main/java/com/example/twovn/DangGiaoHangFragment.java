package com.example.twovn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.twovn.adapter.OrderAdapter;
import com.example.twovn.adapter.OrderHistoryAdapter;
import com.example.twovn.model.Order;
import com.example.twovn.model.OrderDetail;
import com.example.twovn.repo.OrderDetailRepository;
import com.example.twovn.repo.OrderRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DangGiaoHangFragment extends Fragment {

    private RecyclerView recyclerViewOrder;
    private OrderHistoryAdapter orderHistoryAdapter;
    private List<OrderDetail> orderDetailList = new ArrayList<>();
    private TextView totalAmountTextView;
    private ImageView icHome;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dang_giao_hang, container, false);

        recyclerViewOrder = view.findViewById(R.id.recyclerViewDangGiaoHang);
        totalAmountTextView = view.findViewById(R.id.totalAmountTextViewOrder);
        icHome = view.findViewById(R.id.icHome);

        orderHistoryAdapter = new OrderHistoryAdapter(getContext(), new ArrayList<>());
        recyclerViewOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewOrder.setAdapter(orderHistoryAdapter);

        icHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace DangGiaoHangFragment with HomepageFragment
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        fetchOrderHistory();

        return view;
    }

    private void fetchOrderHistory() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySession", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            OrderRepository.getOrderService().getOrdersByUserId(userId).enqueue(new Callback<Order[]>() {
                @Override
                public void onResponse(Call<Order[]> call, Response<Order[]> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Order[] orders = response.body();
                        List<Order> pendingOrders = new ArrayList<>();
                        final double[] totalAmount = {0}; // Use an array to hold the total amount

                        for (Order order : orders) {
                            if ("Pending".equals(order.getStatus())) {
                                pendingOrders.add(order);
                            }
                        }

                        if (!pendingOrders.isEmpty()) {
                            for (Order order : pendingOrders) {
                                String orderId = order.getId();

                                OrderDetailRepository.getOrderDetailService().getOrderDetailsByOrderId(orderId).enqueue(new Callback<OrderDetail[]>() {
                                    @Override
                                    public void onResponse(Call<OrderDetail[]> call, Response<OrderDetail[]> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            OrderDetail[] orderDetailsArray = response.body();

                                            if (orderDetailsArray.length > 0) {
                                                for (OrderDetail orderDetail : orderDetailsArray) {
                                                    if (orderDetail.getProductId() != null) {
                                                        orderDetailList.add(orderDetail);
                                                        totalAmount[0] += orderDetail.getPrice() * orderDetail.getQuantity();
                                                    } else {
                                                        Log.e("OrderSuccessful", "Product ID is null for OrderDetail: " + orderDetail.get_id());
                                                    }
                                                }

                                                // Update Adapter and total amount TextView
                                                orderHistoryAdapter.setOrderDetailList(orderDetailList);
                                                totalAmountTextView.setText(String.format("Tổng tiền: %,dđ", (int) totalAmount[0]));
                                            }
                                        } else {
                                            Log.e("OrderSuccessful", "Failed to get order details. Code: " + response.code());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<OrderDetail[]> call, Throwable t) {
                                        Log.e("OrderSuccessful", "Failed to fetch order details: " + t.getMessage());
                                    }
                                });
                            }
                        } else {
                            Log.e("OrderSuccessful", "No pending orders found");
                        }
                    } else {
                        Log.e("OrderSuccessful", "Failed to get orders. Code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Order[]> call, Throwable t) {
                    Log.e("OrderSuccessful", "Failed to fetch orders: " + t.getMessage());
                }
            });
        }
    }
}
