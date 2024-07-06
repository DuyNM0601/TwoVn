package com.example.twovn.service;

import com.example.twovn.model.Account;
import com.example.twovn.model.Cart;
import com.example.twovn.model.Product;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AccountService {
    String ACCOUNTS = "accounts";
    @GET(ACCOUNTS + "/{id}")
    Call<Account> getAccountById (@Path("id") Object id);
    @PUT(ACCOUNTS + "/{id}")
    Call<Account> updateAccount(@Path("id") String id, @Body Account account);}
