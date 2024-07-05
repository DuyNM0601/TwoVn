package com.example.twovn.repo;

import com.example.twovn.api.APIClient;
import com.example.twovn.service.AccountService;
import com.example.twovn.service.AuthService;

public class AccountRepository {
    public static AccountService getAccountService(){
        return APIClient.getClient().create(AccountService.class);
    }
}
