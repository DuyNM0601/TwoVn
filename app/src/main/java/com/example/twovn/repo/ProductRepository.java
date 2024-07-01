package com.example.twovn.repo;

import com.example.twovn.api.APIClient;
import com.example.twovn.service.ProductService;

public class ProductRepository {
    public static ProductService getProductService(){
        return APIClient.getClient().create(ProductService.class);
    }
}
