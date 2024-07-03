package com.example.twovn.service;

import com.example.twovn.model.Auth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AuthService {
    String AUTH = "auth";

    @GET(AUTH)
    Call<Auth[]> getAllAuth();

    @GET(AUTH + "/{id}")
    Call<Auth> getAuthById(@Path("id") Object id);

    @POST(AUTH)
    Call<Auth> createAuth(@Body Auth auth);

    @PUT(AUTH + "/{id}")
    Call<Auth> updateAuth(@Path("id") Object id, @Body Auth auth);

    @DELETE(AUTH + "/{id}")
    Call<Auth> deleteAuth(@Path("id") Object id);

    @POST(AUTH + "/register")
    Call<Auth> registerUser(@Body Auth auth);
}
