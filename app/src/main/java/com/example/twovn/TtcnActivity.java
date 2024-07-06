package com.example.twovn;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twovn.QldnActivity;
import com.example.twovn.R;
import com.example.twovn.TtcnActivity;
import com.example.twovn.adapter.CartAdapter;
import com.example.twovn.model.Account;
import com.example.twovn.model.Product;
import com.example.twovn.repo.AccountRepository;
import com.example.twovn.repo.CartRepository;
import com.example.twovn.utils.VNPayUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TtcnActivity extends AppCompatActivity {

    Button logoutButton;
    EditText nameText, phoneText, addressText,emailText;
    Button updateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ttcn);
        nameText = findViewById(R.id.edtName);
        phoneText = findViewById(R.id.edtPhone);
        addressText = findViewById(R.id.edtAddress);
        emailText = findViewById(R.id.edtEmail);
        updateButton = findViewById(R.id.btnsave);
        logoutButton = findViewById(R.id.btnLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        loadUserInfo();
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new Account object with the updated information
                Account updatedAccount = new Account();
                updatedAccount.setUserName(nameText.getText().toString());
                updatedAccount.setAddress(addressText.getText().toString());
                updatedAccount.setPhoneNumber(phoneText.getText().toString());
                updatedAccount.setEmail(emailText.getText().toString());

                // Call the updateAccount method
                SharedPreferences sharedPreferences = getSharedPreferences("MySession", MODE_PRIVATE);
                String userId = sharedPreferences.getString("userId", null);
                AccountRepository.getAccountService().updateAccount(userId, updatedAccount).enqueue(new Callback<Account>() {
                    @Override
                    public void onResponse(Call<Account> call, Response<Account> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(TtcnActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TtcnActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                            Log.d("UpdateUser", "Response Code: " + response.code() + " Response Message: " + response.message());

                        }
                    }

                    @Override
                    public void onFailure(Call<Account> call, Throwable t) {
                        Toast.makeText(TtcnActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void logoutUser() {
        // Xóa session
        SharedPreferences sharedPreferences = getSharedPreferences("MySession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Xóa tất cả dữ liệu trong SharedPreferences
        editor.apply();

        // Chuyển hướng người dùng về trang đăng nhập
        Intent intent = new Intent(TtcnActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa tất cả các activity trước đó
        startActivity(intent);
        finish();
    }
    private void loadUserInfo() {
        // Lấy thông tin từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MySession", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            AccountRepository.getAccountService().getAccountById(userId).enqueue(new Callback<Account>() {
                @Override
                public void onResponse(Call<Account> call, Response<Account> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Account account = response.body();
                        nameText.setText(account.getUserName());
                        addressText.setText(account.getAddress());
                        phoneText.setText(account.getPhoneNumber());
                        emailText.setText(account.getEmail());


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
