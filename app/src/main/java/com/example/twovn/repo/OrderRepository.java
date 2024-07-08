package com.example.twovn.repo;

import com.example.twovn.api.APIClient;
import com.example.twovn.service.OrderService;

public class OrderRepository {
    public static OrderService getOrderService(){
        return APIClient.getClient().create(OrderService.class);
    }
}
