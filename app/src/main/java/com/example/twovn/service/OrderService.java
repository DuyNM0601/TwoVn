package com.example.twovn.service;

import com.example.twovn.model.Order;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OrderService {

    @GET("order")
    Call<Order> getOrder();

    @GET("orders/user/{id}")
    Call<Order> getOrderByUserId(@Path("id") String userId);

    // Bổ sung annotation @GET hoặc @POST cho phương thức này
    @GET("orders/user/{id}")
    Call<Order[]> getOrdersByUserId(@Path("id") String userId);
}
