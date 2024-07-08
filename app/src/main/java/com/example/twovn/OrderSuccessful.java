package com.example.twovn;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.twovn.model.Account;
import com.example.twovn.repo.AccountRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderSuccessful extends Fragment {

    TextView nameText, phoneText, addressText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_successful, container, false);

        nameText = view.findViewById(R.id.orderName);
        phoneText = view.findViewById(R.id.orderPhone);
        addressText = view.findViewById(R.id.orderAddress);
        loadUserInfo();
        // Inflate the layout for this fragment
        return view;
    }
    private void loadUserInfo() {
        // Lấy thông tin từ SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySession", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            AccountRepository.getAccountService().getAccountById(userId).enqueue(new Callback<Account>() {
                @Override
                public void onResponse(Call<Account> call, Response<Account> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Account account = response.body();
                        nameText.setText(account.getUserName());
                        phoneText.setText(account.getPhoneNumber());
                        addressText.setText(account.getAddress());
                    }
                }
                @Override
                public void onFailure(Call<Account> call, Throwable t) {
                    Log.e("Fail to load user info", "Error loading user info: " + t.getMessage());
                }
            });
        } else {
            Log.e("Fail user info", "UserId is null");
        }
    }
}