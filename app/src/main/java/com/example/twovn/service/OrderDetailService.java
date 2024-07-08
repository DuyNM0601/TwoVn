package com.example.twovn.service;

import com.example.twovn.model.OrderDetail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderDetailService {
    String ORDER_DETAILS = "order-details";

    @GET("order-details")
    Call<OrderDetail[]> getAllOrderDetails();

    @GET("order-details/{id}")
    Call<OrderDetail> getOrderDetailById(@Path("id") String id);

    @GET("order-details/{orderId}")
    Call<OrderDetail[]> getOrderDetailsByOrderId(@Path("orderId") String orderId);

    @POST(ORDER_DETAILS)
    Call<OrderDetail> createOrderDetail(@Body OrderDetail orderDetail);

    @PUT(ORDER_DETAILS + "/{id}")
    Call<OrderDetail> updateOrderDetail(@Path("id") String id, @Body OrderDetail orderDetail);

    @DELETE(ORDER_DETAILS + "/{id}")
    Call<OrderDetail> deleteOrderDetail(@Path("id") String id);
}
