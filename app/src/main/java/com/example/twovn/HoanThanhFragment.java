package com.example.twovn;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class HoanThanhFragment extends Fragment {

    private RecyclerView recyclerViewOrder;
    TextView totalPriceText;

    private OrderHistoryAdapter orderHistoryAdapter;
    private OrderAdapter orderAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private List<OrderDetail> orderDetailList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_hoan_thanh, container, false);
        View view2 = inflater.inflate(R.layout.item_order_history, container, false);
        totalPriceText = view2.findViewById(R.id.textTotal);
        recyclerViewOrder = view.findViewById(R.id.recyclerViewHoanThanh);
        orderAdapter = new OrderAdapter(getContext(), orderDetailList);
        recyclerViewOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        orderHistoryAdapter = new OrderHistoryAdapter(getContext(), new ArrayList<>());
        recyclerViewOrder.setAdapter(orderHistoryAdapter);

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
                        double totalAmount = 0;

                        for (Order order : orders) {
                            if ("Shipped".equals(order.getStatus())) {
                                pendingOrders.add(order);
                                totalAmount += order.getTotalAmount();
                                totalPriceText.setText("Tổng tiền: " + order.getTotalAmount() + " đ");
                                Log.d("OrderSuccessful", "Total amount: " + order.getTotalAmount());// Cộng dồn tổng số tiền// Cộng dồn tổng số tiền
                            }
                        }

                        if (!pendingOrders.isEmpty()) {
                            // Set giá trị của TextView ở đây

                            for (Order order : pendingOrders) {
                                String orderId = order.getId();

                                OrderDetailRepository.getOrderDetailService().getOrderDetailsByOrderId(orderId).enqueue(new Callback<OrderDetail[]>() {
                                    @Override
                                    public void onResponse(Call<OrderDetail[]> call, Response<OrderDetail[]> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            OrderDetail[] orderDetailsArray = response.body();

                                            if (orderDetailsArray.length > 0) {
                                                OrderDetail firstDetail = orderDetailsArray[0];
                                                if (firstDetail.getProductId() != null) {
                                                    String productName = firstDetail.getProductId().getName();
                                                    if (orderDetailsArray.length > 2) {
                                                        productName += "...";
                                                    }
                                                    firstDetail.getProductId().setName(productName);

                                                    orderDetailList.add(firstDetail);

                                                    // Cập nhật Adapter
                                                    orderHistoryAdapter.setOrderDetailList(orderDetailList);
                                                } else {
                                                    Log.e("OrderSuccessful", "Product ID is null for OrderDetail: " + firstDetail.get_id());
                                                }
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