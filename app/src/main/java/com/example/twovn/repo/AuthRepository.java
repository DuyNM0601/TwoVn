package com.example.twovn.repo;

import com.example.twovn.api.APIClient;
import com.example.twovn.service.AuthService;
import com.example.twovn.service.ProductService;

public class AuthRepository {
    public static AuthService getAuthService(){
        return APIClient.getClient().create(AuthService.class);
    }
}
