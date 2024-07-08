package com.example.twovn.repo;

import com.example.twovn.api.APIClient;
import com.example.twovn.service.OrderDetailService;

public class OrderDetailRepository {
    public static OrderDetailService getOrderDetailService(){
        return APIClient.getClient().create(OrderDetailService.class);
    }
}
